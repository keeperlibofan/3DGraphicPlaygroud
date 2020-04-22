package com.bn.Playground.IndiaBuildingFactory;

import com.bn.Playground.Graph;
import com.bn.Playground.MathUtil.BezierUtil.BNPosition;
import com.bn.Playground.MatrixState;
import com.bn.Playground.MySurfaceView;

import java.util.ArrayList;

/**
 * 绘制塔的类
 */
public class Tower implements Graph {
    float scale;//塔的大小
    boolean texFlag;//是否绘制纹理的标志位
    int texId;

    TowerPart tower1;//塔的第一部分
    TowerPart tower2;//塔中圆柱部分
    TowerPart tower3;//

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


    Tower(MySurfaceView mv, float scale, int nCol, int nRow){
        this.scale=scale;//大小赋值
        // 创建对象
        ArrayList<BNPosition> position1 = new ArrayList<>();
        position1.add(new BNPosition(76, 91));
		position1.add(new BNPosition(168, 102));
		position1.add(new BNPosition(-69, 107));
		position1.add(new BNPosition(183, 74));
		position1.add(new BNPosition(143, 205));
		position1.add(new BNPosition(12, 179));
		position1.add(new BNPosition(-1, 220));
		position1.add(new BNPosition(36, 197));
		position1.add(new BNPosition(6, 165));
		position1.add(new BNPosition(0, 233));
        tower1 = new TowerPart(mv,0.4f*scale, nCol, nRow, texId, position1); //穹顶的第一部分

        tower2 = new TowerPart(mv,0.35f*scale, nCol, nRow, texId, null); //穹顶的第二部分

        tower3 = new TowerPart(mv,0.4f*scale,nCol,nRow, texId, null); //穹顶的第二部分

    }
    public void drawSelf()
    {
        MatrixState.rotate(xAngle, 1, 0, 0);
        MatrixState.rotate(yAngle, 0, 1, 0);
        MatrixState.rotate(zAngle, 0, 0, 1);

        //塔的第一部分——穹顶
        MatrixState.pushMatrix();
        MatrixState.translate(0f, 3.0f*scale, 0f);
        tower1.drawSelf();//穹顶的第一部分
        MatrixState.popMatrix();

        //**************塔的第二部分——四根圆柱****************************************
        //四根圆柱
        MatrixState.pushMatrix();
        MatrixState.translate(0.62f*scale, 2.15f*scale, 0.62f*scale);
        tower2.drawSelf();//穹顶的第一部分
        MatrixState.popMatrix();

        MatrixState.pushMatrix();
        MatrixState.translate(-0.62f*scale, 2.15f*scale, 0.62f*scale);
        tower2.drawSelf();//穹顶的第一部分
        MatrixState.popMatrix();

        MatrixState.pushMatrix();
        MatrixState.translate(0.62f*scale, 2.15f*scale, -0.62f*scale);
        tower2.drawSelf();//穹顶的第一部分
        MatrixState.popMatrix();

        MatrixState.pushMatrix();
        MatrixState.translate(-0.62f*scale, 2.15f*scale, -0.62f*scale);
        tower2.drawSelf();//穹顶的第一部分
        MatrixState.popMatrix();
        //**************塔的第二部分****************************************

        //塔的第一部分
        MatrixState.pushMatrix();
        MatrixState.translate(0f, 0.7f*scale, 0f);
        tower3.drawSelf();
        MatrixState.popMatrix();

        MatrixState.pushMatrix();
        MatrixState.translate(0f, -1.4f*scale, 0f);
        tower3.drawSelf();
        MatrixState.popMatrix();

        MatrixState.pushMatrix();
        MatrixState.translate(0f, -3.55f*scale, 0f);
        tower3.drawSelf();
        MatrixState.popMatrix();
    }

}

