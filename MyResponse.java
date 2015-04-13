package webserver;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import xmlmaker.XmlMaker;
interface Response{
	HTTPStatusCode getStatus();
	Response setStatus(HTTPStatusCode status);
	Calendar getDate();
	String getContent();
	Response setContent(String content);
	Response setContent(ResponseHTML html);
	String toString();
	Response addCookie(Cookie c);
}
enum HTTPStatusCode{
	OK(200, "OK"), NotFound(404, "Not Found"), ServerError(500, "Server Error");
	
	private int code;
	private String description;
	
	private HTTPStatusCode(int code, String description){
		this.code=code;
		this.description=description;
	}
	public int getCode(){
		return this.code;
	}
	public String getDescription(){
		return this.description;
	}
	
}
public class MyResponse implements Response{
	private static Pattern phpPattern=Pattern.compile("<\\?php[\\s\\n\\r](.*[\\n\\r]?)*?\\?>");
	@SuppressWarnings("finally")
	public static String phpOutput(String php){
		String s=null;
		try{
			Runtime rt=Runtime.getRuntime();
			Process proc=rt.exec("C:\\Program Files (x86)\\php\\php.exe -r \""+php+"\"");
			InputStream is=proc.getInputStream();
			BufferedReader br=new BufferedReader(new InputStreamReader(is));
			s=br.readLine();
		}catch(Throwable t){
			t.printStackTrace();
		}finally{
			return s;
		}
	}
	public static String replacePhp(String string){
		StringBuilder sb=new StringBuilder(string);
		Matcher matcher=phpPattern.matcher(string);
		ArrayList<String> old=new ArrayList<String>();
		ArrayList<String> out=new ArrayList<String>();
		int begin, end, oldbegin=0, oldend=0;
		while(matcher.find()){
			begin=matcher.start();
			end=matcher.end();
			oldend=begin;
			old.add(string.substring(oldbegin, oldend));
			oldbegin=end;
			out.add(phpOutput(string.substring(begin+6,end-2)));
		}
		String s="";
		for(int i=0;i<old.size();i++){
			s+=old.get(i)+out.get(i);
		}
		if(oldbegin<string.length()){s+=string.substring(oldbegin,string.length());}
		return s;
	}
	
	private HTTPStatusCode status;
	private String content;
	private ArrayList<Cookie> cookies;
	public MyResponse(){
		this.content="";
		this.cookies=new ArrayList<Cookie>();
	}
	public MyResponse addCookie(Cookie c){
		this.cookies.add(c);
		return this;
	}
	public HTTPStatusCode getStatus(){
		return this.status;
	}
	public MyResponse setStatus(HTTPStatusCode status){
		this.status=status;
		return this;
	}
	public String getContent(){
		return this.content;
	}
	public MyResponse setContent(String content){
		
		this.content=replacePhp(content);
		return this;
	}
	public MyResponse setContent(XmlMaker xml){
		String s=xml.toString();
		return setContent(s);
	}
	public MyResponse setContent(ResponseHTML html){
		return setContent(html.toString());
	}
	public Calendar getDate(){
		return Calendar.getInstance();
	}
	public String toString(){
		ArrayList<String> lines=new ArrayList<String>();
		lines.add("HTTP/1.1 "+this.status.getCode()+" "+this.status.getDescription());
		lines.add("Date: "+new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", new Locale("en")).format(this.getDate().getTime()));
		lines.add("Server: Apache/2.0.63 (Unix) mod_ssl/2.0.63 OpenSSL/0.9.7e PHP/5.2.8");
		lines.add("Connection: close");
		lines.add("Content-Type: text/html; charset=UTF-8");
		for(int i=0;i<this.cookies.size();i++){
			lines.add("Set-Cookie: "+cookies.get(i).toString());
		}
		lines.add("");
		lines.add(this.content);
		String s="";
		for(int i=0;i<lines.size();i++){
			s+=lines.get(i)+"\n";
		}
		return s;
	}
}
