package com.bn.Playground.MathUtil;//包声明

import java.util.ArrayList;

/**计算三角形法向量的工具类*/
public class VectorUtil {
	/**缩放坐标*/
	public static float[] scaleVector(double k, float[] vec) {
		float[] result = new float[3];
		for (int i = 0; i < vec.length; i++) {
			result[i] = vec[i] * (float)k;
		}
		return result;
	}

	/**向量规格化的方法*/
	public static float[] normalizeVector(float [] vec){
		float mod=module(vec);
		return new float[]{vec[0]/mod, vec[1]/mod, vec[2]/mod};//返回规格化后的向量
	}
	/**求向量的模的方法*/
	public static float module(float [] vec){
		float sumOfSquares = 0;
		for (float scalar: vec) {
			sumOfSquares += scalar*scalar;
		}
		return (float) Math.sqrt(sumOfSquares);
	}
	//两个向量叉乘的方法
	public static float[] crossTwoVectors(float[] a, float[] b)
	{
		float x=a[1]*b[2]-a[2]*b[1];
		float y=a[2]*b[0]-a[0]*b[2];
		float z=a[0]*b[1]-a[1]*b[0];
		return new float[]{x, y, z};//返回叉乘结果
	}
	
	public static float dotTwoVectors(float[] a, float[] b)
	{//两个向量点积的方法
		return a[0]*b[0]+a[1]*b[1]+a[2]*b[2];//返回点积结果
	}

	//根据顶点编号生成卷绕后顶点纹理坐标数组的方法
	public static float[] cullTexCoor(
			ArrayList<Float> alST,
			ArrayList<Integer> alTexIndex
			)
	{
		float[] textures=new float[alTexIndex.size()*2];//结果纹理坐标数组
		
		int stCount=0;//纹理坐标计数器
		for(int i:alTexIndex){//对顶点编号列表进行循环
			textures[stCount++]=alST.get(2*i);//将当前编号顶点的S坐标值存入最终数组
			textures[stCount++]=alST.get(2*i+1);//将当前编号顶点的T坐标值存入最终数组
		}
		return textures;//返回结果纹理坐标数组
	}
	//根据顶点编号生成卷绕后顶点坐标数组的方法
	public static float[] cullVertex(
			ArrayList<Float> alv,
			ArrayList<Integer> alFaceIndex
			)
	{
		float[] vertices=new float[alFaceIndex.size()*3];//存放卷绕后顶点坐标值的数组
		
		int vCount=0;//顶点计数器
		for(int i:alFaceIndex){//对顶点编号列表进行循环
			vertices[vCount++]=alv.get(3*i);//将当前编号顶点的X坐标值存入最终数组
			vertices[vCount++]=alv.get(3*i+1);//将当前编号顶点的Y坐标值存入最终数组
			vertices[vCount++]=alv.get(3*i+2);//将当前编号顶点的Z坐标值存入最终数组
		}
		return vertices;//返回结果顶点坐标数组
	}

	/**
	 * 计算圆弧的n等分点坐标的方法，r为半径，start为从球心到圆弧起始点的向量，
	 * end为从球心到圆弧终点的向量，n为切分的总份数，i为所求点对应的份数编号
	 * @param r 球的半径
	 * @param start 指向圆弧起点的向量
	 * @param end 指向圆弧终点的向量
	 * @param n 圆弧分的份数
	 * @param i 求第i份在圆弧上的坐标（i为0和n时分别代表起点和终点坐标）
	 **/
	public static float[] devideBall(
			float r, //球的半径
			float[] start, //指向圆弧起点的向量
			float[] end, //指向圆弧终点的向量
			int n, //圆弧分的份数
			int i //求第i份在圆弧上的坐标（i为0和n时分别代表起点和终点坐标）
			)
	{
		/**
		 * 先求出所求向量的规格化向量，再乘以半径r即可
		 * s0*x+s1*y+s2*z=cos(angle1)//根据所求向量和起点向量夹角为angle1---1式
		 * e0*x+e1*y+e2*z=cos(angle2)//根据所求向量和终点向量夹角为angle2---2式
		 * x*x+y*y+z*z=1//所球向量的规格化向量模为1---3式
		 * x*n0+y*n1+z*n2=0//所球向量与法向量垂直---4式
		 * 算法为：将1、2两式用换元法得出x=a1+b1*z，y=a2+b2*z的形式，
		 * 将其代入4式求出z，再求出x、y，最后将向量(x,y,z)乘以r即为所求坐标。
		 * 1式和2式是将3式代入得到的，因此已经用上了。
		 * 由于叉乘的结果做了分母，因此起点、终点、球心三点不能共线
		 * 注意结果是将劣弧等分
		 */
		//先将指向起点和终点的向量规格化
		float[] s= VectorUtil.normalizeVector(start);//先将指向起点和终点的向量规格化
		float[] e= VectorUtil.normalizeVector(end);
		if(n==0){	//如果n为零，返回起点坐标
			return new float[]{s[0]*r, s[1]*r, s[2]*r};
		} else if (n == i) { // 如果n份的第(i+1)个点直接返回终点
			return new float[]{e[0]*r, e[1]*r, e[2]*r};
		}
		//求两个向量的夹角
		double angrad = Math.acos(VectorUtil.dotTwoVectors(s, e));//起点终点向量夹角 acos就是arccos
		double angrad1 = angrad*i/n;//所求向量和起点向量的夹角
		double angrad2 = angrad-angrad1;//所求向量和终点向量的夹角
		
		float[] normal= VectorUtil.crossTwoVectors(s, e);//normal法向量, 求与s、e向量正交的向量
	
		double matrix[][]={//用doolittle分解算法解n元一次线性方程组所需的系数矩阵
				{s[0],s[1],s[2],Math.cos(angrad1)}, // 夹角cos值
				{e[0],e[1],e[2],Math.cos(angrad2)}, // 夹角cos值
				{normal[0],normal[1],normal[2],0}   // 正交结果0
		};
		double result[] = MyMathUtil.doolittle(matrix); //解n元一次线性方程组
		//求规格化向量xyz的值
		float x=(float) result[0];//得到从球心到所求点向量的规格化版本
		float y=(float) result[1];
		float z=(float) result[2];
		
		return new float[]{x*r, y*r, z*r};//得到所求点的坐标并返回
	}

	/**计算线段的n等分点坐标的方法，start为线段起点坐标，end为线段终点坐标
	n为切分的总份数，i为所求点对应的份数编号*/
	public static float[] devideLine(
			float[] start, //线段起点坐标
			float[] end, //线段终点坐标
			int n, //线段分的份数
			int i //求第i份在线段上的坐标（i为0和n时分别代表起点和终点坐标）
			)
	{
		if(n==0){//如果n为零，返回起点坐标
			return start;
		}
		//求起点到终点的向量
		float[] ab=new float[]{end[0]-start[0], end[1]-start[1], end[2]-start[2]};
		
		float vecRatio=i/(float)n;//求向量比例
		//求起点到所求点的向量
		float[] ac=new float[]{ab[0]*vecRatio, ab[1]*vecRatio, ab[2]*vecRatio};
		//得到所求点坐标
		float x=start[0]+ac[0];
		float y=start[1]+ac[1];
		float z=start[2]+ac[2];
		//返回线段所求点坐标
		return new float[]{x, y, z};
	}

	/**求多个向量的向量相减*/
	public static float[] subtract(float[] startVec, float[]... vecs) {
		float[] result = startVec.clone();
		for (float[] vec: vecs) {
			for (int i = 0; i < startVec.length; i++) {
				result[i] -= vec[i];
			}
		}
		return result;
	}

	/**求多个向量的向量和*/
	public static float[] add(float[] baseVec, float[]... vecs) {
		float[] result = baseVec.clone();
		for (float[] vec: vecs) {
			for (int i = 0; i < baseVec.length; i++) {
				result[i] += vec[i];
			}
		}
		return result;
	}

	/**求两点的距离*/
	public static float distanceTwoVector(float[] vec1, float[] vec2) {
		float result = 0;
		for (int i = 0; i < vec1.length; i++) {
			result += Math.pow(vec1[i] - vec2[i], 2);
		}
		return (float) Math.sqrt(result);
	}

	/**求两个向量的夹角*/
	public static float angle(float[] vec1,float[] vec2){
		//先求点积
		float dp=dotTwoVectors(vec1,vec2);
		//再求两个向量的模
		float m1=module(vec1);
		float m2=module(vec2);

		float acos=dp/(m1*m2);

		//为了避免计算误差带来的问题
		if(acos>1)	{
			acos=1;
		}
		else if(acos<-1){
			acos=-1;
		}
		return (float)Math.acos(acos);
	}

	/**计算圆锥面指定棱顶点法向量的方法*/
	public static float[] calConeNormal
			(
			 float x0,float y0,float z0,//A，中心点(底面圆的圆心)
			 float x1,float y1,float z1,//B，底面圆上的某一点
			 float x2,float y2,float z2 //C，圆锥中心最高点
			)
	{
		float[] a={x1-x0, y1-y0, z1-z0};//向量AB
		float[] b={x2-x0, y2-y0, z2-z0};//向量AC
		float[] c={x2-x1, y2-y1, z2-z1};//向量BC
		float[] k=crossTwoVectors(a,b);//先求平面ABC的法向量k

		float[] d=crossTwoVectors(c,k);//将c和k做叉乘，得出所求向量d
		return normalizeVector(d);//返回规格化后的法向量
	}
}
