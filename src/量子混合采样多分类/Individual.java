package ���ӻ�ϲ��������;

import java.util.ArrayList;
import java.util.List;

import weka.core.Instance;
import weka.core.Instances;

public class Individual {
	public List<Instance> handledInstances;	//�������������ϣ���������ø������Ӧ��
	public int[] flag;
	public Phase[] phase;
	public double fitness; //�������Ӧ��
	public Setting setting;
	public InstancesSet instancesSet;
	public Individual(Setting setting, InstancesSet instancesSet) {
		this.setting = setting;
		this.instancesSet = instancesSet;
		fitness = 0d;
	}
	
	/*
	 * TODO:��ʼ�����壬�Ը����е����ö������ʵ��������ֹ����null����
	 * */
	public void initializeIndividual() {
		handledInstances = new ArrayList<Instance>();
		flag = new int[instancesSet.majorityInstances.size()];
		phase = new Phase[instancesSet.majorityInstances.size()];
	}
	/*
	 * TODO: ����������Ӧ��
	 * RETURN: �޸ĳ�Ա����fitness
	 * */
	public void calFitness() {
		
	}
	
	/*
	 * TODO:�Ը�����л�ϲ���
	 * RETURN������һ��������������������
	 * */
	public void mixedSampling() {
		//�����handledInstances����
		handledInstances.clear();
		underSampling();
		overSampling();
	}
	

	/*
	 * TODO:����flag�Ľ�����Զ��������Ƿ����
	 * RETURN: ��Ƿ��������������뵽handledInstances��
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
	 * TODO�����������й�����
	 * RETURN: �����ɵ��������뵽handledInstances��
	 * */
	public void overSampling() {
		
	}
	
	/*
	 * TODO: �Ը�����в���������Phase����λ�õ���ʱ�̣������������Ĺ۲�ֵ
	 * RETUN: �޸���flag����
	 * */
	public void watchByPhase() {
		
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
