package ���ӻ�ϲ��������;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.classifiers.functions.SMO;
import weka.classifiers.lazy.IBk;
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
		flag = new int[instancesSet.indexOfInstancesInMajorityInstancesIntoPopulation.size()];
		phase = new Phase[instancesSet.indexOfInstancesInMajorityInstancesIntoPopulation.size()];
		for(int i = 0; i < instancesSet.indexOfInstancesInMajorityInstancesIntoPopulation.size(); ++i) {
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
	public void calFitness(Enum_Classifier cls) throws Exception {
		//����handledInstances���з���ʵ��
		Instances newInstances = new Instances(instancesSet.rawInstances);
		newInstances.clear();
		//��handledInstancesȫ�����뵽newInstances��
		for(Instance inst: handledInstances) {
			newInstances.add(inst);
		}
		Classifier classifier = chooseClassifier(cls);
		classifier.buildClassifier(newInstances);
		Evaluation evaluation = new Evaluation(newInstances);
		evaluation.evaluateModel(classifier, instancesSet.validateInstances);
		fitness = evaluation.areaUnderROC(0);
	}
	
	/*
	 * TODO:�Ը�����л�ϲ���
	 * RETURN������һ��������������������
	 * */
	public void mixedSampling() {
		//�����handledInstances���Ϻ�handledMajorityInstances����
		handledInstances.clear();
		handledMajorityInstances.clear();
		//���������������뵽handledInstnaces��
		
		//����������뵽handledMajorityInstances��
		for(Instance inst: instancesSet.majorityInstances) {
			handledInstances.add(inst);
		}
//		System.out.println("ԭʼ����������������:"+handledInstances.size()+"ԭʼ����������������"+instancesSet.majorityInstances.size());
		underSampling();
//		System.out.println("������������������Ϊ��"+handledMajorityInstances.size());
		for(Instance inst: instancesSet.minorityInstances) {
			handledInstances.add(inst);
		}
		overSampling();
	//	System.out.println("�������������Ϊ"+handledInstances.size());
	}
	

	/*
	 * TODO:����flag�Ľ�����Զ��������Ƿ����
	 * RETURN: ��Ƿ��������������뵽handledInstances��
	 * */
	public void underSampling() {
		//���ݹ۲�ĸ���״̬����flag[0]�����������Ƴ�
		List<Integer> indexOfRemovedInstances = new ArrayList<>();
		for(int i = 0; i < flag.length; ++i) {
			if(flag[i] == 0) {
				indexOfRemovedInstances.add(instancesSet.indexOfInstancesInMajorityInstancesIntoPopulation.get(i));
			}
		}
		Collections.sort(indexOfRemovedInstances, new Comparator<Integer>() {

			@Override
			public int compare(Integer o1, Integer o2) {
				// TODO Auto-generated method stub
				if(o1 > o2) {
					return -1;
				}else if(o1 < o2){
					return 1;
				}else {
					return 0;
				}
			}
			
		});
		List<Instance> majorityInstances = instancesSet.majorityInstances;
		for(int i = 0; i < indexOfRemovedInstances.size(); ++i) {
			int index = indexOfRemovedInstances.get(i);
			System.out.println(index);
			Instance inst = majorityInstances.get(index);
			handledInstances.remove(inst);
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
		int average = handledMajorityInstances.size()/instancesSet.rawInstances.numClasses();
	//	System.out.println("ƽ����������Ϊ��"+average);
		while(iterator.hasNext()) {
			int label = iterator.next();
			List<Instance> instances = instancesSet.instancesByClass.get(label);
			//�����������������ƽ����������������ô�Ͳ���Ҫ���й�����
			if(instances.size() > average) {
				return;
			}
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
//		System.out.println("���ɵ���������Ϊ��"+output.size());
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
			double rand = Math.random();
			if(phase[i].alpha*phase[i].alpha < rand) {
				flag[i] = 1;
			}
		}
	}
	
	/*
	 * TODO:ͨ������ѡ�����������
	 * RETURN: ͨ����������ѡ��ĳһ�ַ�������������Ҫ����calFitness�����н����޸�
	 * */
	public Classifier chooseClassifier(Enum_Classifier cls) {
		Classifier classifier = null;
		switch (cls) {
		case C45:
			classifier = new J48();
			break;
		case KNN:
			classifier = new IBk();
			break;
		case SMO:
			classifier = new SMO();
			break;
		case NB:
			classifier = new NaiveBayes();
			break;
		case MLP:
			classifier = new MultilayerPerceptron();
			break;
		default:
			System.out.println("δ�ҵ����㷨������");
			break;
		}
		return classifier;
	}
	
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		Enum_Classifier classifier = Enum_Classifier.C45;
		Setting setting  = new Setting(100, 4, 4, 10, 30, classifier);
		InstancesSet instancesSet = new InstancesSet("pima", setting);
		instancesSet.initializeInstancesSet(0);
		Individual individual = new Individual(setting, instancesSet);
		individual.initializeIndividual();
		individual.watchByPhase();
		individual.mixedSampling();
		individual.calFitness(setting.cls);
		System.out.println(individual.fitness);
	}
}
