package Sample1232.copy;

import org.testng.Assert;

class sample123 implements Runnable {
   private Thread t;
   private String threadName;
   
   int counter =0;
   
   int n=1000;
   sample123( String name) {
      threadName = name;
      System.out.println("Creating " +  threadName );
      
   }
   
   
   public void run() {
	   
	   if (counter==0){
		  
		  System.out.println("hello one time");
	   
		
		counter++;
		 
		}
	   
	   else{
		   
			   System.out.println("hello more than one time");
		  
		  
		   }
	   while(true){  
		   for(int i=0; i<=n; i++){
	        System.out.println("count"+i); 
		   
	     String actualgroup= "Vantage - Groups";
	     String expectedgroup = "Vantage - Groups";
	     Assert.assertEquals(actualgroup, expectedgroup);
		System.out.println("Title page"+" : "+actualgroup);
		
		String actualdashboard = "Vantage - Dashboard";
		
		String expecteddashboard = "Vantage - Dashboard";
		Assert.assertEquals(actualgroup, expectedgroup);
		System.out.println("Title page"+" : "+actualdashboard);
		   }
		
		
	   }
		
		 }
   
   public void start () {
	      System.out.println("Starting " +  threadName );
	      if (t == null) {
	         t = new Thread (this, threadName);
	         t.start ();
	      }
	   }
   

   public static void main(String args[]) {
	   sample123 R1 = new sample123( "Thread-1");
	      R1.start();
	      
	     /* sample123 R2 = new sample123( "Thread-2");
	      R2.start();*/
	   }   
	}  

