package com.bn.Playground.IndiaFactory;

/**
 * 绘制穹顶的类
 */
import com.bn.Playground.Graph;
import com.bn.Playground.MathUtil.BezierUtil.BNPosition;
import com.bn.Playground.MatrixState;
import com.bn.Playground.MySurfaceView;
import java.util.ArrayList;

public class Top implements Graph {
    float scale;//穹顶的大小

    TopPart top1;//穹顶的第一部分
    TopPart top2;//穹顶的第二部分
    TopPart top3;//穹顶的第三部分
    TopPart top4;//穹顶的第四部分

    private int texId;

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

    public Top(MySurfaceView mv, float scale, int nCol , int nRow)
    {
        this.scale=scale;//茶壶大小赋值

        ArrayList<BNPosition> position1 = new ArrayList<BNPosition>();
        position1.add(new BNPosition(-1, 171));//控制点坐标数据1
        position1.add(new BNPosition(14, 191));//控制点坐标数据2
        position1.add(new BNPosition(17, 183));//控制点坐标数据3
        position1.add(new BNPosition(5, 154));	//控制点坐标数据4
        position1.add(new BNPosition(31, 274));//控制点坐标数据5
        position1.add(new BNPosition(32, 243));	//控制点坐标数据6
        position1.add(new BNPosition(30, 230));	//控制点坐标数据7
        position1.add(new BNPosition(0, 253));//控制点坐标数据8
        ArrayList<BNPosition> position2 = new ArrayList<BNPosition>();

        ArrayList<BNPosition> position3 = new ArrayList<BNPosition>();

        ArrayList<BNPosition> position4 = new ArrayList<BNPosition>();


        //创建对象
        top1=new TopPart(mv,0.4f*scale,nCol,nRow, texId, position1);//穹顶的第一部分
        top2=new TopPart(mv,0.4f*scale,nCol,nRow, texId, position2);//穹顶的第二部分
        top3=new TopPart(mv,0.8f*scale,nCol,nRow, texId, position3);//穹顶的第三部分
        top4=new TopPart(mv,0.8f*scale,nCol,nRow, texId, position4);//穹顶的第四部分

    }
    public void drawSelf()
    {

        MatrixState.rotate(xAngle, 1, 0, 0);
        MatrixState.rotate(yAngle, 0, 1, 0);
        MatrixState.rotate(zAngle, 0, 0, 1);


        //穹顶的第一部分
        MatrixState.pushMatrix();
        MatrixState.translate(0f, 4.0f*scale, 0f);
        top1.drawSelf();//穹顶的第一部分
        MatrixState.popMatrix();
        //穹顶的第二部分
        MatrixState.pushMatrix();
        MatrixState.translate(0f, 3.7f*scale, 0f);
        top2.drawSelf();//穹顶的第二部分
        MatrixState.popMatrix();
        //穹顶的第三部分
        MatrixState.pushMatrix();
        MatrixState.translate(0f, 0f*scale, 0f);
        top3.drawSelf();
        MatrixState.popMatrix();
        //穹顶的第四部分
        MatrixState.pushMatrix();
        MatrixState.translate(0f, -1.9f*scale, 0f);
        top4.drawSelf();
        MatrixState.popMatrix();
    }

}

