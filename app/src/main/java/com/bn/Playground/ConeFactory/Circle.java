package com.bn.Playground.ConeFactory;

import android.opengl.GLES30;

import com.bn.Playground.MatrixState;
import com.bn.Playground.MySurfaceView;
import com.bn.Playground.ShaderUtil;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import static com.bn.Playground.ShaderUtil.createProgram;


//Բ��
public class Circle 
{	
	private int mProgram;//�Զ�����Ⱦ������ɫ������id
    private int muMVPMatrixHandle;//�ܱ任��������
    private int maPositionHandle; //����λ����������
    private int maTexCoorHandle; //��������������������
    private int muMMatrixHandle;
    
    private int maCameraHandle; //�����λ����������
    private int maNormalHandle; //���㷨������������
    private int maLightLocationHandle;//��Դλ����������
    
    
    private String mVertexShader; //������ɫ������ű�
    private String mFragmentShader; //ƬԪ��ɫ������ű�
	
	private FloatBuffer   mVertexBuffer;//�����������ݻ���
	private FloatBuffer   mTexCoorBuffer;//���������������ݻ���
	private FloatBuffer   mNormalBuffer;//���㷨�������ݻ���
    private int vCount=0;
    float xAngle=0;//��x����ת�ĽǶ�
    float yAngle=0;//��y����ת�ĽǶ�
    float zAngle=0;//��z����ת�ĽǶ�
    
    public Circle(MySurfaceView mv, float scale, float r, int n)
    {
    	//���ó�ʼ���������ݵ�initVertexData����
    	initVertexData(scale,r,n);
    	//���ó�ʼ����ɫ����intShader����
    	initShader(mv);
    }
    
    //�Զ���ĳ�ʼ���������ݵķ���
    public void initVertexData(
    		float scale,	//��С
    		float r,		//�뾶
    		int n)		//�зֵķ���
    {
    	r=r*scale;
		float angdegSpan=360.0f/n;	//���ǵĶ���
		vCount=3*n;//�������������n�������Σ�ÿ�������ζ�����������
		
		float[] vertices=new float[vCount*3];//��������
		float[] textures=new float[vCount*2];//��������S��T����ֵ����
		//�������ݳ�ʼ��
		int count=0;
		int stCount=0;
		for(float angdeg=0;Math.ceil(angdeg)<360;angdeg+=angdegSpan)
		{
			double angrad=Math.toRadians(angdeg);//��ǰ����
			double angradNext=Math.toRadians(angdeg+angdegSpan);//��һ����
			//���ĵ�
			vertices[count++]=0;//��������
			vertices[count++]=0; 
			vertices[count++]=0;
			
			textures[stCount++]=0.5f;//st����
			textures[stCount++]=0.5f;
			//��ǰ��
			vertices[count++]=(float) (-r*Math.sin(angrad));//��������
			vertices[count++]=0;
            vertices[count++]=(float) (r*Math.cos(angrad));

            textures[stCount++]=(float) (0.5f-0.5f*Math.sin(angrad));//st����
			textures[stCount++]=(float) (0.5f-0.5f*Math.cos(angrad));
			//��һ��
			vertices[count++]=(float) (-r*Math.sin(angradNext));//��������
			vertices[count++]=0;
            vertices[count++]=(float) (r*Math.cos(angradNext));

            textures[stCount++]=(float) (0.5f-0.5f*Math.sin(angradNext));//st����
			textures[stCount++]=(float) (0.5f-0.5f*Math.cos(angradNext));
		}
		ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length*4);//���������������ݻ���
        vbb.order(ByteOrder.nativeOrder());//�����ֽ�˳��Ϊ���ز���ϵͳ˳��
        mVertexBuffer = vbb.asFloatBuffer();//ת��Ϊfloat�ͻ���
        mVertexBuffer.put(vertices);//�򻺳����з��붥����������
        mVertexBuffer.position(0);//���û�������ʼλ��
        //���������ݳ�ʼ�� 
        float[] normals=new float[vertices.length];
        for(int i=0;i<normals.length;i+=3){
        	normals[i]=0;
        	normals[i+1]=0;
        	normals[i+2]=1;
        }
        ByteBuffer nbb = ByteBuffer.allocateDirect(normals.length*4);//�������㷨�������ݻ���
        nbb.order(ByteOrder.nativeOrder());//�����ֽ�˳��Ϊ���ز���ϵͳ˳��
        mNormalBuffer = nbb.asFloatBuffer();//ת��Ϊfloat�ͻ���
        mNormalBuffer.put(normals);//�򻺳����з��붥�㷨��������
        mNormalBuffer.position(0);//���û�������ʼλ��
        
        //�����������ݳ�ʼ��
        ByteBuffer cbb = ByteBuffer.allocateDirect(textures.length*4);//���������������ݻ���
        cbb.order(ByteOrder.nativeOrder());//�����ֽ�˳��Ϊ���ز���ϵͳ˳��
        mTexCoorBuffer = cbb.asFloatBuffer();//ת��Ϊfloat�ͻ���
        mTexCoorBuffer.put(textures);//�򻺳����з��붥����������
        mTexCoorBuffer.position(0);//���û�������ʼλ�� 
    }

    //�Զ����ʼ����ɫ��initShader����
    public void initShader(MySurfaceView mv){
    	//���ض�����ɫ���Ľű�����
        mVertexShader= ShaderUtil.loadFromAssetsFile("tex_light.vert", mv.getResources());
        //����ƬԪ��ɫ���Ľű�����
        mFragmentShader=ShaderUtil.loadFromAssetsFile("tex_light.frag", mv.getResources());
        //���ڶ�����ɫ����ƬԪ��ɫ����������
        mProgram = createProgram(mVertexShader, mFragmentShader);
        //��ȡ�����ж���λ����������id  
        maPositionHandle = GLES30.glGetAttribLocation(mProgram, "aPosition");
        //��ȡ�����ж�������������������id  
        maTexCoorHandle= GLES30.glGetAttribLocation(mProgram, "aTexCoor");
        //��ȡ�������ܱ任��������id
        muMVPMatrixHandle = GLES30.glGetUniformLocation(mProgram, "uMVPMatrix"); 
        //��ȡ�����ж��㷨������������id  
        maNormalHandle= GLES30.glGetAttribLocation(mProgram, "aNormal"); 
        //��ȡ�����������λ������id
        maCameraHandle=GLES30.glGetUniformLocation(mProgram, "uCamera"); 
        //��ȡ�����й�Դλ������id
        maLightLocationHandle=GLES30.glGetUniformLocation(mProgram, "uLightLocation"); 
        //��ȡλ�á���ת�任��������id
        muMMatrixHandle = GLES30.glGetUniformLocation(mProgram, "uMMatrix");  
    }
    
    public void drawSelf(int texId)
    {        
    	 //�ƶ�ʹ��ĳ��shader����
    	 GLES30.glUseProgram(mProgram);        
         
         //�����ձ任������shader����
         GLES30.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0);
         
         //��λ�á���ת�任������shader����
         GLES30.glUniformMatrix4fv(muMMatrixHandle, 1, false, MatrixState.getMMatrix(), 0); 
         //�������λ�ô���shader����   
         GLES30.glUniform3fv(maCameraHandle, 1, MatrixState.cameraFB);
         //����Դλ�ô���shader����   
         GLES30.glUniform3fv(maLightLocationHandle, 1, MatrixState.lightPositionFB);
         
         
         //���Ͷ���λ������
         GLES30.glVertexAttribPointer  
         (
         		maPositionHandle,   
         		3, 
         		GLES30.GL_FLOAT, 
         		false,
                3*4,   
                mVertexBuffer
         );       
         //���Ͷ���������������
         GLES30.glVertexAttribPointer  
         (
        		maTexCoorHandle, 
         		2, 
         		GLES30.GL_FLOAT, 
         		false,
                2*4,   
                mTexCoorBuffer
         ); 
         //���Ͷ��㷨��������
         GLES30.glVertexAttribPointer  
         (
        		maNormalHandle, 
         		4, 
         		GLES30.GL_FLOAT, 
         		false,
                3*4,   
                mNormalBuffer
         ); 
         
         //���ö���λ������
         GLES30.glEnableVertexAttribArray(maPositionHandle);
         //���ö�����������
         GLES30.glEnableVertexAttribArray(maTexCoorHandle);  
         //���ö��㷨��������
         GLES30.glEnableVertexAttribArray(maNormalHandle);
         
         
         //������
         GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
         GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, texId);
         
         //�����������
         GLES30.glDrawArrays(GLES30.GL_TRIANGLE_FAN, 0, vCount); 
    }
}
