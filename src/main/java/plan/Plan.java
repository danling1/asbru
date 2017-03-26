package plan;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import bean.ResultBean;
import constants.AsbruNode;
import constants.Order;
import constants.ResultState;
import constants.Type;

//�ƻ���
public class Plan extends PlanBase{

	private String order;
	private List<PlanBase> subPlanList;
	private Set<String> mandaPlanSet;
	
	Plan(String name) {
		
		super(name);
		type = Type.PLAN;
		order = Order.PARALLEL;
		subPlanList = new ArrayList<PlanBase>();
		mandaPlanSet = new HashSet<String>();
	}
	
	@Override
	ResultBean execute() {
		
		ResultBean resultBean;
		if((Order.SEQUENTIALLY).equals(this.order))
			resultBean = seqExecute();
		else
			resultBean = parExecute();
		
		return resultBean;
	}
	
	//��ʱ��˳���йص�ִ�в�������plan�����ʱ��˳��Բ��������÷� + �����������״̬��
	ResultBean seqExecute(){
		
		int state_completed_count = 0; //��plan����ɵ���plan����
		int state_considered_count = 0; //��plan��һ��δ��ɵ���plan����
		float score = 0; //�÷�
		float fullScore = 0; //����
		Timestamp lasttime = new Timestamp(0); //���ʱ��
		
		//�����������е�plan
		for(PlanBase subPlan : this.subPlanList) {
			ResultBean result = subPlan.execute();
			
			//1.�����÷ֲ��ۼӵ÷�
			boolean penalty = false; //�Ƿ���ʱ��˳�򲻶Ա��۷ֵı�ʶ
			if(!(result.getState()).equals(ResultState.CONSIDERED)) { //����ֻ�����ɻ���������
				if(result.getTime().getTime() >= lasttime.getTime()){ //ʱ����֮�󣬶Եģ�����time
					lasttime = result.getTime();
				} else { //ʱ����֮ǰ�����ԣ�������time���۷�
					result.setScore(result.getScore() - PlanDirector.getInstance().getPenalActionScore());
					penalty = true;
				}
			}
			score += result.getScore();
			fullScore += result.getFullScore();
			
			//2.�������״̬
			if(penalty == false && (result.getState()).equals(ResultState.COMPLETED)){ //�����
				state_completed_count++;
			} else if(!(this.mandaPlanSet).contains(AsbruNode.all)
				   && !(this.mandaPlanSet).contains(subPlan.getName())) { //��ȻsubPlanδ��ɣ���������ǿ�Ƶģ�Ҳ��������ˣ����÷��ǲ���ӵ���
				state_completed_count++; 
			} else if((result.getState()).equals(ResultState.CONSIDERED)){ //δ���
				state_considered_count++;
			}
			
			//3.��Resultд�����ݿ�
			//�����ã�
			System.out.println(result.toString());
		}
		
		//4.��װ�� �÷�+���״̬�����ɵ�ǰ�����plan��result
		String state = ResultState.ACTIVATED; //��ʼ��Ϊ�������
		if(state_completed_count == this.subPlanList.size()) //ȫ�����
			state = ResultState.COMPLETED;
		else if(state_considered_count == this.subPlanList.size()) //ȫ��δ���
			state = ResultState.CONSIDERED;

		ResultBean resultBean = new ResultBean();
		resultBean.setName(this.name);
		resultBean.setType(this.type);
		resultBean.setScore(score);
		resultBean.setFullScore(fullScore);
		resultBean.setState(state);
		resultBean.setTime(lasttime);
		
		return resultBean; //��Ϊ��ʱ��˳���йأ�����ʱ��������һ����Ч��plan�����ʱ��time
	}

	
	//��ʱ��˳���޹ص�ִ�в�������plan�����������״̬��
	ResultBean parExecute(){
		
		int state_completed_count = 0; //��plan����ɵ���plan����
		int state_considered_count = 0; //��plan��һ��δ��ɵ���plan����
		float score = 0; //�÷�
		float fullScore = 0; //����
		Timestamp lasttime = new Timestamp(0);
		
		//�����������е�plan
		for(PlanBase subPlan : this.subPlanList) {
			ResultBean result = subPlan.execute();
			
			//0.��¼ʱ��
			if(result.getTime().getTime() >= lasttime.getTime()) //ʱ����֮��Ÿ���time
				lasttime = result.getTime();
			
			//1.�ۼӵ÷֡�����
			score += result.getScore();
			fullScore += result.getFullScore();
			
			//2.�������״̬
			if((result.getState()).equals(ResultState.COMPLETED)){ //�����
				state_completed_count++;
			} else if(!(this.mandaPlanSet).contains(AsbruNode.all)
				   && !(this.mandaPlanSet).contains(subPlan.getName())) { //��ȻsubPlanδ��ɣ���������ǿ�Ƶģ�Ҳ��������ˣ����÷��ǲ���ӵ���
				state_completed_count++; 
			} else if((result.getState()).equals(ResultState.CONSIDERED)){ //δ���
				state_considered_count++;
			}
			
			//3.��Resultд�����ݿ�
			//�����ã�
			System.out.println(result.toString());
		}
		
		//��װ �÷�+���״̬�����ɵ�ǰ�����plan��result
		String state = ResultState.ACTIVATED; //��ʼ��Ϊ�������
		if(state_completed_count == this.subPlanList.size()) //ȫ�����
			state = ResultState.COMPLETED;
		else if(state_considered_count == this.subPlanList.size()) //ȫ��δ���
			state = ResultState.CONSIDERED;
		
		ResultBean resultBean = new ResultBean();
		resultBean.setName(this.name);
		resultBean.setType(this.type);
		resultBean.setScore(score);
		resultBean.setFullScore(fullScore);
		resultBean.setState(state);
		resultBean.setTime(lasttime);
		
		return resultBean; //��Ϊ��ʱ��˳���޹أ�����ʱ�����-1
	}
	
	
	
	

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}

	public List<PlanBase> getSubPlanList() {
		return subPlanList;
	}

	public void setSubPlanList(List<PlanBase> subPlanList) {
		this.subPlanList = subPlanList;
	}

	public Set<String> getMandaPlanSet() {
		return mandaPlanSet;
	}

	public void setMandaPlanSet(Set<String> mandaPlanSet) {
		this.mandaPlanSet = mandaPlanSet;
	}

	@Override
	public String toString() {
		String res = "��������������������������������������������������������������������\n����=" + name + ", ����=" + type + ", ˳��=" + order + "\n�Ӽƻ�:";
		for(PlanBase subPlan : subPlanList)
			res += subPlan.getName() + ", ";
		res += "\nǿ�Ƽƻ�:";
		for(String mandaPlanName : mandaPlanSet)
			res += mandaPlanName + ",";
		
		return res;
	}
}
