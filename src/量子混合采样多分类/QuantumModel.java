package 量子混合采样多分类;

import java.util.*;

import weka.core.EuclideanDistance;
import weka.core.Instance;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Normalize;

public class QuantumModel {
	public Individual[] population;	//种群个体
	public final int iter;
	public final int popSize;
	public Individual gBestIndividual;	//最优的染色体
	public final Setting setting;
	public final InstancesSet instancesSet;
	
	public QuantumModel(Setting setting, InstancesSet instancesSet) {
		this.setting = setting;
		this.instancesSet = instancesSet;
		this.iter = setting.iteration;
		this.popSize = setting.popSize;
	}
	/*
	 * TODO:量子优化算法的迭代求解函数
	 * RETURN：返回最优的个体
	 * */
	public void run() {
		//1. 初始化种群
		initializePopulation();
		for(int i = 0; i < iter; ++i) {
			
		}
	}
	
	/*
	 * TODO:初始化种群
	 * RETURN：修改population数组
	 * */
	public void initializePopulation() {
		population = new Individual[popSize];
		for(int i = 0; i < popSize; ++i) {
			population[i].initializeIndividual();
		}
	}
	
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		
		
	}
}
