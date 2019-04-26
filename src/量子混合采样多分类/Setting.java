package 量子混合采样多分类;

public class Setting {
	public int T;
	public int K;
	public int noisyK;
	public int popSize;
	public int iteration;
	public Setting(int T, int K, int noisyK) {
		this.T = T;
		this.K = K;
		this.noisyK = noisyK;
	}
	public static void test() {}
}
