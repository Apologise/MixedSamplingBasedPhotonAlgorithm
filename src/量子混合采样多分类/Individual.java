package ���ӻ�ϲ��������;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.trees.J48;
import weka.core.Instance;
import weka.core.Instances;

public class Individual {
	public List<Instance> handledInstances;	//�������������ϣ���������ø������Ӧ��
	public int[] flag;
	public Phase[] phase;
	public double fitness; //�������Ӧ��
	public Setting setting;
	public InstancesSet instancesSet;
	public Instances handledMajorityInstances;

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
		for(int i = 0; i < instancesSet.majorityInstances.size(); ++i) {
			phase[i] = new Phase();
		}
		handledMajorityInstances = new Instances(instancesSet.rawInstances);
		handledMajorityInstances.clear();
	
	}
	
	/*
	 * TODO:��λ��ת
	 * RETURN���ı�������λֵ
	 * */
	public void phaseRotate() {
		for(int i = 0; i < phase.length; ++i) {
			double nextAlpha = phase[i].alpha*Math.cos(setting.rotateAngle)
					-phase[i].beta*Math.sin(setting.rotateAngle);
			double nextBeta = phase[i].alpha*Math.sin(setting.rotateAngle)+
					phase[i].beta*Math.cos(setting.rotateAngle);
			phase[i].alpha = nextAlpha;
			phase[i].beta = nextBeta;
		}
	}
	/*
	 * TODO: ����������Ӧ��
	 * RETURN: �޸ĳ�Ա����fitness
	 * */
	public void calFitness() throws Exception {
		//����handledInstances���з���ʵ��
		Instances newInstances = new Instances(instancesSet.rawInstances);
		newInstances.clear();
		//��handledInstancesȫ�����뵽newInstances��
		for(Instance inst: handledInstances) {
			newInstances.add(inst);
		}
		Classifier classifier = new J48();
		classifier.buildClassifier(newInstances);
		Evaluation evaluation = new Evaluation(newInstances);
	}
	
	/*
	 * TODO:�Ը�����л�ϲ���
	 * RETURN������һ��������������������
	 * */
	public void mixedSampling() {
		//�����handledInstances����
		handledInstances.clear();
		//���������������뵽handledInstnaces��
		for(Instance inst: instancesSet.minorityInstances) {
			handledInstances.add(inst);
		}
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
			if(flag[i] == 0) {
				handledInstances.add(majorityInstances.get(i));
				handledMajorityInstances.add(majorityInstances.get(i));
			}
		}
	}
	
	/*
	 * TODO: ���������й�����
	 * RETURN: �����ɵ��������뵽handledInstances��
	 * */
	public void overSampling() {
		//���������������ĸ����Ͷ����������ĸ���
		Set<Integer> labels = instancesSet.minorityClassLabel;
		Iterator<Integer> iterator = labels.iterator();
		GenerateSample generator = new GenerateSample(setting);
		List<Instance> output = new ArrayList<>();
		int average = instancesSet.originInstances.size()/instancesSet.rawInstances.numClasses();

		while(iterator.hasNext()) {
			int label = iterator.next();
			List<Instance> instances = instancesSet.instancesByClass.get(label);
			int[] n = calInstanceToGenerate(instancesSet.minorityInstances, average);
			//���instances���й�����
			Instances tempInstancesMajority = new Instances(instancesSet.rawInstances);
			tempInstancesMajority.clear();
			for(Instance inst: handledMajorityInstances) {
				tempInstancesMajority.add(inst);
			}
			Instances tempInstancesMinority = new Instances(instancesSet.rawInstances);
			tempInstancesMinority.clear();
			for(Instance inst: instances) {
				tempInstancesMinority.add(inst);
			}
			for(int i = 0; i < instances.size(); ++i) {
				generator.generateSample(instances.get(i), tempInstancesMinority, tempInstancesMajority, output, n[i]);
			}
		}
		//�����ɵ��������뵽handleInstances��
		for(Instance inst: output) {
			handledInstances.add(inst);
		}
		 
	}
	
	/*
	 * TODO: ����ÿ����������Ҫ���ɵ�������
	 * RETURN�� ÿ�������Ľ�������
	 * */
	public int[] calInstanceToGenerate(List<Instance> minority, int average) {
		int minoritySize = minority.size(), majoritySize = average;
		int[] n = new int[minoritySize];
		int generatesize = majoritySize - minoritySize;
		for (int i = 0; i < minoritySize; ++i) {
			n[i] = (int) Math.floor(generatesize / minoritySize);
		}
		int flag = n[0];

		int reminder = generatesize - (int) Math.floor(generatesize / minoritySize) * minoritySize;
		// println(minoritySamples.size());
		// println(reminder);

		// println("reminder:" + reminder);
		for (int i = 0; i < reminder;) {
			Random rand = new Random();
			int index = rand.nextInt(minoritySize);
			if (n[index] == flag) {
				n[index]++;
				i++;
			} else {
			}

		}

		int count = 0;
		for (int i = 0; i < minoritySize; ++i) {
			count += n[i];
		}
		return n;
	}
	/*
	 * TODO: �Ը�����в���������Phase����λ�õ���ʱ�̣������������Ĺ۲�ֵ
	 * RETUN: �޸���flag����
	 * */
	public void watchByPhase() {
		List<Double> weight = instancesSet.weightOfMajorityInstance;
		for(int i = 0; i < phase.length; ++i) {
			System.out.println(weight.get(i));
			double rand = Math.random();
			if(phase[i].alpha*phase[i].alpha < rand) {
				flag[i] = 1;
			}
		}
	}
	
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		Setting setting  = new Setting(100, 4, 4, 10, 200, 30);
		InstancesSet instancesSet = new InstancesSet("dataset/test.arff", setting);
		instancesSet.initializeInstancesSet();
		Individual individual = new Individual(setting, instancesSet);
		individual.initializeIndividual();
		individual.watchByPhase();
	}
}
