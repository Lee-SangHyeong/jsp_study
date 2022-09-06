package com.leesh.web.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;

@WebFilter("/*")
public class CharacterEncodingFilter implements Filter {

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		// TODO Auto-generated method stub

		//System.out.println("before filter(학습중...3)");
		request.setCharacterEncoding("UTF-8");
		
		chain.doFilter(request, response);
		
		//System.out.println("after filter(학습중...5)");
	}

}
