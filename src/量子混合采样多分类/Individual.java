package ���ӻ�ϲ��������;

import java.util.List;

import weka.core.Instance;
import weka.core.Instances;

public class Individual {
	public List<Instance> handleInstances;	//�������������ϣ���������ø������Ӧ��
	public double fitness; //�������Ӧ��
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
