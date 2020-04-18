package com.bn.Playground.MathUtil;

import java.util.ArrayList;

public class BezierUtil
{
    public static ArrayList<BNPosition> al=new ArrayList<BNPosition>();

    /**根据al绘制出所有的贝塞尔点*/
    public static ArrayList<BNPosition> getBezierData(float span)
    {
        ArrayList<BNPosition> result=new ArrayList<BNPosition>();

        int n=al.size()-1;

        if(n<1)
        {
            return result;
        }

        int steps=(int) (1.0f/span);
        long[] factorialNA=new long[n+1];

        for(int i=0;i<=n;i++)
        {
            factorialNA[i]= factorial(i);
        }

        for(int i=0;i<=steps;i++)
        {
            float t=i*span;
            if(t>1)
            {
                t=1;
            }
            float xf=0;
            float yf=0;

            float[] tka=new float[n+1];
            float[] otka=new float[n+1];
            for(int j=0;j<=n;j++)
            {
                tka[j]=(float) Math.pow(t, j);
                otka[j]=(float) Math.pow(1-t, j);
            }

            for(int k=0;k<=n;k++)
            {
                float xs=(factorialNA[n]/(factorialNA[k]*factorialNA[n-k]))*tka[k]*otka[n-k];
                xf=xf+al.get(k).x*xs;
                yf=yf+al.get(k).y*xs;
            }
            result.add(new BNPosition(xf,yf));
        }

        return result;
    }

    //求阶乘
    public  static long factorial(int n)
    {
        long result=1;
        if(n==0)
        {
            return 1;
        }

        for(int i=2;i<=n;i++)
        {
            result=result*i;
        }

        return result;
    }

    /**贝塞尔曲线上点的类*/
    public static class BNPosition
    {
        public int x;//曲线上点的x坐标
        public int y;//曲线上点的y坐标

        public BNPosition(float x,float y)
        {
            this.x=(int) x;//x坐标赋值
            this.y=(int) y;//y坐标赋值
        }
    }
}
