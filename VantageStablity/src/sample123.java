


class sample123 implements Runnable {
   private Thread t;
   private String threadName;
   
   int counter =0;
   sample123( String name) {
      threadName = name;
      System.out.println("Creating " +  threadName );
      
   }
   
   
   public void run() {
	   
	   if (counter!=1){
		  
		  System.out.println("hello");
	   
		
		counter++;
		 
		}
	   
	   else{
		   
			   System.out.println("hello22");
		  
		  
		   }
	   while(true){  
	        System.out.println("infinitive while loop"); 
	   
	     String actualgroup= "Vantage - Groups";
	     String expectedgroup = "Vantage - Groups";
		
		System.out.println("Title page"+" : "+actualgroup);
		
		if (actualgroup.contains(expectedgroup))
		{
			System.out.println("Continue performing click operation");
		}
		else{
			System.out.println("Fail the test flow");
		}
		
	
		
		String actualdashboard = "Vantage - Dashboard";
		
		String expecteddashboard = "Vantage - Dashboard";
		
		if (actualdashboard.contains(expecteddashboard))
		{
			System.out.println("Continue performing click operation");
		}
		else{
			System.out.println("Fail the test flow");
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
	      RunnableDemo R1 = new RunnableDemo( "Thread-1");
	      R1.start();
	      
	      RunnableDemo R2 = new RunnableDemo( "Thread-2");
	      R2.start();
	   }   
	}  

