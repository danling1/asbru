package adl;

import java.io.File;
import java.sql.Timestamp;
import java.util.List;

import org.apache.http.impl.conn.ProxySelectorRoutePlanner;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.amazon.speech.speechlet.State;
import com.amazonaws.services.s3.internal.RestUtils;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.sun.corba.se.spi.orbutil.fsm.Guard.Result;
import com.sun.jmx.remote.util.OrderClassLoaders;
import com.sun.xml.internal.fastinfoset.alphabet.BuiltInRestrictedAlphabets;

public class DomTest {

	public static void main(String[] args) throws DocumentException {
		new Solution().start();
	}

}

class Solution {
	void start() throws DocumentException {
		SAXReader sax = new SAXReader();// ����һ��SAXReader����
		File xmlFile = new File("Plan1.xml");// ����ָ����·������file����
		Document document = sax.read(xmlFile);// ��ȡdocument����,����ĵ��޽ڵ㣬����׳�Exception��ǰ����
		Element root = document.getRootElement();// ��ȡ���ڵ�

		getNodes(root);// �Ӹ��ڵ㿪ʼ�������нڵ�
	}

	public void getNodes(Element node) {
		System.out.println("--------------------");

		// ��ǰ�ڵ�����ơ��ı����ݺ�����
		System.out.println("��ǰ�ڵ����ƣ�" + node.getName());// ��ǰ�ڵ�����
		System.out.println("��ǰ�ڵ�����ݣ�" + node.getTextTrim());// ��ǰ�ڵ�����
		List<Attribute> listAttr = node.attributes();// ��ǰ�ڵ���������Ե�list
		for (Attribute attr : listAttr) {// ������ǰ�ڵ����������
			String name = attr.getName();// ��������
			String value = attr.getValue();// ���Ե�ֵ
			System.out.println("�������ƣ�" + name + "����ֵ��" + value);
		}

		// �ݹ������ǰ�ڵ����е��ӽڵ�
		List<Element> listElement = node.elements("plans");// ����һ���ӽڵ��list
		for (Element e : listElement) {// ��������һ���ӽڵ�
			List<Element> plans = e.elements("plan");// ����һ���ӽڵ��list
			for (Element plan : plans) {// ��������һ���ӽڵ�
				this.getNodes(plan);// �ݹ�
			}
		}
	}
	
	
	//������node�ڵ�
	//node��parentPlan�Ѿ�����һ�㹹�����˴�����
	//��ʼ���õ�ʱ������д��
	//Plan rootPlan = new Plan("��plan��",type);
	//expandedPlansMap.put("��plan��", rootPlan);
	//buildPlan(<plans>�ڵ㣬 rootPlan)
	public void buildPlan(Element node, Plan parentPlan){
		
		//ȡ�ڵ�����
		String nodeName = node.getName();
		
		//���ݽڵ����ֲ�ͬ���в�ͬ����
		//δ����ı�ǩ����plan-body��wait-for��wait-for-group��plan-activation�Ƕ�
		//parentPlanû��Ӱ��͸ı�ģ���˲��ô������ǣ�ֱ��������δ��룬�����¶Σ�ȡ�����ӽڵ�
		if("plan".equals(nodeName)){
			try{
				String planName = node.attribute("name").getValue();// ��plan.name
				String planTitle = node.attribute("title").getValue();// ��plan.title
				
				//�鿴��ǰplan�Ƿ��ѱ���չ��
				if(expandedPlansMap.containsKey(planName)){ //�ѱ���չ
					Plan curParentPlan = expandedPlansMap.get(planName);
					parentPlan = curParentPlan;
				} else { //δ����չ���µ�
					Plan curParentPlan = new Plan(planName, planTitle, type, ...);
					expandedPlansMap.put(planName, curParentPlan);
					parentPlan = curParentPlan;
				}
			} catch(Exception e){
				System.out.println("plan��ǩȱ��name��title���ԣ������");
			}
		} else if("subplans".equals(nodeName)){
			try{
				String order = node.attribute("type").getValue();// parentPlan.order
				parentPlan.setOrder(order);
			} catch(Exception e){
				System.out.println("subplans��ǩȱ��type���ԣ������");
			}
		} else if("all".equals(nodeName)) {
			(parentPlan.mandaPlansSet).add("All"); //parentPlan.demand����������ȫ������ǿ�Ƶ���plan��
			return; //�ײ��ǩ��û�������ˣ� ���Է�����
		} else if("static-plan-pointer".equals(nodeName)) {
			try{
				String subPlanName = node.attribute("plan-name").getValue();// parentPlan.demand
				(parentPlan.mandaPlansSet).add(subPlanName);
			} catch(Exception e){
				System.out.println("static-plan-pointerȱ��plan-name���ԣ������");
			}
			return; //�ײ��ǩ,����
		} else if("plan-schema".equals(nodeName)) {
			try{
				String subPlanName = node.attribute("plan-name").getValue();// ��plan.name
				Plan subPlan = new Plan(subPlanName, type); //����һ���µ�plan�������ǿյģ����������ɽ��������ݹ��Լ�ʱ��ȡ�������new������
				 								   //ֻ��Ϊ������parentPlan�Ĺ�ϵ
				expandedPlansMap.put(subPlanName, subPlan);
				parentPlan.add(subPlan); //���븸plan���б���	
			} catch(Exception e){
				System.out.println("plan-schemaȱ��name���ԣ������");
			}
			return; //�ײ��ǩ,����
		} else if("variable-assignment".equals(nodeName)) {
			try{		
				String actionName = node.attribute("variable").getValue();// action.name
				(parentPlan.mandaPlansSet).add(actionName); //action����ײ㣬����ǿ��
				
				Action action = new Action(actionName, type); //����һ���µ�plan�������ǿյģ����������ɽ��������ݹ��Լ�ʱ��ȡ�������new������
				 								   //ֻ��Ϊ������parentPlan�Ĺ�ϵ
				parentPlan.add(action); //���븸plan���б���	
			} catch(Exception e){
				System.out.println("variable-assignmentȱ��variable���ԣ������");
			}
			return; //�ײ��ǩ,����
		}
	
		//��node���µ�����һ���ӽڵ���Ϊ�µĸ��ڵ㣬��ȱ���
		List<Element> subNodeList = node.elements();
		for (Element subNode : subNodeList)
			buildPlan(subNode, parentPlan);
	}
	
	
	//Plan.execute()
	Result execute() {
		
		Result result;
		if("sequential".equals(this.order))
			result = seqExecute();
		else
			result = parExecute();
		
		return result;
	}

//��ʱ��˳���йص�ִ�в�������plan�����ʱ��˳��Բ��������÷� + �����������״̬��
Result seqExecute(){
	
	int state_completed_count = 0; //��plan����ɵ���plan����
	int state_considered_count = 0; //��plan��һ��δ��ɵ���plan����
	int score = 0; //�÷�
	Timestamp time = -1; //��plan�Ĵ����ص�ʱ���
	
	//�����������е�plan
	for(PlanBase subpPlan : subPlans) {
		Result result = subPlan.execute();
		
		//1.�����÷ֲ��ۼӵ÷�
		boolean penalty = false; //�Ƿ���ʱ��˳�򲻶Ա��۷ֵı�ʶ 
		if(result.time >= time){ //ʱ����֮�󣬶Եģ�����time
			time = result.time;
		} else { //ʱ����֮ǰ�����ԣ�������time���۷�
			result.score -= PlanManager.ACTION_SCORE_PENALTY;
			penalty = true
		}
		score += result.score;
		
		//2.�������״̬
		if(penalty == false && result.state == State.COMPLETED){ //�����
			state_completed_count++;
		} else if(!(this.mandaPlansSet).contains("All")
			   && !(this.mandaPlansSet).contains(subPlan.name)) { //��ȻsubPlanδ��ɣ���������ǿ�Ƶģ�Ҳ��������ˣ����÷��ǲ���ӵ���
			state_completed_count++; 
		} else if(result.state == State.CONSIDERED){ //δ���
			state_considered_count++;
		}
		
		//3.��Resultд�����ݿ�
		//%%%%%%%%%%%%%%%%%%%
		
	}
	
	//4.��װ�� �÷�+���״̬�����ɵ�ǰ�����plan��result
	int state = State.ACTIVITED; //��ʼ��Ϊ�������
	if(state_completed_count == subPlans.size()) //ȫ�����
		state = State.COMPLETED;
	else if(state_considered_count == subPlans.size()) //ȫ��δ���
		state = State.CONSIDERED;
	
	return new Result(score, state, time); //��Ϊ��ʱ��˳���йأ�����ʱ��������һ����Ч��plan�����ʱ��time
}
	
	//��ʱ��˳���޹ص�ִ�в�������plan�����������״̬��
	Result parExecute(){
		
		int state_completed_count = 0; //��plan����ɵ���plan����
		int state_considered_count = 0; //��plan��һ��δ��ɵ���plan����
		int score = 0; //�÷�
		
		//�����������е�plan
		for(PlanBase subpPlan : subPlans) {
			Result result = subPlan.execute();
			
			//1.�ۼӵ÷�
			score += result.score;
			
			//2.�������״̬
			if(result.state == State.COMPLETED){ //�����
				state_completed_count++;
			} else if(!(this.mandaPlansSet).contains("All")
				   && !(this.mandaPlansSet).contains(subPlan.name)) { //��ȻsubPlanδ��ɣ���������ǿ�Ƶģ�Ҳ��������ˣ����÷��ǲ���ӵ���
				state_completed_count++; 
			} else if(result.state == State.CONSIDERED){ //δ���
				state_considered_count++;
			}
			
			//3.��Resultд�����ݿ�
			//%%%%%%%%%%%%%%%%%%%
		}
		
		//��װ �÷�+���״̬�����ɵ�ǰ�����plan��result
		int state = State.ACTIVITED; //��ʼ��Ϊ�������
		if(state_completed_count == subPlans.size()) //ȫ�����
			state = State.COMPLETED;
		else if(state_considered_count == subPlans.size()) //ȫ��δ���
			state = State.CONSIDERED;
		
		return new Result(score, state, -1); //��Ϊ��ʱ��˳���޹أ�����ʱ�����-1
	}
	
	
	
	
	
	
	
	
	//Action.execute()
	Result execute(){
		
		int score; //�÷�
		State state; //���״̬
		Timestamp time = -1; //ʱ��
		
		//�鿴���붯��input���Ƿ��и�action�� ������ƥ��
		if(inputActionsMap.containsKey(this.name)){
			score = PlanManager.ACTION_SCORE;
			state = State.COMPLETED;
			time = inputActionsMap.get(this.name); //��ȡʱ��
		} else {
			score = 0;
			state = State.CONSIDERED;
		}
	
		return new Result(score, state, time);
	}
	
	
}

