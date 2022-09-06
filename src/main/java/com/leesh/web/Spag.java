package com.leesh.web;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/spag")
public class Spag extends HttpServlet {
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		int num = 0;
		String num_ = request.getParameter("num");
		if(num_ != null && !num_.equals(""))
			num = Integer.parseInt(num_);
		
		String result = "";
		if (num%2 != 0)
			result = "홀수";
		else
			result = "짝수";
		
		request.setAttribute("result", result);
		
		String[] names = {"사과","배","딸기"};		
		request.setAttribute("names", names);
		
		Map<String, Object> notice = new HashMap<String, Object>();
		notice.put("id", 1);
		notice.put("title", "EL 좋아요...");
		request.setAttribute("notice", notice);		
		
		//redirect--> 현재와 상관없이 이동
		//forward --> 현재 작업 상황을 갖고 감
		RequestDispatcher dispatcher = request.getRequestDispatcher("spag.jsp");
		dispatcher.forward(request, response);
	}
}
