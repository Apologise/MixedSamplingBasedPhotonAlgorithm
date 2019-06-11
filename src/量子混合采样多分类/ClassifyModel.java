package 量子混合采样多分类;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.omg.CORBA.TRANSACTION_MODE;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.SMO;
import weka.classifiers.lazy.IBk;
import weka.classifiers.trees.J48;
import weka.core.Instance;
import weka.core.Instances;

/*
 * Author: apolo
 * Time:   2019.5.22
 * INFO:   多分类框架，用于分类，最终返回一个特定的多分类指标（MAUC, MGmean）
 * */
public class ClassifyModel {
	public List<Instance> m_train;
	public Instances m_test;
	public int[] flag;
	//将m_train和m_test均按类别拆分
	public List<List<Instance>> m_trainInstancesByClass;
	public List<List<Double>> distanceMatrix;
	public Enum_Classifier cls;
	
	
	/*
	 * trian为已经经过混合采样后的数据集
	 * test为验证集合
	 * */
	public ClassifyModel(List<Instance> train, Instances test, Enum_Classifier classfier) {
		m_train = train;
		m_test = test;
		flag = new int[m_test.size()]; 
		splitTrainByClass();
		cls = classfier;
	}
	
	/*
	 * TODO: 计算多分类指标MAUC
	 * RETURN: 返回MAUC值
	 * */
	public double calMAUC() {
		double mAuc = 0.0d;
		return mAuc;
	}
	
	
	/*
	 * TODO: 计算多分类指标MGMean
	 * RETURN: 返回MGean值
	 * */
	public double calMultiGMean() {
		double mGmean = 1;
		List<Double> mGeanList = new ArrayList<>();
		int numClass = m_train.get(0).numClasses();
		for(int i = 0; i < numClass; ++i) {
			double recall = 0;
			int tp = 0, fn = 0;
			for(int j = 0; j < m_test.size(); ++j) {
				int classLabel = (int)m_test.get(j).classValue();
				if(classLabel == i && flag[j] == 1) {
					tp++;
				}
				if(classLabel == i && flag[j] != 1) {
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
	 * */
	public void evaluateTestInstance(Instances testInstances) throws Exception {
		Evaluation evaluation = new Evaluation(m_test);
		for(int i = 0; i < testInstances.size(); ++i) {
			Instance inst = testInstances.get(i);
			//1. 先找到该测试样本所对应的分类器
			List<Integer> classLabel = getLabelByDistance(inst);
			Classifier cls = builderClassifierByBinaryClass(classLabel.get(0), classLabel.get(1));
			//2. 使用该分类器进行预测
			int predictLabel = (int)evaluation.evaluateModelOnce(cls, inst);
			System.out.println(predictLabel);
			if(predictLabel == (int)inst.classValue()) {
				flag[i] = 1;
			}else {
				flag[i] = 0;
			}
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
	
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		InstanceDao dao = new InstanceDao();
		Instances rawInstances = dao.loadDataFromFile("dataset/wine.arff");
		Instances validationInstances = new Instances(rawInstances);
		validationInstances.clear();
		for(int i = rawInstances.size() -1; i >=0; i--) {
			if(i % 5 != 0) continue;
			Instance inst = rawInstances.get(i);
			rawInstances.remove(i);
			validationInstances.add(inst);
		}
		List<Instance> trainInstances = new ArrayList<>();
		for(int i = 0; i < rawInstances.size(); ++i) {
			trainInstances.add(rawInstances.get(i));
		}
		ClassifyModel clsModel = new ClassifyModel(trainInstances, 
				validationInstances, Enum_Classifier.C45);
		clsModel.evaluateTestInstance(validationInstances);
		double result = clsModel.calMGMean();
		System.out.println(result);
	}

}
