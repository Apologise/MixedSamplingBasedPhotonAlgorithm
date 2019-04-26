package ���ӻ�ϲ��������;

import java.util.*;

import weka.core.EuclideanDistance;
import weka.core.Instance;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Normalize;

public class QuantumModel {
	public Individual[] population;	//��Ⱥ����
	public final int iter;
	public final int popSize;
	public Individual gBestIndividual;	//���ŵ�Ⱦɫ��
	public final Setting setting;
	public final InstancesSet instancesSet;
	
	public QuantumModel(Setting setting, InstancesSet instancesSet) {
		this.setting = setting;
		this.instancesSet = instancesSet;
		this.iter = setting.iteration;
		this.popSize = setting.popSize;
	}
	/*
	 * TODO:�����Ż��㷨�ĵ�����⺯��
	 * RETURN���������ŵĸ���
	 * */
	public void run() {
		//1. ��ʼ����Ⱥ
		initializePopulation();
		for(int i = 0; i < iter; ++i) {
			
		}
	}
	
	/*
	 * TODO:��ʼ����Ⱥ
	 * RETURN���޸�population����
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
