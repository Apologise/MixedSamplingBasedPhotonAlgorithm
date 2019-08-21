package 量子混合采样多分类;

import java.io.FileWriter;
import java.nio.channels.SeekableByteChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import weka.attributeSelection.BestFirst;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.SMO;
import weka.classifiers.lazy.IBk;
import weka.classifiers.meta.Vote;
import weka.classifiers.trees.J48;
import weka.clusterers.SimpleKMeans;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SelectedTag;
import weka.core.Settings;
import weka.core.Utils;
import weka.filters.Filter;
import weka.filters.supervised.attribute.ClassOrder;
import weka.filters.supervised.instance.SMOTE;
import weka.filters.unsupervised.attribute.Normalize;
import weka.filters.unsupervised.attribute.Remove;

/*
 * Author: apolo
 * Time:   2019.5.22
 * INFO:   多分类框架，用于数据的多分类（包含二分类），最终返回一个特定的多分类指标（MAUC, MGmean）
 * */

public class ClassifyModel {
	public List<Instance> m_train;
	public Instances m_test;
	public int[] flag; // 预测类标数组
	// 将m_train按类别拆分
	public List<List<Instance>> m_trainInstancesByClass;
	public List<List<Double>> distanceMatrix;
	public Enum_Classifier cls;
	public List<Instance> clusterCenters;
	public Instances normalizeInstancesTest;
	public Instances normalizeInstancesTrain;
	public List<List<Instance>> centerOfClass;
	public List<Integer> globalClassOrder;
	public List<Integer> currentClassOrder;
	public int DK;
	public Classifier classifier = null;
	public InstancesSet instancesSet;
	public Setting setting;

	/*
	 * trian为已经经过混合采样后的数据集 test为验证集合
	 */
	public ClassifyModel(List<Instance> train, Instances test, Enum_Classifier classfier, InstancesSet instancesSet, Setting setting) throws Exception {
		m_train = train;
		m_test = test;
		flag = new int[m_test.size()];
		cls = classfier;
		DK = Setting.KDistance;
		this.instancesSet = instancesSet;
		this.setting = setting;
		/*
		 * normalizeInstancesTest = new Instances(m_test);
		 * normalizeInstancesTest.clear(); normalizeInstancesTrain= new
		 * Instances(m_test); normalizeInstancesTrain.clear(); for(Instance inst:
		 * m_train) {normalizeInstancesTrain.add(inst);} m_test =
		 * normalizeInstances(m_test); m_train =
		 * normalizeInstances(normalizeInstancesTrain);
		 */
		splitTrainByClass();
//		clusterCenters = new ArrayList<Instance>();
		centerOfClass = new ArrayList<>();
		for (int i = 0; i < m_trainInstancesByClass.size(); ++i) {
			List<Instance> temp = new ArrayList<>();
			centerOfClass.add(temp);
		}
	}

	/*
	 * TODO: 对一个测试样本进行测试分类 testInstance: 测试样本 cls: 分类器 RETURN: 分类结果
	 */
	public int classifySingleInstance(Instance testInstance) throws Exception {
	
		int predictionLabel = -1;
		// 1. 得到一个测试样本的类标序列
		List<Integer> classLabelByDistance = getClassLabelByDistance(testInstance, DK);
		List<Integer> classLabel = new ArrayList<>(classLabelByDistance);
		do {
	//		System.out.println("输出当前的类别集合" + classLabel.toString());
			// 将样本加入到Instances对象中
			List<Instance> instances = splitByLabelDistance(testInstance, classLabel);
			Instances trainInstances = new Instances(m_test);
			trainInstances.clear();
			for (Instance inst : instances) {
				trainInstances.add(inst);
			}
			// 如果数据集为非平衡数据集，那么对该数据集进行平衡采样
			//计算数据集中的非平衡率，然后但非平衡率较大时，则进行混合采样
			classifier = null;
			//mixedSamplingByImbalanceRatio(trainInstances);
			List<Instance> minority = new ArrayList<>();
			List<Instance> majority = new ArrayList<>();
			for(Instance inst: instances) {
				int label = (int)inst.classValue();
				if(label == 0) {
					minority.add(inst);
				}else {
					majority.add(inst);
				}
			}
			//统计两个类别之间的个数
			int minoritySize = minority.size();
			int majoritySize = majority.size();
			double IR;
			//如果少数样本集合的个数少于多数样本集合，那么将两个集合进行交换
			if(minoritySize > majoritySize) {
				List<Instance> temp = new ArrayList<>(minority);
				minority.clear();
				for(Instance inst : majority) {
					minority.add(inst);
				}
				majority.clear();
				for(Instance inst: temp) {
					majority.add(inst);
				}
				temp = null; //有助于垃圾回收
				minoritySize = minority.size();
				majoritySize = majority.size();
				System.out.println("多数类样本个数为："+majoritySize);
				System.out.println("少数类样本个数为："+minoritySize);
			}
			int tempK = setting.K;
			if(setting.K >= minoritySize) {
				setting.K = minoritySize-1;
			}
			
			List<List<Instance>> output = overSamplingByLADBMOTE(trainInstances, minority, majority);
			
			setting.K = tempK;
			Instances[] instancesList = new Instances[output.size()];
			for(int i = 0; i < output.size(); ++i) {
				instancesList[i] = new Instances(trainInstances);
				instancesList[i].addAll(output.get(i));
			}
			Classifier[] classifiers = new Classifier[output.size()];
			for(int i = 0; i < output.size(); ++i) {
				classifiers[i] = chooseClassifier(cls);
				classifiers[i].buildClassifier(instancesList[i]);
				List<Instance> minority1 = new ArrayList<>();
				List<Instance> majority1 = new ArrayList<>();
				for(Instance inst: instancesList[i]) {
					int label = (int)inst.classValue();
					if(label == 0) {
						minority1.add(inst);
					}else {
						majority1.add(inst);
					}
				}
				System.out.println("多数类样本"+minority1.size()+"少数类样本"+majority1.size());
			}
			Vote ensemble = new Vote();
			SelectedTag tag = new SelectedTag(Vote.AVERAGE_RULE, Vote.TAGS_RULES);
			ensemble.setCombinationRule(tag);
			ensemble.setClassifiers(classifiers);
			Evaluation evaluation = new Evaluation(trainInstances);
			predictionLabel = (int) evaluation.evaluateModelOnce(ensemble, testInstance);
			int size = classLabel.size();
			if (predictionLabel == 0) {// 如果预测为正类，那么将classLabel中的负类类标移除
				for (int i = size - 1; i >= (size + 1) / 2; --i) {
					classLabel.remove(i);
				}
			} else {// 如果预测为负类，那么将classLabel中的正类类标移除
				for (int i = 0; i < (classLabel.size() + 1) / 2; ++i) {
					classLabel.remove(0);
				}
			}
		} while (classLabel.size() > 1);
		// 当classLabel只剩下一个类标时，便是最终的预测类标
		return classLabel.get(0);
	}

		
	public void mixedSamplingByImbalanceRatio(Instances instances) throws Exception {
		List<Instance> minority = new ArrayList<>();
		List<Instance> majority = new ArrayList<>();
		for(Instance inst: instances) {
			int label = (int)inst.classValue();
			if(label == 0) {
				minority.add(inst);
			}else {
				majority.add(inst);
			}
		}
		//统计两个类别之间的个数
		int minoritySize = minority.size();
		int majoritySize = majority.size();
		double IR;
		//如果少数样本集合的个数少于多数样本集合，那么将两个集合进行交换
		if(minoritySize > majoritySize) {
			List<Instance> temp = new ArrayList<>(minority);
			minority.clear();
			for(Instance inst : majority) {
				minority.add(inst);
			}
			for(Instance inst: temp) {
				majority.add(inst);
			}
			temp = null; //有助于垃圾回收
			minoritySize = minority.size();
			majoritySize = majority.size();
			System.out.println("多数类样本个数为："+majoritySize);
			System.out.println("少数类样本个数为："+minoritySize);
		}
		IR = majoritySize/minoritySize;
		if(IR >= 1.5) {
			//当非平衡率超过1.5时，进行混合采样
			classifier =  mixedSamplingByQuantumModel(instances, minority, majority);
		}
	}
	
	public Classifier mixedSamplingByQuantumModel(Instances instances, List<Instance> minority, List<Instance> majority) throws Exception {
		Setting setting = new Setting(10, 200, cls);
		InstancesSet instancesSet = new InstancesSet(instances, setting);
		instancesSet.initializeInstancesSet();
		instancesSet.minority = minority;
		instancesSet.majority = majority;
		QuantumModel quantumModel = new QuantumModel(setting, instancesSet);
		
			quantumModel.run();
			//取出最优
			Individual best = quantumModel.gBestIndividual;
			return best.cls;
		
	}	
	
	public List<List<Instance>> overSamplingByLADBMOTE(Instances instances, List<Instance> minority, List<Instance> majority) {
		List<List<Instance>> output = new ArrayList<List<Instance>>();
		// 当少数类样本的K值少于等于设定的K值，则需要重新调整K值
		int tempK = setting.K;
		if (minority.size() <= tempK) {
			setting.K = minority.size() - 1;
		}
		for(int i = 0; i < setting.K; ++i) {
			List<Instance> list = new ArrayList<>();
			output.add(list);
		}
		
		// 计算需要合成少数类样本的数量
		int[] n = new int[minority.size()];
		int IR = (majority.size()-minority.size())/minority.size();
		for(int i = 0; i < minority.size(); ++i) {
			n[i] = IR;
		}
		int reminder = (majority.size()-minority.size()) - IR*minority.size();
		for(int i = 0; i < reminder; ++i) {
			double rand = Math.random();
			int index =(int) (rand*minority.size());
			n[index] += 1;
		}
		Instances minorityInstances = new Instances(instancesSet.rawInstances);
		minorityInstances.clear();
		for (Instance inst : minority) {
			minorityInstances.add(inst);
		}
		Instances majorityInstances = new Instances(instancesSet.rawInstances);
		majorityInstances.clear();
		for (Instance inst : majority) {
			majorityInstances.add(inst);
		}
		GenerateSample generateSample = new GenerateSample(setting);
		for(int i = 0; i < minority.size(); ++i) {
			generateSample.generateSample(minority.get(i), minorityInstances, majorityInstances, output, n[i]);
		}
		setting.K = tempK;
		return output;
	}
	
	/*
	 * 根据采样的结果，对样本继续进行分类测试
	 * */
	/*
	 * 根据累到的类标，对数据集进行拆分,，构成一个二分类数据集
	 */
	public List<Instance> splitByLabelDistance(Instance testInstance, List<Integer> classOrder) {
		List<Instance> instances = new ArrayList<Instance>();
		// 1. 获得当前的类标序列,分为正类和负类
		List<Integer> positiveLabel = new ArrayList<Integer>();
		List<Integer> negativeLabel = new ArrayList<Integer>();
		int size = classOrder.size();
		for (int i = 0; i < size; ++i) {
			if (i < (size + 1) / 2) {
				positiveLabel.add(classOrder.get(i));
			} else {
				negativeLabel.add(classOrder.get(i));
			}
		}

		// 将positiveLabel类别的样本加入到positive中
		List<Instance> positiveList = new ArrayList<Instance>();
		for (int i = 0; i < positiveLabel.size(); ++i) {
			int label = positiveLabel.get(i);
			List<Instance> instancesLabel = m_trainInstancesByClass.get(label);
			for (Instance inst : instancesLabel) {
				// 将类标设置为0
				Instance tempInstance = (Instance) inst.copy();
				tempInstance.setClassValue(0);
				positiveList.add(tempInstance);
			}
		}
		// 将negativeLabel类别的样本加入到negative中
		List<Instance> negativeList = new ArrayList<Instance>();
		// 对划分的类别进行数据集拆分
		for (int i = 0; i < negativeLabel.size(); ++i) {
			int label = negativeLabel.get(i);
			List<Instance> instancesLabel = m_trainInstancesByClass.get(label);
			for (Instance inst : instancesLabel) {
				// 将类标设置为1
				Instance tempInstance = (Instance) inst.copy();
				tempInstance.setClassValue(1);
				negativeList.add(tempInstance);
			}
		}

		// 将所有样本全部加入到instances中
		instances.addAll(positiveList);
		instances.addAll(negativeList);
		return instances;
	}

	/*
	 * TODO: 通过m_trainInstanceByClass来计算测试样本到每个类比的K近邻 RETURN: 返回该样本的K近邻
	 */
	public List<Instance> calKneighbors(Instance testInstance, int classLabel, int DK) {
		int K = DK;
		List<Instance> neighbors = new ArrayList<Instance>();
		List<Instance> instances = m_trainInstancesByClass.get(classLabel);
		// 计算测试样本test到类别i样本的所有距离
		Map<Double, Instance> distanceMap = new TreeMap<>();
		for (Instance inst : instances) {
			double distance = calDistance(testInstance, inst);
			distanceMap.put(distance, inst);
		}
//		System.out.println("当前样本");
		// 取出前K个近邻
		int cnt = 0;
		for (Entry<Double, Instance> entry : distanceMap.entrySet()) {
//			System.out.println(entry.getValue().toString() +" "+entry.getKey());
			neighbors.add(entry.getValue());
			cnt++;
			if (cnt >= K) {
				break;
			}
		}
		return neighbors;
	}

	/*
	 * TODO: 根据距离对类别进行类别划分
	 */
	public List<Integer> getClassLabelByDistance(Instance testInstance, int DK) {
		List<Integer> classOrder = new ArrayList<>();
		// 计算每个类别的平均距离

		TreeMap<Double, Integer> tm = new TreeMap<>();
		for (int i = 0; i < m_train.get(0).numClasses(); ++i) {
			try {
				double averageDistance = 0d;
				List<Instance> neighbors = calKneighbors(testInstance, i, DK);
				for (int j = 0; j < neighbors.size(); ++j) {
					averageDistance += calDistance(testInstance, neighbors.get(j));
				}
				averageDistance /= neighbors.size();
				while (tm.containsKey(averageDistance)) {
					averageDistance += 0.1;
				}
				tm.put(averageDistance, i);
			} catch (Exception exception) {
				System.out.println("发生错误");
			}
		}

		// 根据tm存放的内容，得到classOrder
		for (Entry<Double, Integer> entry : tm.entrySet()) {
			classOrder.add(entry.getValue());
		}
		return classOrder;
	}

	public Instances normalizeInstances(Instances instances) throws Exception {
		Normalize normalizer = new Normalize();
		normalizer.setInputFormat(instances);
		instances = Filter.useFilter(instances, normalizer);
		return instances;
	}

	/*
	 * TODO: 计算多分类指标Marco-F1 RETURN: 返回Marco-F1
	 */
	public double calMarcoF1() {
		double marcoF1 = 0;
		double marcoP = calMarcoP();
		double marcoR = calMarcoR();
		marcoF1 = (2 * marcoP * marcoR) / (marcoP + marcoR);
		return marcoF1;
	}

	/*
	 * TODO: 计算多分类指标Marco-R RETURN: 返回Marco-R值
	 */
	public double calMarcoP() {
		double marcoP = 0.0d;
		int numClass = normalizeInstancesTest.get(0).numClasses();
		List<Double> pList = new ArrayList<>();
		for (int i = 0; i < numClass; ++i) {
			double p = 0;
			int tp = 0, fp = 0;
			for (int j = 0; j < m_test.size(); ++j) {
				int classLabel = (int) m_test.get(j).classValue();
				if (classLabel == i && flag[j] == i) {
					tp++;
				}
				if (classLabel != i && flag[j] == i) {
					fp++;
				}
			}
			p = tp * 1.0 / (tp + fp);
			pList.add(p);
		}
		for (int i = 0; i < pList.size(); ++i) {
			marcoP += pList.get(i);
		}
		marcoP /= pList.size();
		return marcoP;
	}

	/*
	 * TODO: 计算多分类指标Marco-R RETURN: 返回Marco-R值
	 */
	public double calMarcoR() {
		double marcoR = 0.0d;
		int numClass = normalizeInstancesTest.get(0).numClasses();
		List<Double> rList = new ArrayList<>();
		for (int i = 0; i < numClass; ++i) {
			double r = 0;
			int tp = 0, fn = 0;
			for (int j = 0; j < m_test.size(); ++j) {
				int classLabel = (int) m_test.get(j).classValue();
				if (classLabel == i && flag[j] == i) {
					tp++;
				}
				if (classLabel == i && flag[j] != i) {
					fn++;
				}
			}
			r = tp * 1.0 / (tp + fn);
			rList.add(r);
		}
		for (int i = 0; i < rList.size(); ++i) {
			marcoR += rList.get(i);
		}
		marcoR /= rList.size();
		return marcoR;
	}

	/*
	 * TODO: 计算多分类指标MGMean RETURN: 返回MGean值
	 */
	public double calMultiGMean() {
		double mGmean = 1;
		List<Double> mGeanList = new ArrayList<>();
		int numClass = normalizeInstancesTest.get(0).numClasses();
		for (int i = 0; i < numClass; ++i) {
			double recall = 0;
			int tp = 0, fn = 0;
			for (int j = 0; j < m_test.size(); ++j) {
				int classLabel = (int) m_test.get(j).classValue();
				if (classLabel == i && flag[j] == i) {
					tp++;
				}
				if (classLabel == i && flag[j] != i) {
					fn++;
				}
			}
			recall = 1.0 * tp / (tp + fn);
			mGeanList.add(recall);
		}
		for (int i = 0; i < mGeanList.size(); ++i) {
			mGmean *= mGeanList.get(i);
		}
		mGmean = Math.pow(mGmean, 1.0 / numClass);
		return mGmean;
	}

	/*
	 * TODO: 将m_trainMajorityInstances按照类别进行拆分 RETURN: 返回成员变量m_trainInstancesByClass
	 */
	public void splitTrainByClass() {
		int numOfClass = m_train.get(0).numClasses();
		m_trainInstancesByClass = new ArrayList<>();
		for (int i = 0; i < numOfClass; ++i) {
			List<Instance> temp = new ArrayList<>();
			m_trainInstancesByClass.add(temp);
		}
		for (Instance inst : m_train) {
			int classLabel = (int) inst.classValue();
			// 获得类标为classLabel的List，并将其加入其中
			m_trainInstancesByClass.get(classLabel).add(inst);
		}
	}

	

	/*
	 * TODO: 根据classLabelByInstance得到类标对，训练一个分类器 RETURN
	 */
	public Classifier builderClassifierByBinaryClass(int class1, int class2) throws Exception {
		// 1. 将上述两类样本融合为一个Instance类型的数据集
		Instances trainBinaryInstance = new Instances(m_test);
		trainBinaryInstance.clear();
		for (Instance inst : m_trainInstancesByClass.get(class1)) {
			trainBinaryInstance.add(inst);
		}
		for (Instance inst : m_trainInstancesByClass.get(class2)) {
			trainBinaryInstance.add(inst);
		}
		// 2. 利用trianBinaryInstance创建分类器
		Classifier classifier = chooseClassifier(cls);
		classifier.buildClassifier(trainBinaryInstance);
		return classifier;
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


	public static double calDistance(Instance first, Instance second) {
		double distance = 0;
		for (int i = 0; i < first.numAttributes() - 1; ++i) {
			double diff = first.value(i) - second.value(i);
			distance += diff * diff;
		}
		return Math.sqrt(distance);
	}

	
	
	/*
	 * TODO: 利用Kmeans算法求得聚类中心
	 */
	public void getClusterCenters() throws Exception {
		// 将ArrayList类型转为Instances类型
		List<Instances> instancesOfClass = new ArrayList<>();
		for (int i = 0; i < m_trainInstancesByClass.size(); ++i) {
			Instances temp = new Instances(m_test);
			temp.clear();
			for (Instance inst : m_trainInstancesByClass.get(i)) {
				temp.add(inst);
			}
			instancesOfClass.add(temp);
		}
		// 对每个类进行聚类处理
		for (int i = 0; i < instancesOfClass.size(); ++i) {
			if (instancesOfClass.get(i).size() < 10) {
				// 直接用样本当作中心点
				for (Instance inst : instancesOfClass.get(i)) {
					centerOfClass.get(i).add(inst);
				}
				continue;
			}
			// 得到聚类中心个数
			int numOfCentroids = (int) (instancesOfClass.get(i).size() * 0.3);
			// 对多数类进行聚类
			SimpleKMeans kMeans = new SimpleKMeans();
			// 设置聚类个数为少数类的样本数
			Remove remove = new Remove();
			String[] options = Utils.splitOptions("-R " + (instancesOfClass.get(i).classIndex() + 1));
			remove.setOptions(options);
			remove.setInputFormat(instancesOfClass.get(i));
			Instances clusterdata = Filter.useFilter(instancesOfClass.get(i), remove);

			kMeans.setNumClusters(numOfCentroids);
			kMeans.buildClusterer(clusterdata);
			Instances centers = kMeans.getClusterCentroids();
			centers.insertAttributeAt(instancesOfClass.get(i).attribute(instancesOfClass.get(i).numAttributes() - 1),
					centers.numAttributes());
			centers.setClassIndex(centers.numAttributes() - 1);
			for (Instance inst : centers) {
				inst.setClassValue(instancesOfClass.get(i).get(0).classValue());
				centerOfClass.get(i).add(inst);
			}
		}
	}

	public static boolean isCorrect(Instance instance, int rank, List<Integer> classLabel) {
		boolean flag = false;

		for (int i = 0; i < rank; ++i) {
			try {
				int label = (int) instance.classValue();
				if (label == classLabel.get(i)) {
					flag = true;
					break;
				}
			} catch (Exception e) {
				// TODO: handle exception
				System.out.println();
			}
		}

		return flag;
	}

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		InstanceDao dao = new InstanceDao();
		String[] dataSets = { "automobileMulti", "balanceMulti", "contraceptiveMulti", "dermatologyMulti", "ecoliMulti",
				"glassMulti", "hayesrothMulti", "lymphographyMulti", "newthyroidMulti", "pageblocksMulti",
				"penbasedMulti", "shuttleMulti", "thyroidMulti", "wineMulti", "yeastMulti", "vowelMulti",
				"vehicleMulti", "taeMulti", "segmentMulti" };
		int[] K = {13,7,13,3,5,5,3,5,3,3,3,3,3,5,3,3,3,5,6};
		FileWriter fw = new FileWriter("实验结果/多分类框架实验结果+MOLAD/测试分类框架结果.dat", true);
		for (int set = 10; set < 15; ++set) {
			fw.write(""+dataSets[set]+":");
			Setting.KDistance = K[set];
			double averageAccuacy = 0.0;
			for (int fold = 0; fold <= 0; ++fold) {
	//			System.out.println("当前数据集为" + dataSets[set]);
				String[] trainPath = Dataset.chooseDataset(dataSets[set], 0);
				String[] testPath = Dataset.chooseDataset(dataSets[set], 1);
				Instances rawInstances = dao.loadDataFromFile(trainPath[fold]);
				Instances testInstances = dao.loadDataFromFile(testPath[fold]);
				List<Instance> trainInstances = new ArrayList<>();
				for (int i = 0; i < rawInstances.size(); ++i) {
					trainInstances.add(rawInstances.get(i));
				}
				
				int cnt = 0;
				Setting setting = new Setting(10, 100, Enum_Classifier.C45);
				InstancesSet instancesSet = new InstancesSet(rawInstances, setting);
				for (Instance testInstance : testInstances) {
					ClassifyModel clsModel = new ClassifyModel(trainInstances, testInstances,Enum_Classifier.C45,instancesSet, setting);
				System.out.println("样本的真实类标为" + (int) testInstance.classValue());
					int label = clsModel.classifySingleInstance(testInstance);
				System.out.println("样本的预测类标为" + label);
					if (label == (int) testInstance.classValue()) {
						cnt++;
					System.out.println("分类正确");
					}
				}
				
				averageAccuacy += cnt * 1.0 / testInstances.size();
			}
			System.out.print(String.format("%.3f", averageAccuacy)+" ");
			fw.write(String.format("%.3f", averageAccuacy)+"\n");
			/*
			 * for (int DK = 3; DK < 20; ++DK) { int cnt2 = 0; int cnt3 = 0; int cnt4 = 0;
			 * for (Instance inst : validationInstances) { List<Integer>
			 * classLabelByDistance = clsModel.getClassLabelByDistance(inst, DK);
			 * 
			 * if (isCorrect(inst, 2, classLabelByDistance)) { cnt2++; } if (isCorrect(inst,
			 * 3, classLabelByDistance)) { cnt3++; } if (validationInstances.numClasses() >=
			 * 4) { if (isCorrect(inst, 4, classLabelByDistance)) { cnt4++; } } //
			 * fw.write("\n"); } fw.write(" " + DK + ":"); fw.write("正确率(2)：" + cnt2 * 1.0 /
			 * validationInstances.size()); fw.write("正确率(3)：" + cnt3 * 1.0 /
			 * validationInstances.size()); // fw.write("正确率(4)：" + cnt4 * 1.0 /
			 * validationInstances.size()); fw.write("\n"); }
			 */
			
		}
		 fw.close();

		/*
		 * // clsModel.getClusterCenters(); // clsModel.getClusterCenters(); int cnt2 =
		 * 0; int cnt3 = 0; int cnt4 = 0; FileWriter fw = new
		 * FileWriter("多分类数据集/距离测试结果(移除BUG)/高斯核距离25+"+dataSets[set]+".dat", true); for
		 * (Instance inst : clsModel.m_test) { // List<Integer> labelList =
		 * clsModel.getLabelByDistance(inst, clsModel.m_trainInstancesByClass); //
		 * if(labelList == null) {continue;} fw.write("该测试样本的真实类标为" + inst.classValue()
		 * + "\n基于高斯距离排序："); for (int i = 0; i < labelList.size(); ++i) { fw.write(" " +
		 * labelList.get(i)); } if(isCorrect(inst, 2, labelList)) { cnt2++; }
		 * if(isCorrect(inst, 3, labelList)) { cnt3++; } if(isCorrect(inst, 4,
		 * labelList)) { cnt4++; } fw.write("\n"); }
		 * fw.write("正确率(2)："+cnt2*1.0/clsModel.m_test.size());
		 * fw.write("正确率(3)："+cnt3*1.0/clsModel.m_test.size());
		 * fw.write("正确率(4)："+cnt4*1.0/clsModel.m_test.size()); fw.close();
		 */

	}
}
