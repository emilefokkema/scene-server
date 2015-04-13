package webserver;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
public class ConnectionHandler implements Runnable{
	private Socket socket;
	private ArrayList<String> lines;
	private Request request;
	private Response response;
	private WebApplication a;
	public ConnectionHandler(Socket toHandle, WebApplication a){
		this.socket=toHandle;
		this.a=a;
	}
	public void run(){
		
		try{
			BufferedReader reader=new BufferedReader(new InputStreamReader(socket.getInputStream()));
			lines=new ArrayList<String>();
			String line=reader.readLine();
			System.out.println();
			while(!(line==null||line.isEmpty())){
				lines.add(line);
				line=reader.readLine();
			}
			response=new MyResponse().setStatus(HTTPStatusCode.OK);
			if(lines.size()>0){
				request=new MyRequest(lines);
			}
			String contentLength;
			if(request!=null&&request.getHttpMethod()==HTTPMethod.POST&&(contentLength=request.getHeaderParameterValue("Content-Length"))!=null){
				if(Integer.parseInt(contentLength)>0){
					line=reader.readLine();
					if(!line.isEmpty()){request.setPostQuery(line);}
				}
			}
			if(request!=null){a.process(request, response);}
			BufferedWriter writer=new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			writer.write(response.toString());
			writer.flush();
			writer.close();
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}
}
