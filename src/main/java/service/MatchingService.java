package service;

import bean.ResultBean;
import plan.PlanDirector;

public class MatchingService {

	public static void main(String[] args) {
		
		ResultBean finalResult = PlanDirector.getInstance().start();
		
		//��ʾ����
		System.out.println("======================================================================================");
		System.out.println("�ܼƻ�����=" + finalResult.getName() + ", ���յ÷�=" + finalResult.getScore() + ", ����������=" + finalResult.getState() + ", ������ʱ��=" + finalResult.getTime());
	}
}
