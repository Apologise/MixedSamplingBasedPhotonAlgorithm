package 量子混合采样多分类;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.SMO;
import weka.classifiers.lazy.IBk;
import weka.classifiers.trees.J48;
import weka.clusterers.SimpleKMeans;
import weka.core.DistanceFunction;
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
	public int[] flag;	//预测类标数组
	//将m_train按类别拆分
	public List<List<Instance>> m_trainInstancesByClass;
	public List<List<Double>> distanceMatrix;
	public Enum_Classifier cls;
	public List<Instance> clusterCenters;
	public Instances normalizeInstances;
	
	
	/*
	 * trian为已经经过混合采样后的数据集
	 * test为验证集合
	 * */
	public ClassifyModel(List<Instance> train, Instances test, Enum_Classifier classfier) throws Exception {
		m_train = train;
		m_test = test;
		flag = new int[m_test.size()]; 
		cls = classfier;
		/*
		normalizeInstances = new Instances(m_test);
		normalizeInstances.clear();
		for(Instance inst: m_train) {
			normalizeInstances.add(inst);
		}
		normalizeInstances = normalizeInstances(normalizeInstances);
		m_test = normalizeInstances(m_test);
		*/
		splitTrainByClass();
		clusterCenters = new ArrayList<Instance>();
	}
	
	public Instances normalizeInstances(Instances instances) throws Exception {
		Normalize normalizer = new Normalize();
		normalizer.setInputFormat(instances);
		instances = Filter.useFilter(instances, normalizer);
		return instances;
	}
	/*
	 * TODO: 计算多分类指标Marco-F1
	 * RETURN: 返回Marco-F1
	 * */
	public double calMarcoF1() {
		double marcoF1 = 0;
		double marcoP = calMarcoP();
		double marcoR = calMarcoR();
		marcoF1 = (2*marcoP*marcoR)/(marcoP+marcoR);
		return marcoF1;
	}
	/*
	 * TODO: 计算多分类指标Marco-R
	 * RETURN: 返回Marco-R值
	 * */
	public double calMarcoP() {
		double marcoP = 0.0d;
		int numClass = normalizeInstances.get(0).numClasses();
		List<Double> pList = new ArrayList<>();
		for(int i = 0; i < numClass; ++i) {
			double p = 0;
			int tp = 0, fp = 0;
			for(int j = 0; j < m_test.size(); ++j) {
				int classLabel = (int)m_test.get(j).classValue();
				if(classLabel == i && flag[j] == i) {
					tp++;
				}
				if(classLabel != i && flag[j] == i) {
					fp++;
				}
			}
			p = tp*1.0/(tp+fp);
			pList.add(p);
		}
		for(int i = 0; i < pList.size(); ++i) {
			marcoP += pList.get(i);
		}
		marcoP /= pList.size();
		return marcoP;
	}
	
	/*
	 * TODO: 计算多分类指标Marco-R
	 * RETURN: 返回Marco-R值
	 * */
	public double calMarcoR() {
		double marcoR = 0.0d;
		int numClass = normalizeInstances.get(0).numClasses();
		List<Double> rList = new ArrayList<>();
		for(int i = 0; i < numClass; ++i) {
			double r = 0;
			int tp = 0, fn = 0;
			for(int j = 0; j < m_test.size(); ++j) {
				int classLabel = (int)m_test.get(j).classValue();
				if(classLabel == i && flag[j] == i) {
					tp++;
				}
				if(classLabel == i && flag[j] != i) {
					fn++;
				}
			}
			r = tp*1.0/(tp+fn);
			rList.add(r);
		}
		for(int i = 0; i < rList.size(); ++i) {
			marcoR += rList.get(i);
		}
		marcoR /= rList.size();
		return marcoR;
	}
	
	/*
	 * TODO: 计算多分类指标MGMean
	 * RETURN: 返回MGean值
	 * */
	public double calMultiGMean() {
		double mGmean = 1;
		List<Double> mGeanList = new ArrayList<>();
		int numClass = normalizeInstances.get(0).numClasses();
		for(int i = 0; i < numClass; ++i) {
			double recall = 0;
			int tp = 0, fn = 0;
			for(int j = 0; j < m_test.size(); ++j) {
				int classLabel = (int)m_test.get(j).classValue();
				if(classLabel == i && flag[j] == i) {
					tp++;
				}
				if(classLabel == i && flag[j] != i) {
					fn++;
				}
			}
			recall = 1.0*tp/(tp+fn);
			mGeanList.add(recall);
		}
		for(int i = 0; i < mGeanList.size(); ++i) {
			mGmean *= mGeanList.get(i);
		}
		mGmean = Math.pow(mGmean, 1.0/numClass);
		return mGmean;
	}
	
	/*
	 * TODO: 将m_trainMajorityInstances按照类别进行拆分
	 * RETURN: 返回成员变量m_trainInstancesByClass
	 * */
	public void splitTrainByClass() {
			int numOfClass = m_train.get(0).numClasses();
			m_trainInstancesByClass = new ArrayList<>();
			for(int i = 0; i < numOfClass; ++i) {
				List<Instance> temp = new ArrayList<>();
				m_trainInstancesByClass.add(temp);
			}
			for(Instance inst: m_train) {
				int classLabel = (int)inst.classValue();
				//获得类标为classLabel的List，并将其加入其中
				m_trainInstancesByClass.get(classLabel).add(inst);
			}
	}
	
	/*
	 * TODO: 对测试样本进行评估
	 * RETURN: 返回每一个样本的预测类标数组flag
	 * */
	public void evaluateTestInstance() throws Exception {
		Evaluation evaluation = new Evaluation(m_test);
		//先初始化flag数组，全部置为-1
		for(int i = 0; i < flag.length; ++i) {
			flag[i] = -1;
		}
		for(int i = 0; i < m_test.size(); ++i) {
			Instance inst = m_test.get(i);
			//1. 先找到该测试样本所对应的分类器
			List<Integer> classLabel = getLabelByDistance(inst);
				Classifier cls = builderClassifierByBinaryClass(classLabel.get(0), classLabel.get(1));
			//2. 使用该分类器进行预测
				int predictLabel = (int)evaluation.evaluateModelOnce(cls, inst);
				flag[i] = predictLabel;
		}
	}
	/*
	 * TODO: 根据classLabelByInstance得到类标对，训练一个分类器
	 * RETURN
	 * */
	public Classifier builderClassifierByBinaryClass(int class1, int class2) throws Exception {
		//1. 将上述两类样本融合为一个Instance类型的数据集
		Instances trainBinaryInstance = new Instances(m_test);
		trainBinaryInstance.clear();
		for(Instance inst: m_trainInstancesByClass.get(class1)) {
			trainBinaryInstance.add(inst);
		}
		for(Instance inst: m_trainInstancesByClass.get(class2)) {
			trainBinaryInstance.add(inst);
		}
		//2. 利用trianBinaryInstance创建分类器
		Classifier classifier = chooseClassifier(cls);
		classifier.buildClassifier(trainBinaryInstance);
		return classifier;
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
		default:
			System.out.println("未找到该算法！！！");
			System.exit(0);
			break;
		}
		return classifier;
	}
	
	/*
	 * TODO: 根据测试样本得到距离最近的两个类
	 * RETURN: 得到距离最近的2个类的标号
	 * */
	public List<Integer> getLabelByDistance(Instance testInstance){
		List<Integer> classLabelByDistance = new ArrayList<Integer>();
		//1. 求出测试样本对每个类的平均距离
		Map<Double, Integer> distanceForEveryClass = new HashMap<Double, Integer>();
		for(int i = 0; i < m_trainInstancesByClass.size(); ++i) {
			Double distance = 0d;
			for(Instance inst: m_trainInstancesByClass.get(i)) {
				distance += calDistance(testInstance, inst);
			}
			distance /= m_trainInstancesByClass.get(i).size();
			distanceForEveryClass.put(distance, i);
		}
		//2. 对距离排序，得到最近的两个下标
		List<Entry<Double, Integer>> distanceEntries = new ArrayList<>(distanceForEveryClass.entrySet());
		//2.1 对distanceEntries进行排序
		Collections.sort(distanceEntries, new Comparator<Entry<Double, Integer>>() {
			@Override
			public int compare(Entry<Double, Integer> o1, Entry<Double, Integer> o2) {
				// TODO Auto-generated method stub
				if(o1.getKey() > o1.getKey()) {
					return 1;
				}else if(o1.getKey() < o1.getKey()) {
					return -1;
				}else {
				return 0;
				}
			}
		});
		//3. 根据距离大小，由小到大将类标放入classLabel中
		for(Entry<Double, Integer> entry: distanceEntries) {
			classLabelByDistance.add(entry.getValue());
		}
		return classLabelByDistance;
	}
	
	
	public static double calDistance(Instance first, Instance second) {
		double distance = 0;
		for(int i = 0; i < first.numAttributes()-1; ++i) {
			double diff = first.value(i) - second.value(i);
			distance += diff*diff;
		}
		return Math.sqrt(distance);
	}
	
	/*
	 * TODO: 利用Kmeans算法求得聚类中心
	 * */
	public void  getClusterCenters() throws Exception{
		//将ArrayList类型转为Instances类型
		List<Instances> instancesOfClass = new ArrayList<>();
		for(int i = 0; i < m_trainInstancesByClass.size(); ++i) {
			Instances temp = new Instances(m_test);
			temp.clear();
			for(Instance inst: m_trainInstancesByClass.get(i)) {
				temp.add(inst);
			}
			instancesOfClass.add(temp);
		}
		//对每个类进行聚类处理
		for(int i = 0; i < instancesOfClass.size(); ++i) {
			//得到聚类中心个数
			int numOfCentroids = (int)(instancesOfClass.get(i).size()*0.1);
			//对多数类进行聚类
			SimpleKMeans kMeans = new SimpleKMeans();
			//设置聚类个数为少数类的样本数
			Remove remove = new Remove();
			String[] options = Utils.splitOptions("-R "+instancesOfClass.get(i).numAttributes());
			remove.setOptions(options);
			remove.setInputFormat(instancesOfClass.get(i));
			Instances clusterdata = Filter.useFilter(instancesOfClass.get(i), remove);
			 
			kMeans.setNumClusters(numOfCentroids);
			kMeans.buildClusterer(clusterdata);
			Instances centers = kMeans.getClusterCentroids();
			centers.insertAttributeAt(instancesOfClass.get(i).attribute(instancesOfClass.get(i).numAttributes()-1),centers.numAttributes());
			centers.setClassIndex(centers.numAttributes()-1);
			//将产生的中心点作为多数类
		}
	}
	
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		InstanceDao dao = new InstanceDao();
		Instances rawInstances = dao.loadDataFromFile("多分类数据集/shuttle-5-fold/shuttle-5-1tra.arff");
		Instances validationInstances = dao.loadDataFromFile("多分类数据集/shuttle-5-fold/shuttle-5-1tst.arff");
		List<Instance> trainInstances = new ArrayList<>();
		for(int i = 0; i < rawInstances.size(); ++i) {
			trainInstances.add(rawInstances.get(i));
		}
		ClassifyModel clsModel = new ClassifyModel(trainInstances, 
				validationInstances, Enum_Classifier.C45);
		clsModel.getClusterCenters();
	}
}


