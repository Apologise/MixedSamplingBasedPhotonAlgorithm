package ���ӻ�ϲ��������;


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
		System.out.println("����ģ��->Starting...");
		//1. ��ʼ����Ⱥ
		initializePopulation();
		for(int i = 0; i < iter; ++i) {
			
		}
		System.out.println("����ģ��->End...");
		System.out.println("������Ÿ���");
		System.out.println("�㷨���н���");
	}
	
	/*
	 * TODO:��ʼ����Ⱥ{����ʵ����population��ÿһ��������󣬵��ó�ʼ������}
	 * RETURN���޸�population����
	 * */
	public void initializePopulation() {
		System.out.println("��ʼ����Ⱥ->Starting...");
		population = new Individual[popSize];
		for(int i = 0; i < popSize; ++i) {
			population[i] = new Individual(setting, instancesSet);
			population[i].initializeIndividual();
		}
		System.out.println("��ʼ����Ⱥ->End...");
	}
	
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		Setting setting = new Setting(100, 5, 5, 10, 100);
		InstancesSet instancesSet = new InstancesSet("dataset/pima.arff", setting); 
		instancesSet.initializeInstancesSet();
		QuantumModel quantumModel = new QuantumModel(setting, instancesSet);
		quantumModel.initializePopulation();
		
	}
}
