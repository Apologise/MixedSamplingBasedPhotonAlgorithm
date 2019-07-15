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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.classifiers.functions.SMO;
import weka.classifiers.lazy.IBk;
import weka.classifiers.meta.Vote;
import weka.classifiers.trees.J48;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SelectedTag;

public class Individual implements Serializable {
	public List<Instance> handledInstances; // 经过欠采样和过采样后的样本集合
	public int[] flag;
	public Phase[] phase;
	public double fitness; // 个体的适应度
	public Setting setting;
	public InstancesSet instancesSet;
	public Instances handledMajorityInstances;
	public List<List<Instance>> instancesByClass;
	public List<List<Instance>> allHandledInstances;

	public Individual(Setting setting, InstancesSet instancesSet) {
		this.setting = setting;
		this.instancesSet = instancesSet;
		fitness = 0d;
	}

	/*
	 * TODO:初始化个体，对个体中的引用对象进行实例化，防止出现null类型
	 */
	public void initializeIndividual() {
		handledInstances = new ArrayList<Instance>();
		flag = new int[instancesSet.originInstances.size()];
		phase = new Phase[instancesSet.originInstances.size()];
		for (int i = 0; i < instancesSet.originInstances.size(); ++i) {
			phase[i] = new Phase();
		}
		handledMajorityInstances = new Instances(instancesSet.rawInstances);
		handledMajorityInstances.clear();
		// 初始化allHanledInstance;
		allHandledInstances = new ArrayList<List<Instance>>();
		for (int i = 0; i < setting.K; ++i) {
			List<Instance> list = new ArrayList<Instance>();
			allHandledInstances.add(list);
		}
	}

	/*
	 * TODO:相位旋转 RETURN：改变个体的相位值
	 */
	public void phaseRotate(double[] angle) {
		for (int i = 0; i < phase.length; ++i) {
			double nextAlpha = phase[i].alpha * Math.cos(angle[i]) - phase[i].beta * Math.sin(angle[i]);
			double nextBeta = phase[i].alpha * Math.sin(angle[i]) + phase[i].beta * Math.cos(angle[i]);
			phase[i].alpha = nextAlpha;
			phase[i].beta = nextBeta;
		}
	}

	/*
	 * TODO: 计算个体的适应度 RETURN: 修改成员变量fitness
	 */
	public void calFitness(Enum_Classifier cls) throws Exception {
		// 使用集成规则来聚类
		Classifier[] classifier = new Classifier[setting.K];
		for (int i = 0; i < setting.K; ++i) {
			Instances newInstances = new Instances(instancesSet.rawInstances);
			newInstances.clear();
			// 将handledInstances全部加入到newInstances中
			for (Instance inst : allHandledInstances.get(i)) {
				newInstances.add(inst);
			}
			classifier[i] = chooseClassifier(cls);
			classifier[i].buildClassifier(newInstances);
		}
		Vote ensemble = new Vote();
		SelectedTag tag = new SelectedTag(Vote.AVERAGE_RULE, Vote.TAGS_RULES);
		ensemble.setCombinationRule(tag);
		ensemble.setClassifiers(classifier);
		// 测试
		Evaluation eval = new Evaluation(instancesSet.validateInstances);
		eval.evaluateModel(ensemble, instancesSet.validateInstances);
		fitness = eval.areaUnderROC(0);
	}

	/*
	 * TODO: 计算最终的分类值
	 * 
	 */
	public double calAUC(Enum_Classifier cls) throws Exception {
		// 使用集成规则来聚类
		Classifier[] classifier = new Classifier[setting.K];
		for (int i = 0; i < setting.K; ++i) {
			Instances newInstances = new Instances(instancesSet.rawInstances);
			newInstances.clear();
			// 将handledInstances全部加入到newInstances中
			for (Instance inst : allHandledInstances.get(i)) {
				newInstances.add(inst);
			}
			classifier[i] = chooseClassifier(cls);
			classifier[i].buildClassifier(newInstances);
		}
		Vote ensemble = new Vote();
		SelectedTag tag = new SelectedTag(Vote.AVERAGE_RULE, Vote.TAGS_RULES);
		ensemble.setCombinationRule(tag);
		ensemble.setClassifiers(classifier);
		// 测试
		Evaluation eval = new Evaluation(instancesSet.testInstances);
		eval.evaluateModel(ensemble, instancesSet.testInstances);
		return eval.areaUnderROC(0);
		/*
		 * //利用handledInstances进行分类实验 Instances newInstances = new
		 * Instances(instancesSet.rawInstances); newInstances.clear();
		 * //将handledInstances全部加入到newInstances中 for(Instance inst: handledInstances) {
		 * newInstances.add(inst); } Classifier classifier = chooseClassifier(cls);
		 * classifier.buildClassifier(newInstances); Evaluation evaluation = new
		 * Evaluation(newInstances); evaluation.evaluateModel(classifier,
		 * instancesSet.testInstances); return evaluation.areaUnderROC(0)
		 */

	}

	public double calAUC1(Enum_Classifier cls) throws Exception {
		// 利用handledInstances进行分类实验
		double[] result = new double[setting.K];
		for (int i = 0; i < setting.K; ++i) {
			Instances newInstances = new Instances(instancesSet.rawInstances);
			newInstances.clear();
			// 将handledInstances全部加入到newInstances中
			for (Instance inst : allHandledInstances.get(i)) {
				newInstances.add(inst);
			}
			Classifier classifier = chooseClassifier(cls);
			classifier.buildClassifier(newInstances);
			Evaluation evaluation = new Evaluation(newInstances);
			evaluation.evaluateModel(classifier, instancesSet.validateInstances);
			result[i] = evaluation.areaUnderROC(0);
		}
		double sum = 0;
		for (int i = 0; i < setting.K; ++i) {
			sum += result[i];
		}
		return sum / setting.K;
		/*
		 * Instances newInstances = new Instances(instancesSet.rawInstances);
		 * newInstances.clear(); //将handledInstances全部加入到newInstances中 for(Instance
		 * inst: handledInstances) { newInstances.add(inst); } Classifier classifier =
		 * chooseClassifier(cls); classifier.buildClassifier(newInstances); Evaluation
		 * evaluation = new Evaluation(newInstances);
		 * evaluation.evaluateModel(classifier, instancesSet.validateInstances); return
		 * evaluation.areaUnderROC(0);
		 */
	}

	/*
	 * TODO:对个体进行混合采样 RETURN：返回一个经过处理后的样本集合
	 */
	public void mixedSampling() {
		// 1. 先清空handledInstances集合和handledMajorityInstances集合
		handledInstances.clear();
		handledMajorityInstances.clear();
		// 2. 将originInstances样本加入到handledInstances中进行处理
		for (Instance inst : instancesSet.originInstances) {
			handledInstances.add(inst);
		}
		instancesByClass = splitByClass();
		underSampling();
		overSampling();
	}

	/*
	 * TODO:根据flag的结果，对多数类进行欠采样 RETURN: 将欠采样后的样本加入到handledInstances中
	 */
	public void underSampling() {
		// 根据观察的个体状态，将flag[0]的样本进行移除
		List<Integer> indexOfRemovedInstances = new ArrayList<>();
		for (int i = 0; i < flag.length; ++i) {
			if (flag[i] == 0) {
				indexOfRemovedInstances.add(i);
			}
		}
		Collections.sort(indexOfRemovedInstances, new Comparator<Integer>() {
			@Override
			public int compare(Integer o1, Integer o2) {
				// TODO Auto-generated method stub
				if (o1 > o2) {
					return -1;
				} else if (o1 < o2) {
					return 1;
				} else {
					return 0;
				}
			}

		});
		for (int i = 0; i < indexOfRemovedInstances.size(); ++i) {
			int index = indexOfRemovedInstances.get(i);
			Instance inst = instancesSet.originInstances.get(index);
			//判断该类是否为少数类
			if(!isMinorityClass(inst)) {
				//如果不为少数类就进行移除
				handledInstances.remove(inst);
			}
		}
	}


	/*
	 * TODO: 对样本进行过采样 RETURN: 将生成的样本加入到handledInstances中
	 */
	public void overSampling() {
		List<List<Instance>> output = new ArrayList<List<Instance>>();
		for (int i = 0; i < setting.K; ++i) {
			List<Instance> temp = allHandledInstances.get(i);
			temp.clear();
			List<Instance> tempOutput = new ArrayList<>();
			output.add(tempOutput);
		}
		// 计算少数类样本的个数和多数类样本的个数

		int numClass = instancesSet.rawInstances.numClasses();

		// 1. 首先将所有样本按照类标进行拆分，此时存储所有样本的对象为handledInstances
		instancesByClass = splitByClass();
		for(int i = 0; i < handledInstances.size(); ++i) {
			
		}
		
		// 求出类别中数量最多的类别的样本
		int max = 0;
		for (int i = 0; i < numClass; ++i) {
			if (max < instancesByClass.get(i).size()) {
				max = instancesByClass.get(i).size();
			}
		}
//		int average = handledInstances.size()/numClass;
		// 2. 对每一个少数类进行过采样
		for (int classValue = 0; classValue < numClass; classValue++) {
			List<Instance> instances = instancesByClass.get(classValue);
			// 2. 如果少数类样本多于平均的样本数量，那么就不需要进行过采样
			if (instances.size() >= max) {
				continue;
			}
			//如果当前少数类样本数量小于设定的K值，那么将K值设置为少数类样本数量-1
			//并且在进行采样后将K值复原
			int tempK = setting.K;
			if(instancesByClass.get(classValue).size() <= setting.K) {
				setting.K = instancesByClass.get(classValue).size()-1;
			}
			// 2.2 根据个体的flag筛选结果计算少选后的少数类进行欠采样的个数（不需要欠采样的样本值为0，反之不为0）
			int[] n = calInstanceToGenerate(instances, max);
			/*
			 * //2.3 根据当前经过欠采样后的样本进行计算类别间距 Map<Integer, Double> instancesOfMargin =
			 * calMargin(); //2.4 针对instanceOfMargin进行过采样
			 * 
			 * for(Entry<Integer, Double> entry: instancesOfMargin.entrySet()) { Instance
			 * instTemp = handledInstances.get(entry.getKey());
			 * //根据inst来获取在originInstances中的下标 int index =
			 * instancesSet.originInstances.indexOf(instTemp); for(int i = 0; i < n[index];
			 * ++i) { Instance inst = generateSample(entry, classValue); output.add(inst); }
			 * }
			 */
			Instances majority = new Instances(instancesSet.rawInstances);
			majority.clear();
			for (int i = 0; i < handledInstances.size(); ++i) {
				if (handledInstances.get(i).classValue() != classValue) {
					majority.add(handledInstances.get(i));
				}
			}
			Instances minority = new Instances(instancesSet.rawInstances);
			minority.clear();
			for (Instance inst : handledInstances) {
				int  label = (int)inst.classValue();
				if(classValue != label) {continue;}
				minority.add(inst);
			}
			GenerateSample generateSample = new GenerateSample(setting);
			for (int i = 0; i < n.length; ++i) {
				Instance inst = instancesSet.originInstances.get(i);
				if (n[i] <= 0) {
					continue;
				}
				generateSample.generateSample(inst, minority, majority, output, n[i]);
			}
			setting.K = tempK;

		}
		// 将生成的样本加入到handleInstances中
		for (int i = 0; i < setting.K; ++i) {
			for (Instance inst : handledInstances) {
				allHandledInstances.get(i).add(inst);
			}
			for (Instance inst : output.get(i)) {
//				handledInstances.add(inst);
				allHandledInstances.get(i).add(inst);
			}
		}
	}

	/*
	 * TODO: 根据样本的Margin生成一个样本 Entry<Integer, Double> entry:
	 * Integer为样本的下标，Double为对应样本的安全间距， classValue为当前采样类的类标 RETURN: 返回一个生成的样本
	 */
	public Instance generateSample(Entry<Integer, Double> entry, double classValue) {
		// 1. 初始化一个单位方向向量
		int dimensionSize = handledInstances.get(0).numAttributes();
		double[] directionVector = new double[dimensionSize];
		int sum2 = 0; // 变量sum2保存向量的2范数的平方
		// 1.1 随机生成[-10,10]的方向向量
		for (int i = 0; i < dimensionSize - 1; ++i) {
			Random rand = new Random();
			int value = rand.nextInt(20) - 10;
			directionVector[i] = value;
			sum2 += value * value;
		}
		double tempSum2 = Math.sqrt(sum2);
		// 1. 2.将方向向量单位化
		for (int i = 0; i < dimensionSize; ++i) {
			directionVector[i] = directionVector[i] / tempSum2;
		}

		// 2.根据得到的随机数，生成新样本
		double[] instanceValue = new double[dimensionSize];
		Instance currInst = instancesSet.originInstances.get(entry.getKey());
		for (int i = 0; i < dimensionSize; ++i) {
			// 2.1 随机化[0,margin]的随机数
			double margin = entry.getValue();
			double rand = Math.random() * margin;
			// 2.2 将[0.margin]乘以方向向量
			instanceValue[i] = currInst.value(i) + rand * directionVector[i];
		}
		// 3.1 为样本设置类标
		instanceValue[dimensionSize - 1] = classValue;
		// 4. 得到新的合成样本
		Instance newInstance = handledInstances.get(0).copy(instanceValue);
		return newInstance;
	}

	/*
	 * TODO: 计算每个样本的margin RETURN: 修改List<Double>类型变量margin
	 */

	public Map<Integer, Double> calMargin() {
		Map<Integer, Double> instanceOfMargin = new HashMap<>();
		List<List<Double>> distanceMatrix = initializeDistanceMatrix(handledInstances);
		for (int i = 0; i < handledInstances.size(); ++i) {
			List<Double> distance = distanceMatrix.get(i);
			// 找到最近距离的同类样本点，更新indexOfNearestHit
			int indexOfNearestHit = -1, indexOfNearestMiss = -1;
			double tempMinDistanceOfNearestHit = Double.MAX_VALUE, tempMinDistanceOfNearestMiss = Double.MAX_VALUE;
			for (int j = 0; j < distance.size(); ++j) {
				if (i == j) {
					continue;
				}
				// 寻找同类样本点的最近距离，更新indexOfNearestHit和tempMinDistanceOfNearestHit
				int classLabel1 = (int) handledInstances.get(i).classValue();
				int classLabel2 = (int) handledInstances.get(j).classValue();
				if (classLabel1 == classLabel2 && distance.get(j) < tempMinDistanceOfNearestHit) {
					tempMinDistanceOfNearestHit = distance.get(j);
					indexOfNearestHit = j;
				}
				// 寻找异类样本点的最近距离，更新indexOfNearestMiss和tempMinDistanceOfNearestMiss
				if (classLabel1 != classLabel2 && distance.get(j) < tempMinDistanceOfNearestMiss) {
					tempMinDistanceOfNearestMiss = distance.get(j);
					indexOfNearestMiss = j;
				}
			}
			// 找到了同类最近距离和异类最近距离，就可以求出样本i的margin
			double margin = 0.5 * (tempMinDistanceOfNearestMiss - tempMinDistanceOfNearestHit);
			if (margin < 0) {
				margin = 0;
			}
			instanceOfMargin.put(i, margin);
		}
		return instanceOfMargin;
	}

	public List<List<Double>> initializeDistanceMatrix(List<Instance> instances) {
		List<List<Double>> distanceMatrix = new ArrayList<List<Double>>();
		for (Instance first : instances) {

			List<Double> tempDistance = new ArrayList<>();
			for (Instance second : instances) {
				if (first == second) {
					tempDistance.add(Double.MAX_VALUE);
				} else {
					tempDistance.add(calDistance(first, second));
				}
			}
			distanceMatrix.add(tempDistance);
		}
		return distanceMatrix;
	}

	public static double calDistance(Instance first, Instance second) {
		double distance = 0;
		for (int i = 0; i < first.numAttributes() - 1; ++i) {
			double diff = first.value(i) - second.value(i);
			distance += diff * diff;
		}
		return Math.sqrt(distance);
	}

	/*
	 * TODO:按照类标对整个数据集进行拆分 RETURN:得到一个经过类别拆分的对象
	 */
	public List<List<Instance>> splitByClass() {
		int numOfClass = instancesSet.rawInstances.numClasses();
		List<List<Instance>> instancesByClass = new ArrayList<List<Instance>>();
		for (int i = 0; i < numOfClass; ++i) {
			List<Instance> temp = new ArrayList<>();
			instancesByClass.add(temp);
		}
		for (Instance inst : handledInstances) {
			int classLabel = (int) inst.classValue();
			// 获得类标为classLabel的List，并将其加入其中
			instancesByClass.get(classLabel).add(inst);
		}
		return instancesByClass;
	}

	/*
	 * TODO: 计算每个少数类需要生成的样本数 RETURN： 每个样本的近邻数组
	 */
	public int[] calInstanceToGenerate(List<Instance> minority, int average) {

		int classLabel = (int) minority.get(0).classValue();
		int minoritySize = minority.size();
		int[] n = new int[instancesSet.originInstances.size()];
		int generatesize = average - minoritySize;
		List<Instance> instancesToOverSampling = new ArrayList<>();
		// 1. 将需要过采样的样本的下标记录到instancesToOverSampling,并统计其个数
		for (int i = 0; i < flag.length; ++i) {
			if (flag[i] == 1) {
				Instance inst = instancesSet.originInstances.get(i);
				if ((int) inst.classValue() == classLabel) {
					instancesToOverSampling.add(inst);
				}
			}
		}
		for (int i = 0; i < flag.length; ++i) {
			if (flag[i] == 1) {
				Instance inst = instancesSet.originInstances.get(i);
				if ((int) inst.classValue() == classLabel) {
					if(instancesToOverSampling.size()!=0) {
						n[i] = generatesize / instancesToOverSampling.size();
					}else {
						n[i] = 0;
					}
				}
			}
		}
		if(instancesToOverSampling.size() == 0) {return n;}
		int reminder = generatesize % instancesToOverSampling.size();
		// println(minoritySamples.size());
		for (int i = 0; i < reminder;) {
			Random rand = new Random();
			int index = rand.nextInt(n.length);
			Instance inst = instancesSet.originInstances.get(index);
			if (flag[index] == 1 && ((int) inst.classValue() == classLabel)) {
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
	 * TODO: 对个体进行测量，根据Phase的相位得到该时刻（迭代次数）的观察值 RETUN: 修改了flag数组
	 */
	public void watchByPhase() {
		for (int i = 0; i < phase.length; ++i) {
			double rand = Math.random();
			if (phase[i].alpha * phase[i].alpha < rand) {
				flag[i] = 1;
			} else {
				flag[i] = 0;
			}
		}
	}

	/*
	 * TODO:通过参数选择分类器函数 RETURN: 通过参数进行选择某一种分类器，而不需要进入calFitness函数中进行修改
	 */
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
		default:
			System.out.println("未找到该算法！！！");
			System.exit(0);
			break;
		}
		return classifier;
	}

	/*
	 * TODO: 对 对象进行深复制 RETURN: 返回一个经过深复制后的Object对象
	 */
	public Object deepCopy() throws ClassNotFoundException {
		Object desObject = null;
		try {
			// 1. 将srcObject对象写入ByteArray流中
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
			objectOutputStream.writeObject(this);
			objectOutputStream.close();
			// 2. 从ByteArray中读入对象并赋给desObject
			ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
			ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
			desObject = objectInputStream.readObject();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return desObject;
	}

	
	/*
	 * TODO: 判断一个样本是否属于少数类
	 * RETURN： 如果该样本为少数类，那么就返回true, 否则返回false
	 * */
	
	public boolean isMinorityClass(Instance inst) {
		//求得整个数据集的平均样本个数
		int average = 0;
		for(int i = 0; i < instancesByClass.size(); ++i) {
			average += instancesByClass.get(i).size();
		}
		average /= inst.numClasses();
		int classLabel = (int)inst.classValue();
		if(instancesByClass.get(classLabel).size() < average*0.75) {
			return true;
		}
		return false;
	}
	
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		Enum_Classifier classifier = Enum_Classifier.C45;
		Setting setting = new Setting(4, 10, classifier);
		InstancesSet instancesSet = new InstancesSet("pima", setting);
		instancesSet.initializeInstancesSet(0);
		Individual individual = new Individual(setting, instancesSet);
		individual.initializeIndividual();
		individual.watchByPhase();
		individual.mixedSampling();
	}
}
