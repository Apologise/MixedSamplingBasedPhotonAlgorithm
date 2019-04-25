package 量子混合采样多分类;

import java.util.List;

import weka.core.Instance;
import weka.core.Instances;

public class Individual {
	public List<Instance> handleInstances;	//处理后的样本集合，用作计算该个体的适应度
	public double fitness; //个体的适应度
	public int[] flag;
	public InstancesSet instancesSet;
	public Phase[] phase;
	public Individual() {
		flag = new int[instancesSet.majorityInstances.size()];
		phase = new Phase[instancesSet.majorityInstances.size()];
		fitness = 0d;
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
