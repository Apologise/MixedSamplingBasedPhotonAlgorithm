package 量子混合采样多分类;

import java.util.Random;

public class Test {
	public static void main(String[] args) {
		Random rand = new Random();
		for(int i = 0; i < 10; ++i) {
			
			int value = rand.nextInt(10);
			System.out.println(value);
		}
	}
}
