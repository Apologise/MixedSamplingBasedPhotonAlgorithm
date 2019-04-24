package 量子混合采样多分类;

public class Main {
	public static void moveOddToEven(int[] num) {
		int odd = 0; //用来寻找奇数的个数,同时odd也是在找到奇数时，与num[odd]调换的数
		for(int i = 0; i < num.length; ++i) {
			if(num[i]%2 == 1) {
				//先与num[odd]交换
				int temp = num[odd];
				num[odd] = num[i];
				num[i] = temp;
				odd++;
			}
		}
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int[] num = {2,1,1,4,5,7,8,94,0};
		moveOddToEven(num);
		for(int i = 0; i < num.length; ++i) {
			System.out.print(num[i]+" ");
		}
	}

}
