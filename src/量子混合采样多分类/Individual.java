package ���ӻ�ϲ��������;

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
	public List<Instance> handledInstances; // ����Ƿ�����͹����������������
	public int[] flag;
	public Phase[] phase;
	public double fitness; // �������Ӧ��
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
	 * TODO:��ʼ�����壬�Ը����е����ö������ʵ��������ֹ����null����
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
		// ��ʼ��allHanledInstance;
		allHandledInstances = new ArrayList<List<Instance>>();
		for (int i = 0; i < setting.K; ++i) {
			List<Instance> list = new ArrayList<Instance>();
			allHandledInstances.add(list);
		}
	}

	/*
	 * TODO:��λ��ת RETURN���ı�������λֵ
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
	 * TODO: ����������Ӧ�� RETURN: �޸ĳ�Ա����fitness
	 */
	public void calFitness(Enum_Classifier cls) throws Exception {
		// ʹ�ü��ɹ���������
		Classifier[] classifier = new Classifier[setting.K];
		for (int i = 0; i < setting.K; ++i) {
			Instances newInstances = new Instances(instancesSet.rawInstances);
			newInstances.clear();
			// ��handledInstancesȫ�����뵽newInstances��
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
		// ����
		Evaluation eval = new Evaluation(instancesSet.validateInstances);
		eval.evaluateModel(ensemble, instancesSet.validateInstances);
		fitness = eval.areaUnderROC(0);
	}

	/*
	 * TODO: �������յķ���ֵ
	 * 
	 */
	public double calAUC(Enum_Classifier cls) throws Exception {
		// ʹ�ü��ɹ���������
		Classifier[] classifier = new Classifier[setting.K];
		for (int i = 0; i < setting.K; ++i) {
			Instances newInstances = new Instances(instancesSet.rawInstances);
			newInstances.clear();
			// ��handledInstancesȫ�����뵽newInstances��
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
		// ����
		Evaluation eval = new Evaluation(instancesSet.testInstances);
		eval.evaluateModel(ensemble, instancesSet.testInstances);
		return eval.areaUnderROC(0);
		/*
		 * //����handledInstances���з���ʵ�� Instances newInstances = new
		 * Instances(instancesSet.rawInstances); newInstances.clear();
		 * //��handledInstancesȫ�����뵽newInstances�� for(Instance inst: handledInstances) {
		 * newInstances.add(inst); } Classifier classifier = chooseClassifier(cls);
		 * classifier.buildClassifier(newInstances); Evaluation evaluation = new
		 * Evaluation(newInstances); evaluation.evaluateModel(classifier,
		 * instancesSet.testInstances); return evaluation.areaUnderROC(0)
		 */

	}

	public double calAUC1(Enum_Classifier cls) throws Exception {
		// ����handledInstances���з���ʵ��
		double[] result = new double[setting.K];
		for (int i = 0; i < setting.K; ++i) {
			Instances newInstances = new Instances(instancesSet.rawInstances);
			newInstances.clear();
			// ��handledInstancesȫ�����뵽newInstances��
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
		 * newInstances.clear(); //��handledInstancesȫ�����뵽newInstances�� for(Instance
		 * inst: handledInstances) { newInstances.add(inst); } Classifier classifier =
		 * chooseClassifier(cls); classifier.buildClassifier(newInstances); Evaluation
		 * evaluation = new Evaluation(newInstances);
		 * evaluation.evaluateModel(classifier, instancesSet.validateInstances); return
		 * evaluation.areaUnderROC(0);
		 */
	}

	/*
	 * TODO:�Ը�����л�ϲ��� RETURN������һ��������������������
	 */
	public void mixedSampling() {
		// 1. �����handledInstances���Ϻ�handledMajorityInstances����
		handledInstances.clear();
		handledMajorityInstances.clear();
		// 2. ��originInstances�������뵽handledInstances�н��д���
		for (Instance inst : instancesSet.originInstances) {
			handledInstances.add(inst);
		}
		instancesByClass = splitByClass();
		underSampling();
		overSampling();
	}

	/*
	 * TODO:����flag�Ľ�����Զ��������Ƿ���� RETURN: ��Ƿ��������������뵽handledInstances��
	 */
	public void underSampling() {
		// ���ݹ۲�ĸ���״̬����flag[0]�����������Ƴ�
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
			//�жϸ����Ƿ�Ϊ������
			if(!isMinorityClass(inst)) {
				//�����Ϊ������ͽ����Ƴ�
				handledInstances.remove(inst);
			}
		}
	}


	/*
	 * TODO: ���������й����� RETURN: �����ɵ��������뵽handledInstances��
	 */
	public void overSampling() {
		List<List<Instance>> output = new ArrayList<List<Instance>>();
		for (int i = 0; i < setting.K; ++i) {
			List<Instance> temp = allHandledInstances.get(i);
			temp.clear();
			List<Instance> tempOutput = new ArrayList<>();
			output.add(tempOutput);
		}
		// ���������������ĸ����Ͷ����������ĸ���

		int numClass = instancesSet.rawInstances.numClasses();

		// 1. ���Ƚ������������������в�֣���ʱ�洢���������Ķ���ΪhandledInstances
		instancesByClass = splitByClass();
		for(int i = 0; i < handledInstances.size(); ++i) {
			
		}
		
		// ������������������������
		int max = 0;
		for (int i = 0; i < numClass; ++i) {
			if (max < instancesByClass.get(i).size()) {
				max = instancesByClass.get(i).size();
			}
		}
//		int average = handledInstances.size()/numClass;
		// 2. ��ÿһ����������й�����
		for (int classValue = 0; classValue < numClass; classValue++) {
			List<Instance> instances = instancesByClass.get(classValue);
			// 2. �����������������ƽ����������������ô�Ͳ���Ҫ���й�����
			if (instances.size() >= max) {
				continue;
			}
			//�����ǰ��������������С���趨��Kֵ����ô��Kֵ����Ϊ��������������-1
			//�����ڽ��в�����Kֵ��ԭ
			int tempK = setting.K;
			if(instancesByClass.get(classValue).size() <= setting.K) {
				setting.K = instancesByClass.get(classValue).size()-1;
			}
			// 2.2 ���ݸ����flagɸѡ���������ѡ������������Ƿ�����ĸ���������ҪǷ����������ֵΪ0����֮��Ϊ0��
			int[] n = calInstanceToGenerate(instances, max);
			/*
			 * //2.3 ���ݵ�ǰ����Ƿ��������������м�������� Map<Integer, Double> instancesOfMargin =
			 * calMargin(); //2.4 ���instanceOfMargin���й�����
			 * 
			 * for(Entry<Integer, Double> entry: instancesOfMargin.entrySet()) { Instance
			 * instTemp = handledInstances.get(entry.getKey());
			 * //����inst����ȡ��originInstances�е��±� int index =
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
		// �����ɵ��������뵽handleInstances��
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
	 * TODO: ����������Margin����һ������ Entry<Integer, Double> entry:
	 * IntegerΪ�������±꣬DoubleΪ��Ӧ�����İ�ȫ��࣬ classValueΪ��ǰ���������� RETURN: ����һ�����ɵ�����
	 */
	public Instance generateSample(Entry<Integer, Double> entry, double classValue) {
		// 1. ��ʼ��һ����λ��������
		int dimensionSize = handledInstances.get(0).numAttributes();
		double[] directionVector = new double[dimensionSize];
		int sum2 = 0; // ����sum2����������2������ƽ��
		// 1.1 �������[-10,10]�ķ�������
		for (int i = 0; i < dimensionSize - 1; ++i) {
			Random rand = new Random();
			int value = rand.nextInt(20) - 10;
			directionVector[i] = value;
			sum2 += value * value;
		}
		double tempSum2 = Math.sqrt(sum2);
		// 1. 2.������������λ��
		for (int i = 0; i < dimensionSize; ++i) {
			directionVector[i] = directionVector[i] / tempSum2;
		}

		// 2.���ݵõ��������������������
		double[] instanceValue = new double[dimensionSize];
		Instance currInst = instancesSet.originInstances.get(entry.getKey());
		for (int i = 0; i < dimensionSize; ++i) {
			// 2.1 �����[0,margin]�������
			double margin = entry.getValue();
			double rand = Math.random() * margin;
			// 2.2 ��[0.margin]���Է�������
			instanceValue[i] = currInst.value(i) + rand * directionVector[i];
		}
		// 3.1 Ϊ�����������
		instanceValue[dimensionSize - 1] = classValue;
		// 4. �õ��µĺϳ�����
		Instance newInstance = handledInstances.get(0).copy(instanceValue);
		return newInstance;
	}

	/*
	 * TODO: ����ÿ��������margin RETURN: �޸�List<Double>���ͱ���margin
	 */

	public Map<Integer, Double> calMargin() {
		Map<Integer, Double> instanceOfMargin = new HashMap<>();
		List<List<Double>> distanceMatrix = initializeDistanceMatrix(handledInstances);
		for (int i = 0; i < handledInstances.size(); ++i) {
			List<Double> distance = distanceMatrix.get(i);
			// �ҵ���������ͬ�������㣬����indexOfNearestHit
			int indexOfNearestHit = -1, indexOfNearestMiss = -1;
			double tempMinDistanceOfNearestHit = Double.MAX_VALUE, tempMinDistanceOfNearestMiss = Double.MAX_VALUE;
			for (int j = 0; j < distance.size(); ++j) {
				if (i == j) {
					continue;
				}
				// Ѱ��ͬ���������������룬����indexOfNearestHit��tempMinDistanceOfNearestHit
				int classLabel1 = (int) handledInstances.get(i).classValue();
				int classLabel2 = (int) handledInstances.get(j).classValue();
				if (classLabel1 == classLabel2 && distance.get(j) < tempMinDistanceOfNearestHit) {
					tempMinDistanceOfNearestHit = distance.get(j);
					indexOfNearestHit = j;
				}
				// Ѱ�������������������룬����indexOfNearestMiss��tempMinDistanceOfNearestMiss
				if (classLabel1 != classLabel2 && distance.get(j) < tempMinDistanceOfNearestMiss) {
					tempMinDistanceOfNearestMiss = distance.get(j);
					indexOfNearestMiss = j;
				}
			}
			// �ҵ���ͬ��������������������룬�Ϳ����������i��margin
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
	 * TODO:���������������ݼ����в�� RETURN:�õ�һ����������ֵĶ���
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
			// ������ΪclassLabel��List���������������
			instancesByClass.get(classLabel).add(inst);
		}
		return instancesByClass;
	}

	/*
	 * TODO: ����ÿ����������Ҫ���ɵ������� RETURN�� ÿ�������Ľ�������
	 */
	public int[] calInstanceToGenerate(List<Instance> minority, int average) {

		int classLabel = (int) minority.get(0).classValue();
		int minoritySize = minority.size();
		int[] n = new int[instancesSet.originInstances.size()];
		int generatesize = average - minoritySize;
		List<Instance> instancesToOverSampling = new ArrayList<>();
		// 1. ����Ҫ���������������±��¼��instancesToOverSampling,��ͳ�������
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
	 * TODO: �Ը�����в���������Phase����λ�õ���ʱ�̣������������Ĺ۲�ֵ RETUN: �޸���flag����
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
	 * TODO: �� ���������� RETURN: ����һ��������ƺ��Object����
	 */
	public Object deepCopy() throws ClassNotFoundException {
		Object desObject = null;
		try {
			// 1. ��srcObject����д��ByteArray����
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
			objectOutputStream.writeObject(this);
			objectOutputStream.close();
			// 2. ��ByteArray�ж�����󲢸���desObject
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
	 * TODO: �ж�һ�������Ƿ�����������
	 * RETURN�� ���������Ϊ�����࣬��ô�ͷ���true, ���򷵻�false
	 * */
	
	public boolean isMinorityClass(Instance inst) {
		//����������ݼ���ƽ����������
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
