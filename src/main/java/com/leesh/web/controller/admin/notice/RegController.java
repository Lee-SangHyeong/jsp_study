package com.leesh.web.controller.admin.notice;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Collection;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import com.leesh.web.entity.Notice;
import com.leesh.web.service.NoticeService;

// location="/tmp", --> 보통설정 안함
//1024*1024 --> 1M
//1024*1024*50 --> 50M
//1024*1024*50*5 --> 250M
@MultipartConfig(
		fileSizeThreshold = 1024*1024,
		maxFileSize = 1024*1024*50,
		maxRequestSize = 1024*1024*50*5
)
@WebServlet("/admin/board/notice/reg")
public class RegController extends HttpServlet {
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		// super.doGet(req, resp);

		// redirect--> 현재와 상관없이 이동
		// forward --> 현재 작업 상황을 갖고 감
		RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/view/admin/board/notice/reg.jsp");
		dispatcher.forward(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String title = request.getParameter("title");
		System.out.printf("RegController title: %s\n", title);
		
		String content = request.getParameter("content");
		String isOpen = request.getParameter("open");
		boolean pub = false;
		if (isOpen != null)
			pub = true;
		
		Collection<Part> parts = (Collection<Part>) request.getParts();
		StringBuilder sb = new StringBuilder();
		for(Part p : parts) {
			if(!p.getName().equals("file")) continue;
			if(p.getSize() == 0) continue;
			
			Part filePart = p;
			InputStream fis = filePart.getInputStream();
			String fileName = filePart.getSubmittedFileName();
			sb.append(fileName);
			sb.append(",");
			
			String realPath = request.getServletContext().getRealPath("/upload");
			System.out.printf("RegController realPath: %s\n", realPath);
			File path = new File(realPath);
			if(!path.exists()) path.mkdirs(); //부모 폴더까지 함께 생성			

			String filePath = realPath + File.separator + fileName;
			FileOutputStream fos = new FileOutputStream(filePath);
			
			//int b = fis.read();
			byte[] buf = new byte[1024];
			int size = 0;
			while((size=fis.read(buf)) != -1) {
				fos.write(buf, 0, size);
			}
			
			fos.close();
			fis.close();
		}
		
		sb.delete(sb.length()-1, sb.length());	//마지막 ,를 제거하기 위해서
		
		Notice notice = new Notice();
		notice.setTitle(title);
		notice.setContent(content);
		notice.setPub(pub);
		notice.setWriterId("newlec");
		notice.setFiles(sb.toString());

		int result = 0;
		NoticeService service = new NoticeService();
		result = service.insertNotice(notice);

		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/html; charset=UTF-8");

		// sendRedirect 의미: 동일한 주소("/admin/board/notice/reg")에서 
		//     reg --> list 변경하고 doGet() 매소드를 호출 한다의 의미
		//     서버쪽에서 바로 호출하는 방식
		response.sendRedirect("list");

	}
}
