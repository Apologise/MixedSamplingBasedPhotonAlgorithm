package 量子混合采样多分类;

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
	public List<Instance> handledInstances;	//处理后的样本集合，用作计算该个体的适应度
	public int[] flag;
	public Phase[] phase;
	public double fitness; //个体的适应度
	public Setting setting;
	public InstancesSet instancesSet;
	public Instances handledMajorityInstances;

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
		for(int i = 0; i < instancesSet.majorityInstances.size(); ++i) {
			phase[i] = new Phase();
		}
		handledMajorityInstances = new Instances(instancesSet.rawInstances);
		handledMajorityInstances.clear();
	
	}
	
	/*
	 * TODO:相位旋转
	 * RETURN：改变个体的相位值
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
	 * TODO: 计算个体的适应度
	 * RETURN: 修改成员变量fitness
	 * */
	public void calFitness() throws Exception {
		//利用handledInstances进行分类实验
		Instances newInstances = new Instances(instancesSet.rawInstances);
		newInstances.clear();
		//将handledInstances全部加入到newInstances中
		for(Instance inst: handledInstances) {
			newInstances.add(inst);
		}
		Classifier classifier = new J48();
		classifier.buildClassifier(newInstances);
		Evaluation evaluation = new Evaluation(newInstances);
	}
	
	/*
	 * TODO:对个体进行混合采样
	 * RETURN：返回一个经过处理后的样本集合
	 * */
	public void mixedSampling() {
		//先清空handledInstances集合
		handledInstances.clear();
		//将少数类样本加入到handledInstnaces中
		for(Instance inst: instancesSet.minorityInstances) {
			handledInstances.add(inst);
		}
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
			if(flag[i] == 0) {
				handledInstances.add(majorityInstances.get(i));
				handledMajorityInstances.add(majorityInstances.get(i));
			}
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
		int average = instancesSet.originInstances.size()/instancesSet.rawInstances.numClasses();

		while(iterator.hasNext()) {
			int label = iterator.next();
			List<Instance> instances = instancesSet.instancesByClass.get(label);
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
		//将生成的样本加入到handleInstances中
		for(Instance inst: output) {
			handledInstances.add(inst);
		}
		 
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
	 * TODO: 对个体进行测量，根据Phase的相位得到该时刻（迭代次数）的观察值
	 * RETUN: 修改了flag数组
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
