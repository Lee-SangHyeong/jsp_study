package com.leesh.web.controller;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/index")
public class IndexController extends HttpServlet {
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		//super.doGet(req, resp);
		//redirect--> 현재와 상관없이 이동
		//forward --> 현재 작업 상황을 갖고 감
		RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/view/index.jsp");
		dispatcher.forward(request, response);
		
	}

}
