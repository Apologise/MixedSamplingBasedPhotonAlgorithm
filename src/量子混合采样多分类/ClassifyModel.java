package ���ӻ�ϲ��������;

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
 * INFO:   ������ܣ��������ݵĶ���ࣨ���������ࣩ�����շ���һ���ض��Ķ����ָ�꣨MAUC, MGmean��
 * */


public class ClassifyModel {
	public List<Instance> m_train;
	public Instances m_test;
	public int[] flag;	//Ԥ���������
	//��m_train�������
	public List<List<Instance>> m_trainInstancesByClass;
	public List<List<Double>> distanceMatrix;
	public Enum_Classifier cls;
	public List<Instance> clusterCenters;
	public Instances normalizeInstances;
	
	
	/*
	 * trianΪ�Ѿ�������ϲ���������ݼ�
	 * testΪ��֤����
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
	 * TODO: ��������ָ��Marco-F1
	 * RETURN: ����Marco-F1
	 * */
	public double calMarcoF1() {
		double marcoF1 = 0;
		double marcoP = calMarcoP();
		double marcoR = calMarcoR();
		marcoF1 = (2*marcoP*marcoR)/(marcoP+marcoR);
		return marcoF1;
	}
	/*
	 * TODO: ��������ָ��Marco-R
	 * RETURN: ����Marco-Rֵ
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
	 * TODO: ��������ָ��Marco-R
	 * RETURN: ����Marco-Rֵ
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
	 * TODO: ��������ָ��MGMean
	 * RETURN: ����MGeanֵ
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
	 * TODO: ��m_trainMajorityInstances���������в��
	 * RETURN: ���س�Ա����m_trainInstancesByClass
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
				//������ΪclassLabel��List���������������
				m_trainInstancesByClass.get(classLabel).add(inst);
			}
	}
	
	/*
	 * TODO: �Բ���������������
	 * RETURN: ����ÿһ��������Ԥ���������flag
	 * */
	public void evaluateTestInstance() throws Exception {
		Evaluation evaluation = new Evaluation(m_test);
		//�ȳ�ʼ��flag���飬ȫ����Ϊ-1
		for(int i = 0; i < flag.length; ++i) {
			flag[i] = -1;
		}
		for(int i = 0; i < m_test.size(); ++i) {
			Instance inst = m_test.get(i);
			//1. ���ҵ��ò�����������Ӧ�ķ�����
			List<Integer> classLabel = getLabelByDistance(inst);
				Classifier cls = builderClassifierByBinaryClass(classLabel.get(0), classLabel.get(1));
			//2. ʹ�ø÷���������Ԥ��
				int predictLabel = (int)evaluation.evaluateModelOnce(cls, inst);
				flag[i] = predictLabel;
		}
	}
	/*
	 * TODO: ����classLabelByInstance�õ����ԣ�ѵ��һ��������
	 * RETURN
	 * */
	public Classifier builderClassifierByBinaryClass(int class1, int class2) throws Exception {
		//1. ���������������ں�Ϊһ��Instance���͵����ݼ�
		Instances trainBinaryInstance = new Instances(m_test);
		trainBinaryInstance.clear();
		for(Instance inst: m_trainInstancesByClass.get(class1)) {
			trainBinaryInstance.add(inst);
		}
		for(Instance inst: m_trainInstancesByClass.get(class2)) {
			trainBinaryInstance.add(inst);
		}
		//2. ����trianBinaryInstance����������
		Classifier classifier = chooseClassifier(cls);
		classifier.buildClassifier(trainBinaryInstance);
		return classifier;
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
		default:
			System.out.println("δ�ҵ����㷨������");
			System.exit(0);
			break;
		}
		return classifier;
	}
	
	/*
	 * TODO: ���ݲ��������õ����������������
	 * RETURN: �õ����������2����ı��
	 * */
	public List<Integer> getLabelByDistance(Instance testInstance){
		List<Integer> classLabelByDistance = new ArrayList<Integer>();
		//1. �������������ÿ�����ƽ������
		Map<Double, Integer> distanceForEveryClass = new HashMap<Double, Integer>();
		for(int i = 0; i < m_trainInstancesByClass.size(); ++i) {
			Double distance = 0d;
			for(Instance inst: m_trainInstancesByClass.get(i)) {
				distance += calDistance(testInstance, inst);
			}
			distance /= m_trainInstancesByClass.get(i).size();
			distanceForEveryClass.put(distance, i);
		}
		//2. �Ծ������򣬵õ�����������±�
		List<Entry<Double, Integer>> distanceEntries = new ArrayList<>(distanceForEveryClass.entrySet());
		//2.1 ��distanceEntries��������
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
		//3. ���ݾ����С����С����������classLabel��
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
	 * TODO: ����Kmeans�㷨��þ�������
	 * */
	public void  getClusterCenters() throws Exception{
		//��ArrayList����תΪInstances����
		List<Instances> instancesOfClass = new ArrayList<>();
		for(int i = 0; i < m_trainInstancesByClass.size(); ++i) {
			Instances temp = new Instances(m_test);
			temp.clear();
			for(Instance inst: m_trainInstancesByClass.get(i)) {
				temp.add(inst);
			}
			instancesOfClass.add(temp);
		}
		//��ÿ������о��ദ��
		for(int i = 0; i < instancesOfClass.size(); ++i) {
			//�õ��������ĸ���
			int numOfCentroids = (int)(instancesOfClass.get(i).size()*0.1);
			//�Զ�������о���
			SimpleKMeans kMeans = new SimpleKMeans();
			//���þ������Ϊ�������������
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
			//�����������ĵ���Ϊ������
		}
	}
	
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		InstanceDao dao = new InstanceDao();
		Instances rawInstances = dao.loadDataFromFile("��������ݼ�/shuttle-5-fold/shuttle-5-1tra.arff");
		Instances validationInstances = dao.loadDataFromFile("��������ݼ�/shuttle-5-fold/shuttle-5-1tst.arff");
		List<Instance> trainInstances = new ArrayList<>();
		for(int i = 0; i < rawInstances.size(); ++i) {
			trainInstances.add(rawInstances.get(i));
		}
		ClassifyModel clsModel = new ClassifyModel(trainInstances, 
				validationInstances, Enum_Classifier.C45);
		clsModel.getClusterCenters();
	}
}


