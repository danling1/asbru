package plan;

import java.io.File;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.plaf.basic.BasicTreeUI.TreeTraverseAction;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import bean.ResultBean;
import constants.AsbruAttribute;
import constants.AsbruNode;
import constants.Type;

//plan�����࣬����������Ʒֹ��̵�ִ��
public class PlanDirector {
	
	private String xmlFilePath = "Plan1.xml";
	
	//�Ʒֳ���
	private int totalScore;
	private int totalActionNum;
	private float gainedActionScore;
	private float penalActionScore;
	private final float penalActionRate;
	
	//�ƻ���ر���
	private Plan rootPlan;
	private Map<String, Timestamp> inputActionMap;
	private Map<String, Plan> expandedPlanMap; //ֻ������Plan������Action
	
	
	public PlanDirector() {
		
		penalActionRate = 0.2f;
		inputActionMap = new HashMap<String, Timestamp>();
		expandedPlanMap = new HashMap<String, Plan>();
	}
	
	//��ʼ��
	public void start(){
		
		//readInputActions(); //1.��ȡ���ݿ�õ�input
		
		buildRootPlan(); //2.����xml�õ�rootPlan
		//traverse(rootPlan); //����rootPlan,�����ã�
		//rootPlan.execute(); //3.ƥ�俪ʼ!
	}
	
	//�����ݿ�����û�����Ķ���
	public void readInputActions() {
		
	}
	
	//��ȡxml�ļ�·���õ�rootPlan
	public void buildRootPlan(){
		
		SAXReader sax = new SAXReader();
		File xmlFile = new File(this.xmlFilePath);
		Document document = null;
		try { 
			document = sax.read(xmlFile);
		} catch (Exception e) { 
			System.out.println("XML�ļ�������");
		}
		
		//ȡplans�ڵ�
		Element plansNode = null;
		try	{ 
			Element rootNode = document.getRootElement();
			plansNode = (Element) rootNode.elements("plans").get(0);
		} catch (Exception e) { 
			System.out.println("plans�ڵ㲻���ڣ������");
		}
		
		//��ȡ��plan�����֣�����֮���map����ȡ����
		String rootPlanName = null;
		try	{ 
			Element rootPlanNode = (Element) plansNode.elements(AsbruNode.plan).get(0);
			rootPlanName = rootPlanNode.attribute(AsbruAttribute.name).getValue();
		} catch (Exception e) { 
			System.out.println("��һ��plan�ڵ㲻���ڣ������");
		}
		
		//rootPlan��ʼ��
		rootPlan = new Plan(rootPlanName);
		expandedPlanMap.put(rootPlanName, rootPlan);
		
		//��ʼ����
	    analyzeNode(plansNode, rootPlan);
	    
	    //���ԣ����� 
	    traverse(rootPlan);
	}
	
	//����node�ڵ㣬����parentPlan��Ϊ���ƻ�������ע�������������
	public void analyzeNode(Element node, Plan parentPlan){
		
		//ȡ�ڵ�����
		String nodeName = node.getName();
		
		//���ݽڵ����ֲ�ͬ���в�ͬ����
		//δ����ı�ǩ����plan-body��wait-for��wait-for-group��plan-activation�Ƕ�
		//parentPlanû��Ӱ��͸ı�ģ���˲��ô������ǣ�ֱ��������δ��룬�����¶Σ�ȡ�����ӽڵ�
		if((AsbruNode.plan).equals(nodeName)){
			try{
				String planName = node.attribute(AsbruAttribute.name).getValue();// ��plan.name
				
				//�鿴��ǰplan�Ƿ��ѱ���չ��
				if(expandedPlanMap.containsKey(planName)){ //�ѱ���չ
					Plan curParentPlan = expandedPlanMap.get(planName);
					parentPlan = curParentPlan;
				} else { //δ����չ���µ�
					Plan curParentPlan = new Plan(planName);
					expandedPlanMap.put(planName, curParentPlan);
					parentPlan = curParentPlan;
				}
			} catch(Exception e){
				System.out.println("plan��ǩȱ��name��title���ԣ������");
			}
		} else if((AsbruNode.subplans).equals(nodeName)){
			try{
				String order = node.attribute(AsbruAttribute.type).getValue();// parentPlan.order
				parentPlan.setOrder(order);
			} catch(Exception e){
				System.out.println("subplans��ǩȱ��type���ԣ������");
			}
		} else if((AsbruNode.all).equals(nodeName)) {
			(parentPlan.getMandaPlanSet()).add("All"); //parentPlan.demand����������ȫ������ǿ�Ƶ���plan��
			return; //�ײ��ǩ��û�������ˣ� ���Է�����
		} else if((AsbruNode.static_plan_pointer).equals(nodeName)) {
			try{
				String subPlanName = node.attribute(AsbruAttribute.plan_name).getValue();// parentPlan.demand
				(parentPlan.getMandaPlanSet()).add(subPlanName);
			} catch(Exception e){
				System.out.println("static-plan-pointerȱ��plan-name���ԣ������");
			}
			return; //�ײ��ǩ,����
		} else if((AsbruNode.plan_schema).equals(nodeName)) {

			try{
				String subPlanName = node.attribute(AsbruAttribute.name).getValue();// ��plan.name
				Plan subPlan = new Plan(subPlanName); //����һ���µ�plan�������ǿյģ����������ɽ��������ݹ��Լ�ʱ��ȡ�������new������
				 								   //ֻ��Ϊ������parentPlan�Ĺ�ϵ
				expandedPlanMap.put(subPlanName, subPlan);
				(parentPlan.getSubPlanList()).add(subPlan); //���븸plan���б���	
			} catch(Exception e){
				System.out.println("plan-schemaȱ��name���ԣ������");
			}
			return; //�ײ��ǩ,����
		} else if((AsbruNode.variable_assignment).equals(nodeName)) {
			try{		
				String actionName = node.attribute(AsbruAttribute.variable).getValue();// action.name
				(parentPlan.getMandaPlanSet()).add(actionName); //action����ײ㣬����ǿ��
				
				Action action = new Action(actionName); //����һ���µ�plan�������ǿյģ����������ɽ��������ݹ��Լ�ʱ��ȡ�������new������
				 								   //ֻ��Ϊ������parentPlan�Ĺ�ϵ
				(parentPlan.getSubPlanList()).add(action); //���븸plan���б���	
			} catch(Exception e){
				System.out.println("variable-assignmentȱ��variable���ԣ������");
			}
			return; //�ײ��ǩ,����
		}
	
		//��node���µ�����һ���ӽڵ���Ϊ�µĸ��ڵ㣬��ȱ���
		List<Element> subNodeList = node.elements();
		for (Element subNode : subNodeList)
			analyzeNode(subNode, parentPlan);
	}
	
	//����ڴ���
	void traverse(PlanBase plan) {

		System.out.println(plan.toString());
		if(plan.getType().equals(Type.PLAN)) {
			for (PlanBase subPlan : ((Plan) plan).getSubPlanList())
				traverse(subPlan);
		}
	}
	
	//�����ݿ�д��Ʒֽ������plan.execute()�е��ã�
	public static void writeResult(ResultBean resultBean){
		
	}
	
	public int getTotalScore() {
		return totalScore;
	}
	public void setTotalScore(int totalScore) {
		this.totalScore = totalScore;
	}
	public int getTotalActionNum() {
		return totalActionNum;
	}
	public void setTotalActionNum(int totalActionNum) {
		this.totalActionNum = totalActionNum;
	}
	public float getGainedActionScore() {
		return gainedActionScore;
	}
	public void setGainedActionScore(float gainedActionScore) {
		this.gainedActionScore = gainedActionScore;
	}
	public float getPenalActionScore() {
		return penalActionScore;
	}
	public void setPenalActionScore(float penalActionScore) {
		this.penalActionScore = penalActionScore;
	}
}
