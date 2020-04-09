package com.bn.Playground.Ragular20Factory;

import android.opengl.GLES30;

import com.bn.Playground.Graph;
import com.bn.Playground.MatrixState;
import com.bn.Playground.MySurfaceView;
import com.bn.Playground.ShaderUtil;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import static com.bn.Playground.ShaderUtil.createProgram;

public class Regular20L implements Graph {
    private int vCount=0;
    private int mProgram;//自定义渲染管线着色器程序id
    private int muMVPMatrixHandle;//总变换矩阵引用
    private int maPositionHandle; //顶点位置属性引用
    private int maColorHandle; //顶点颜色属性引用
    private FloatBuffer mVertexBuffer;//顶点坐标数据缓冲
    private FloatBuffer mColorBuffer;	//顶点颜色数据缓冲

    private String mVertexShader;//顶点着色器
    private String mFragmentShader;//片元着色器


    public Regular20L(MySurfaceView mv, float scale, float aHalf, int n)
    {
        //调用初始化顶点数据的initVertexData方法
        initVertexData(scale,aHalf,n);
        //调用初始化着色器的intShader方法
        initShader(mv);
    }

    public void initVertexData(float scale, float aHalf, int n)
    {//自定义的初始化顶点数据的方法
        aHalf*=scale;		//黄金长方形长边的一半
        float bHalf=aHalf*0.618034f;	//黄金长方形短边的一半
        float r = (float) Math.sqrt(aHalf*aHalf+bHalf*bHalf);//几何球的半径
        vCount=3*20*n*n;//顶点个数 n*n 为最终将分成几份

        ArrayList<Float> alVertix20 = new ArrayList<Float>();//正二十面体的顶点列表
        ArrayList<Integer> alFaceIndex20 = new ArrayList<Integer>();//用于卷绕构成正二十面体各个三角形的顶点编号列表

        initAlVertix20(alVertix20,aHalf,bHalf);//初始化正二十面体的顶点坐标数据

        initAlFaceIndex20(alFaceIndex20);//初始化用于卷绕构成正二十面体各个三角形的顶点编号列表
        //构成正二十面体的各个三角形顶点的坐标数据数组
        float[] vertices20 = VectorUtil.cullVertex(alVertix20, alFaceIndex20);

        ArrayList<Float> alVertix = new ArrayList<Float>();//几何球原始顶点列表
        ArrayList<Integer> alFaceIndex = new ArrayList<Integer>();//构成几何球的各三角形顶点编号列表
        int vnCount = 0;//顶点计数器
        for(int k = 0;k < vertices20.length; k += 9) // 对正二十面体中的每个三角形循环, 卷绕排序
        {
            // 当前20面体中的一个三角形的三个点
            float [] v1 = new float[]{vertices20[k], vertices20[k+1], vertices20[k+2]};	//当前三角形3个
            float [] v2 = new float[]{vertices20[k+3], vertices20[k+4], vertices20[k+5]};//顶点的坐标
            float [] v3 = new float[]{vertices20[k+6], vertices20[k+7], vertices20[k+8]};

            for(int i=0;i<=n;i++) // 如果 n == 2，那么就会有三个点; n == 1, 就会有两个点
            {//根据切分的份数求出几何球原始顶点的坐标
                float[] viStart = VectorUtil.devideBall(r, v1, v2, n, i);//对圆弧进行切分
                float[] viEnd = VectorUtil.devideBall(r, v1, v3, n, i);//对圆弧进行切分
                for(int j=0; j<=i; j++)
                {
                    float[] vi = VectorUtil.devideBall(r, viStart, viEnd, i, j); // 对圆弧进行切分
                    alVertix.add(vi[0]); alVertix.add(vi[1]); alVertix.add(vi[2]); // 将坐标存入原始顶点列表
                }
            }

            // 一个正20面体的一个面运行一次
            for(int i=0; i<n; i++)
            {//循环生成构成几个球各个三角形的顶点编号列表
                if(i == 0){ //若是第0行，顶点编号012
                    alFaceIndex.add(vnCount); alFaceIndex.add(vnCount+1);alFaceIndex.add(vnCount+2);
                    vnCount+=1;//顶点计数器加1
                    if(i==n-1){ //如果是正二十面体三角形的最后一次循环，将下一拨的顶点个数也加上
                        vnCount+=2;
                    }
                    continue;
                }
                int iStart=vnCount;//第i行开始的编号(这里的行指的是平面展开图中的行)
                int viCount=i+1;//第i行顶点数
                int iEnd=iStart+viCount-1;//第i行结束顶点编号

                int iStartNext=iStart+viCount;//第i+1行开始的顶点编号
                int viCountNext=viCount+1;//第i+1行顶点数
                int iEndNext=iStartNext+viCountNext-1;//第i+1行结束的顶点编号

                for(int j=0;j<viCount-1;j++)
                {//前面的四边形
                    int index0=iStart+j;//四边形4个顶点的编号
                    int index1=index0+1;
                    int index2=iStartNext+j;
                    int index3=index2+1;
                    //将四边形4个顶点卷绕成两个三角形
                    alFaceIndex.add(index0); alFaceIndex.add(index2);alFaceIndex.add(index3);
                    alFaceIndex.add(index0); alFaceIndex.add(index3);alFaceIndex.add(index1);
                }
                //最后一个三角形3个顶点的编号
                alFaceIndex.add(iEnd); alFaceIndex.add(iEndNext-1);alFaceIndex.add(iEndNext); //最后一个三角形
                vnCount+=viCount;//第i行前所有顶点数的和
                if(i==n-1){ //如果是正二十面体三角形的最后一次循环，将下一拨的顶点个数也加上
                    vnCount+=viCountNext;
                }
            }
        }

        //按照生成的顶点编号序列填充顶点坐标数据数组
        float[] vertices=VectorUtil.cullVertex(alVertix, alFaceIndex);

        //顶点坐标数据初始化
        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length*4);//创建顶点坐标数据缓冲
        vbb.order(ByteOrder.nativeOrder());//设置字节顺序为本地操作系统顺序
        mVertexBuffer = vbb.asFloatBuffer();//转换为float型缓冲
        mVertexBuffer.put(vertices);//向缓冲区中放入顶点坐标数据
        mVertexBuffer.position(0);//设置缓冲区起始位置

        float[] colors=new float[vCount*4];//顶点颜色数组
        int Count=0;
        for(int i=0;i<vCount;i++)
        {
            colors[Count++]=1;	//r
            colors[Count++]=1;	//g
            colors[Count++]=1;	//b
            colors[Count++]=1;	//a

        }
        //创建顶点着色数据缓冲
        ByteBuffer cbb = ByteBuffer.allocateDirect(colors.length*4);
        cbb.order(ByteOrder.nativeOrder());//设置字节顺序为本地操作系统顺序
        mColorBuffer = cbb.asFloatBuffer();//转换为Float型缓冲
        mColorBuffer.put(colors);//向缓冲区中放入顶点着色数据
        mColorBuffer.position(0);//设置缓冲区起始位置
    }

    //初始化着色器
    public void initShader(MySurfaceView mv)
    {
        //加载顶点着色器的脚本内容
        mVertexShader = ShaderUtil.loadFromAssetsFile("color.vert", mv.getResources());
        //加载片元着色器的脚本内容
        mFragmentShader = ShaderUtil.loadFromAssetsFile("color.frag", mv.getResources());
        //基于顶点着色器与片元着色器创建程序
        mProgram = createProgram(mVertexShader, mFragmentShader);
        //获取程序中顶点位置属性引用id
        maPositionHandle = GLES30.glGetAttribLocation(mProgram, "aPosition");
        //获取程序中顶点颜色属性引用id
        maColorHandle= GLES30.glGetAttribLocation(mProgram, "aColor");
        //获取程序中总变换矩阵引用id
        muMVPMatrixHandle = GLES30.glGetUniformLocation(mProgram, "uMVPMatrix");
    }

    private void initAlVertix20(ArrayList<Float> alVertix20, float aHalf, float bHalf){

        alVertix20.add(0f); alVertix20.add(aHalf); alVertix20.add(-bHalf);//对应图8-17的1号点

        alVertix20.add(0f); alVertix20.add(aHalf); alVertix20.add(bHalf);//对应图8-17的2号点
        alVertix20.add(aHalf); alVertix20.add(bHalf); alVertix20.add(0f);//对应图8-17的3号点
        alVertix20.add(bHalf); alVertix20.add(0f); alVertix20.add(-aHalf);//对应图8-17的4号点
        alVertix20.add(-bHalf); alVertix20.add(0f); alVertix20.add(-aHalf);//对应图8-17的5号点
        alVertix20.add(-aHalf); alVertix20.add(bHalf); alVertix20.add(0f);//对应图8-17的6号点

        alVertix20.add(-bHalf); alVertix20.add(0f); alVertix20.add(aHalf);//对应图8-17的7号点
        alVertix20.add(bHalf); alVertix20.add(0f); alVertix20.add(aHalf);//对应图8-17的8号点
        alVertix20.add(aHalf); alVertix20.add(-bHalf); alVertix20.add(0f);//对应图8-17的9号点
        alVertix20.add(0f); alVertix20.add(-aHalf); alVertix20.add(-bHalf);//对应图8-17的10号点
        alVertix20.add(-aHalf); alVertix20.add(-bHalf); alVertix20.add(0f);//对应图8-17的11号点

        alVertix20.add(0f); alVertix20.add(-aHalf); alVertix20.add(bHalf);//对应图8-17的12号点

    }

    private void initAlFaceIndex20(ArrayList<Integer> alFaceIndex20){ //初始化正二十面体的顶点索引数据
        //第一行5个三角形的各个顶点的坐标编号
        alFaceIndex20.add(0); alFaceIndex20.add(1); alFaceIndex20.add(2);
        alFaceIndex20.add(0); alFaceIndex20.add(2); alFaceIndex20.add(3);
        alFaceIndex20.add(0); alFaceIndex20.add(3); alFaceIndex20.add(4);
        alFaceIndex20.add(0); alFaceIndex20.add(4); alFaceIndex20.add(5);
        alFaceIndex20.add(0); alFaceIndex20.add(5); alFaceIndex20.add(1);
        //第二行10个三角形的各个顶点的坐标编号
        alFaceIndex20.add(1); alFaceIndex20.add(6); alFaceIndex20.add(7);
        alFaceIndex20.add(1); alFaceIndex20.add(7); alFaceIndex20.add(2);
        alFaceIndex20.add(2); alFaceIndex20.add(7); alFaceIndex20.add(8);
        alFaceIndex20.add(2); alFaceIndex20.add(8); alFaceIndex20.add(3);
        alFaceIndex20.add(3); alFaceIndex20.add(8); alFaceIndex20.add(9);
        alFaceIndex20.add(3); alFaceIndex20.add(9); alFaceIndex20.add(4);
        alFaceIndex20.add(4); alFaceIndex20.add(9); alFaceIndex20.add(10);
        alFaceIndex20.add(4); alFaceIndex20.add(10); alFaceIndex20.add(5);
        alFaceIndex20.add(5); alFaceIndex20.add(10); alFaceIndex20.add(6);
        alFaceIndex20.add(5); alFaceIndex20.add(6); alFaceIndex20.add(1);
        //第三行5个三角形的各个顶点的坐标编号
        alFaceIndex20.add(6); alFaceIndex20.add(11); alFaceIndex20.add(7);
        alFaceIndex20.add(7); alFaceIndex20.add(11); alFaceIndex20.add(8);
        alFaceIndex20.add(8); alFaceIndex20.add(11); alFaceIndex20.add(9);
        alFaceIndex20.add(9); alFaceIndex20.add(11); alFaceIndex20.add(10);
        alFaceIndex20.add(10); alFaceIndex20.add(11); alFaceIndex20.add(6);
    }


    public void drawSelf() {
        MatrixState.rotate(xAngle, 1, 0, 0);
        MatrixState.rotate(yAngle, 0, 1, 0);
        MatrixState.rotate(zAngle, 0, 0, 1);

        //制定使用某套shader程序
        GLES30.glUseProgram(mProgram);
        //将最终变换矩阵传入shader程序
        GLES30.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0);

        //传送顶点位置数据
        GLES30.glVertexAttribPointer
                (
                        maPositionHandle,
                        3,
                        GLES30.GL_FLOAT,
                        false,
                        3*4,
                        mVertexBuffer
                );
        //传送顶点颜色数据
        GLES30.glVertexAttribPointer
                (
                        maColorHandle,
                        4,
                        GLES30.GL_FLOAT,
                        false,
                        4*4,
                        mColorBuffer
                );

        //启用顶点位置数据
        GLES30.glEnableVertexAttribArray(maPositionHandle);
        //启用顶点颜色数据
        GLES30.glEnableVertexAttribArray(maColorHandle);

        //绘制线条的粗细
        GLES30.glLineWidth(2);
        //绘制纹理矩形
        GLES30.glDrawArrays(GLES30.GL_LINE_STRIP, 0, vCount); // 线段要三个点为一个三角形画完整
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
