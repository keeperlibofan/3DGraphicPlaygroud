package com.bn.Playground.ConeFactory;

import com.bn.Playground.Graph;
import com.bn.Playground.MatrixState;
import com.bn.Playground.MySurfaceView;

//�Ǽ�Բ׶��
public class ConeL implements Graph
{
	CircleL bottomCircle;//��Բ�ĹǼ��������
	ConeSideL coneSide;//����ĹǼ��������
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
    float h;
    float scale;
    
	public ConeL(MySurfaceView mySurfaceView, float scale, float r, float h, int n)
	{
		this.scale=scale;
		this.h=h;
		bottomCircle=new CircleL(mySurfaceView,scale,r,n);  //��������Ǽ�Բ�Ķ���
		coneSide=new ConeSideL(mySurfaceView,scale,r,h,n); //���������޶�Բ׶�ǼܵĶ���
	}
	public void drawSelf()
	{
		MatrixState.rotate(xAngle, 1, 0, 0);
		MatrixState.rotate(yAngle, 0, 1, 0);
		MatrixState.rotate(zAngle, 0, 0, 1);		
		
		//����
		MatrixState.pushMatrix();
		MatrixState.translate(0, -h/2, 0);
		MatrixState.rotate(90, 1, 0, 0);
		MatrixState.rotate(180, 0, 0, 1);
		bottomCircle.drawSelf();
		MatrixState.popMatrix();
		
		//����
		MatrixState.pushMatrix();
		MatrixState.translate(0, -h/2, 0);
		coneSide.drawSelf();
		MatrixState.popMatrix();
	}
}
