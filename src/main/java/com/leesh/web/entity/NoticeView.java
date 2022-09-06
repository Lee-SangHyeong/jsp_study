package com.leesh.web.entity;

import java.util.Date;

public class NoticeView extends Notice {
	
	private int commentCnt;

	public NoticeView() {
		// TODO Auto-generated constructor stub
	}
	
	public NoticeView(int id, String title, String writerId, Date regDate, int hit, String files, String content,
			boolean pub, int commentCnt) {
		super(id, title, writerId, regDate, hit, files, content, pub);
		this.commentCnt = commentCnt;
	}

	public int getCommentCnt() {
		return commentCnt;
	}

	public void setCommentCnt(int commentCnt) {
		this.commentCnt = commentCnt;
	}
}
