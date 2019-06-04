package 量子混合采样多分类;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
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

public class Individual  implements Serializable{
	public List<Instance> handledInstances;	//处理后的样本集合，用作计算该个体的适应度
	public int[] flag;
	public Phase[] phase;
	public double fitness; //个体的适应度
	public Setting setting;
	public InstancesSet instancesSet;
	public Instances handledMajorityInstances;
	public List<List<Instance>> instanceByClass;

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
		flag = new int[instancesSet.indexOfInstancesInMajorityInstancesIntoPopulation.size()];
		phase = new Phase[instancesSet.indexOfInstancesInMajorityInstancesIntoPopulation.size()];
		for(int i = 0; i < instancesSet.indexOfInstancesInMajorityInstancesIntoPopulation.size(); ++i) {
			phase[i] = new Phase();
		}
		handledMajorityInstances = new Instances(instancesSet.rawInstances);
		handledMajorityInstances.clear();
	}
	
	/*
	 * TODO:相位旋转
	 * RETURN：改变个体的相位值
	 * */
	public void phaseRotate(double[] angle) {
		for(int i = 0; i < phase.length; ++i) {
			double nextAlpha = phase[i].alpha*Math.cos(angle[i])
					-phase[i].beta*Math.sin(angle[i]);
			double nextBeta = phase[i].alpha*Math.sin(angle[i])+
					phase[i].beta*Math.cos(angle[i]);
			phase[i].alpha = nextAlpha;
			phase[i].beta = nextBeta;
		}
	}
	
	
	/*
	 * TODO: 计算个体的适应度
	 * RETURN: 修改成员变量fitness
	 * */
	public void calFitness(Enum_Classifier cls) throws Exception {
		//利用handledInstances进行分类实验
		Instances newInstances = new Instances(instancesSet.rawInstances);
		newInstances.clear();
		//将handledInstances全部加入到newInstances中
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
	 * TODO:对个体进行混合采样
	 * RETURN：返回一个经过处理后的样本集合
	 * */
	public void mixedSampling() {
		//先清空handledInstances集合和handledMajorityInstances集合
		handledInstances.clear();
		handledMajorityInstances.clear();
		//将少数类样本加入到handledInstnaces中
		
		//将多数类加入到handledMajorityInstances中
		for(Instance inst: instancesSet.majorityInstances) {
			handledInstances.add(inst);
		}
//		System.out.println("原始的少数类样本个数:"+handledInstances.size()+"原始多数类样本个数："+instancesSet.majorityInstances.size());
		underSampling();
		//将处理后的多数类样本加入到handledMajorityInstances中
		for(Instance inst: handledInstances) {
			handledMajorityInstances.add(inst);
		}
		for(Instance inst: instancesSet.minorityInstances) {
			handledInstances.add(inst);
		}
		
		
		System.out.println("过采样前样本的个数："+handledInstances.size());
		overSampling();
		System.out.println("过采样后样本的个数："+handledInstances.size());
	//	System.out.println("处理后样本个数为"+handledInstances.size());
	}
	

	/*
	 * TODO:根据flag的结果，对多数类进行欠采样
	 * RETURN: 将欠采样后的样本加入到handledInstances中
	 * */
	public void underSampling() {
		//根据观察的个体状态，将flag[0]的样本进行移除
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
			Instance inst = majorityInstances.get(index);
			handledInstances.remove(inst);
		}
	}
	
	/*
	 * TODO: 对样本进行过采样
	 * RETURN: 将生成的样本加入到handledInstances中
	 * */
	public void overSampling() {
		//计算少数类样本的个数和多数类样本的个数
		Set<Integer> labels = instancesSet.minorityClassLabel;
		Iterator<Integer> iterator = labels.iterator();
		GenerateSample generator = new GenerateSample(setting);
		List<Instance> output = new ArrayList<>();
		int average = handledInstances.size()/instancesSet.rawInstances.numClasses();
		int  numClass = instancesSet.rawInstances.numClasses();
		//1. 首先将所有样本按照类标进行拆分，此时存储所有样本的对象为handledInstances
		instanceByClass = splitByClass();
		//System.out.println("平均样本个数为："+average);
		for(int classValue = 0; classValue < numClass; classValue++){
			List<Instance> instances = instanceByClass.get(classValue);
			//如果少数类样本多于平均的样本数量，那么就不需要进行过采样
			if(instances.size() > average) {
				continue;
			}
			int[] n = calInstanceToGenerate(instancesSet.minorityInstances, average);
			//针对instances进行过采样
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
		
		System.out.println("生成的样本个数为："+output.size());
		//将生成的样本加入到handleInstances中
		for(Instance inst: output) {
			handledInstances.add(inst);
		}
	}
	
	/*
	 * TODO:按照类标对整个数据集进行拆分
	 * RETURN:得到一个经过类别拆分的对象
	 * */
	public List<List<Instance>> splitByClass() {
		int numOfClass = instancesSet.rawInstances.numClasses();
		List<List<Instance>> instancesByClass = new ArrayList<List<Instance>>();
		for(int i = 0; i < numOfClass; ++i) {
			List<Instance> temp = new ArrayList<>();
			instancesByClass.add(temp);
		}
		for(Instance inst: handledInstances) {
			int classLabel = (int)inst.classValue();
			//获得类标为classLabel的List，并将其加入其中
			instancesByClass.get(classLabel).add(inst);
		}
		return instancesByClass;
	}
	
	/*
	 * TODO: 计算每个少数类需要生成的样本数
	 * RETURN： 每个样本的近邻数组
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
	 * TODO: 对个体进行测量，根据Phase的相位得到该时刻（迭代次数）的观察值
	 * RETUN: 修改了flag数组
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
	 * TODO:通过参数选择分类器函数
	 * RETURN: 通过参数进行选择某一种分类器，而不需要进入calFitness函数中进行修改
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
			System.out.println("未找到该算法！！！");
			System.exit(0);
			break;
		}
		return classifier;
	}
	
	/*
	 * TODO: 对 对象进行深复制
	 * RETURN: 返回一个经过深复制后的Object对象
	 * */
	public Object deepCopy() throws ClassNotFoundException {
		Object desObject = null;
		try {
			//1. 将srcObject对象写入ByteArray流中
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
			objectOutputStream.writeObject(this);
			objectOutputStream.close();
			//2. 从ByteArray中读入对象并赋给desObject
			ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
			ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
			desObject = objectInputStream.readObject();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return desObject;
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
		Individual newIndividual = (Individual)individual.deepCopy();
		individual.calFitness(setting.cls);
		individual.handledInstances.get(0).setValue(0, -1);
		System.out.println(individual.fitness);
	}
}
