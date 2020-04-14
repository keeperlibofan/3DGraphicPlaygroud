package com.bn.Playground;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import android.opengl.Matrix;

import com.bn.Playground.MathUtil.VectorUtil;

//�洢ϵͳ����״̬����
public class MatrixState 
{  
	private static float[] mProjMatrix = new float[16];//4x4���� ͶӰ��

    private static float[] mVMatrix = new float[16];//�����λ�ó���9�������� ������̲߳���ȫ
    private static float[] currMatrix;//��ǰ�任����
    public static float[] lightLocation = new float[]{0,0,0};//��λ���Դλ��

    /**���̰߳�ȫ��*/
    public static FloatBuffer cameraFB;
    public static FloatBuffer lightPositionFB;
    private static ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    public static ReentrantReadWriteLock.ReadLock lightPositionFBReadLock = readWriteLock.readLock();
    public static ReentrantReadWriteLock.WriteLock lightPositionFBWriteLock = readWriteLock.writeLock();

    //�����任�����ջ
    static float[][] mStack=new float[10][16];
    static int stackTop=-1;

    /**��ȡ���任��ʼ����(��ʼ��֤)*/
    public static void setInitStack()
    {
    	currMatrix=new float[16];
    	Matrix.setRotateM(currMatrix, 0, 0, 1, 0, 0);
    }
    
    public static void pushMatrix()//�����任����
    {
    	stackTop++;
    	for(int i=0;i<16;i++)
    	{
    		mStack[stackTop][i]=currMatrix[i];
    	}
    }
    
    public static void popMatrix()//�ָ��任����
    {
    	for(int i=0;i<16;i++)
    	{
    		currMatrix[i]=mStack[stackTop][i];
    	}
    	stackTop--;
    }
    
    public static void translate(float x,float y,float z)//������xyz���ƶ�
    {
    	Matrix.translateM(currMatrix, 0, x, y, z);
    }
    
    public static void rotate(float angle, float x, float y, float z)//������xyz���ƶ�
    {
    	Matrix.rotateM(currMatrix,0, angle, x, y, z);
    }
    
    public static void scale(float x,float y,float z)
    {
    	Matrix.scaleM(currMatrix,0, x, y, z);
    }
    
    //�����Դ�����
    public static void matrix(float[] self)
    {
    	float[] result=new float[16];
    	Matrix.multiplyMM(result,0,currMatrix,0,self,0);
    	currMatrix=result;
    }
    
    //���������
    static ByteBuffer llbb = ByteBuffer.allocateDirect(3*4);

    /**�����԰�ȫ������*/
    static float[] cameraLocation = new float[3]; //�����λ��
    static float[] cameraTargetPoint = new float[3]; //����������
    static float[] cameraUpVec = new float[3]; //����������
    //static float[] cameraRightVec = new float[3]; // ������ҷ�������

    /**
     * ����������������ƽ�����
     * up����Ϊdy����
     **/
    public static void translateCamera(float dx, float dy, float scale) {
        dx *= scale;
        dy *= scale;
        float[] cameraOrientationVec = VectorUtil.subtract(cameraTargetPoint, cameraLocation);
        float[] cameraLeftVec = VectorUtil.normalizeVector(VectorUtil.crossTwoVectors(cameraUpVec, cameraOrientationVec));
        // �ı��������λ�úͳ����
        cameraLocation = VectorUtil.add(cameraLocation, VectorUtil.scaleVector(dx, cameraLeftVec));
        cameraLocation = VectorUtil.add(cameraLocation, VectorUtil.scaleVector(dy, cameraUpVec));

        cameraTargetPoint = VectorUtil.add(cameraTargetPoint, VectorUtil.scaleVector(dx, cameraLeftVec));
        cameraTargetPoint = VectorUtil.add(cameraTargetPoint, VectorUtil.scaleVector(dy, cameraUpVec));

        float[] tempmVMatrix = new float[16];
        Matrix.setLookAtM( // ����һ�����ԭʼ�������޸ĵ�һ������, �Ƿ��̰߳�ȫ��
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
        llbb.order(ByteOrder.nativeOrder());//�����ֽ�˳��
        tempCameraFB=llbb.asFloatBuffer(); // ����buffer���ǿյ�
        tempCameraFB.put(cameraLocation);
        tempCameraFB.position(0);
        cameraFB = tempCameraFB;
    }

    /**
     * �ı��������,ͬʱ�ı����
     * ��up���������targetPoint����
     **/
    public static void rotateCamera(float dx, float dy) {

    }

    public static void setCamera
    (
    		float cx,	//�����λ��x
    		float cy,   //�����λ��y
    		float cz,   //�����λ��z
    		float tx,   //�����Ŀ���x
    		float ty,   //�����Ŀ���y
    		float tz,   //�����Ŀ���z
    		float upx,  //�����UP����X����
    		float upy,  //�����UP����Y����
    		float upz   //�����UP����Z����		
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
        llbb.order(ByteOrder.nativeOrder());//�����ֽ�˳��
        cameraFB=llbb.asFloatBuffer();
        cameraFB.put(cameraLocation);
        cameraFB.position(0);
    }
    
    //����͸��ͶӰ����
    public static void setProjectFrustum
    ( 
    	float left,		//near���left
    	float right,    //near���right
    	float bottom,   //near���bottom
    	float top,      //near���top
    	float near,		//near�����
    	float far       //far�����
    )
    {
    	Matrix.frustumM(mProjMatrix, 0, left, right, bottom, top, near, far);
    }
    
    //��������ͶӰ����
    public static void setProjectOrtho
    (
    	float left,		//near���left
    	float right,    //near���right
    	float bottom,   //near���bottom
    	float top,      //near���top
    	float near,		//near�����
    	float far       //far�����
    )
    {    	
    	Matrix.orthoM(mProjMatrix, 0, left, right, bottom, top, near, far);
    }   
    //��ȡ����������ܱ任����
    static float[] mMVPMatrix=new float[16];
    public static float[] getFinalMatrix()
    {	
    	Matrix.multiplyMM(mMVPMatrix, 0, mVMatrix, 0, currMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mMVPMatrix, 0);        
        return mMVPMatrix;
    }
    
    //��ȡ��������ı任����
    public static float[] getMMatrix()
    {       
        return currMatrix;
    }
    
    //��ȡͶӰ����
    public static float[] getProjMatrix()
    {
		return mProjMatrix;
    }
    
    //��ȡ���������ľ���
    public static float[] getCaMatrix()
    {
		return mVMatrix;
    }
    
    
    
    //���õƹ�λ�õķ���
    static ByteBuffer llbbL = ByteBuffer.allocateDirect(3*4);
    public static void setLightLocation(float x,float y,float z)
    {
    	llbbL.clear();
    	
    	lightLocation[0]=x;
    	lightLocation[1]=y;
    	lightLocation[2]=z;
    	
        llbbL.order(ByteOrder.nativeOrder());//�����ֽ�˳��
        lightPositionFB=llbbL.asFloatBuffer();
        lightPositionFBWriteLock.lock();
        lightPositionFB.put(lightLocation);
        lightPositionFB.position(0);
        lightPositionFBWriteLock.unlock();
    }
}
