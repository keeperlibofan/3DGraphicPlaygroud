package com.bn.Playground.ConeFactory;

import com.bn.Playground.Graph;
import com.bn.Playground.MatrixState;
import com.bn.Playground.MySurfaceView;

//骨架圆锥类
public class ConeL implements Graph
{
	CircleL bottomCircle;//底圆的骨架类的引用
	ConeSideL coneSide;//侧面的骨架类的引用
	private float xAngle=0;//绕x轴旋转的角度
	public void addxAngle(float xAngle) {
		this.xAngle += xAngle;
	}
	private float yAngle=0;//绕y轴旋转的角度
	public void addyAngle(float yAngle) {
		this.yAngle += yAngle;
	}
	private float zAngle=0;//绕z轴旋转的角度
	public void addzAngle(float zAngle) {
		this.zAngle += zAngle;
	}
    float h;
    float scale;
    
	public ConeL(MySurfaceView mySurfaceView, float scale, float r, float h, int n)
	{
		this.scale=scale;
		this.h=h;
		bottomCircle=new CircleL(mySurfaceView,scale,r,n);  //创建底面骨架圆的对象
		coneSide=new ConeSideL(mySurfaceView,scale,r,h,n); //创建侧面无顶圆锥骨架的对象
	}
	public void drawSelf()
	{
		MatrixState.rotate(xAngle, 1, 0, 0);
		MatrixState.rotate(yAngle, 0, 1, 0);
		MatrixState.rotate(zAngle, 0, 0, 1);		
		
		//底面
		MatrixState.pushMatrix();
		MatrixState.translate(0, -h/2, 0);
		MatrixState.rotate(90, 1, 0, 0);
		MatrixState.rotate(180, 0, 0, 1);
		bottomCircle.drawSelf();
		MatrixState.popMatrix();
		
		//侧面
		MatrixState.pushMatrix();
		MatrixState.translate(0, -h/2, 0);
		coneSide.drawSelf();
		MatrixState.popMatrix();
	}
}
