package webserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class MyWebserver implements Runnable{
	private WebApplication a;
	public MyWebserver(WebApplication a){
		this.a=a;
	}
	
	public void run(){
		ServerSocket socket;
		try{
			socket=new ServerSocket(9090);
			while(true){
				Socket newConnection=socket.accept();
				Thread thread=new Thread(new ConnectionHandler(newConnection, a));
				thread.start();
			}
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}
}
