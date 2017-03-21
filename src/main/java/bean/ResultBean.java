package bean;

import java.sql.Timestamp;

public class ResultBean {
	
	private String name; //����
	private String type; //��𣺼ƻ�/����
	private Timestamp time; //���ʱ��
	private int score; //�÷�
	private String state; //������

	public ResultBean(String name, String type, Timestamp time, int score, String state) {
		this.name = name;
		this.type = type;
		this.time = time;
		this.score = score;
		this.state = state;
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
	public int getScore() {
		return score;
	}
	public void setScore(int score) {
		this.score = score;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	
}
