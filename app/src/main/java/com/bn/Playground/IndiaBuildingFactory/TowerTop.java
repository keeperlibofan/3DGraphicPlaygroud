package com.bn.Playground.IndiaBuildingFactory;

import com.bn.Playground.MatrixState;
import com.bn.Playground.MySurfaceView;

/**
 * 绘制塔顶的类
 */
public class TowerTop {
    float scale;//塔的大小

    TowerPart tower1;//塔的第一部分
    TowerPart tower2;//塔中圆柱部分
    TowerPart tower3;//


    int texId;
    float xAngle=0;//绕x轴旋转的角度
    float yAngle=0;//绕y轴旋转的角度
    float zAngle=0;//绕z轴旋转的角度

    TowerTop(MySurfaceView mv, float scale, int nCol , int nRow,int texId)
    {
        this.texId = texId;
        this.scale=scale;//茶壶大小赋值

        //创建对象
        tower1=new TowerPart(mv,0.4f*scale,20,40, texId, null);//穹顶的第一部分
        tower2=new TowerPart(mv,0.35f*scale,20,40, texId, null);//穹顶的第二部分

    }
    public void drawSelf()
    {
        MatrixState.rotate(xAngle, 1, 0, 0);
        MatrixState.rotate(yAngle, 0, 1, 0);
        MatrixState.rotate(zAngle, 0, 0, 1);


        //塔的第一部分
        MatrixState.pushMatrix();
        MatrixState.translate(0f, 3.0f*scale, 0f);
        tower1.drawSelf();//穹顶的第一部分
        MatrixState.popMatrix();

        //**************塔的第二部分****************************************
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
        //**************塔的第二部分***************************************		
    }

}

