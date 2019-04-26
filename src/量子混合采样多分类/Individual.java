package 量子混合采样多分类;

import java.util.ArrayList;
import java.util.List;

import weka.core.Instance;
import weka.core.Instances;

public class Individual {
	public List<Instance> handledInstances;	//处理后的样本集合，用作计算该个体的适应度
	public int[] flag;
	public Phase[] phase;
	public double fitness; //个体的适应度
	public Setting setting;
	public InstancesSet instancesSet;
	public Individual(Setting setting, InstancesSet instancesSet) {
		this.setting = setting;
		this.instancesSet = instancesSet;
		fitness = 0d;
	}
	
	/*
	 * TODO:初始化个体，对个体中的引用对象进行实例化，防止出现null类型
	 * */
	public void initializeIndividual() {
		handledInstances = new ArrayList<Instance>();
		flag = new int[instancesSet.majorityInstances.size()];
		phase = new Phase[instancesSet.majorityInstances.size()];
	}
	/*
	 * TODO: 计算个体的适应度
	 * RETURN: 修改成员变量fitness
	 * */
	public void calFitness() {
		
	}
	
	/*
	 * TODO:对个体进行混合采样
	 * RETURN：返回一个经过处理后的样本集合
	 * */
	public void mixedSampling() {
		//先清空handledInstances集合
		handledInstances.clear();
		underSampling();
		overSampling();
	}
	

	/*
	 * TODO:根据flag的结果，对多数类进行欠采样
	 * RETURN: 将欠采样后的样本加入到handledInstances中
	 * */
	public void underSampling() {
		List<Instance> majorityInstances = instancesSet.majorityInstances;
		for(int i = 0; i < majorityInstances.size(); ++i) {
			if(flag[i] == 1) {
				handledInstances.add(majorityInstances.get(i));
			}
		}
	}
	
	/*
	 * TODO：对样本进行过采样
	 * RETURN: 将生成的样本加入到handledInstances中
	 * */
	public void overSampling() {
		
	}
	
	/*
	 * TODO: 对个体进行测量，根据Phase的相位得到该时刻（迭代次数）的观察值
	 * RETUN: 修改了flag数组
	 * */
	public void watchByPhase() {
		
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
