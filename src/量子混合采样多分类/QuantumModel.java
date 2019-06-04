package 量子混合采样多分类;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.FormatFlagsConversionMismatchException;

public class QuantumModel {
	public Individual[] population; // 种群个体
	public final int iter;
	public final int popSize;
	public Individual gBestIndividual; // 全局最优的个体
	public Individual lBestIndividual; // 局部最优的个体
	public final Setting setting;
	public final InstancesSet instancesSet;

	public QuantumModel(Setting setting, InstancesSet instancesSet) {
		this.setting = setting;
		this.instancesSet = instancesSet;
		this.iter = setting.iteration;
		this.popSize = setting.popSize;
	}

	/*
	 * TODO:量子优化算法的迭代求解函数 RETURN：返回最优的个体
	 */
	public void run() throws Exception {
		System.out.println("量子模型->Starting...");
		// 1. 初始化种群
		initializePopulation();
		int T = iter;
		do {

			// System.out.println("第"+(iter-T+1)+"次迭代");
			// 2. 对所有的个体进行观测
			for (int i = 0; i < population.length; ++i) {
				if (iter - T + 1 == 3) {
					population[i].watchByPhase();
				}
			}
			// 3. 根据观测结果，对每个个体进行评价
			// a.根据flag数组，对数据集进行混合采样
			// c.利用新的数据集进行分类，得到分类结果
			for (int i = 0; i < population.length; ++i) {
				population[i].mixedSampling();
				population[i].calFitness(setting.cls);
			}

			// 4.根据适应度保留最佳个体
			if (T == iter) {
				gBestIndividual = (Individual)getLocalBestIndividual().deepCopy();
			} else {
				lBestIndividual = getLocalBestIndividual();
				if (lBestIndividual.fitness > gBestIndividual.fitness) {
					gBestIndividual = (Individual)lBestIndividual.deepCopy();
				}
			}
			// 5.利用量子门更新每一个个体
			for (int i = 0; i < population.length; ++i) {
				//1.首先根据最优个体和当前个体计算旋转方向和旋转角度
				double angle[] = updateAngle(population[i], gBestIndividual);
				//2.根据旋转角对量子位进行旋转
				population[i].phaseRotate(angle);
			}
			T--;
		} while (T != 0);
		System.out.println("量子模型->End...");
		System.out.println("输出最优个体");
		System.out.println(gBestIndividual.fitness);
		System.out.println("算法运行结束");
	}
	
	/*
	 * TODO: 根据个体currentIndividual和全局最优globalBestIndividual调整旋转角度
	 * RETURN: 返回更新后的旋转角度
	 * */
	public double[] updateAngle(Individual currIndividual, Individual globalIndividual) {
		//1. 首先根据当前个体和最优个体计算旋转方向
		int[] direction = rotateDirection(currIndividual, globalIndividual);
		double angle[] = new double[currIndividual.flag.length];
		for(int i = 0; i < currIndividual.flag.length; ++i) {
			if(direction[i] == 0) {continue;}
			angle[i] = setting.minRotateAngle+(setting.maxRotateAngle - setting.minRotateAngle)*hamingDistance(currIndividual, globalIndividual);
			angle[i] = angle[i]*direction[i];
		}
		return angle;
	}
	
	/*
	 * TODO: 计算两个向量的海明距离
	 * */
	public int hamingDistance(Individual currIndividual, Individual globalIndividual) {
		int hamingDistance = 0;
		for(int i = 0; i < currIndividual.flag.length; ++i) {
			if(currIndividual.flag[i] != globalIndividual.flag[i]) {
				hamingDistance++;
			}
		}
		hamingDistance /= currIndividual.flag.length;
		return hamingDistance;
	}
	/*
	 * TODO: 根据论文《基于改进量子进化算法的特征选择》中的转向策略表得到当前个体每一个量子位的转向
	 * RETURN: -1: 逆时针 +1: 顺时针  0: 不需要旋转
	 * */
	public int[] rotateDirection(Individual currIndividual, Individual globalIndividual) {
		int[] direction = new int[currIndividual.flag.length];
		double currFitness = currIndividual.fitness;
		double globalFitness = globalIndividual.fitness;
		for(int i = 0; i < direction.length; ++i) {
			double alpha = currIndividual.phase[i].alpha;
			double beta = currIndividual.phase[i].beta;
			int a = currIndividual.flag[i];
			int b = globalIndividual.flag[i];
			boolean tag = 
				globalIndividual.fitness > currIndividual.fitness ? true : false;
			
			if(alpha * beta > 0) {
				//1. -1
				if(a == 1 && b == 1) { direction[i] = -1;}
				if(a == 1 && b == 0 && !tag) {direction[i] = -1;}
				if(a == 0 && b == 1 && tag) {direction[i] = -1;}
				//2. +1
				if(a == 0 && b == 0) { direction[i] = 1;}
				if(a == 1 && b == 0 && tag) {direction[i] = 1;}
				if(a == 0 && b == 1 && !tag) {direction[i] = 1;}
			}
			if(alpha * beta < 0) {
				//1. -1
				if(a == 1 && b == 1) { direction[i] = 1;}
				if(a == 1 && b == 0 && !tag) {direction[i] = 1;}
				if(a == 0 && b == 1 && tag) {direction[i] = 1;}
				//2. +1
				if(a == 0 && b == 0) { direction[i] = -1;}
				if(a == 1 && b == 0 && tag) {direction[i] = -1;}
				if(a == 0 && b == 1 && !tag) {direction[i] = -1;}
			}
			if(Math.abs(alpha)<0.00005) {
				//1.0
				if(a == 1 && b == 1) { direction[i] = 0;}
				if(a == 1 && b == 0 && !tag) {direction[i] = 0;}
				if(a == 0 && b == 1 && tag) {direction[i] = 0;}
				//2. +1
				if(a == 0 && b == 0) { direction[i] = -1;}
				if(a == 1 && b == 0 && tag) {direction[i] = -1;}
				if(a == 0 && b == 1 && !tag) {direction[i] = -1;}
			}
			if(Math.abs(beta)<0.00005) {
				//1. +1
				if(a == 1 && b == 1) { direction[i] = 1;}
				if(a == 1 && b == 0 && !tag) {direction[i] = 1;}
				if(a == 0 && b == 1 && tag) {direction[i] = 1;}
				//2. 0
				if(a == 0 && b == 0) { direction[i] = 0;}
				if(a == 1 && b == 0 && tag) {direction[i] = 0;}
				if(a == 0 && b == 1 && !tag) {direction[i] = 0;}
			}
		}
		return direction;
	}
	
	/*
	 * TODO: 根据alpha, beta来查找旋转方向对照表
	 * */
	/*
	 * TODO: 更新每个个体的旋转角度
	 * RETURN: 返回当前个体在在下一次旋转时的旋转角度
	 * */
	public double calAngleForIndividual() {
		double angle = 0;
		return angle;
	}
	/*
	 * TODO:初始化种群{包括实例化population中每一个个体对象，调用初始化函数} RETURN：修改population数组
	 */
	public void initializePopulation() {
		System.out.println("初始化种群->Starting...");
		population = new Individual[popSize];
		for (int i = 0; i < popSize; ++i) {
			population[i] = new Individual(setting, instancesSet);
			population[i].initializeIndividual();
		}
		System.out.println("初始化种群->End...");
	}

	/*
	 * TODO:从种群中选出局部最佳个体 
	 * RETURN：返回局部最佳个体
	 */
	public Individual getLocalBestIndividual() {
		int index = 0;
		for (int i = 0; i < population.length; ++i) {
			if (population[i].fitness > population[index].fitness) {
				index = i;
			}
		}
		return population[index];
	}
	
	
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		/*
		Setting setting = new Setting(4, 6, 20, 200, 30,Enum_Classifier.C45);
		InstancesSet instancesSet = new InstancesSet("dataset/pima.arff", setting);
		instancesSet.initializeInstancesSet(0);
		QuantumModel quantumModel = new QuantumModel(setting, instancesSet);
		quantumModel.initializePopulation();
		for (int i = 0; i < quantumModel.population.length; ++i) {
			quantumModel.population[i].mixedSampling();
			quantumModel.population[i].calFitness(setting.cls);
		}
		Individual localIndividual = quantumModel.population[0];
		localIndividual.phase[0].beta = 0;
		localIndividual.flag[0] = 0;
		Individual globalIndividual = quantumModel.population[1];
		globalIndividual.flag[0] = 0;
		
		globalIndividual.fitness = localIndividual.fitness-1;
		int[] direction = quantumModel.rotateDirection(localIndividual, globalIndividual);
		System.out.println(direction[0]);
		return ;
		*/
		String[] dataSets = { "glass1", "pima", "glass0", "yeast1", "vehicle1", "glass0123vs456", "ecoli1",
				"newthyroid1", "newthyroid2", "ecoli2", "glass6", "yeast3", "ecoli3", "glass016v2", "yeast1v7",
				"glass4", "glass5", "yeast2v8", "yeast4", "yeast6" };
		FileWriter fw = new FileWriter("dataset/基于边界的实验结果NB.dat", true);
		for (int set = 0; set < dataSets.length; ++set) {
			System.out.println("当前运行数据集为："+dataSets[set]);
			int maxK = 1, maxNoiseK=2;
			double maxFitness = 0;
			int k = 1, noisyK = 2;
			for (int tempK = 2; tempK <= 2; ++tempK) {
				for (int tempNoiseK = 2; tempNoiseK <= 2; tempNoiseK++) {
					Setting setting = new Setting(tempK, tempNoiseK, 20, 200, 30,Enum_Classifier.C45);
					InstancesSet instancesSet = new InstancesSet(dataSets[set], setting);
					double sum = 0;
					for (int i = 0; i < 5; ++i) {
						try {
							instancesSet.initializeInstancesSet(i);
							QuantumModel quantumModel = new QuantumModel(setting, instancesSet);
							quantumModel.run();
							sum += quantumModel.gBestIndividual.fitness;
						}catch(Exception e) {
							System.out.println("参数不符合模型要求，运行失败");
						}
					}
					
					if(maxFitness < sum) {
						maxFitness = sum;
						maxK = tempK;
						maxNoiseK = tempNoiseK;
					}
				}
			}
			fw.write(dataSets[set] + ":" + maxFitness / 5 + " K："+maxK+"NoiseK:"+maxNoiseK);
			fw.write('\n');
			System.out.println("最终的最优结果为：" + maxFitness / 5);
		}
		fw.close();
	}
}
