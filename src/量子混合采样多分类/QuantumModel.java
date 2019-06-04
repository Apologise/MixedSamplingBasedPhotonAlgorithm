package ���ӻ�ϲ��������;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.FormatFlagsConversionMismatchException;

public class QuantumModel {
	public Individual[] population; // ��Ⱥ����
	public final int iter;
	public final int popSize;
	public Individual gBestIndividual; // ȫ�����ŵĸ���
	public Individual lBestIndividual; // �ֲ����ŵĸ���
	public final Setting setting;
	public final InstancesSet instancesSet;

	public QuantumModel(Setting setting, InstancesSet instancesSet) {
		this.setting = setting;
		this.instancesSet = instancesSet;
		this.iter = setting.iteration;
		this.popSize = setting.popSize;
	}

	/*
	 * TODO:�����Ż��㷨�ĵ�����⺯�� RETURN���������ŵĸ���
	 */
	public void run() throws Exception {
		System.out.println("����ģ��->Starting...");
		// 1. ��ʼ����Ⱥ
		initializePopulation();
		int T = iter;
		do {

			// System.out.println("��"+(iter-T+1)+"�ε���");
			// 2. �����еĸ�����й۲�
			for (int i = 0; i < population.length; ++i) {
				if (iter - T + 1 == 3) {
					population[i].watchByPhase();
				}
			}
			// 3. ���ݹ۲�������ÿ�������������
			// a.����flag���飬�����ݼ����л�ϲ���
			// c.�����µ����ݼ����з��࣬�õ�������
			for (int i = 0; i < population.length; ++i) {
				population[i].mixedSampling();
				population[i].calFitness(setting.cls);
			}

			// 4.������Ӧ�ȱ�����Ѹ���
			if (T == iter) {
				gBestIndividual = (Individual)getLocalBestIndividual().deepCopy();
			} else {
				lBestIndividual = getLocalBestIndividual();
				if (lBestIndividual.fitness > gBestIndividual.fitness) {
					gBestIndividual = (Individual)lBestIndividual.deepCopy();
				}
			}
			// 5.���������Ÿ���ÿһ������
			for (int i = 0; i < population.length; ++i) {
				//1.���ȸ������Ÿ���͵�ǰ���������ת�������ת�Ƕ�
				double angle[] = updateAngle(population[i], gBestIndividual);
				//2.������ת�Ƕ�����λ������ת
				population[i].phaseRotate(angle);
			}
			T--;
		} while (T != 0);
		System.out.println("����ģ��->End...");
		System.out.println("������Ÿ���");
		System.out.println(gBestIndividual.fitness);
		System.out.println("�㷨���н���");
	}
	
	/*
	 * TODO: ���ݸ���currentIndividual��ȫ������globalBestIndividual������ת�Ƕ�
	 * RETURN: ���ظ��º����ת�Ƕ�
	 * */
	public double[] updateAngle(Individual currIndividual, Individual globalIndividual) {
		//1. ���ȸ��ݵ�ǰ��������Ÿ��������ת����
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
	 * TODO: �������������ĺ�������
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
	 * TODO: �������ġ����ڸĽ����ӽ����㷨������ѡ���е�ת����Ա�õ���ǰ����ÿһ������λ��ת��
	 * RETURN: -1: ��ʱ�� +1: ˳ʱ��  0: ����Ҫ��ת
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
	 * TODO: ����alpha, beta��������ת������ձ�
	 * */
	/*
	 * TODO: ����ÿ���������ת�Ƕ�
	 * RETURN: ���ص�ǰ����������һ����תʱ����ת�Ƕ�
	 * */
	public double calAngleForIndividual() {
		double angle = 0;
		return angle;
	}
	/*
	 * TODO:��ʼ����Ⱥ{����ʵ����population��ÿһ��������󣬵��ó�ʼ������} RETURN���޸�population����
	 */
	public void initializePopulation() {
		System.out.println("��ʼ����Ⱥ->Starting...");
		population = new Individual[popSize];
		for (int i = 0; i < popSize; ++i) {
			population[i] = new Individual(setting, instancesSet);
			population[i].initializeIndividual();
		}
		System.out.println("��ʼ����Ⱥ->End...");
	}

	/*
	 * TODO:����Ⱥ��ѡ���ֲ���Ѹ��� 
	 * RETURN�����ؾֲ���Ѹ���
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
		FileWriter fw = new FileWriter("dataset/���ڱ߽��ʵ����NB.dat", true);
		for (int set = 0; set < dataSets.length; ++set) {
			System.out.println("��ǰ�������ݼ�Ϊ��"+dataSets[set]);
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
							System.out.println("����������ģ��Ҫ������ʧ��");
						}
					}
					
					if(maxFitness < sum) {
						maxFitness = sum;
						maxK = tempK;
						maxNoiseK = tempNoiseK;
					}
				}
			}
			fw.write(dataSets[set] + ":" + maxFitness / 5 + " K��"+maxK+"NoiseK:"+maxNoiseK);
			fw.write('\n');
			System.out.println("���յ����Ž��Ϊ��" + maxFitness / 5);
		}
		fw.close();
	}
}
