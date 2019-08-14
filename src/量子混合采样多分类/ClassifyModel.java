package ���ӻ�ϲ��������;

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
 * INFO:   ������ܣ��������ݵĶ���ࣨ���������ࣩ�����շ���һ���ض��Ķ����ָ�꣨MAUC, MGmean��
 * */

public class ClassifyModel {
	public List<Instance> m_train;
	public Instances m_test;
	public int[] flag; // Ԥ���������
	// ��m_train�������
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
	 * trianΪ�Ѿ�������ϲ���������ݼ� testΪ��֤����
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
	 * TODO: ͨ��m_trainInstanceByClass���������������ÿ����ȵ�K���� RETURN: ���ظ�������K����
	 */
	public List<Instance> calKneighbors(Instance testInstance, int classLabel, int DK) {
		int K = DK;
		List<Instance> neighbors = new ArrayList<Instance>();
		List<Instance> instances = m_trainInstancesByClass.get(classLabel);
		// �����������test�����i���������о���
		Map<Double, Instance> distanceMap = new TreeMap<>();
		for (Instance inst : instances) {
			double distance = calDistance(testInstance, inst);
			distanceMap.put(distance, inst);
		}
//		System.out.println("��ǰ����");
		// ȡ��ǰK������
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
	 * TODO: ���ݾ������������𻮷�
	 */
	public List<Integer> getClassLabelByDistance(Instance testInstance, int DK) {
		List<Integer> classOrder = new ArrayList<>();
		// ����ÿ������ƽ������

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
		// ����tm��ŵ����ݣ��õ�classOrder
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
	 * TODO: ��������ָ��Marco-F1 RETURN: ����Marco-F1
	 */
	public double calMarcoF1() {
		double marcoF1 = 0;
		double marcoP = calMarcoP();
		double marcoR = calMarcoR();
		marcoF1 = (2 * marcoP * marcoR) / (marcoP + marcoR);
		return marcoF1;
	}

	/*
	 * TODO: ��������ָ��Marco-R RETURN: ����Marco-Rֵ
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
	 * TODO: ��������ָ��Marco-R RETURN: ����Marco-Rֵ
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
	 * TODO: ��������ָ��MGMean RETURN: ����MGeanֵ
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
	 * TODO: ��m_trainMajorityInstances���������в�� RETURN: ���س�Ա����m_trainInstancesByClass
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
			// ������ΪclassLabel��List���������������
			m_trainInstancesByClass.get(classLabel).add(inst);
		}
	}

	/*
	 * TODO: �Բ��������������� RETURN: ����ÿһ��������Ԥ���������flag
	 */
	public void evaluateTestInstance() throws Exception {
		Evaluation evaluation = new Evaluation(m_test);
		// �ȳ�ʼ��flag���飬ȫ����Ϊ-1
		for (int i = 0; i < flag.length; ++i) {
			flag[i] = -1;
		}
		for (int i = 0; i < m_test.size(); ++i) {
			Instance inst = m_test.get(i);
			// 1. ���ҵ��ò�����������Ӧ�ķ�����
			List<Integer> classLabel = getLabelByDistance(inst, m_trainInstancesByClass);
			// Classifier cls = builderClassifierByBinaryClass(((int)classLabel.get(0)),
			// ((int)classLabel.get(1)));
			// 2. ʹ�ø÷���������Ԥ��
			// int predictLabel = (int)evaluation.evaluateModelOnce(cls, inst);
			// flag[i] = predictLabel;
		}
	}

	/*
	 * TODO: ����classLabelByInstance�õ����ԣ�ѵ��һ�������� RETURN
	 */
	public Classifier builderClassifierByBinaryClass(int class1, int class2) throws Exception {
		// 1. ���������������ں�Ϊһ��Instance���͵����ݼ�
		Instances trainBinaryInstance = new Instances(m_test);
		trainBinaryInstance.clear();
		for (Instance inst : m_trainInstancesByClass.get(class1)) {
			trainBinaryInstance.add(inst);
		}
		for (Instance inst : m_trainInstancesByClass.get(class2)) {
			trainBinaryInstance.add(inst);
		}
		// 2. ����trianBinaryInstance����������
		Classifier classifier = chooseClassifier(cls);
		classifier.buildClassifier(trainBinaryInstance);
		return classifier;
	}

	/*
	 * TODO:ͨ������ѡ����������� RETURN: ͨ����������ѡ��ĳһ�ַ�������������Ҫ����calFitness�����н����޸�
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
			System.out.println("δ�ҵ����㷨������");
			System.exit(0);
			break;
		}
		return classifier;
	}

	/*
	 * TODO: ���ݲ��������õ���������������� RETURN: �õ����������2����ı��
	 */
	public List<Integer> getLabelByDistance(Instance testInstance, List<List<Instance>> samples) {
		List<Integer> classLabelByDistance = new ArrayList<Integer>();
		// 1. �������������ÿ�����ƽ������
		Map<Integer, Double> distanceForEveryClass = new HashMap<Integer, Double>();
		for (int i = 0; i < samples.size(); ++i) {
			Double distance = 0d;
			for (Instance inst : samples.get(i)) {
				distance += calDistanceByGaussianKernel(testInstance, inst, 25);
			}
			if (samples.get(i).size() == 0) {
				System.out.println("������ѵ������û��");
				distance = Double.MAX_VALUE;
				distanceForEveryClass.put(i, distance);
				continue;
			}
			distance /= samples.get(i).size();
			distanceForEveryClass.put((int) samples.get(i).get(0).classValue(), distance);

		}

		// 2. �Ծ������򣬵õ�����������±�
		List<Entry<Integer, Double>> distanceEntries = new ArrayList<>(distanceForEveryClass.entrySet());
		// 2.1 ��distanceEntries��������
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
		// 3. ���ݾ����С����С����������classLabel��
		for (Entry<Integer, Double> entry : distanceEntries) {
			classLabelByDistance.add(entry.getKey());

		}
		System.out.println("����������ʵ���Ϊ" + testInstance.classValue());
		System.out.println("����������������Ϊ" + classLabelByDistance.toString());
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
	 * TODO: ����Kmeans�㷨��þ�������
	 */
	public void getClusterCenters() throws Exception {
		// ��ArrayList����תΪInstances����
		List<Instances> instancesOfClass = new ArrayList<>();
		for (int i = 0; i < m_trainInstancesByClass.size(); ++i) {
			Instances temp = new Instances(m_test);
			temp.clear();
			for (Instance inst : m_trainInstancesByClass.get(i)) {
				temp.add(inst);
			}
			instancesOfClass.add(temp);
		}
		// ��ÿ������о��ദ��
		for (int i = 0; i < instancesOfClass.size(); ++i) {
			if (instancesOfClass.get(i).size() < 10) {
				// ֱ���������������ĵ�
				for (Instance inst : instancesOfClass.get(i)) {
					centerOfClass.get(i).add(inst);
				}
				continue;
			}
			// �õ��������ĸ���
			int numOfCentroids = (int) (instancesOfClass.get(i).size() * 0.3);
			// �Զ�������о���
			SimpleKMeans kMeans = new SimpleKMeans();
			// ���þ������Ϊ�������������
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
			System.out.println("��ǰ���ݼ�Ϊ" + dataSets[set]);
			String[] trainPath = Dataset.chooseDataset(dataSets[set], 0);
			String[] testPath = Dataset.chooseDataset(dataSets[set], 1);
			Instances rawInstances = dao.loadDataFromFile(trainPath[0]);
			Instances validationInstances = dao.loadDataFromFile(testPath[0]);

			List<Instance> trainInstances = new ArrayList<>();
			for (int i = 0; i < rawInstances.size(); ++i) {
				trainInstances.add(rawInstances.get(i));
			}
			FileWriter fw = new FileWriter("��������ݼ�/KNN���ھ���/" + "K" + dataSets[set] + ".dat", true);

			ClassifyModel clsModel = new ClassifyModel(trainInstances, validationInstances, Enum_Classifier.C45);
			for (int DK = 3; DK < 20; ++DK) {
				int cnt2 = 0;
				int cnt3 = 0;
				int cnt4 = 0;
				for (Instance inst : validationInstances) {
					List<Integer> classLabelByDistance = clsModel.getClassLabelByDistance(inst, DK);
					// System.out.println("����������ʵ���Ϊ��" + inst.classValue());
					// System.out.println(classLabelByDistance.toString());
					// fw.write("�ò�����������ʵ���Ϊ" + inst.classValue() + "\n");
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
				fw.write("��ȷ��(2)��" + cnt2 * 1.0 / validationInstances.size());
				fw.write("��ȷ��(3)��" + cnt3 * 1.0 / validationInstances.size());
				// fw.write("��ȷ��(4)��" + cnt4 * 1.0 / validationInstances.size());
				fw.write("\n");
			}
			fw.close();
		}

		/*
		 * // clsModel.getClusterCenters(); // clsModel.getClusterCenters(); int cnt2 =
		 * 0; int cnt3 = 0; int cnt4 = 0; FileWriter fw = new
		 * FileWriter("��������ݼ�/������Խ��(�Ƴ�BUG)/��˹�˾���25+"+dataSets[set]+".dat", true); for
		 * (Instance inst : clsModel.m_test) { // List<Integer> labelList =
		 * clsModel.getLabelByDistance(inst, clsModel.m_trainInstancesByClass); //
		 * if(labelList == null) {continue;} fw.write("�ò�����������ʵ���Ϊ" + inst.classValue()
		 * + "\n���ڸ�˹��������"); for (int i = 0; i < labelList.size(); ++i) { fw.write(" " +
		 * labelList.get(i)); } if(isCorrect(inst, 2, labelList)) { cnt2++; }
		 * if(isCorrect(inst, 3, labelList)) { cnt3++; } if(isCorrect(inst, 4,
		 * labelList)) { cnt4++; } fw.write("\n"); }
		 * fw.write("��ȷ��(2)��"+cnt2*1.0/clsModel.m_test.size());
		 * fw.write("��ȷ��(3)��"+cnt3*1.0/clsModel.m_test.size());
		 * fw.write("��ȷ��(4)��"+cnt4*1.0/clsModel.m_test.size()); fw.close();
		 */

	}
}
