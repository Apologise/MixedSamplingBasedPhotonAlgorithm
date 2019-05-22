package ���ӻ�ϲ��������;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import weka.core.Instance;
import weka.core.Instances;
import weka.core.expressionlanguage.common.IfElseMacro;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Normalize;

/*
 * InstancesSet��ʵ�����ڱ���������͵���������
 * InstancesSetʵ������һ������initializeInstancesSet()������ı��������ᱻ���������޸�
 * */
public class InstancesSet {
	public final String fileName;
	public Instances rawInstances;	//δ���κδ����ԭʼ����
	public List<Instance> originInstances;	//�����˹�һ������׼����ȥ��������������
	public List<List<Double>> distanceMatrix;	//�������֮��ľ������
	public int noisyK;	//�ж��Ƿ�Ϊ�����Ĳ���K
	public List<Instance> majorityInstances;
	public List<Double> weightOfMajorityInstance;	//������������Ȩ��
	public List<Instance> minorityInstances;
	public List<List<Instance>> instancesByClass;
	public Set<Integer> minorityClassLabel;
	public Instances validateInstances;
	public List<Double> instanceOfMargin;
	public List<Integer> indexOfInstancesInMajorityInstancesIntoPopulation;
	public int fold;
	
	public InstancesSet(String fileName, Setting setting) {
	    this.fileName = fileName;
	    noisyK = setting.noisyK;
	}
	
	/*
	 * TODO:��ʼ��InstancesSet�����е�majrotityInstances, instancesByClass��Ա
	 * RETURN�� ���س�ʼ����ĳ�Ա����
	 * */
	public void initializeInstancesSet(int curFold) throws Exception {
		InstanceDao instanceDao = new InstanceDao();
		String[] trainSet = Dataset.chooseDataset(fileName, 0);
		String[] testSet = Dataset.chooseDataset(fileName, 1);
		rawInstances = instanceDao.loadDataFromFile("dataset/5-fold-pima/pima-5-1tra.arff");
		validateInstances = instanceDao.loadDataFromFile("dataset/5-fold-pima/pima-5-1tst.arff");
		//��ʼ���������
		distanceMatrix = new ArrayList<List<Double>>();
		initializeDistanceMatrix(rawInstances);
		//�Ƴ��ظ�����
		removeDuplicateInstance();
		initializeDistanceMatrix(rawInstances);
		//�����ݼ����й�һ��
//		rawInstances = normalizeInstances(rawInstances);
		removeNoiseInstance();
		//���Ƴ�����������ݼ����������뵽originInstances������
		
		originInstances = new ArrayList<>();
		for(int i = 0; i < rawInstances.size(); ++i) {
			originInstances.add(rawInstances.get(i));
		}
		//����Ƴ�������ľ������(��Ϊ��������٣�����������������Ҳ��仯)
		initializeDistanceMatrixAfterRemoveNoise(originInstances);
		//�Ƴ�����������������
		instanceOfMargin = new ArrayList<>();
		calMargin();
		//������꽫����ԭʼ���ݼ����в�ִ����instancesByClass
		instancesByClass = new ArrayList<List<Instance>>();
		for(int i = 0; i < rawInstances.numClasses(); ++i) {
			List<Instance> temp = new ArrayList<>();
			instancesByClass.add(temp);
		}
		minorityClassLabel = new HashSet<>();
		splitByClass();
		//��origin���ϻ���Ϊ�����������������
		majorityInstances = new ArrayList<>();
		minorityInstances = new ArrayList<>();
		for(Instance inst: originInstances) {
			if(isMajorityClass(inst)) {
				majorityInstances.add(inst);
			}else {
				minorityInstances.add(inst);
			}
		}
		weightOfMajorityInstance = new ArrayList<>();
		calWeight();
		indexOfInstancesInMajorityInstancesIntoPopulation = new ArrayList<>();
		indexOfInstancesInMajorityInstancesIntoPopulation = getIndexOfInstanceToPopulation();
		System.out.println("\nInstancesSet�����ʼ������");
	}
	/*
	 * TODO: �����ļ��ж�ȡ�����ݹ淶��
	 * RETURN: �õ�һ���淶�������ݼ�
	 * */
	public Instances normalizeInstances(Instances instances) throws Exception {
		Normalize normalizer = new Normalize();
		normalizer.setInputFormat(instances);
		instances = Filter.useFilter(rawInstances, normalizer);
		return instances;
	}
	
	public List<List<Double>> initializeDistanceMatrix(Instances rawInstances) {
		distanceMatrix.clear();
		for(Instance first: rawInstances) {
			List<Double> rawDistance = new ArrayList<>();
			for(Instance second: rawInstances) {
				if(first == second) {
					rawDistance.add(Double.MAX_VALUE);
				}else {
					rawDistance.add(calDistance(first, second));
				}
			}
			distanceMatrix.add(rawDistance);
		}
		return distanceMatrix;
	}
	
	public void initializeDistanceMatrixAfterRemoveNoise(List<Instance> instances) {
		distanceMatrix.clear();
		for(Instance first: instances) {

			List<Double> rawDistance = new ArrayList<>();
			for(Instance second: instances) {
				if(first == second) {
					rawDistance.add(Double.MAX_VALUE);
				}else {
					rawDistance.add(calDistance(first, second));
				}
			}
			distanceMatrix.add(rawDistance);
		}
	}

	/*
	 * TODO��ȥ�����ݼ��е�����
	 * ��һ��������Χ��K���ڶ�������겻��ͬʱ�����ж�Ϊ��������
	 * RETURN���޸�originInstances����
	 * */
	public void removeNoiseInstance() {
		List<Instance> noisyInstances = new ArrayList<>();
		for(int i = 0; i < rawInstances.size(); ++i) {
			//�����i��������k����
			List<Instance> knearestInstances = knearestNeighbours(i, distanceMatrix, rawInstances);
			//����K���ڵ�������ж��Ƿ�Ϊ��������
			boolean isNoiseInstance = true;
			for(int j = 0; j < knearestInstances.size(); ++j) {
				if((int)rawInstances.get(i).classValue()  == (int)knearestInstances.get(j).classValue()) {
					isNoiseInstance = false;
					break;
				}
			}
			if(isNoiseInstance) {
				noisyInstances.add(rawInstances.get(i));
			}
		}
		//�Ƴ���������
		for(int i = 0; i < noisyInstances.size(); ++i) {
			rawInstances.remove(noisyInstances.get(i));
		}
	}
	
	public List<Instance> knearestNeighbours(int instanceIndex, List<List<Double>> distanceMatrix, Instances instancesSet){
		List<Instance> knearestNeighbours = new ArrayList<Instance>();
		//�Ծ����������
		//ȡ��inst��������Ӧ�ľ�������
		List<Double> distance = distanceMatrix.get(instanceIndex);
		int[] visited = new int[distance.size()];
		int tempK = noisyK;
		while(tempK!=0) {
			double temp = Double.MAX_VALUE;
			int index = -1;
			for(int i = 0; i < distance.size(); ++i) {
				if(visited[i] == 0 && temp > distance.get(i)) {
					temp = distance.get(i);
					index = i;
				}
			}
			visited[index] = 1;
			knearestNeighbours.add(instancesSet.get(index));
			tempK--;
		}
		return knearestNeighbours;
	}
	
	public static double calDistance(Instance first, Instance second) {
		double distance = 0;
		for(int i = 0; i < first.numAttributes()-1; ++i) {
			double diff = first.value(i) - second.value(i);
			distance += diff*diff;
		}
		return Math.sqrt(distance);
	}
	
	public static void printInstances(Instances Instances) {
		for(Instance inst: Instances) {
			System.out.println(inst);
		}
	}
	
	/*
	 * TODO:���������������ݼ����в��
	 * RETURN:��ֺ��޸�List<List<Instances>>���͵Ķ���instancesByClass����һά�ķ����±꼴Ϊ���
	 * */
	public void splitByClass() {
		for(Instance inst: originInstances) {
			int classLabel = (int)inst.classValue();
			//������ΪclassLabel��List���������������
			List<Instance> instances = instancesByClass.get(classLabel);
			instances.add(inst);
		}
	}
	
	/*
	 * TODO���ж�һ�������Ƿ�Ϊ������
	 * RETURN:
	 *        true:�����Ϊ������
	 *        false:�����Ϊ������
	 * 
	 * */
	public boolean isMajorityClass(Instance inst) {
		boolean flag = false;
		int classLabel = (int)inst.classValue();
		int theSizeOfClassLabel = instancesByClass.get(classLabel).size();
		int averageSize = originInstances.size()/rawInstances.numClasses();
		if(theSizeOfClassLabel > averageSize) {
			flag = true;
		}
		if(flag == false) {
			minorityClassLabel.add(classLabel);
		}
		return flag;
	}
	
	/*
	 * TODO: �������������������ÿһ��������Ȩ��
	 * */
	public void calWeight() {
		for(int i = 0; i < majorityInstances.size(); ++i) {
			Instance inst1 = majorityInstances.get(i);
			int classLabel1 = (int)inst1.classValue();
			//���inst1��originInstances�е��±�
			int indexInOriginList = originInstances.indexOf(inst1);
			List<Double> distance = distanceMatrix.get(indexInOriginList);
			//�����������inst���������������������
			double minDis = 0x3fffffff;
			for(int j = 0; j < distance.size(); ++j) {
				try{
				Instance inst2 = originInstances.get(j);
				int classLabel2 = (int)inst2.classValue();
				if(classLabel2 != classLabel1 && minDis > distance.get(j)) {
					minDis = distance.get(j);
				}
				}catch(Exception e) {
					System.out.println();
				}
				
			}
			weightOfMajorityInstance.add(minDis);
		}
		//��Ȩ�ع�һ��
		double min = 0x3fffffff, max = -1;
		for(int i = 0; i<weightOfMajorityInstance.size(); ++i) {
			double tempValue = weightOfMajorityInstance.get(i);
			if(tempValue < min) {
				min = tempValue;
			}
			if(tempValue > max) {
				max = tempValue;
			}
		}
		for(int i = 0; i<weightOfMajorityInstance.size(); ++i) {
			double tempValue = weightOfMajorityInstance.get(i);
			double updateVaue = (tempValue - min)/(max-min);
			weightOfMajorityInstance.set(i, updateVaue);
		}
	}
	
	/*
	 * TODO: ����ÿ��������margin
	 * RETURN: �޸�List<Double>���ͱ���margin
	 * */
	
	public void calMargin() {
		for(int i = 0; i < originInstances.size(); ++i) {
			List<Double> distance = distanceMatrix.get(i);
			
			//�ҵ���������ͬ�������㣬����indexOfNearestHit
			int indexOfNearestHit = -1, indexOfNearestMiss = -1;
			double tempMinDistanceOfNearestHit = Double.MAX_VALUE, tempMinDistanceOfNearestMiss = Double.MAX_VALUE;
			for(int j = 0; j < distance.size(); ++j) {
				if(i == j) {continue;}
				//Ѱ��ͬ���������������룬����indexOfNearestHit��tempMinDistanceOfNearestHit
				int classLabel1 = (int)originInstances.get(i).classValue();
				int classLabel2 = (int)originInstances.get(j).classValue();
				if(classLabel1 == classLabel2 && distance.get(j) < tempMinDistanceOfNearestHit) {
					tempMinDistanceOfNearestHit = distance.get(j);
					indexOfNearestHit = j;
				}
				//Ѱ�������������������룬����indexOfNearestMiss��tempMinDistanceOfNearestMiss
				if(classLabel1 != classLabel2 && distance.get(j) < tempMinDistanceOfNearestMiss) {
					tempMinDistanceOfNearestMiss = distance.get(j);
					indexOfNearestMiss = j;
				}
			}
			//�ҵ���ͬ��������������������룬�Ϳ����������i��margin
			double margin = 0.5*(tempMinDistanceOfNearestMiss-tempMinDistanceOfNearestHit);
			instanceOfMargin.add(margin);
		}
	}
	
	/*
	 * TODO: ȥ���ظ�����
	 * RETURN������ȥ���ظ��������rawInstances
	 * */
	public void removeDuplicateInstance() {
		//1. ��2������֮��ľ���Ϊ0�����ʾΪ�ظ�����
		List<Integer> duplicateInstances = new ArrayList<>();
		for(int i = 0; i < rawInstances.size(); ++i) {
			//�������i�ľ�������
			List<Double> distance = distanceMatrix.get(i);
			for(int j = 0; j < distance.size(); ++j) {
				//2.�ҵ��ظ������������±���뵽list��
				if(Math.abs(distance.get(j)-0.000001) < 0.00001 && i < j) {
					if(duplicateInstances.contains(j)){continue;}
					duplicateInstances.add(j);
				}
			}
		}
		//3. ���ظ�������rawInstance��ɾ��
		//3.1 ���ڽ�rawInstance��ɾ������ʱ�������±����ı䣬��ˣ�������Ҫ�Ƚ��±꽵�������ɸߵ��׿�ʼ�Ƴ�����ô���±����������������Ƴ�ʱ����
		Collections.sort(duplicateInstances, new Comparator<Integer>() {
			@Override
			public int compare(Integer o1, Integer o2) {
				// TODO Auto-generated method stub
				if(o1 > o2) {
					return -1;
				}else if(o1 == o2){
					return 0;
				}else {
					return 0;
				}
			}
			
		});
		//3.2 ����������duplicate����ɾ��
		for(int i = 0; i < duplicateInstances.size(); ++i) {
			rawInstances.remove((int)duplicateInstances.get(i));
		}
	}
	
	/*
	 * TODO: ����margin�������ȥ�����������margin����Ϊ�������ж�Ϊ����
	 * RETURN: ����ȥ���������List<Instance>���͵�originInstances
	 * */
	public void removeNoiseInstanceByMargin(){
		//1.�ҳ���������������Ӧ�±���뵽indexOfNoiseInstance��
		List<Integer> indexOfNoiseInstance = new ArrayList<>();
		for(int i = 0; i < instanceOfMargin.size(); ++i) {
			double margin = instanceOfMargin.get(i);
			if(margin < 0) {
				indexOfNoiseInstance.add(i);
			}
		}
		
		//2. ��indexOfNoiseInstance���н�������
		Collections.sort(indexOfNoiseInstance, new Comparator<Integer>() {
			@Override
			public int compare(Integer o1, Integer o2) {
				// TODO Auto-generated method stub
				if(o1 > o2) {
					return -1;
				}else if(o1 == o2){
					return 0;
				}else {
					return 0;
				}
			}
			
		});
		//3. ����������indexOfNoiseInstance�Ƴ���������
		for(int i = 0; i < indexOfNoiseInstance.size(); ++i) {
			originInstances.remove((int)indexOfNoiseInstance.get(i));
		}
	}
	
	/*
	 * TODO�� ����margin�����ǰ30%�Ķ����������±�,��Щ�±��Ӧ���������뵽�����Ż��㷨�н���Ƿ����
	 * RETURN�� ����ǰ30%�������������±�
	 * */
	public List<Integer> getIndexOfInstanceToPopulation(){
		List<Integer> indexOfInstance = new ArrayList<>();
		//1. ��instanceOfMargin�л�ö�����������marginֵ
		List<Double> marginOfMajority = new ArrayList<>();
		for(Instance inst: majorityInstances) {
			//�ҵ�inst��originInstances�е��±�
			int index = originInstances.indexOf(inst);
			double margin = instanceOfMargin.get(index);
			marginOfMajority.add(margin);
		}
		//2. ���������margin����<�����±꣨majorityInstances�е��±꣩������margin>��ŵ�indexMapToMargin��
		Map<Integer, Double> indexMapToMargin = new HashMap<>();
		for(int i = 0; i < marginOfMajority.size(); ++i) {
			indexMapToMargin.put(i, marginOfMajority.get(i));
		}
		//3. ����margin��indexMapToMargin������������
		//3.1 �����޷�ֱ�Ӷ�indexMapToMargin����value�������������Ҫ��indexMapToMargin�е�ʵ����뵽List�н�������
		List<Map.Entry<Integer, Double>> tempEntries = new ArrayList<>(indexMapToMargin.entrySet());
		Collections.sort(tempEntries, new Comparator<Map.Entry<Integer, Double>>() {
			@Override
			public int compare(Map.Entry<Integer, Double> o1, Map.Entry<Integer, Double> o2) {
				if(o1.getValue() > o2.getValue()) {
					return 1;
				}else if(o1.getValue() < o2.getValue()) {
					return -1;
				}else {
					return 0;
				}
			}
		});
		
		//4. ��majorityInstance��ȡ��ǰһ���ٷֱ�(�˴�Ϊ30%)������
		for(int i = 0; i < tempEntries.size()*0.3; ++i) {
			indexOfInstance.add(tempEntries.get(i).getKey());
		}
		//5.����ѡ�����������±꣬��ŵ�indexOfInstance��
		return indexOfInstance;
	}
	
	
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		InstanceDao instanceDao = new InstanceDao();
		Setting setting = new Setting(200, 5, 5, 1, 1);
		InstancesSet instancesSet = new InstancesSet("", setting);
		instancesSet.initializeInstancesSet(1);
		return;
	}
}
