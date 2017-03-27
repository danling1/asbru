package service;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.ibatis.session.SqlSession;

import bean.ResultBean;
import mapper.ResultMapper;
import mapper.SqlSessionHelper;
import plan.PlanDirector;

/**
 * �������activity�������ط����������
 */
public class QueryActivityServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

    public QueryActivityServlet() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		//1.��ͨ����ѯresult�����beginTime��endTime֮���result�Ƿ���֮ǰ�Ѿ���������:
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.set(Calendar.HOUR, 0); //��ȡ��ʼʱ�� 
		calendar.set(Calendar.MINUTE, 0);  
		calendar.set(Calendar.SECOND, 0);  
		calendar.set(Calendar.MILLISECOND, 0);  
		calendar.add(Calendar.DATE, -1); //��Ϊ��ȥ��27�Żص�26��

		Timestamp beginTime = new Timestamp(calendar.getTimeInMillis()); //�õ���ʼʱ��
		calendar.add(Calendar.DATE, 1); 
		Timestamp endTime = new Timestamp(calendar.getTimeInMillis()); //�õ���ֹʱ��
		
		List<ResultBean> resultBeanList = readResults(beginTime, endTime);
		if(resultBeanList == null || resultBeanList.isEmpty()) {
			PlanDirector.getInstance().start(beginTime, endTime);
			resultBeanList = readResults(beginTime, endTime);
		}
		
		//2.�Ѳ�ѯ�����װ��JSON����ǰ��

		
		
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
	
	
	//�����ݿ��ȡbeginTime-endTime֮���result�б�
	private List<ResultBean> readResults(Timestamp beginTime, Timestamp endTime){
		
		SqlSession session = null;
		List<ResultBean> resultBeanList = null;
		try {
			session = SqlSessionHelper.getSessionFactory().openSession();
			ResultMapper mapper = session.getMapper(ResultMapper.class);
			resultBeanList = mapper.selectResultBeanByPeriod(beginTime, endTime);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			session.close();
		}
		
		return resultBeanList;
	}
}
