
public class Threadtest {

	
	   
	    public static void main(String[] args) {
	        threadOne.start();
	        threadTwo.start();
	    }
	    static Thread threadOne = new Thread() {
	        public void loop1() {
	            while(true) {
	                System.out.println("1");
	            }
	        }
	    };
	    static Thread threadTwo = new Thread() {
	        public void loop2() {
	            while(true) {
	                System.out.println("2");
	            }
	        }
	    };
	}