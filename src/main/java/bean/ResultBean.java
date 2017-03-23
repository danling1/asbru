package bean;

import java.sql.Timestamp;

public class ResultBean {
	
	private String name; //����
	private String type; //��𣺼ƻ�/����
	private Timestamp time; //���ʱ��
	private float score; //�÷�
	private float fullScore; //����
	private String state; //������

	public ResultBean(String name, String type, float score, float fullScore, String state, Timestamp time) {
		this.name = name;
		this.type = type;
		this.score = score;
		this.fullScore = fullScore;
		this.state = state;
		this.time = time;
	}
	
	@Override
	public String toString() {

		String res = "����=" + name + ", ����=" + type + ", ������=" + state + ", �÷�=" + score + ", ����=" + fullScore + ", ʱ��=" + time;
		return res;
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
	public Timestamp getTime() {
		return time;
	}
	public void setTime(Timestamp time) {
		this.time = time;
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
