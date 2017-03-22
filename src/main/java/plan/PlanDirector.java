package plan;

import java.io.File;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import bean.ActionBean;
import bean.ResultBean;
import constants.AsbruAttribute;
import constants.AsbruNode;
import constants.Type;

//plan�����࣬����������Ʒֹ��̵�ִ��
public class PlanDirector {
	
	//����
	private static PlanDirector instance;
	
	private final String xmlFilePath = "morning.xml";
	
	//�Ʒֳ���
	private final float TOTAL_SCORE = 100.0f; //�ܷ���
	private final float PENAL_ACTION_RATE = 0.2f; //�ͷ���
	
	private int totalActionNum = 0; //��action��
	private float gainedActionScore = 0.0f; //ÿ��action��ֵ
	private float penalActionScore = 0.0f;  //ÿ��action��ʱ��˳�򲻶�ʱ�ĳͷ���
	
	//�ƻ���ر���
	private Plan rootPlan;
	public static Map<String, ActionBean> inputActionMap; //��action����
	private Map<String, Plan> expandedPlanMap; //ֻ������Plan������Action
	
	
	public PlanDirector() {
		
		inputActionMap = new HashMap<String, ActionBean>();
		this.expandedPlanMap = new HashMap<String, Plan>();
	}
	
	//������ȡ
	public static PlanDirector getInstance() {
		if(instance == null) instance = new PlanDirector();
		return instance;
	}
	
	//��ʼ��
	public ResultBean start(){
		
		//1.��ȡ���ݿ�õ�input
		readInputActions();
		
		//2.����xml�õ�rootPlan
		buildRootPlan();
		
		//3.�������
		initScore();
		
		//4.ƥ�俪ʼ!
		return rootPlan.execute();		
	}
	
	//�����ݿ�����û�����Ķ���
	private void readInputActions() {
		
		List<ActionBean> actionBeanList = new ArrayList<ActionBean>();
		
		//getting up-seq
			actionBeanList.add(new ActionBean("enter bathroom", new Timestamp(1)));
			actionBeanList.add(new ActionBean("wash hands", new Timestamp(2)));
			actionBeanList.add(new ActionBean("brush teeth", new Timestamp(3)));
			actionBeanList.add(new ActionBean("wash face", new Timestamp(4)));
			actionBeanList.add(new ActionBean("leave bathroom", new Timestamp(5)));
		
		//breakfast-seq
			actionBeanList.add(new ActionBean("enter kitchen", new Timestamp(6)));
		
			//prepare breakfast-seq
				actionBeanList.add(new ActionBean("open fridge", new Timestamp(7)));
				actionBeanList.add(new ActionBean("find food", new Timestamp(8)));
				actionBeanList.add(new ActionBean("close fridge", new Timestamp(9)));
				actionBeanList.add(new ActionBean("cook food", new Timestamp(10)));
		
			actionBeanList.add(new ActionBean("eat", new Timestamp(11)));
			actionBeanList.add(new ActionBean("leave kitchen", new Timestamp(12)));
		
		//run
			actionBeanList.add(new ActionBean("run", new Timestamp(13)));
		
		//entertainment-par
			actionBeanList.add(new ActionBean("sing", new Timestamp(14)));
			actionBeanList.add(new ActionBean("play game", new Timestamp(15)));
			actionBeanList.add(new ActionBean("watch TV", new Timestamp(16)));
		
		actionBeanList.add(new ActionBean("read book", new Timestamp(17)));
		
		//cleaning-seq
			actionBeanList.add(new ActionBean("open window", new Timestamp(18)));
		
			//cleaning floor-seq
				actionBeanList.add(new ActionBean("sweep floor", new Timestamp(19)));
				actionBeanList.add(new ActionBean("mod floor", new Timestamp(20)));
			
			//wipe furniture
			actionBeanList.add(new ActionBean("wipe furniture", new Timestamp(21)));
			
			//empty trash
			actionBeanList.add(new ActionBean("empty trash", new Timestamp(22)));
		
		//washing clothes-seq
			actionBeanList.add(new ActionBean("enter washroom", new Timestamp(23)));
			actionBeanList.add(new ActionBean("take dirty clothes", new Timestamp(24)));
			actionBeanList.add(new ActionBean("wash clothes", new Timestamp(25)));
			actionBeanList.add(new ActionBean("wring clothes", new Timestamp(26)));
			actionBeanList.add(new ActionBean("leave washroom", new Timestamp(27)));
			//actionBeanList.add(new ActionBean("sun clothes", new Timestamp(28)));	
		
		
		//����inputActionMap��
		for(ActionBean actionBean : actionBeanList)
			inputActionMap.put(actionBean.getName(), actionBean);
	}
	
	//��ȡxml�ļ�·���õ�rootPlan
	private void buildRootPlan(){
		
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
	    //traverse(rootPlan);
	}
	
	//����node�ڵ㣬����parentPlan��Ϊ���ƻ�������ע�������������
	private void analyzeNode(Element node, Plan parentPlan){
		
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
			(parentPlan.getMandaPlanSet()).add(AsbruNode.all); //parentPlan.demand����������ȫ������ǿ�Ƶ���plan��
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
				
				Action action = new Action(actionName); //����һ���µ�plan�������ǿյģ����������ɽ��������ݹ��Լ�ʱ��ȡ�������new������ֻ��Ϊ������parentPlan�Ĺ�ϵ
				(parentPlan.getSubPlanList()).add(action); //���븸plan���б���	
				
				totalActionNum++; //action����1
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
	
	//����ÿ��actionƥ���ĵ÷���ͷ���
	private void initScore(){
		gainedActionScore = TOTAL_SCORE / totalActionNum;
		penalActionScore  = gainedActionScore * PENAL_ACTION_RATE;
	}
	
	//����ڴ���
	private void traverse(PlanBase plan) {

		System.out.println(plan.toString());
		if(plan.getType().equals(Type.PLAN)) {
			for (PlanBase subPlan : ((Plan) plan).getSubPlanList())
				traverse(subPlan);
		}
	}
	
	//�����ݿ�д��Ʒֽ������plan.execute()�е��ã�
	public static void writeResult(ResultBean resultBean){
		
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
