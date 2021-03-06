package com.bn.Playground;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import android.opengl.Matrix;

import com.bn.Playground.MathUtil.VectorUtil;

//存储系统矩阵状态的类
public class MatrixState 
{  
	private static float[] mProjMatrix = new float[16];//4x4矩阵 投影用

    private static float[] mVMatrix = new float[16];//摄像机位置朝向9参数矩阵 这个类线程不安全
    private static float[] currMatrix;//当前变换矩阵
    public static float[] lightLocation = new float[]{0,0,0};//定位光光源位置

    /**非线程安全类*/
    public static FloatBuffer cameraFB;
    public static FloatBuffer lightPositionFB;
    private static ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    public static ReentrantReadWriteLock.ReadLock lightPositionFBReadLock = readWriteLock.readLock();
    public static ReentrantReadWriteLock.WriteLock lightPositionFBWriteLock = readWriteLock.writeLock();

    //保护变换矩阵的栈
    static float[][] mStack=new float[10][16];
    static int stackTop=-1;

    /**获取不变换初始矩阵(初始举证)*/
    public static void setInitStack()
    {
    	currMatrix=new float[16];
    	Matrix.setRotateM(currMatrix, 0, 0, 1, 0, 0);
    }
    
    public static void pushMatrix()//保护变换矩阵
    {
    	stackTop++;
    	for(int i=0;i<16;i++)
    	{
    		mStack[stackTop][i]=currMatrix[i];
    	}
    }
    
    public static void popMatrix()//恢复变换矩阵
    {
    	for(int i=0;i<16;i++)
    	{
    		currMatrix[i]=mStack[stackTop][i];
    	}
    	stackTop--;
    }
    
    public static void translate(float x,float y,float z)//设置沿xyz轴移动
    {
    	Matrix.translateM(currMatrix, 0, x, y, z);
    }
    
    public static void rotate(float angle, float x, float y, float z)//设置绕xyz轴移动
    {
    	Matrix.rotateM(currMatrix,0, angle, x, y, z);
    }
    
    public static void scale(float x,float y,float z)
    {
    	Matrix.scaleM(currMatrix,0, x, y, z);
    }
    
    //插入自带矩阵
    public static void matrix(float[] self)
    {
    	float[] result=new float[16];
    	Matrix.multiplyMM(result,0,currMatrix,0,self,0);
    	currMatrix=result;
    }
    
    //设置摄像机
    static ByteBuffer llbb = ByteBuffer.allocateDirect(3*4);

    /**非线性安全的数组*/
    private static float[] cameraLocation = new float[3]; //摄像机位置
    private static float[] cameraTargetPoint = new float[3]; //摄像机朝向点
    private static float[] cameraUpVec = new float[3]; //摄像机朝向点
    //static float[] cameraRightVec = new float[3]; // 摄像机右方向向量

    /**
     * 在相机相机朝向面上平移相机
     * up向量为dy控制
     **/
    static void translateCamera(float dx, float dy, float scale) {
        dx *= scale;
        dy *= scale;
        float[] cameraOrientationVec = VectorUtil.subtract(cameraTargetPoint, cameraLocation);
        float[] cameraLeftVec = VectorUtil.normalizeVector(VectorUtil.crossTwoVectors(cameraUpVec, cameraOrientationVec));
        // 改变摄像机的位置和朝向点
        float[] tempCameraLocation = VectorUtil.add(cameraLocation, VectorUtil.scaleVector(dx, cameraLeftVec));
        tempCameraLocation = VectorUtil.add(tempCameraLocation, VectorUtil.scaleVector(dy, cameraUpVec));

        float[] tempCameraTargetPoint = VectorUtil.add(cameraTargetPoint, VectorUtil.scaleVector(dx, cameraLeftVec));
        tempCameraTargetPoint = VectorUtil.add(tempCameraTargetPoint, VectorUtil.scaleVector(dy, cameraUpVec));

        setCamera(tempCameraLocation, tempCameraTargetPoint, cameraUpVec);
    }

    /**沿着相机朝向拉近拉远*/
    static void zoomInCamera(float dz, float scale) {
        dz *= scale;
        float[] orientation = VectorUtil.subtract(cameraTargetPoint, cameraLocation);// 朝向
        orientation = VectorUtil.scaleVector(dz, orientation); // 正负效果

        float[] tempCameraLocation = VectorUtil.add(cameraLocation, orientation);
        float[] tempCameraTargetPoint = VectorUtil.add(cameraTargetPoint, orientation);

        setCamera(tempCameraLocation, tempCameraTargetPoint, cameraUpVec);
    }

    /**
     * 改变相机朝向,同时改变相机
     * 的up向量方向和targetPoint向量
     **/
    public static void changeCameraOrientation(float dx, float dy) {

    }

    /**旋转摄像机*/
    public static void rotateCamera(float dx, float dy) {

    }

    /**设置摄像机*/
    synchronized private static void setCamera(float[] tempCameraLocation, float[] tempCameraTargetPoint, float[] tempCameraUpVec) {
        // 只有此处可以写
        cameraLocation = tempCameraLocation;
        cameraUpVec = tempCameraTargetPoint;
        cameraUpVec = tempCameraUpVec;

        float[] tempmVMatrix = new float[16];
        Matrix.setLookAtM( // 这是一个会对原始数组做修改的一个函数, 是非线程安全的
                tempmVMatrix,
                0,
                cameraLocation[0],
                cameraLocation[1],
                cameraLocation[2],
                cameraTargetPoint[0],
                cameraTargetPoint[1],
                cameraTargetPoint[2],
                cameraUpVec[0],
                cameraUpVec[1],
                cameraUpVec[2]
        );
        mVMatrix = tempmVMatrix;

        FloatBuffer tempCameraFB;
        llbb.clear();
        llbb.order(ByteOrder.nativeOrder());//设置字节顺序
        tempCameraFB=llbb.asFloatBuffer(); // 此事buffer还是空的
        tempCameraFB.put(cameraLocation);
        tempCameraFB.position(0);
        cameraFB = tempCameraFB;
    }

    public static void setCamera
    (
    		float cx,	//摄像机位置x
    		float cy,   //摄像机位置y
    		float cz,   //摄像机位置z
    		float tx,   //摄像机目标点x
    		float ty,   //摄像机目标点y
    		float tz,   //摄像机目标点z
    		float upx,  //摄像机UP向量X分量
    		float upy,  //摄像机UP向量Y分量
    		float upz   //摄像机UP向量Z分量		
    )
    {
        Matrix.setLookAtM
        (
                mVMatrix,
                0,
                cx,
                cy,
                cz,
                tx,
                ty,
                tz,
                upx,
                upy,
                upz
        );

        cameraLocation[0]=cx;
        cameraLocation[1]=cy;
        cameraLocation[2]=cz;

        cameraTargetPoint[0] = tx;
        cameraTargetPoint[1] = ty;
        cameraTargetPoint[2] = tz;

        cameraUpVec[0] = upx;
        cameraUpVec[1] = upy;
        cameraUpVec[2] = upz;

        llbb.clear();
        llbb.order(ByteOrder.nativeOrder());//设置字节顺序
        cameraFB=llbb.asFloatBuffer();
        cameraFB.put(cameraLocation);
        cameraFB.position(0);
    }
    
    //设置透视投影参数
    public static void setProjectFrustum
    ( 
    	float left,		//near面的left
    	float right,    //near面的right
    	float bottom,   //near面的bottom
    	float top,      //near面的top
    	float near,		//near面距离
    	float far       //far面距离
    )
    {
    	Matrix.frustumM(mProjMatrix, 0, left, right, bottom, top, near, far);
    }
    
    //设置正交投影参数
    public static void setProjectOrtho
    (
    	float left,		//near面的left
    	float right,    //near面的right
    	float bottom,   //near面的bottom
    	float top,      //near面的top
    	float near,		//near面距离
    	float far       //far面距离
    )
    {    	
    	Matrix.orthoM(mProjMatrix, 0, left, right, bottom, top, near, far);
    }   
    //获取具体物体的总变换矩阵
    static float[] mMVPMatrix=new float[16];
    public static float[] getFinalMatrix()
    {	
    	Matrix.multiplyMM(mMVPMatrix, 0, mVMatrix, 0, currMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mMVPMatrix, 0);        
        return mMVPMatrix;
    }
    
    //获取具体物体的变换矩阵
    public static float[] getMMatrix()
    {       
        return currMatrix;
    }
    
    //获取投影矩阵
    public static float[] getProjMatrix()
    {
		return mProjMatrix;
    }
    
    //获取摄像机朝向的矩阵
    public static float[] getCaMatrix()
    {
		return mVMatrix;
    }
    
    
    
    //设置灯光位置的方法
    static ByteBuffer llbbL = ByteBuffer.allocateDirect(3*4);
    public static void setLightLocation(float x,float y,float z)
    {
    	llbbL.clear();
    	
    	lightLocation[0]=x;
    	lightLocation[1]=y;
    	lightLocation[2]=z;
    	
        llbbL.order(ByteOrder.nativeOrder());//设置字节顺序
        lightPositionFB=llbbL.asFloatBuffer();
        lightPositionFBWriteLock.lock();
        lightPositionFB.put(lightLocation);
        lightPositionFB.position(0);
        lightPositionFBWriteLock.unlock();
    }
}
