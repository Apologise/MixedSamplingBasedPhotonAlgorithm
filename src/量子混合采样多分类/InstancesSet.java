package ���ӻ�ϲ��������;

import java.util.ArrayList;

import java.util.List;

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
	public List<Instance> minorityInstances;
	public List<List<Instance>> instancesByClass;
	
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
		normalizeInstances(rawInstances);
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
		System.out.println("InstancesSet�����ʼ������\n");
	}
	/*
	 * TODO: �����ļ��ж�ȡ�����ݹ淶��
	 * RETURN: �õ�һ���淶�������ݼ�
	 * */
	public void normalizeInstances(Instances rawInstances) throws Exception {
		Normalize normalizer = new Normalize();
		normalizer.setInputFormat(rawInstances);
		rawInstances = Filter.useFilter(rawInstances, normalizer);
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
		return distance;
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
		return flag;
	}
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
	

	
	}
}
