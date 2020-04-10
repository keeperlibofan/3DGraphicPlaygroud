package com.bn.Playground.SoccerFacotry;

import com.bn.Playground.Graph;
import com.bn.Playground.MathUtil.ZQTEdgeUtil;
import com.bn.Playground.MatrixState;
import com.bn.Playground.MySurfaceView;

public class Soccer implements Graph {
    // 工具类UtilTools对象的引用
    UtilTools utilTools;
    // 球对象引用
    Ball ball;
    // 棍对象引用
    Stick stick;
    // 球和棍位置信息对象
    ResultData resultData;

    public Soccer(MySurfaceView mv) {
        utilTools = new UtilTools();
        // 初始化资源对象！
        resultData = utilTools.initVertexData(Constant.TRIANGLE_SCALE,
                Constant.TRIANGLE_AHALF, Constant.SPLIT_COUNT);
        // 初始化变换矩阵
        MatrixState.setInitStack();
        float[] colorValue = {1,0,0,1};	//创建颜色数组
        ball = new Ball(mv, Constant.BALL_R, colorValue);// 创建球对象
        colorValue = new float[]{0,1,0,1};	//创建颜色数组
        stick = new Stick(mv, Constant.LENGTH, Constant.R,
                Constant.ANGLE_SPAN, colorValue);// 创建圆管对象
    }

    public void drawSelf() {
        MatrixState.pushMatrix();
        MatrixState.translate(0, 0, -10f);
        MatrixState.rotate(yAngle, 0, 1, 0);
        MatrixState.rotate(zAngle, 0, 0, 1);
        // 根据顶点的个数绘制球体
        for (int i = 0; i < resultData.CAtomicPosition.length; i++) {
            MatrixState.pushMatrix();
            MatrixState.translate(resultData.CAtomicPosition[i][0],
                    resultData.CAtomicPosition[i][1], resultData.CAtomicPosition[i][2]);
            ball.drawSelf();
            MatrixState.popMatrix();
        }

        // 根据边顶点绘制出stick梗
        for (float[] ab : resultData.ChemicalBondPoints) {
            float[] result = ZQTEdgeUtil.calTranslateRotateScale(ab);

            MatrixState.pushMatrix();
            MatrixState.translate(result[0], result[1], result[2]);
            MatrixState.rotate(result[3], result[4], result[5],result[6]);
            MatrixState.scale(result[7], result[8], result[9]); // 基于当前缩放而得
            stick.drawSelf(); // draw一遍复制一遍
            MatrixState.popMatrix();
        }
        MatrixState.popMatrix();
    }

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
}
