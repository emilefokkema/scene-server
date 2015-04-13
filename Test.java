package webserver;



public class Test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		WebApplication a=new MyWebApplication();
		
		new MyWebserver(a).run();
		
		
	}

}
