package plan;

import bean.ResultBean;
import constants.Type;

//������
public class Action extends PlanBase{

	Action(String name) {
		super(name);
		this.type = Type.ACTION;
	}

	@Override
	ResultBean execute() {
		
		return null;
	}

}
