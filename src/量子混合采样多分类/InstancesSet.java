package 量子混合采样多分类;

import java.util.ArrayList;
import java.util.FormatFlagsConversionMismatchException;
import java.util.List;

import weka.core.Instance;
import weka.core.Instances;
import weka.core.neighboursearch.NearestNeighbourSearch;
import weka.core.pmml.Constant;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Normalize;

public class InstancesSet {
	public final String filePath;
	public Instances rawInstances;	//未经任何处理的原始数据
	
	public List<Instance> originInstances;	//经过了归一化、标准化和去除了噪声的数据
	public List<List<Double>> distanceMatrix;	//存放样本之间的距离矩阵
	public int noisyK;	//判定是否为噪声的参数K
	public List<Instance> majorityInstances;
	public List<Instance> minorityInstances;
	public List<List<Instance>> instancesByClass;
	
	public InstancesSet(String filePath) {
		this.noisyK = Setting.noisyK;
	    this.filePath = filePath;
	}
	
	/*
	 * TODO:初始化InstancesSet对象中的majrotityInstances, instancesByClass成员
	 * RETURN： 返回初始化后的成员变量
	 * */
	public void initializeInstancesSet() throws Exception {
		InstanceDao instanceDao = new InstanceDao();
		rawInstances = instanceDao.loadDataFromFile(filePath);
		//初始化距离矩阵
		distanceMatrix = new ArrayList<List<Double>>();
		initializeDistanceMatrix(rawInstances);
		//将数据集进行归一化
		normalizeInstances(rawInstances);
		removeNoiseInstance();
		//将移除噪声后的数据集的样本加入到originInstances集合中
		for(int i = 0; i < rawInstances.size(); ++i) {
			originInstances.add(rawInstances.get(i));
		}
		//获得移除噪声后的距离矩阵
		initializeDistanceMatrixAfterRemoveNoise(originInstances);
		//根据类标将整个原始数据集进行拆分存放于instancesByClass
		instancesByClass = new ArrayList<List<Instance>>();
		for(int i = 0; i < rawInstances.numClasses(); ++i) {
			List<Instance> temp = new ArrayList<>();
			instancesByClass.add(temp);
		}
		splitByClass();
		//将origin集合划分为多数类和少数类样本
		majorityInstances = new ArrayList<>();
		minorityInstances = new ArrayList<>();
		for(Instance inst: originInstances) {
			if(isMajorityClass(inst)) {
				majorityInstances.add(inst);
			}else {
				minorityInstances.add(inst);
			}
		}
	}
	/*
	 * TODO: 将从文件中读取的数据规范化
	 * RETURN: 得到一个规范化的数据集，然后付给orginInstances;
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
	 * TODO：去除数据集中的噪声
	 * 当一个样本周围的K近邻都与其类标不相同时，则判定为噪声样本
	 * RETURN：返回一个噪声样本集合
	 * */
	public void removeNoiseInstance() {
		//计算距离矩阵
		List<List<Double>> distanceMatrix = initializeDistanceMatrix(rawInstances);
		List<Instance> noisyInstances = new ArrayList<>();
		for(int i = 0; i < rawInstances.size(); ++i) {
			//计算第i个样本的k近邻
			List<Instance> knearestInstances = knearestNeighbours(i, distanceMatrix, rawInstances);
			//根据K近邻的类标来判断是否为噪声样本
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
		//移除噪声样本
		for(int i = 0; i < noisyInstances.size(); ++i) {
			rawInstances.remove(noisyInstances.get(i));
		}
	}
	
	public List<Instance> knearestNeighbours(int instanceIndex, List<List<Double>> distanceMatrix, Instances instancesSet){
		List<Instance> knearestNeighbours = new ArrayList<Instance>();
		//对距离进行排序
		//取出inst样本所对应的距离向量
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
	 * TODO:按照类标对整个数据集进行拆分
	 * RETURN:拆分后返回Instances类型的instancesByClass[]数组，数组下标为类标
	 * */
	public void splitByClass() {
		for(Instance inst: originInstances) {
			int classLabel = (int)inst.classValue();
			//获得类标为classLabel的List，并将其加入其中
			List<Instance> instances = instancesByClass.get(classLabel);
			instances.add(inst);
		}
	}
	
	/*
	 * TODO：判断一个样本是否为多数类
	 * RETURN:
	 *        true:该类别为多数类
	 *        false:该类别为少数类
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
		InstanceDao instanceDao = new InstanceDao();

	
	}
}
