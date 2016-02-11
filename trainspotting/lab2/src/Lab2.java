import TSim.*;
import java.awt.Point;
public class Lab2 {
	
	public static Point s0 = new Point(15,3),s1 = new Point(6,6),s2 = new Point(11,7),s3 = new Point(15,7),s4  = new Point(19,9),s6 = new Point(12,9),s7 = new Point(7,9),s8 = new Point(1,10),s10 = new Point(6,11),s11 = new Point(15,11),s12 = new Point(8,5),s13 = new Point(10,8), s14 = new Point(15,8), s15 = new Point(13,10), s16 = new Point(6,10), s17 = new Point(4,13), s18 = new Point(15,13), s19 = new Point(15,5);
	
	public Lab2(Integer speed1, Integer speed2) {
		TSimInterface tsi = TSimInterface.getInstance();
		
		try {
			Train train1 = new Train(speed1, 1);
			Train train2 = new Train(speed2, 2);
			Thread t1 = new Thread(train1);
			Thread t2 = new Thread(train2);

			
			t1.start();
			t2.start();
			

		}catch (CommandException e) {
			e.printStackTrace();    // or only e.getMessage() for the error
			System.exit(1);
		}
	}
}
