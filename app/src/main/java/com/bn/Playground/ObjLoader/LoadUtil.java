package com.bn.Playground.ObjLoader;

import android.content.res.Resources;
import android.util.Log;
import com.bn.Playground.MathUtil.VectorUtil;
import com.bn.Playground.MySurfaceView;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class LoadUtil {
    public static LoadedObjectVertex loadFromFile(String fname, Resources r, MySurfaceView mv) {
        LoadedObjectVertex lo = null;
        ArrayList<Float> alv = new ArrayList<>();
        ArrayList<Float> alvResult = new ArrayList<>();
        ArrayList<Float> alnResult = new ArrayList<>();
        HashMap<Integer, HashSet<Normal>> hmn = new HashMap<>();
        ArrayList<Integer> alFaceIndex=new ArrayList<Integer>();

        try {
            InputStream in = r.getAssets().open(fname);
            InputStreamReader isr = new InputStreamReader(in);

            BufferedReader br = new BufferedReader(isr);
            String temps;
            while ((temps = br.readLine()) != null) {
                String[] tempsa = temps.split("[ ]+");
                if (tempsa[0].trim().equals("v")) {
                    alv.add(Float.parseFloat(tempsa[1]));
                    alv.add(Float.parseFloat(tempsa[2]));
                    alv.add(Float.parseFloat(tempsa[3]));
                } else if (tempsa[0].trim().equals("f")) {
                    /**
                     *若为三角形面行则根据 组成面的顶点的索引从原始顶点坐标列表中
                     *提取相应的顶点坐标值添加到结果顶点坐标列表中，同时根据三个
                     *顶点的坐标计算出法向量并添加到结果法向量列表中
                     */

                    //提取三角形第一个顶点的坐标
                    int[] index = new int[3];
                    index[0] = Integer.parseInt(tempsa[1].split("/")[0])-1;//得到顶点编号
                    //将三角形第1个顶点的x、y、z坐标取出
                    float x0=alv.get(3*index[0]);
                    float y0=alv.get(3*index[0]+1);
                    float z0=alv.get(3*index[0]+2);
                    alvResult.add(x0);
                    alvResult.add(y0);
                    alvResult.add(z0);

                    //提取三角形第二个顶点的坐标
                    index[1]=Integer.parseInt(tempsa[2].split("/")[0])-1;
                    float x1=alv.get(3*index[1]);
                    float y1=alv.get(3*index[1]+1);
                    float z1=alv.get(3*index[1]+2);
                    alvResult.add(x1);
                    alvResult.add(y1);
                    alvResult.add(z1);

                    //提取三角形第三个顶点的坐标
                    index[2]=Integer.parseInt(tempsa[3].split("/")[0])-1;
                    float x2=alv.get(3*index[2]);
                    float y2=alv.get(3*index[2]+1);
                    float z2=alv.get(3*index[2]+2);
                    alvResult.add(x2);
                    alvResult.add(y2);
                    alvResult.add(z2);

                    //通过三角形面两个边向量0-1，0-2求叉积得到此面的法向量
                    //求三角形中第一个点到第二个点的向量
                    alFaceIndex.add(index[0]);
                    alFaceIndex.add(index[1]);
                    alFaceIndex.add(index[2]);

                    float vxa=x1-x0;
                    float vya=y1-y0;
                    float vza=z1-z0;
                    float[] v1 = new float[]{vxa, vya, vza};
                    //求三角形中第一个点到第三个点的向量
                    float vxb=x2-x0;
                    float vyb=y2-y0;
                    float vzb=z2-z0;
                    float[] v2 = new float[]{vxb, vyb, vzb};
                    //通过求两个向量的叉积计算出此三角形面的法向量
                    float[] vNormal = VectorUtil.crossTwoVectors(v1, v2);
                    //将计算出的法向量添加到结果法向量列表中
                    for (int tempIndex : index) {
                        HashSet<Normal> hsn = hmn.get(tempIndex);
                        if (hsn == null) {
                             hsn = new HashSet<>();
                        }
                        hsn.add(new Normal(vNormal[0], vNormal[1], vNormal[2]));
                        hmn.put(tempIndex, hsn);  // 每个点的所有的邻面的法向量都需要加上
                    }
                }
            }
            int size = alvResult.size();
            float[] vXYZ = new float[size];
            for (int i = 0; i < size; i++) {
                vXYZ[i] = alvResult.get(i);
            }

            float[] nXYZ=new float[alFaceIndex.size()*3];
            int c=0;
            for(Integer i:alFaceIndex)
            {
                HashSet<Normal> hsn=hmn.get(i);
                float[] tn=Normal.getAverage(hsn);
                nXYZ[c++]=tn[0];
                nXYZ[c++]=tn[1];
                nXYZ[c++]=tn[2];
            }

            lo = new LoadedObjectVertex(mv, vXYZ, nXYZ);
            System.out.println("vert parse finish");
        } catch (Exception e) {
            Log.d("load error", "load error");
            e.printStackTrace();
        }
        return lo;
    }
}
