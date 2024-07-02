package com.slgames.store.infra;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

public class CachedBodyHttpRequest extends HttpServletRequestWrapper {

	private String body;
	
	public CachedBodyHttpRequest(HttpServletRequest request) {
		super(request);
		StringBuffer sb = new StringBuffer();
		try(InputStream inputStream = request.getInputStream();){
			int b;
			while ((b = inputStream.read()) != -1) {
				sb.append((char) b);
			}
			body = sb.toString();
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
	}

	@Override
	public ServletInputStream getInputStream() throws IOException {
		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(body.getBytes(StandardCharsets.UTF_8));
		return new ServletInputStream() {
			
			@Override
			public int read() throws IOException {
				
				return byteArrayInputStream.read();
			}
			
			@Override
			public void setReadListener(ReadListener listener) {
				
			}
			
			@Override
			public boolean isReady() {
				
				return true;
			}
			
			@Override
			public boolean isFinished() {
				return byteArrayInputStream.available() == 0;
			}
		};
	}

	@Override
	public BufferedReader getReader() throws IOException {
		// TODO Auto-generated method stub
		return new BufferedReader(new InputStreamReader(this.getInputStream(), StandardCharsets.UTF_8));
	}

}
