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
		int T = iter;
		do {
			//2. �����еĸ�����й۲�
			for(int j = 0; j < population.length; ++j) {
				population[j].watchByPhase();
			}
			//3. ���ݹ۲�������ÿ�������������
				//a.����flag���飬�Զ��������Ƿ����
				//b.����������й�����
				//c.�����µ����ݼ����з��࣬�õ�������
			//4.������Ӧ�ȱ�����Ѹ���
			//5.���������Ÿ���ÿһ������
			T--;
		}while(T!=0);
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
		Setting setting = new Setting(100, 5, 5, 10, 100,30);
		InstancesSet instancesSet = new InstancesSet("dataset/pima.arff", setting); 
		instancesSet.initializeInstancesSet();
		QuantumModel quantumModel = new QuantumModel(setting, instancesSet);
		quantumModel.initializePopulation();
		
	}
}
