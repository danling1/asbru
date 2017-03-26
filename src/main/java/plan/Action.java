package plan;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import bean.ActionBean;
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
		
		PlanDirector planDirector = PlanDirector.getInstance();
		
		String state; //���״̬
		float score = 0; //�÷�
		float fullScore = planDirector.getGainedActionScore(); //����
		Timestamp time = new Timestamp(0);
		
		//�鿴���붯��input���Ƿ��и�action�� ������ƥ��
		Map<String, List<ActionBean>> inputActionMap = planDirector.getInputActionMap();
		if(inputActionMap.containsKey(this.name) && inputActionMap.get(this.name).size() > 0){
			score = fullScore;
			state = ResultState.COMPLETED;
			time = inputActionMap.get(this.name).get(0).getTime(); //��ȡʱ��
			inputActionMap.get(this.name).remove(0); //ɾ�������붯������������ѯ����
		} else {
			score = 0;
			state = ResultState.CONSIDERED;
		}

		ResultBean resultBean = new ResultBean();
		resultBean.setName(this.name);
		resultBean.setType(this.type);
		resultBean.setScore(score);
		resultBean.setFullScore(fullScore);
		resultBean.setState(state);
		resultBean.setTime(time);

		return resultBean;
	}

}
