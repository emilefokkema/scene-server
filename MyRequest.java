package webserver;
import java.util.ArrayList;
enum HTTPMethod {GET, POST}
interface Request{
	HTTPMethod getHttpMethod();
	String getResourcePath();
	public ArrayList<String> getHeaderParameterNames();
	public String getHeaderParameterValue(String s);
	public ArrayList<MyParameter> getHeaderParams();
	public MyQuery getResourcePathQuery();
	public Request setPostQuery(String line);
	public MyQuery getPostQuery();
	public ArrayList<Cookie> getCookies();
	public String getCookieValue(String name);
	public LastNBooks getLastNBooks(int n);
}
public class MyRequest implements Request{
	private HTTPMethod method;
	private String resourcePath;
	private ArrayList<MyParameter> headerParams;
	private ArrayList<Cookie> cookies;
	private MyQuery resourcePathQuery;
	private MyQuery postQuery;
	public MyRequest(ArrayList<String> lines){
		String[] parts=lines.get(0).split("\\s");
		this.method=parts[0].equals("GET")?HTTPMethod.GET:HTTPMethod.POST;
		this.resourcePath=parts[1];
		String[] part1parts=parts[1].split("\\?");
		if(part1parts.length>1){
			this.resourcePath=part1parts[0];
			this.resourcePathQuery=new MyQuery(part1parts[1]);
			}
		this.headerParams=new ArrayList<MyParameter>();
		this.cookies=new ArrayList<Cookie>();
		for(int i=1;i<lines.size();i++){
			MyParameter p=new MyParameter(lines.get(i), ":\\s");
			this.headerParams.add(p);
			if(p.getName().equals("Cookie")){
				this.cookies=MyCookie.cookieList(p.getValue());
			}
		}
		
	}
	public ArrayList<MyParameter> getHeaderParams(){return this.headerParams;}
	public ArrayList<String> getHeaderParameterNames(){
		ArrayList<String> names=new ArrayList<String>();
		for(int i=0;i<this.headerParams.size();i++){names.add(headerParams.get(i).getName());}
		return names;
	}
	public String getHeaderParameterValue(String s){
		for(int i=0;i<this.headerParams.size();i++){
			if(headerParams.get(i).getName().equals(s)){return headerParams.get(i).getValue();}
		}
		return null;
	}
	public LastNBooks getLastNBooks(int n){
		LastNBooks l;
		String lastNBooks=getCookieValue("lastnbooks");
		if(lastNBooks==null){
			l=new LastNBooks(n);
		}else{
			l=new LastNBooks(lastNBooks);
		}
		return l;
	}
	public ArrayList<Cookie> getCookies(){return this.cookies;}
	public HTTPMethod getHttpMethod(){
		return this.method;
	}
	public String getResourcePath(){
		return this.resourcePath;
	}
	public String getCookieValue(String name){
		for(int i=0;i<this.cookies.size();i++){
			if(this.cookies.get(i).getName().equals(name)){return this.cookies.get(i).getValue();}
		}
		return null;
	}
	public MyRequest setPostQuery(String line){
		this.postQuery=new MyQuery(line);
		return this;
	}
	public MyQuery getPostQuery(){return this.postQuery;}
	public MyQuery getResourcePathQuery(){return this.resourcePathQuery;}
	public String toString(){
		return ""+this.method+" "+this.resourcePath;
	}
}
class MyParameter{
	private String name, value;
	public MyParameter(String line, String delimiter){
		String[] pair=line.split(delimiter);
		this.name=pair[0];
		this.value=pair[1];
	}
	public String getName(){return this.name;}
	public String getValue(){return this.value;}
}
