package 量子混合采样多分类;


public class QuantumModel {
	public Individual[] population;	//种群个体
	public final int iter;
	public final int popSize;
	public Individual gBestIndividual;	//最优的染色体
	public final Setting setting;
	public final InstancesSet instancesSet;
	
	public QuantumModel(Setting setting, InstancesSet instancesSet) {
		this.setting = setting;
		this.instancesSet = instancesSet;
		this.iter = setting.iteration;
		this.popSize = setting.popSize;
	}
	/*
	 * TODO:量子优化算法的迭代求解函数
	 * RETURN：返回最优的个体
	 * */
	public void run() {
		System.out.println("量子模型->Starting...");
		//1. 初始化种群
		initializePopulation();
		int T = iter;
		do {
			//2. 对所有的个体进行观测
			for(int j = 0; j < population.length; ++j) {
				population[j].watchByPhase();
			}
			//3. 根据观测结果，对每个个体进行评价
				//a.根据flag数组，对多数类进行欠采样
				//b.对少数类进行过采样
				//c.利用新的数据集进行分类，得到分类结果
			//4.根据适应度保留最佳个体
			//5.利用量子门更新每一个个体
			T--;
		}while(T!=0);
		System.out.println("量子模型->End...");
		System.out.println("输出最优个体");
		System.out.println("算法运行结束");
	}
	
	/*
	 * TODO:初始化种群{包括实例化population中每一个个体对象，调用初始化函数}
	 * RETURN：修改population数组
	 * */
	public void initializePopulation() {
		System.out.println("初始化种群->Starting...");
		population = new Individual[popSize];
		for(int i = 0; i < popSize; ++i) {
			population[i] = new Individual(setting, instancesSet);
			population[i].initializeIndividual();
		}
		System.out.println("初始化种群->End...");
	}
	
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		Setting setting = new Setting(100, 5, 5, 10, 100,30);
		InstancesSet instancesSet = new InstancesSet("dataset/pima.arff", setting); 
		instancesSet.initializeInstancesSet();
		QuantumModel quantumModel = new QuantumModel(setting, instancesSet);
		quantumModel.initializePopulation();
		
	}
}
