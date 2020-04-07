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
import com.bn.Playground.SpringFactory.Spring;
import com.bn.Playground.SpringFactory.SpringL;
import com.bn.Playground.TorusFactory.Torus;
import com.bn.Playground.TorusFactory.TorusL;

public class MySurfaceView extends GLSurfaceView {
    
	private final float TOUCH_SCALE_FACTOR = 180.0f/320;//�Ƕ����ű���
	private float mPreviousY;//�ϴεĴ���λ��Y����
    private float mPreviousX;//�ϴεĴ���λ��X����
	
	private SceneRenderer mRenderer;//������Ⱦ��
    int[] textureIds = new int[3];      //ϵͳ���������id
    
    boolean drawWhatFlag=true;	//��������䷽ʽ�ı�־λ
    boolean lightFlag=true;
    int count = 0;

	public MySurfaceView(Context context) {
        super(context);
        this.setEGLContextClientVersion(3); //����ʹ��OPENGL ES3.0
        mRenderer = new SceneRenderer();	//����������Ⱦ��
        setRenderer(mRenderer);				//������Ⱦ��		        
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);//������ȾģʽΪ������Ⱦ   
    }
	
	//�����¼��ص�����
    @Override 
    public boolean onTouchEvent(MotionEvent e) {
        float y = e.getY();
        float x = e.getX();
        switch (e.getAction()) {
        case MotionEvent.ACTION_MOVE:
            float dy = y - mPreviousY;//���㴥�ر�Yλ��
            float dx = x - mPreviousX;//���㴥�ر�Xλ��
            mRenderer.graph.addyAngle(dx * TOUCH_SCALE_FACTOR);//������y����ת�Ƕ�
            mRenderer.graph.addzAngle(dy * TOUCH_SCALE_FACTOR);//������z����ת�Ƕ�

            mRenderer.graphl.addyAngle(dx * TOUCH_SCALE_FACTOR);//������x����ת�Ƕ�
            mRenderer.graphl.addzAngle(dy * TOUCH_SCALE_FACTOR);//������z����ת�Ƕ�
        }
        mPreviousY = y;//��¼���ر�λ��
        mPreviousX = x;//��¼���ر�λ��
        return true;
    }
    
	private class SceneRenderer implements GLSurfaceView.Renderer 
    {
        Graph graph;
        Graph graphl;

        // ����ͼ��
		Cylinder cylinder;
		CylinderL cylinderl;

        Cone cone;
        ConeL conel;

        Torus torus;
        TorusL torusl;

        Spring spring;
        SpringL springl;

        public void onDrawFrame(GL10 gl) 
        { 
        	//�����Ȼ�������ɫ����
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
            }
            //�����ֳ�
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
            //�����Ӵ���С��λ�� 
        	GLES30.glViewport(0, 0, width, height); 
        	//����GLSurfaceView�Ŀ�߱�
            float ratio= (float) width / height;
            //���ô˷����������͸��ͶӰ����
            MatrixState.setProjectFrustum(-ratio, ratio, -1, 1, 4f, 100);
            //���ô˷������������9����λ�þ���
            MatrixState.setCamera(0,0,8.0f,0f,0f,0f,0f,1.0f,0.0f); 
            
	        //��ʼ����Դ
	        MatrixState.setLightLocation(10 , 0 , -10);
	                      
	        //����һ���̶߳�ʱ�޸ĵƹ��λ��
	        new Thread()
	        {
				public void run()
				{
					float redAngle = 0;
					while(lightFlag)
					{	
						//���ݽǶȼ���ƹ��λ��
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

        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            //������Ļ����ɫRGBA
            GLES30.glClearColor(0.0f,0.0f,0.0f, 1.0f);  
            //������Ȳ���
            GLES30.glEnable(GLES30.GL_DEPTH_TEST);
    		//����Ϊ�򿪱������
            GLES30.glEnable(GLES30.GL_CULL_FACE);
            //��ʼ���任����
            MatrixState.setInitStack();
            //��������
            textureIds[0]=initTexture(R.drawable.android_robot0);
            textureIds[1]=initTexture(R.drawable.android_robot1);
            textureIds[2]=initTexture(R.drawable.android_robot2);

            //����Բ������ ��ʼ��
            cylinder = new Cylinder(MySurfaceView.this,1,1.2f,3.9f,36, textureIds[0], textureIds[0], textureIds[0]);
            //����Բ���Ǽܶ���
            cylinderl = new CylinderL(MySurfaceView.this,1,1.2f,3.9f,36);
            //����Բ׶����
            cone = new Cone(MySurfaceView.this,1,1.6f,3.9f,36,textureIds[0],textureIds[0]);
            //����Բ׶�Ǽܶ���
            conel= new ConeL(MySurfaceView.this,1,1.6f,3.9f,36);
            //����Բ������
            torus = new Torus(MySurfaceView.this,2.25f, 1.2f, 10, 30, textureIds[1]);
            //����Բ������
            torusl= new TorusL(MySurfaceView.this,2.25f, 1.2f, 10, 30);
            //���������ܶ���
            spring = new Spring(MySurfaceView.this,1.8f,1.0f,7f,3.3f,10,80, textureIds[2]);
            //���������ܹǼܶ���
            springl= new SpringL(MySurfaceView.this,1.8f,1.0f,7f,3.3f,10,80);

        }
    }
	
	public int initTexture(int drawableId)//textureId
	{
		//��������ID
		int[] textures = new int[1];
		GLES30.glGenTextures
		(
				1,          //����������id������
				textures,   //����id������
				0           //ƫ����
		);    
		int textureId=textures[0];    
		GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureId);
		GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER,GLES30.GL_NEAREST);
		GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D,GLES30.GL_TEXTURE_MAG_FILTER,GLES30.GL_LINEAR);
		GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S,GLES30.GL_CLAMP_TO_EDGE);
		GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T,GLES30.GL_CLAMP_TO_EDGE);
        
        //ͨ������������ͼƬ===============begin===================
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
        //ͨ������������ͼƬ===============end=====================  
        
        //ʵ�ʼ�������
        GLUtils.texImage2D
        (
        		GLES30.GL_TEXTURE_2D,   //�������ͣ���OpenGL ES�б���ΪGL10.GL_TEXTURE_2D
        		0, 					  //����Ĳ�Σ�0��ʾ����ͼ��㣬�������Ϊֱ����ͼ
        		bitmapTmp, 			  //����ͼ��
        		0					  //����߿�ߴ�
        );
        bitmapTmp.recycle(); 		  //������سɹ����ͷ�ͼƬ
        
        return textureId;
	}
}
