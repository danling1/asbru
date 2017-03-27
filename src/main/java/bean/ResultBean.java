package bean;

import java.sql.Timestamp;

public class ResultBean {
	
	private int id; //���
	private String name; //����
	private String type; //��𣺼ƻ�/����
	private float score; //�÷�
	private float fullScore; //����
	private String state; //������
	private Timestamp finishTime; //���ʱ��
	
	
	@Override
	public String toString() {

		String res = "����=" + name + ", ����=" + type + ", ������=" + state + ", �÷�=" + score + ", ����=" + fullScore + ", ʱ��=" + finishTime;
		return res;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
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
	public Timestamp getFinishTime() {
		return finishTime;
	}
	public void setFinishTime(Timestamp finishTime) {
		this.finishTime = finishTime;
	}
	public float getScore() {
		return score;
	}
	public void setScore(float score) {
		this.score = score;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public float getFullScore() {
		return fullScore;
	}
	public void setFullScore(float fullScore) {
		this.fullScore = fullScore;
	}
}
