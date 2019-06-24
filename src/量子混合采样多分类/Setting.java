package ���ӻ�ϲ��������;

import java.io.Serializable;

public class Setting implements Serializable{
	public int K = 5;
	public int noisyK = 5;
	public double underSampleRatio = 0.3;
	public int popSize;
	public int iteration;
	public double maxRotateAngle;	//��ʼ�����ת�Ƕ�
	public double minRotateAngle;
	public Enum_Classifier cls;
	public Setting(int popSize, int iteration, Enum_Classifier cls) {
		this.popSize = popSize;
		this.iteration = iteration;
		this.maxRotateAngle = Math.PI*0.05;
		this.minRotateAngle = Math.PI*0.001;
		this.cls = cls;
	}
}
