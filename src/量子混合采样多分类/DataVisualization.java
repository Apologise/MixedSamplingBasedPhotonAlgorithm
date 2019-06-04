package ���ӻ�ϲ��������;

import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Random;

public class DataVisualization {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		//�����ݼ��������������
		
		RandomAccessFile randomFile = new RandomAccessFile("dataset/iris.2D.arff", "rw");
		long fileLength = randomFile.length();
		randomFile.seek(fileLength);
		
		//Ϊ��һ���������
		for(int i = 0; i < 100; ++i) {
			 double y = Math.random()*2;
			 double x = Math.random()*2;
			 String string = "\n"+x + ","+y + ",positive";
			 randomFile.write(string.getBytes());
		}
		//Ϊ�ڶ����������
		for(int i = 0; i < 100; ++i) {
					 double y = Math.random()*3+3;
					 double x = Math.random()*3+3;
					 String string = "\n"+x + ","+y + ",negative";
					 randomFile.write(string.getBytes());
		}
		//class-overlapping,Ϊ�������������
		for(int i = 0; i <= 200; i++) {
			 double y = Math.random()*6+2;
			 double x = Math.random()*6+2;
			 String string = "\n"+x + ","+y + ",zero";
			 randomFile.write(string.getBytes());
		}
		randomFile.close();
		
		//1. ͨ������InstancesSet������ÿ��������Margin
		InstanceDao instanceDao = new InstanceDao();
		Setting setting = new Setting(200, 5, 5, 1, 1, Enum_Classifier.C45);
		InstancesSet instancesSet = new InstancesSet("", setting);
		instancesSet.initializeInstancesSet(1);
		//2. ����������ֵ���Լ�Marginд�뵽Json�ļ���
		FileWriter fw = new FileWriter("EchartExample\\dataVisualization.dat");

		//2.1 д���������Լ�marginֵ
		for(int i = 0; i < instancesSet.originInstances.size(); ++i) {
			fw.write(instancesSet.originInstances.get(i).value(0)+","+instancesSet.originInstances.get(i).value(1)+
					","+(int)instancesSet.originInstances.get(i).classValue()+","+instancesSet.instanceOfMargin.get(i)+"\n");
		}
		fw.close();
		System.out.print("��֧Dev�������");
		//3.ͨ��JQuery��json�ļ����ж�ȡ��Ȼ�����echart��������ݽ��п��ӻ���ʾ
		/*
        ProcessBuilder proc = new ProcessBuilder("D:\\SoftInstall\\360BS\\360Chrome\\Chrome\\Application\\360chrome.exe","--allow-file-access-from-files"
                ,"EchartExample\\MarginVisualization.html");
        proc.start();
        */
	}

}
