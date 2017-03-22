package plan;

import java.sql.Timestamp;

import com.sun.corba.se.spi.orbutil.fsm.Guard.Result;

import bean.ResultBean;
import constants.ResultState;
import constants.Type;

//������
public class Action extends PlanBase{

	Action(String name) {
		super(name);
		this.type = Type.ACTION;
	}

	@Override
	ResultBean execute() {
		
		String state; //���״̬
		float score = 0; //�÷�
		Timestamp time = new Timestamp(0);
		
		
		//�鿴���붯��input���Ƿ��и�action�� ������ƥ��
		if((PlanDirector.inputActionMap).containsKey(this.name)){
			score = PlanDirector.getInstance().getGainedActionScore();
			state = ResultState.COMPLETED;
			time = (PlanDirector.inputActionMap).get(this.name).getTime(); //��ȡʱ��
			(PlanDirector.inputActionMap).remove(this.name); //ɾ�������붯������������ѯ����
		} else {
			score = 0;
			state = ResultState.CONSIDERED;
		}

		return new ResultBean(this.name, this.type, score, state, time);
	}

}
