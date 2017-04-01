package plan;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import bean.ActionBean;
import bean.ResultBean;
import constants.ResultState;
import constants.Type;
import dao.ResultDAO;

//������
public class Action extends PlanBase{

	Action(String name) {
		super(name);
		this.type = Type.ACTION;
	}

	@Override
	ResultBean execute(int taskId, Timestamp beginTime) {
		
		PlanDirector planDirector = PlanDirector.getInstance();
		
		String state; //���״̬
		float score = 0; //�÷�
		float fullScore = planDirector.getGainedActionScore(); //����
		Timestamp finishTime = beginTime;
		float belief = 0.0f; //���Ŷ�
		StringBuilder reason = new StringBuilder(""); //��������
		
		//�鿴���붯��input���Ƿ��и�action�� ������ƥ��
		Map<String, List<ActionBean>> inputActionMap = planDirector.getInputActionMap();
		if(inputActionMap.containsKey(this.name) && inputActionMap.get(this.name).size() > 0){
			score = fullScore;
			state = ResultState.COMPLETED;
			finishTime = inputActionMap.get(this.name).get(0).getTime(); //��ȡʱ��
			inputActionMap.get(this.name).remove(0); //ɾ�������붯������������ѯ����
			belief = 1.0f;
			reason.append(this.name).append(" - <b class='text-success'>completed</b><br>");
		} else {
			score = 0;
			state = ResultState.CONSIDERED;
			belief = 0.0f;
			reason.append(this.name).append(" - <b class='text-error'>considered</b><br>");
		}

		ResultBean resultBean = new ResultBean();
		resultBean.setName(this.name);
		resultBean.setType(this.type);
		resultBean.setScore(score);
		resultBean.setFullScore(fullScore);
		resultBean.setState(state);
		resultBean.setFinishTime(finishTime);
		resultBean.setBelief(belief);
		resultBean.setReason(reason.toString());
		resultBean.setTaskId(taskId);
		
		ResultDAO.write(resultBean);
		return resultBean;
	}

}
