package 量子混合采样多分类;

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
 * InstancesSet类实例用于保存各种类型的样本集合
 * InstancesSet实例变量一旦调用initializeInstancesSet()后，里面的变量都不会被其他对象修改
 * */
public class InstancesSet {
	public final String filePath;
	public Instances rawInstances;	//未经任何处理的原始数据
	public List<Instance> originInstances;	//经过了归一化、标准化和去除了噪声的数据
	public List<List<Double>> distanceMatrix;	//存放样本之间的距离矩阵
	public int noisyK;	//判定是否为噪声的参数K
	public List<Instance> majorityInstances;
	public List<Double> weightOfMajorityInstance;	//多数类样本的权重
	public List<Instance> minorityInstances;
	public List<List<Instance>> instancesByClass;
	public Set<Integer> minorityClassLabel;
	public Instances trainInstances;
	
	public InstancesSet(String filePath, Setting setting) {
	    this.filePath = filePath;
	    noisyK = setting.K;
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
	//	rawInstances = normalizeInstances(rawInstances);
		removeNoiseInstance();
		//将移除噪声后的数据集的样本加入到originInstances集合中
		originInstances = new ArrayList<>();
		for(int i = 0; i < rawInstances.size(); ++i) {
			originInstances.add(rawInstances.get(i));
		}
		//获得移除噪声后的距离矩阵(因为样本会减少，因此样本矩阵的行列也会变化)
		initializeDistanceMatrixAfterRemoveNoise(originInstances);
		//根据类标将整个原始数据集进行拆分存放于instancesByClass
		instancesByClass = new ArrayList<List<Instance>>();
		for(int i = 0; i < rawInstances.numClasses(); ++i) {
			List<Instance> temp = new ArrayList<>();
			instancesByClass.add(temp);
		}
		minorityClassLabel = new HashSet<>();
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
		weightOfMajorityInstance = new ArrayList<>();
		calWeight();
		System.out.println("InstancesSet对象初始化结束\n");
	}
	/*
	 * TODO: 将从文件中读取的数据规范化
	 * RETURN: 得到一个规范化的数据集
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
	 * TODO：去除数据集中的噪声
	 * 当一个样本周围的K近邻都与其类标不相同时，则判定为噪声样本
	 * RETURN：返回一个噪声样本集合
	 * */
	public void removeNoiseInstance() {
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
		return Math.sqrt(distance);
	}
	
	public static void printInstances(Instances Instances) {
		for(Instance inst: Instances) {
			System.out.println(inst);
		}
	}
	
	/*
	 * TODO:按照类标对整个数据集进行拆分
	 * RETURN:拆分后修改List<List<Instances>>类型的对象instancesByClass，第一维的访问下标即为类标
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
		if(flag == false) {
			minorityClassLabel.add(classLabel);
		}
		return flag;
	}
	
	/*
	 * TODO: 计算多数类样本集合中每一个样本的权重
	 * */
	public void calWeight() {
		for(int i = 0; i < majorityInstances.size(); ++i) {
			Instance inst1 = majorityInstances.get(i);
			int classLabel1 = (int)inst1.classValue();
			//获得inst1在originInstances中的下标
			int indexInOriginList = originInstances.indexOf(inst1);
			List<Double> distance = distanceMatrix.get(indexInOriginList);
			//求多数类样本inst到其他类样本的最近距离
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
		//将权重归一化
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
