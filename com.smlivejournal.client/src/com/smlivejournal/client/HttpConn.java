package com.smlivejournal.client;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpConn {
	private int rc;
	private String em;
	
	public HttpConn(){
		super();
	}
	
	public String htmlByUrl(String searchUrl,boolean useCashes) {
		rc=0;
		em="";
		try {
			URL url = new URL(searchUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setUseCaches(useCashes);
			InputStream isr = conn.getInputStream();
			InputStreamReader reader = new InputStreamReader(isr);
			BufferedReader breader = new BufferedReader(reader);
			StringBuilder sb = new StringBuilder();
			String s = "";
			while ((s = breader.readLine()) != null) {
				sb.append(s);
			}
			return sb.toString();
		} catch (Exception e) {
			em = e.toString();
			rc = 1;
			e.printStackTrace();
			return "";
		}
	}
	
	
	
}
