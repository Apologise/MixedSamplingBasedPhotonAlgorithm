package ���ӻ�ϲ��������;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import weka.core.Instance;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Normalize;

/*
 * InstancesSet��ʵ�����ڱ���������͵���������
 * InstancesSetʵ������һ������initializeInstancesSet()������ı��������ᱻ���������޸�
 * */
public class InstancesSet {
	public final String filePath;
	public Instances rawInstances;	//δ���κδ����ԭʼ����
	public List<Instance> originInstances;	//�����˹�һ������׼����ȥ��������������
	public List<List<Double>> distanceMatrix;	//�������֮��ľ������
	public int noisyK;	//�ж��Ƿ�Ϊ�����Ĳ���K
	public List<Instance> majorityInstances;
	public List<Double> weightOfMajorityInstance;	//������������Ȩ��
	public List<Instance> minorityInstances;
	public List<List<Instance>> instancesByClass;
	public Set<Integer> minorityClassLabel;
	public Instances trainInstances;
	
	public InstancesSet(String filePath, Setting setting) {
	    this.filePath = filePath;
	    noisyK = setting.K;
	}
	
	/*
	 * TODO:��ʼ��InstancesSet�����е�majrotityInstances, instancesByClass��Ա
	 * RETURN�� ���س�ʼ����ĳ�Ա����
	 * */
	public void initializeInstancesSet() throws Exception {
		InstanceDao instanceDao = new InstanceDao();
		rawInstances = instanceDao.loadDataFromFile(filePath);
		//��ʼ���������
		distanceMatrix = new ArrayList<List<Double>>();
		initializeDistanceMatrix(rawInstances);
		//�����ݼ����й�һ��
	//	rawInstances = normalizeInstances(rawInstances);
		removeNoiseInstance();
		//���Ƴ�����������ݼ����������뵽originInstances������
		originInstances = new ArrayList<>();
		for(int i = 0; i < rawInstances.size(); ++i) {
			originInstances.add(rawInstances.get(i));
		}
		//����Ƴ�������ľ������(��Ϊ��������٣�����������������Ҳ��仯)
		initializeDistanceMatrixAfterRemoveNoise(originInstances);
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
		System.out.println("InstancesSet�����ʼ������\n");
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
	 * RETURN������һ��������������
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
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		
	}
}
