package 量子混合采样多分类;

import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Random;

public class DataVisualization {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		//对数据集的样本进行填充
		
		RandomAccessFile randomFile = new RandomAccessFile("dataset/iris.2D.arff", "rw");
		long fileLength = randomFile.length();
		randomFile.seek(fileLength);
		
		//为第一类添加数据
		for(int i = 0; i < 100; ++i) {
			 double y = Math.random()*2;
			 double x = Math.random()*2;
			 String string = "\n"+x + ","+y + ",positive";
			 randomFile.write(string.getBytes());
		}
		//为第二类添加数据
		for(int i = 0; i < 100; ++i) {
					 double y = Math.random()*3+3;
					 double x = Math.random()*3+3;
					 String string = "\n"+x + ","+y + ",negative";
					 randomFile.write(string.getBytes());
		}
		//class-overlapping,为第三类添加数据
		for(int i = 0; i <= 200; i++) {
			 double y = Math.random()*6+2;
			 double x = Math.random()*6+2;
			 String string = "\n"+x + ","+y + ",zero";
			 randomFile.write(string.getBytes());
		}
		randomFile.close();
		
		//1. 通过调用InstancesSet类来求每个样本的Margin
		InstanceDao instanceDao = new InstanceDao();
		Setting setting = new Setting(200, 5, 5, 1, 1, Enum_Classifier.C45);
		InstancesSet instancesSet = new InstancesSet("", setting);
		instancesSet.initializeInstancesSet(1);
		//2. 根据样本的值，以及Margin写入到Json文件中
		FileWriter fw = new FileWriter("EchartExample\\dataVisualization.dat");

		//2.1 写入样本点以及margin值
		for(int i = 0; i < instancesSet.originInstances.size(); ++i) {
			fw.write(instancesSet.originInstances.get(i).value(0)+","+instancesSet.originInstances.get(i).value(1)+
					","+(int)instancesSet.originInstances.get(i).classValue()+","+instancesSet.instanceOfMargin.get(i)+"\n");
		}
		fw.close();
		System.out.print("分支Dev测试语句");
		//3.通过JQuery对json文件进行读取，然后调用echart插件对数据进行可视化显示
		/*
        ProcessBuilder proc = new ProcessBuilder("D:\\SoftInstall\\360BS\\360Chrome\\Chrome\\Application\\360chrome.exe","--allow-file-access-from-files"
                ,"EchartExample\\MarginVisualization.html");
        proc.start();
        */
	}

}
