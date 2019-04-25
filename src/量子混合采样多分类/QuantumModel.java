package 量子混合采样多分类;

import java.util.*;

import weka.core.EuclideanDistance;
import weka.core.Instance;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Normalize;

public class QuantumModel {
	public Individual[] population;	//种群个体
	public int iter;
	public int popSize;
	public Individual gBestIndividual;	//最优的染色体
	
	/*
	 * TODO:量子优化算法的迭代求解函数
	 * RETURN：返回最优的个体
	 * */
	public void run() {
		for(int i = 0; i < iter; ++i) {
			
		}
	}
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		
		
	}
}
