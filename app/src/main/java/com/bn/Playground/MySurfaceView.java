package com.bn.Playground;

import java.io.IOException;
import java.io.InputStream;

import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.view.MotionEvent;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.bn.Playground.ConeFactory.Cone;
import com.bn.Playground.ConeFactory.ConeL;
import com.bn.Playground.CylinderFactory.Cylinder;
import com.bn.Playground.CylinderFactory.CylinderL;
import com.bn.Playground.MathUtil.VectorUtil;
import com.bn.Playground.ObjLoader.LoadUtil;
import com.bn.Playground.ObjLoader.LoadedObjectVertexOnly;
import com.bn.Playground.Ragular20Factory.Regular20L;
import com.bn.Playground.Ragular20Factory.Regular20;
import com.bn.Playground.SoccerFacotry.Soccer;
import com.bn.Playground.SpringFactory.Spring;
import com.bn.Playground.SpringFactory.SpringL;
import com.bn.Playground.TorusFactory.Torus;
import com.bn.Playground.TorusFactory.TorusL;

public class MySurfaceView extends GLSurfaceView {
    
	private final float TOUCH_SCALE_FACTOR = 90.0f/320;//角度缩放比例
	private float mPreviousY;//上次的触控位置Y坐标
    private float mPreviousX;//上次的触控位置X坐标
    private float mPreviousY1;//上次的触控位置Y坐标
    private float mPreviousX1;//上次的触控位置X坐标

	private SceneRenderer mRenderer;//场景渲染器
    int[] textureIds = new int[4];      //系统分配的纹理id
    
    boolean drawWhatFlag=true;	//绘制线填充方式的标志位
    boolean lightFlag=true;
    int count = 0;

	public MySurfaceView(Context context) {
        super(context);
        this.setEGLContextClientVersion(3); //设置使用OPENGL ES3.0
        mRenderer = new SceneRenderer();	//创建场景渲染器
        setRenderer(mRenderer);				//设置渲染器		        
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);//设置渲染模式为主动渲染   
    }
	
	//触摸事件回调方法
    @Override 
    public boolean onTouchEvent(MotionEvent e) {
        float y = e.getY();
        float x = e.getX();
        // todo 双指触控就是平移整个坐标系, 单指双击就是回到默认视角
        switch (e.getAction()) {
            case MotionEvent.ACTION_MOVE:
                float dy = y - mPreviousY;//计算触控笔Y位移
                float dx = x - mPreviousX;//计算触控笔X位移
                float[] vec = {dx, dy, 0};
                switch (e.getPointerCount()){
                    case 1:
                        if (mPreviousY != 0 || mPreviousX != 0) {
                            mRenderer.graph.addyAngle(dx * TOUCH_SCALE_FACTOR); // 设置绕y轴旋转角度
                            mRenderer.graph.addzAngle(-dy * TOUCH_SCALE_FACTOR); // 设置绕z轴旋转角度

                            mRenderer.graphl.addyAngle(dx * TOUCH_SCALE_FACTOR); // 设置绕x轴旋转角度
                            mRenderer.graphl.addzAngle(-dy * TOUCH_SCALE_FACTOR); // 设置绕z轴旋转角度
                        }
                        break;
                    case 2: // 双指触控
                        /**
                         * 变换判定，如果向量夹角小于90度 判定为移动摄像头
                         * 如果大于90度判定为缩放图形，移动方向判定为两向量和
                         */
                        float x1 = e.getX(1);
                        float y1 = e.getY(1);
                        if (mPreviousY1 != 0 || mPreviousX1 != 0) {
                            float dy1 = y1 - mPreviousY1; // 计算触控笔Y位移
                            float dx1 = x1 - mPreviousX1; // 计算触控笔X位移
                            float[] vec1 = {dx1, dy1, 0};
                            if (VectorUtil.angle(vec, vec1) < Math.PI / 2) {
                                /** 判定为移动摄像机 */
                                float[] resultVec = VectorUtil.add(vec, vec1);
                                // 计算相机观察方向向量, 改变摄像机矩阵
                                MatrixState.translateCamera(-resultVec[0], -resultVec[1], (float)0.01);
                            } else {
                                /** 判定为拉近摄像机 通过两点的距离来判断, 如果距离变大就拉远距离变小就拉近*/
                                // 原距离
                                float[] previousPoint = {mPreviousX, mPreviousY};
                                float[] previousPoint1 = {mPreviousX1, mPreviousY1};
                                float originDistance = VectorUtil.distanceTwoVector(previousPoint, previousPoint1);
                                float[] Point = {x, y};
                                float[] Point1 = {x1, y1};
                                float distance = VectorUtil.distanceTwoVector(Point, Point1);
                                MatrixState.zoomInCamera(distance - originDistance, (float) 0.01);
                            }
                        }
                        mPreviousY1 = y1; // 记录值
                        mPreviousX1 = x1; // 记录值
                        break;
                }
                break;
            case MotionEvent.ACTION_UP: /**只要还有触控点在屏幕上，每当手指离开都会触发这个事件*/
                // 触发时将所有的 mPreviousY 都清零
                mPreviousY = 0;
                mPreviousX = 0;
                mPreviousY1 = 0;
                mPreviousX1 = 0;
        }
        mPreviousY = y;//记录触控笔位置
        mPreviousX = x;//记录触控笔位置
        return true;
    }
    
	private class SceneRenderer implements GLSurfaceView.Renderer
    {
        Graph graph;
        Graph graphl;

        // 所有图形
		Cylinder cylinder;
		CylinderL cylinderl;

        Cone cone;
        ConeL conel;

        Torus torus;
        TorusL torusl;

        Spring spring;
        SpringL springl;

        Regular20 regular20;
        Regular20L regular20L;

        Soccer soccer;

        LoadedObjectVertexOnly lovo;

        public void onDrawFrame(GL10 gl) 
        { 
        	//清除深度缓冲与颜色缓冲
            GLES30.glClear( GLES30.GL_DEPTH_BUFFER_BIT | GLES30.GL_COLOR_BUFFER_BIT);   

            switch (count) {
                case 0:
                    graph = cylinder;
                    graphl = cylinderl;
                    break;
                case 1:
                    graph = cone;
                    graphl = conel;
                    break;
                case 2:
                    graph = torus;
                    graphl = torusl;
                    break;
                case 3:
                    graph = spring;
                    graphl = springl;
                    break;
                case 4:
                    graph = regular20;
                    graphl = regular20L;
                    break;
                case 5:
                    graph = soccer;
                case 6:
                    graph = lovo;
            }
            //保护现场
            MatrixState.pushMatrix();
            MatrixState.translate(0, 0, -10);
            if(drawWhatFlag) {
                graph.drawSelf();
            } else {
                graphl.drawSelf();
            }
            MatrixState.popMatrix();
        }
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            //设置视窗大小及位置 
        	GLES30.glViewport(0, 0, width, height); 
        	//计算GLSurfaceView的宽高比
            float ratio= (float) width / height;
            //调用此方法计算产生透视投影矩阵
            MatrixState.setProjectFrustum(-ratio, ratio, -1, 1, 4f, 500);
            //调用此方法产生摄像机9参数位置矩阵
            MatrixState.setCamera(0,0,8.0f,0f,0f,0f,0f,1.0f,0.0f); 
            
	        //初始化光源
	        MatrixState.setLightLocation(10 , 0 , -10);
	                      
	        //启动一个线程定时修改灯光的位置
	        new Thread()
	        {
				public void run()
				{
					float redAngle = 0;
					while(lightFlag)
					{	
						//根据角度计算灯光的位置
						redAngle=(redAngle+5)%360;
						float rx=(float) (15*Math.sin(Math.toRadians(redAngle)));
						float rz=(float) (15*Math.cos(Math.toRadians(redAngle)));
						MatrixState.setLightLocation(rx, 0, rz);
						
						try {
								Thread.sleep(100);
							} catch (InterruptedException e) {				  			
								e.printStackTrace();
							}
					}
				}
	        }.start();
        }

        /**
         * surface创建的时候将所有的图形都创建, 顶点加载进入缓存
         */
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            //设置屏幕背景色RGBA
            GLES30.glClearColor(0.0f,0.0f,0.0f, 1.0f);  
            //启用深度测试
            GLES30.glEnable(GLES30.GL_DEPTH_TEST);
    		//设置为打开背面剪裁
            GLES30.glEnable(GLES30.GL_CULL_FACE);
            //初始化变换矩阵
            MatrixState.setInitStack();
            //加载纹理
            textureIds[0]=initTexture(R.drawable.android_robot0);
            textureIds[1]=initTexture(R.drawable.android_robot1);
            textureIds[2]=initTexture(R.drawable.android_robot2);
            textureIds[3] = initTexture(R.drawable.android_robot3);

            //创建圆柱对象 初始化
            cylinder = new Cylinder(MySurfaceView.this,1,1.2f,3.9f,36, textureIds[0], textureIds[0], textureIds[0]);
            //创建圆柱骨架对象
            cylinderl = new CylinderL(MySurfaceView.this,1,1.2f,3.9f,36);
            //创建圆锥对象
            cone = new Cone(MySurfaceView.this,1,1.6f,3.9f,36,textureIds[0],textureIds[0]);
            //创建圆锥骨架对象
            conel= new ConeL(MySurfaceView.this,1,1.6f,3.9f,36);
            //创建圆环对象
            torus = new Torus(MySurfaceView.this,2.25f, 1.2f, 10, 30, textureIds[1]);
            //创建圆环对象
            torusl= new TorusL(MySurfaceView.this,2.25f, 1.2f, 10, 30);
            //创建螺旋管对象
            spring = new Spring(MySurfaceView.this,1.8f,1.0f,7f,3.3f,10,80, textureIds[2]);
            //创建螺旋管骨架对象
            springl= new SpringL(MySurfaceView.this,1.8f,1.0f,7f,3.3f,10,80);
            //创建正20面体对象
            regular20 = new Regular20(MySurfaceView.this,1,1.6f,10, textureIds[3]);
            //创建正20面体骨架对象
            regular20L = new Regular20L(MySurfaceView.this,1,1.6f,5);
            //创建soccer对象
            soccer = new Soccer(MySurfaceView.this);
            lovo = LoadUtil.loadFromFile("ch.obj", MySurfaceView.this.getResources(), MySurfaceView.this);
        }
    }

	public int initTexture(int drawableId)//textureId
	{
		//生成纹理ID
		int[] textures = new int[1];
		GLES30.glGenTextures
		(
				1,          //产生的纹理id的数量
				textures,   //纹理id的数组
				0           //偏移量
		);    
		int textureId=textures[0];    
		GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureId);
		GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER,GLES30.GL_NEAREST);
		GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D,GLES30.GL_TEXTURE_MAG_FILTER,GLES30.GL_LINEAR);
		GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S,GLES30.GL_CLAMP_TO_EDGE);
		GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T,GLES30.GL_CLAMP_TO_EDGE);
        
        //通过输入流加载图片===============begin===================
        InputStream is = this.getResources().openRawResource(drawableId);
        Bitmap bitmapTmp;
        try 
        {
        	bitmapTmp = BitmapFactory.decodeStream(is);
        } 
        finally 
        {
            try 
            {
                is.close();
            } 
            catch(IOException e) 
            {
                e.printStackTrace();
            }
        }
        //通过输入流加载图片===============end=====================  
        
        //实际加载纹理
        GLUtils.texImage2D
        (
        		GLES30.GL_TEXTURE_2D,   //纹理类型，在OpenGL ES中必须为GL10.GL_TEXTURE_2D
        		0, 					  //纹理的层次，0表示基本图像层，可以理解为直接贴图
        		bitmapTmp, 			  //纹理图像
        		0					  //纹理边框尺寸
        );
        bitmapTmp.recycle(); 		  //纹理加载成功后释放图片
        
        return textureId;
	}
}
