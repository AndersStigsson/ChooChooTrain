import TSim.*;
import java.awt.Point;
import java.lang.Math;

public class Train implements Runnable {

	private final int LEFT = TSimInterface.SWITCH_LEFT, RIGHT = TSimInterface.SWITCH_RIGHT;
	int speed;
	int id;
	boolean movingUp;
	boolean firstEvent = true;
	TSimInterface tsi;
	public Train(int speed, int id) throws CommandException {
		this.speed = speed;
		this.id = id;
		tsi = TSimInterface.getInstance();
		tsi.setSpeed(id, speed);


	}

	public void run() {
		while(true){
			try {
				Point sensP = new Point(tsi.getSensor(id).getXpos(), tsi.getSensor(id).getYpos());
				if (id == 2) System.out.println(sensP);
				if(firstEvent) {
					//(13,3)
					if(sensP.equals(Lab1.s0)) {
						System.out.println("Train: " + id + " movingUp: " + movingUp);
						Lab1.sem2.acquire();
						movingUp = false;
					} else {
						Lab1.sem6.acquire();
						movingUp = true;
						System.out.println("Train: " + id + " movingUp: " + movingUp);
					}
					firstEvent = false;
				} else {
					//(13,3)
					if(sensP.equals(Lab1.s0)) {
						if(movingUp) {		
							stop();
							wait(Math.min(1000, 1000 * Math.abs(speed)));
							speed = -speed;
							chooChoo();
							firstEvent = true;
						
						} else {
							movingUp = false;
						}
					//(7,7)
					} else if(sensP.equals(Lab1.s1)) {
						if(!movingUp) {
							stop();
							Lab1.sem1.acquire();
							chooChoo();
						} else {
							Lab1.sem1.release();
						}
					//(9,7)
					} else if (sensP.equals(Lab1.s2)) {
						if(movingUp) {
							stop();
							Lab1.sem1.acquire();
							chooChoo();
						} else {
							Lab1.sem1.release();
						}
					//(16,7)
					} else if (sensP.equals(Lab1.s3)) {
						if(!movingUp) {
							stop();
							Lab1.sem3.acquire();
							tsi.setSwitch(17,7,RIGHT);
							chooChoo();
							Lab1.sem2.release();
						} else {
							Lab1.sem3.release();
						}
					//(18,7)
					} else if (sensP.equals(Lab1.s4)) {
						if(movingUp) {
							if(Lab1.sem2.tryAcquire()) {
								Lab1.sem2.acquire();
								tsi.setSwitch(17,7,RIGHT);
							} else {
								tsi.setSwitch(17,7,LEFT);
							}
						} else {
							if(Lab1.sem4.tryAcquire()) {
								Lab1.sem4.acquire();
								tsi.setSwitch(15,9, RIGHT);
							} else {
								tsi.setSwitch(15,9, LEFT);
							}
						}
							
					//(16,9)
					/*} else if (sensP.equals(Lab1.s5)) {
						if(!movingUp) {
							if(Lab1.sem4.tryAcquire()) {
								Lab1.sem4.acquire();
								tsi.setSwitch(15,9, RIGHT);
							} else {
								tsi.setSwitch(15,9, LEFT);
							}
							
						}
						*/
					//(14,9)
					} else if (sensP.equals(Lab1.s6)) {
						if(movingUp) {		
							stop();
							System.out.println(Lab1.sem3.tryAcquire());
							Lab1.sem3.acquire();
							tsi.setSwitch(15,9,RIGHT);
							chooChoo();
							Lab1.sem4.release();
						} else {
							Lab1.sem3.release();
						}
					//5,9
					} else if (sensP.equals(Lab1.s7)) {
						if(!movingUp) {		
							stop();
							Lab1.sem5.acquire();
							chooChoo();
							Lab1.sem4.release();
						
						} else {
							Lab1.sem5.release();
						}
					//3,9
					} else if (sensP.equals(Lab1.s8)) {
						if(movingUp) {		
							if(Lab1.sem4.tryAcquire()) {
								Lab1.sem4.acquire();
								tsi.setSwitch(4,9,LEFT);
							} else {
								tsi.setSwitch(4,9,RIGHT);
							}		
						}
					//2,11
					} else if (sensP.equals(Lab1.s9)) {
						if(!movingUp) {		
							if(Lab1.sem6.tryAcquire()) {
								Lab1.sem6.acquire();
								tsi.setSwitch(3,11,LEFT);
							} else {
								tsi.setSwitch(3,11,RIGHT);
							}		
						
						}
					//4,11
					} else if (sensP.equals(Lab1.s10)) {
						if(movingUp) {		
							stop();
							Lab1.sem5.acquire();
							tsi.setSwitch(3,11,LEFT);
							chooChoo();
							Lab1.sem6.release();
						
						} else {
							Lab1.sem5.release();
						}
					//12,11
					} else if (sensP.equals(Lab1.s11)) {
						if(!movingUp) {		
							stop();
							wait(Math.min(1000, 1000 * Math.abs(speed)));
							speed = -speed;
							chooChoo();
							firstEvent = true;
						
						} else {
							movingUp = true;
						}
					//8,6
					} else if (sensP.equals(Lab1.s12)) {
						if(!movingUp) {		
							stop();
							Lab1.sem1.acquire();
							chooChoo();
						
						} else {
							Lab1.sem1.release();
						}
					//9,8
					} else if (sensP.equals(Lab1.s13)) {
						if(movingUp) {		
							stop();
							Lab1.sem1.acquire();
							chooChoo();
						
						
						} else {
							Lab1.sem1.release();
						}
					//17,8
					} else if (sensP.equals(Lab1.s14)) {
						if(!movingUp) {		
							stop();
							Lab1.sem3.acquire();
							chooChoo();
						
						} else {
							Lab1.sem3.release();
						}
					//15,10
					} else if (sensP.equals(Lab1.s15)) {
						if(movingUp) {		
							stop();
							Lab1.sem3.acquire();
							chooChoo();
						
						} else {
							Lab1.sem3.release();
						}
					//5,10
					} else if (sensP.equals(Lab1.s16)) {
						if(!movingUp) {		
							stop();
							Lab1.sem5.acquire();
							chooChoo();
						
						
						} else {
							Lab1.sem5.release();
						}
					//3,13
					} else if (sensP.equals(Lab1.s17)) {
						if(movingUp) {		
							stop();
							Lab1.sem5.acquire();
							chooChoo();
						
						} else {
							Lab1.sem5.release();
						}
					//12,14
					} else if (sensP.equals(Lab1.s18)) {
						if(!movingUp) {		
							stop();
							wait(Math.min(1000, 1000 * Math.abs(speed)));
							speed = -speed;
							chooChoo();
							firstEvent = true;
						
						} else {
							movingUp = true;
						}
					//14,5
					} else if (sensP.equals(Lab1.s19)) {
						if(movingUp) {		
							stop();
							wait(Math.min(1000, 1000 * Math.abs(speed)));
							speed = -speed;
							chooChoo();
							firstEvent = true;
						
						} else {
							movingUp = false;
						}
					}
				}	

			} catch(CommandException e) {
				System.out.println(e.getMessage());
				System.exit(1);
			}catch(InterruptedException e) {
				System.out.println(e.getMessage());
				System.exit(1);
			}
		}
		
	}

	private void stop() throws CommandException {
		tsi.setSpeed(id,0);

	}
	private void chooChoo() throws CommandException {
		System.out.println("PFIIIIIIIIH");			
		tsi.setSpeed(id,speed);
		System.out.println("CHOOO CHOOO");
	}








}
