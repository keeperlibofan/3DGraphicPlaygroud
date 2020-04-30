package com.bn.Playground.ObjLoader;

import android.content.res.Resources;
import android.util.Log;
import com.bn.Playground.MySurfaceView;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class LoadUtil {
    public static LoadedObjectVertexOnly loadFromFile(String fname, Resources r, MySurfaceView mv) {
        LoadedObjectVertexOnly lo = null;
        ArrayList<Float> alv = new ArrayList<>();
        ArrayList<Float>  alvResult = new ArrayList<>();
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
                    for (int i = 1; i <= 3; i++) {
                        int index = Integer.parseInt(tempsa[i].split("/")[0]) - 1;
                        alvResult.add(alv.get(3*index));
                        alvResult.add(alv.get(3*index+1));
                        alvResult.add(alv.get(3*index+2));
                    }
                }
            }
            int size = alvResult.size();
            float[] vXYZ = new float[size];
            for (int i = 0; i < size; i++) {
                vXYZ[i] = alvResult.get(i);
            }
            lo = new LoadedObjectVertexOnly(mv, vXYZ);
            System.out.println("vert parse finish");

        } catch (Exception e) {
            Log.d("load error", "load error");
            e.printStackTrace();
        }
        return lo;
    }
}
