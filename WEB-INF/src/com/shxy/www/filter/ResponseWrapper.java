package com.shxy.www.filter;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
/**
 * 改变response的响应内�?
 * @author shxy
 *
 */
public class ResponseWrapper extends HttpServletResponseWrapper{

	private CharArrayWriter caw=new CharArrayWriter();
	public ResponseWrapper(HttpServletResponse res) {
		super(res);
	}
	
	/*将response对象getWriter方法返回的输出流的目的地进行修改，－�?caw,这样做的目的�?
	 * 不要让servlet把信息直接输出到浏览器，而是先将信息输出到这个目的地中，然后在filter�?
	 * 对输出内容修改之后再输出*/
	@Override
	public PrintWriter getWriter() throws IOException {
		return new PrintWriter(caw);
	}
	public String toString(){
		return caw.toString();
	}
}
