package com.bn.Playground.ConeFactory;

import com.bn.Playground.Graph;
import com.bn.Playground.MatrixState;
import com.bn.Playground.MySurfaceView;

//圆锥类
public class Cone implements Graph
{
	Circle bottomCircle;//底圆
	ConeSide coneSide;//侧面
	private float xAngle = 0;//绕x轴旋转的角度
	public void addxAngle(float xAngle) {
		this.xAngle += xAngle;
	}
	private float yAngle = 0;//绕y轴旋转的角度
	public void addyAngle(float yAngle) {
		this.yAngle += yAngle;
	}
	private float zAngle = 0;//绕z轴旋转的角度
	public void addzAngle(float zAngle) {
		this.zAngle += zAngle;
	}
    float h;
    float scale;

    int BottomTexId;  //底面纹理
    int sideTexId;  //侧面纹理
    
	public Cone(MySurfaceView mySurfaceView, float scale, float r, float h, int n,
				int BottomTexId, int sideTexId)
	{
		this.scale = scale;
		this.h = h;
		this.BottomTexId = BottomTexId;
		this.sideTexId = sideTexId;
		
		bottomCircle = new Circle(mySurfaceView, scale, r, n);  //创建底面圆对象
		coneSide = new ConeSide(mySurfaceView, scale, r, h, n); //创建圆锥侧面对象
	}
	public void drawSelf()
	{
		MatrixState.rotate(xAngle, 1, 0, 0);
		MatrixState.rotate(yAngle, 0, 1, 0);
		MatrixState.rotate(zAngle, 0, 0, 1);				
		//底面
		MatrixState.pushMatrix();
		MatrixState.translate(0, -h/2*scale, 0);
		bottomCircle.drawSelf(BottomTexId);
		MatrixState.popMatrix();
		//侧面
		MatrixState.pushMatrix();
		MatrixState.translate(0, -h/2*scale, 0);
		coneSide.drawSelf(sideTexId);
		MatrixState.popMatrix();
	}
}
