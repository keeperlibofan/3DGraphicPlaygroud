package com.bn.Playground.ObjLoader;//������

import android.opengl.GLES30;
import com.bn.Playground.Graph;
import com.bn.Playground.MatrixState;
import com.bn.Playground.MySurfaceView;
import com.bn.Playground.ShaderUtil;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

//���غ�����塪����Я��������Ϣ
public class LoadedObjectVertexOnly implements Graph
{
    private float xAngle=0;//��x����ת�ĽǶ�
    public void addxAngle(float xAngle) {
        this.xAngle += xAngle;
    }
    private float yAngle=0;//��y����ת�ĽǶ�
    public void addyAngle(float yAngle) {
        this.yAngle += yAngle;
    }
    private float zAngle=0;//��z����ת�ĽǶ�
    public void addzAngle(float zAngle) {
        this.zAngle += zAngle;
    }

	int mProgram;//�Զ�����Ⱦ������ɫ������id
    int muMVPMatrixHandle;//�ܱ任��������
    int maPositionHandle; //����λ����������  
    String mVertexShader;//������ɫ������ű�    	 
    String mFragmentShader;//ƬԪ��ɫ������ű�
	
	FloatBuffer   mVertexBuffer;//�����������ݻ���
    int vCount=0;  //��������
    
    public LoadedObjectVertexOnly(MySurfaceView mv, float[] vertices) // �ϲ㴫��vertices����
    {    	
    	//��ʼ����������
    	initVertexData(vertices);
    	//��ʼ����ɫ��       
    	initShader(mv);
    }
    
    //��ʼ����������ķ���
    public void initVertexData(float[] vertices)
    {
    	//�����������ݵĳ�ʼ��================begin============================
    	vCount=vertices.length/3;   
		
        //���������������ݻ���
        //vertices.length*4����Ϊһ�������ĸ��ֽ�
        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length*4);//���������������ݻ���
        vbb.order(ByteOrder.nativeOrder());//�����ֽ�˳��
        mVertexBuffer = vbb.asFloatBuffer();//ת��ΪFloat�ͻ���
        mVertexBuffer.put(vertices);//�򻺳����з��붥����������
        mVertexBuffer.position(0);//���û�������ʼλ��
        //�ر���ʾ�����ڲ�ͬƽ̨�ֽ�˳��ͬ���ݵ�Ԫ�����ֽڵ�һ��Ҫ����ByteBuffer
        //ת�����ؼ���Ҫͨ��ByteOrder����nativeOrder()�������п��ܻ������
        //�����������ݵĳ�ʼ��================end============================
    }

    //��ʼ��shader
    public void initShader(MySurfaceView mv)
    {
    	//���ض�����ɫ���Ľű�����
        mVertexShader= ShaderUtil.loadFromAssetsFile("ch_color.vert", mv.getResources());
        //����ƬԪ��ɫ���Ľű�����
        mFragmentShader=ShaderUtil.loadFromAssetsFile("ch_color.frag", mv.getResources());
        //���ڶ�����ɫ����ƬԪ��ɫ����������
        mProgram = ShaderUtil.createProgram(mVertexShader, mFragmentShader);
        //��ȡ�����ж���λ����������  
        maPositionHandle = GLES30.glGetAttribLocation(mProgram, "aPosition");
        //��ȡ�������ܱ任��������
        muMVPMatrixHandle = GLES30.glGetUniformLocation(mProgram, "uMVPMatrix");  
    }
    
    public void drawSelf()
    {
        MatrixState.rotate(xAngle, 1, 0, 0);
        MatrixState.rotate(yAngle, 0, 1, 0);
        MatrixState.rotate(zAngle, 0, 0, 1);

        MatrixState.pushMatrix();
        //�ƶ�ʹ��ĳ����ɫ������
    	 GLES30.glUseProgram(mProgram); 
         //�����ձ任��������ɫ������
         GLES30.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0);
         // ������λ�����ݴ�����Ⱦ����
         GLES30.glVertexAttribPointer  
         (
         		maPositionHandle,   
         		3, 
         		GLES30.GL_FLOAT, 
         		false,
                3*4,   
                mVertexBuffer
         );
         //���ö���λ������
         GLES30.glEnableVertexAttribArray(maPositionHandle); 
         //���Ƽ��ص�����
         GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, vCount);  // �����η�ʽ����
        MatrixState.popMatrix();
    }
}
