package com.leesh.web.controller.admin.notice;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.leesh.web.entity.NoticeView;
import com.leesh.web.service.NoticeService;

@WebServlet("/admin/board/notice/list")
public class ListController extends HttpServlet {
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String[] openIds = request.getParameterValues("open-id"); //공개로 선택된 id
		String[] delIds = request.getParameterValues("del-id");
		String cmd = request.getParameter("cmd");
		String allIds_ = request.getParameter("allIds");
		System.out.printf("/admin/board/notice/list allIds_: %s\n", allIds_);
		String[] allIds = allIds_.trim().split(" ");	//hidden으로 지정된 모든 all_ids

		int result = 0;
		NoticeService service = new NoticeService();
		
		switch(cmd) {
		case "일괄공개":
			int[] oIds = new int[openIds.length];
			
			for(int i=0; i<openIds.length; i++) {
				System.out.printf("open id: %s\n", openIds[i]);
			}
			
			//1.현재id가 open된 상태냐?
			List<String> lOpenIds = Arrays.asList(openIds);	//배열 --> list로
			List<String> lCloseIds = new ArrayList(Arrays.asList(allIds));	//배열 --> list로
			
			//if(lOpenIds.contains(allIds[i])
			//	//pub -> 1
			//else
			//	//pub -> 0
			
			// allIds(1,2,3,4,5,6,7,8,9,10),  lOpenIds(3,5,8)
			// 아래문장 실행후에는 lCloseIds(1,2,4,6,7,9,10)
			lCloseIds.removeAll(lOpenIds);
			System.out.printf("allIds: %s\n", Arrays.asList(allIds));
			System.out.printf("lOpenIds: %s\n", lOpenIds);
			System.out.printf("lCloseIds: %s\n", lCloseIds);
			
			result = service.pubNoticeAll(lOpenIds, lCloseIds);
			break;
			
		case "일괄삭제":
			int[] dIds = new int[delIds.length];
			for(int i=0; i<delIds.length; i++) {
				System.out.printf("del id: %s\n", delIds[i]);
				dIds[i] = Integer.parseInt(delIds[i]);
			}
			
			result = service.deleteNoticeAll(dIds);
			break;
		}
		
		// sendRedirect 의미: 동일한 주소("/admin/board/notice/list")에서 
		//     list --> list 변경하고 doGet() 매소드를 호출 한다의 의미
		//     서버쪽에서 바로 호출하는 방식
		response.sendRedirect("list");   
		
	}
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//super.doGet(req, resp);
		String field_ = request.getParameter("f");
		String query_ = request.getParameter("q");
		String page_ = request.getParameter("p");
		
		String field = "title";
		if(field_ != null && !field_.equals("")) field = field_;
		String query = "";
		if(query_ != null && !query_.equals("")) query = query_;
		int page = 1;
		if(page_ != null) page = Integer.parseInt(page_);
		
		NoticeService service = new NoticeService();
		List<NoticeView> list = service.getNoticeList(field, query, page);
		int totalCnt = service.getNoticeCount(field, query);
		
		request.setAttribute("list", list);
		request.setAttribute("totalCnt", totalCnt);
		
		//redirect--> 현재와 상관없이 이동
		//forward --> 현재 작업 상황을 갖고 감
		RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/view/admin/board/notice/list.jsp");
		dispatcher.forward(request, response);
		
	}
}
