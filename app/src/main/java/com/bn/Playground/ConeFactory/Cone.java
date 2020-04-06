package com.bn.Playground.ConeFactory;

import com.bn.Playground.Graph;
import com.bn.Playground.MatrixState;
import com.bn.Playground.MySurfaceView;

//Բ׶��
public class Cone implements Graph
{
	Circle bottomCircle;//��Բ
	ConeSide coneSide;//����
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

    int BottomTexId;  //��������
    int sideTexId;  //��������
    
	public Cone(MySurfaceView mySurfaceView, float scale, float r, float h, int n,
				int BottomTexId, int sideTexId)
	{
		this.scale=scale;
		this.h=h;
		this.BottomTexId=BottomTexId;
		this.sideTexId=sideTexId;
		
		bottomCircle = new Circle(mySurfaceView, scale, r, n);  //��������Բ����
		coneSide = new ConeSide(mySurfaceView, scale, r, h, n); //����Բ׶�������
	}
	public void drawSelf()
	{
		MatrixState.rotate(xAngle, 1, 0, 0);
		MatrixState.rotate(yAngle, 0, 1, 0);
		MatrixState.rotate(zAngle, 0, 0, 1);				
		//����
		MatrixState.pushMatrix();
		MatrixState.translate(0, -h/2*scale, 0);
		bottomCircle.drawSelf(BottomTexId);
		MatrixState.popMatrix();
		//����
		MatrixState.pushMatrix();
		MatrixState.translate(0, -h/2*scale, 0);
		coneSide.drawSelf(sideTexId);
		MatrixState.popMatrix();
	}
}