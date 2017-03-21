package plan;


import bean.ResultBean;

//�ƻ�����
public abstract class PlanBase {
	
	protected String name;
	protected String type;
	abstract ResultBean execute();
	
	PlanBase(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	} 
	
	@Override
	public String toString() {
		String res = "��������������������������������������������������������������������\n����=" + name + ", ����=" + type + "\n��������������������������������������������������������������������";
		return res;
	}
}
