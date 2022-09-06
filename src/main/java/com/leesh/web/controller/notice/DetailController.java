package com.leesh.web.controller.notice;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.leesh.web.entity.Notice;
import com.leesh.web.service.NoticeService;

@WebServlet("/notice/detail")
public class DetailController extends HttpServlet {
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//super.doGet(req, resp);
		
		int id = Integer.parseInt(request.getParameter("id"));

		NoticeService service = new NoticeService();
		Notice notice = service.getNotice(id);
		
		request.setAttribute("notice", notice);
		
		//redirect--> 현재와 상관없이 이동
		//forward --> 현재 작업 상황을 갖고 감
		RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/view/notice/detail.jsp");
		dispatcher.forward(request, response);
		
	}
}
