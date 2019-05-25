package 量子混合采样多分类;

import java.io.Serializable;

public class Setting implements Serializable{
	public int K;
	public int noisyK;
	public double underSampleRatio = 0.3;
	public int popSize;
	public int iteration;
	public double maxRotateAngle;	//初始最大旋转角度
	public double minRotateAngle;
	public Enum_Classifier cls;
	public Setting(int K, int noisyK, int popSize, int iteration, double rotateAngle,  Enum_Classifier cls) {
		this.K = K;
		this.noisyK = noisyK;
		this.popSize = popSize;
		this.iteration = iteration;
		this.maxRotateAngle = Math.PI*0.05;
		this.minRotateAngle = Math.PI*0.001;
		this.cls = cls;
	}
}
