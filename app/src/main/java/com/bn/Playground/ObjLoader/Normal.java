package com.bn.Playground.ObjLoader;

import com.bn.Playground.MathUtil.VectorUtil;
import java.util.Set;

/**
 * 平均法向量对象
 */
public class Normal
{
    public static final float DIFF=0.0000001f;
    float nx;
    float ny;
    float nz;

    public Normal(float nx,float ny,float nz)
    {
        this.nx=nx;
        this.ny=ny;
        this.nz=nz;
    }

    @Override
    public boolean equals(Object o)
    {
        if(o instanceof  Normal)
        {
            Normal tn=(Normal)o;
            if(Math.abs(nx-tn.nx)<DIFF&&
                    Math.abs(ny-tn.ny)<DIFF&&
                    Math.abs(ny-tn.ny)<DIFF
            )
            {
                return true;
            }
            else
            {
                return false;
            }
        }
        else
        {
            return false;
        }
    }

    @Override
    public int hashCode()
    {
        return 1;
    }

    /**
     * 将set中的法向量平均成一个法向量,
     * 每个法向量分量都是一个规格化向量，最终输出的也是规格化向量
     */
    public static float[] getAverage(Set<Normal> sn)
    {
        float[] result=new float[3];
        for(Normal n:sn)
        {
            result[0]+=n.nx;
            result[1]+=n.ny;
            result[2]+=n.nz;
        }
        return VectorUtil.normalizeVector(result);
    }
}

