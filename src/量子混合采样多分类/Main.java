package ���ӻ�ϲ��������;

public class Main {
	public static void moveOddToEven(int[] num) {
		int odd = 0; //����Ѱ�������ĸ���,ͬʱoddҲ�����ҵ�����ʱ����num[odd]��������
		for(int i = 0; i < num.length; ++i) {
			if(num[i]%2 == 1) {
				//����num[odd]����
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
