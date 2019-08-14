package 量子混合采样多分类;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.SMO;
import weka.classifiers.lazy.IBk;
import weka.classifiers.trees.J48;
import weka.clusterers.SimpleKMeans;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Utils;
import weka.filters.Filter;
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

	/*
	 * trian为已经经过混合采样后的数据集 test为验证集合
	 */
	public ClassifyModel(List<Instance> train, Instances test, Enum_Classifier classfier) throws Exception {
		m_train = train;
		m_test = test;
		flag = new int[m_test.size()];
		cls = classfier;
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
				System.out.println();
			}
		}
		if(tm.size() == 1) {
			System.out.println();
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
	 * TODO: 对测试样本进行评估 RETURN: 返回每一个样本的预测类标数组flag
	 */
	public void evaluateTestInstance() throws Exception {
		Evaluation evaluation = new Evaluation(m_test);
		// 先初始化flag数组，全部置为-1
		for (int i = 0; i < flag.length; ++i) {
			flag[i] = -1;
		}
		for (int i = 0; i < m_test.size(); ++i) {
			Instance inst = m_test.get(i);
			// 1. 先找到该测试样本所对应的分类器
			List<Integer> classLabel = getLabelByDistance(inst, m_trainInstancesByClass);
			// Classifier cls = builderClassifierByBinaryClass(((int)classLabel.get(0)),
			// ((int)classLabel.get(1)));
			// 2. 使用该分类器进行预测
			// int predictLabel = (int)evaluation.evaluateModelOnce(cls, inst);
			// flag[i] = predictLabel;
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

	/*
	 * TODO: 根据测试样本得到距离最近的两个类 RETURN: 得到距离最近的2个类的标号
	 */
	public List<Integer> getLabelByDistance(Instance testInstance, List<List<Instance>> samples) {
		List<Integer> classLabelByDistance = new ArrayList<Integer>();
		// 1. 求出测试样本对每个类的平均距离
		Map<Integer, Double> distanceForEveryClass = new HashMap<Integer, Double>();
		for (int i = 0; i < samples.size(); ++i) {
			Double distance = 0d;
			for (Instance inst : samples.get(i)) {
				distance += calDistanceByGaussianKernel(testInstance, inst, 25);
			}
			if (samples.get(i).size() == 0) {
				System.out.println("该类别的训练样本没有");
				distance = Double.MAX_VALUE;
				distanceForEveryClass.put(i, distance);
				continue;
			}
			distance /= samples.get(i).size();
			distanceForEveryClass.put((int) samples.get(i).get(0).classValue(), distance);

		}

		// 2. 对距离排序，得到最近的两个下标
		List<Entry<Integer, Double>> distanceEntries = new ArrayList<>(distanceForEveryClass.entrySet());
		// 2.1 对distanceEntries进行排序
		Collections.sort(distanceEntries, new Comparator<Entry<Integer, Double>>() {
			@Override
			public int compare(Entry<Integer, Double> o1, Entry<Integer, Double> o2) {
				// TODO Auto-generated method stub
				if (o1.getValue() > o2.getValue()) {
					return 1;
				} else if (o1.getValue() < o2.getValue()) {
					return -1;
				} else {
					return 0;
				}
			}
		});
		// 3. 根据距离大小，由小到大将类标放入classLabel中
		for (Entry<Integer, Double> entry : distanceEntries) {
			classLabelByDistance.add(entry.getKey());

		}
		System.out.println("该样本的真实类标为" + testInstance.classValue());
		System.out.println("距离最近的类标排序为" + classLabelByDistance.toString());
		if (classLabelByDistance.size() == 1) {
			System.out.println();
		}
		return classLabelByDistance;
	}

	public static double calDistance(Instance first, Instance second) {
		double distance = 0;
		for (int i = 0; i < first.numAttributes() - 1; ++i) {
			double diff = first.value(i) - second.value(i);
			distance += diff * diff;
		}
		return Math.sqrt(distance);
	}

	public double calDistanceMahaton(Instance first, Instance second) {
		double distance = 0;
		for (int i = 0; i < first.numAttributes() - 1; ++i) {
			double diff = first.value(i) - second.value(i);
			distance += Math.abs(diff);
		}
		return distance;
	}

	public double calDistanceMinkowski(Instance first, Instance second, int p) {
		double distance = 0;
		for (int i = 0; i < first.numAttributes() - 1; ++i) {
			double diff = first.value(i) - second.value(i);
			diff = Math.abs(diff);
			distance += Math.pow(diff, p);
		}
		return Math.pow(distance, 1.0 / p);
	}

	public double calDistanceByCos(Instance first, Instance second) {
		double distance = 0;
		for (int i = 0; i < first.numAttributes() - 1; ++i) {
			distance += first.value(i) * second.value(i);
		}
		double denominator = 0, denominatorFirst = 0, denominatorSecond = 0;
		for (int i = 0; i < first.numAttributes() - 1; ++i) {
			denominatorFirst += first.value(i) * first.value(i);
			denominatorSecond += second.value(i) * second.value(i);
		}
		denominator = Math.sqrt(denominatorFirst) * Math.sqrt(denominatorSecond);
		distance = distance / denominator;
		return distance;
	}

	public double GaussianFunction(Instance first, Instance second, double sigma) {
		double ans = 0;
		for (int i = 0; i < first.numAttributes() - 1; ++i) {
			double temp = first.value(i) - second.value(i);
			ans += temp * temp;
		}
		ans = Math.exp(-ans / 2 * sigma * sigma);
		return ans;
	}

	public double calDistanceByGaussianKernel(Instance first, Instance second, double sigma) {
		double distance = 0;
		distance = 2 - 2 * GaussianFunction(first, second, sigma);
		return distance;
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
			}
			catch (Exception e) {
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
		for (int set = 0; set < 2; ++set) {
			System.out.println("当前数据集为" + dataSets[set]);
			String[] trainPath = Dataset.chooseDataset(dataSets[set], 0);
			String[] testPath = Dataset.chooseDataset(dataSets[set], 1);
			Instances rawInstances = dao.loadDataFromFile(trainPath[0]);
			Instances validationInstances = dao.loadDataFromFile(testPath[0]);

			List<Instance> trainInstances = new ArrayList<>();
			for (int i = 0; i < rawInstances.size(); ++i) {
				trainInstances.add(rawInstances.get(i));
			}
			FileWriter fw = new FileWriter("多分类数据集/KNN近邻距离/" + "K" + dataSets[set] + ".dat", true);

			ClassifyModel clsModel = new ClassifyModel(trainInstances, validationInstances, Enum_Classifier.C45);
			for (int DK = 3; DK < 20; ++DK) {
				int cnt2 = 0;
				int cnt3 = 0;
				int cnt4 = 0;
				for (Instance inst : validationInstances) {
					List<Integer> classLabelByDistance = clsModel.getClassLabelByDistance(inst, DK);
					// System.out.println("该样本的真实类标为：" + inst.classValue());
					// System.out.println(classLabelByDistance.toString());
					// fw.write("该测试样本的真实类标为" + inst.classValue() + "\n");
					/*
					 * for (int i = 0; i < classLabelByDistance.size(); ++i) { fw.write(" " +
					 * classLabelByDistance.get(i)); }
					 */
					if (isCorrect(inst, 2, classLabelByDistance)) {
						cnt2++;
					}
					if (isCorrect(inst, 3, classLabelByDistance)) {
						cnt3++;
					}
					if (validationInstances.numClasses() >= 4) {
						if (isCorrect(inst, 4, classLabelByDistance)) {
							cnt4++;
						}
					}
					// fw.write("\n");
				}
				fw.write(" " + DK + ":");
				fw.write("正确率(2)：" + cnt2 * 1.0 / validationInstances.size());
				fw.write("正确率(3)：" + cnt3 * 1.0 / validationInstances.size());
				// fw.write("正确率(4)：" + cnt4 * 1.0 / validationInstances.size());
				fw.write("\n");
			}
			fw.close();
		}

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
