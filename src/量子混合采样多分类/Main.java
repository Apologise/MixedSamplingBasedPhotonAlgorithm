package 量子混合采样多分类;

import java.io.FileWriter;

public class Main {
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		String[] dataSets = { "glass1", "pima", "glass0", "yeast1", "vehicle1", "glass0123vs456", "ecoli1",
				"newthyroid1", "newthyroid2", "ecoli2", "glass6", "yeast3", "ecoli3", "glass016v2", "yeast1v7",
				"glass4", "glass5", "yeast2v8", "yeast4", "yeast6" };
		FileWriter fw = new FileWriter("dataset/实验结果IBk .dat", true);
		for (int set = 0; set < dataSets.length; ++set) {
			System.out.println("当前运行数据集为："+dataSets[set]);
			int maxK = 1, maxNoiseK=2;
			double maxFitness = 0;
			int k = 1, noisyK = 2;
			for (int tempK = 2; tempK < 10; ++tempK) {
				for (int tempNoiseK = 2; tempNoiseK < 10; tempNoiseK++) {
					Setting setting = new Setting(tempK, tempNoiseK, 20, 500, 30);
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
