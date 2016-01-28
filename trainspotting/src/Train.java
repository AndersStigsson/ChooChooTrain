import TSim.*;
import java.awt.Point;
import java.lang.Math;

public class Train implements Runnable {

	private final int LEFT = TSimInterface.SWITCH_LEFT, RIGHT = TSimInterface.SWITCH_RIGHT;
	private final int ACTIVE = SensorEvent.ACTIVE, INACTIVE = SensorEvent.INACTIVE;
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
				SensorEvent sensor = tsi.getSensor(id);
				Point sensP = new Point(sensor.getXpos(), sensor.getYpos());
				int status = sensor.getStatus();
				
				if(firstEvent) {
					//(13,3)
					if(sensP.equals(Lab1.s0) && status == ACTIVE) {
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
					if(sensP.equals(Lab1.s0) && status == ACTIVE) {
						if(movingUp) {		
							//stop();
							
							tsi.setSpeed(id, 0);
							wait(Math.min(1000, 1000 * Math.abs(speed)));
							speed = -speed;
							//chooChoo();
							tsi.setSpeed(id, speed);							
							firstEvent = true;
						
						} else {
							movingUp = false;
						}
					//(7,7)
					} else if(sensP.equals(Lab1.s1) && status == ACTIVE) {
						if(!movingUp) {
							//stop();
							tsi.setSpeed(id, 0);
							Lab1.sem1.acquire();
							//chooChoo();
							tsi.setSpeed(id, speed);
						} else {
							Lab1.sem1.release();
						}
					//(9,7)
					} else if (sensP.equals(Lab1.s2) && status == ACTIVE) {
						if(movingUp) {
							//stop();
							tsi.setSpeed(id, 0);
							Lab1.sem1.acquire();
							//chooChoo();
							tsi.setSpeed(id, speed);
						} else {
							Lab1.sem1.release();
						}
					//(16,7)
					} else if (sensP.equals(Lab1.s3) && status == ACTIVE) {
						if(!movingUp) {
							//stop();
							tsi.setSpeed(id, 0);							
							Lab1.sem3.acquire();
							tsi.setSwitch(17,7,RIGHT);
							//chooChoo();
							tsi.setSpeed(id, speed);
							Lab1.sem2.release();
						} else {
							Lab1.sem3.release();
						}
					//(18,7)
					} else if (sensP.equals(Lab1.s4) && status == ACTIVE) {
						if(movingUp) {
							if(Lab1.sem2.tryAcquire()) {
								//Lab1.sem2.acquire();
								tsi.setSwitch(17,7,RIGHT);
							} else {
								tsi.setSwitch(17,7,LEFT);
							}
						} else {
							if(Lab1.sem4.tryAcquire()) {
								//Lab1.sem4.acquire();
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
					} else if (sensP.equals(Lab1.s6) && status == ACTIVE) {
						if(movingUp) {		
							//stop();
							System.err.println("I'm in");
							tsi.setSpeed(id, 0);
							Lab1.sem3.acquire();
							tsi.setSwitch(15,9,RIGHT);
							//chooChoo();
							tsi.setSpeed(id, speed);
							Lab1.sem4.release();
						} else {
							Lab1.sem3.release();
						}
					//5,9
					} else if (sensP.equals(Lab1.s7) && status == ACTIVE) {
						if(!movingUp) {		
							//stop();
							tsi.setSpeed(id,0);
							Lab1.sem5.acquire();
							//chooChoo();
							tsi.setSpeed(id, speed);
							Lab1.sem4.release();
						
						} else {
							Lab1.sem5.release();
						}
					//3,9
					} else if (sensP.equals(Lab1.s8) && status == ACTIVE) {
						if(movingUp) {		
							if(Lab1.sem4.tryAcquire()) {
								//Lab1.sem4.acquire();
								tsi.setSwitch(4,9,LEFT);
							} else {
								tsi.setSwitch(4,9,RIGHT);
							}		
						}
					//2,11
					} else if (sensP.equals(Lab1.s9) && status == ACTIVE) {
						if(!movingUp) {		
							if(Lab1.sem6.tryAcquire()) {
								Lab1.sem6.acquire();
								tsi.setSwitch(3,11,LEFT);
							} else {
								tsi.setSwitch(3,11,RIGHT);
							}		
						
						}
					//4,11
					} else if (sensP.equals(Lab1.s10) && status == ACTIVE) {
						if(movingUp) {		
							//stop();
							tsi.setSpeed(id, 0);
							Lab1.sem5.acquire();
							tsi.setSwitch(3,11,LEFT);
							//chooChoo();
							tsi.setSpeed(id, speed);
							Lab1.sem6.release();
						
						} else {
							Lab1.sem5.release();
						}
					//12,11
					} else if (sensP.equals(Lab1.s11) && status == ACTIVE) {
						if(!movingUp) {		
							//stop();
							tsi.setSpeed(id, 0);
							wait(Math.min(1000, 1000 * Math.abs(speed)));
							speed = -speed;
							//chooChoo();
							tsi.setSpeed(id, speed);
							firstEvent = true;
						
						} else {
							movingUp = true;
						}
					//8,6
					} else if (sensP.equals(Lab1.s12) && status == ACTIVE) {
						if(!movingUp) {		
							//stop();
							tsi.setSpeed(id, 0);
							Lab1.sem1.acquire();
							//chooChoo();
							tsi.setSpeed(id, speed);
						
						} else {
							Lab1.sem1.release();
						}
					//9,8
					} else if (sensP.equals(Lab1.s13) && status == ACTIVE) {
						if(movingUp) {		
							//stop();
							tsi.setSpeed(id, 0);
							Lab1.sem1.acquire();
							//chooChoo();
							tsi.setSpeed(id, speed);
						
						} else {
							Lab1.sem1.release();
						}
					//17,8
					} else if (sensP.equals(Lab1.s14) && status == ACTIVE) {
						if(!movingUp) {		
							//stop();
							tsi.setSpeed(id, 0);
							Lab1.sem3.acquire();
							//chooChoo();
							tsi.setSpeed(id, speed);
						
						} else {
							Lab1.sem3.release();
						}
					//15,10
					} else if (sensP.equals(Lab1.s15) && status == ACTIVE) {
						if(movingUp) {		
							//stop();
							tsi.setSpeed(id,0);
							Lab1.sem3.acquire();
							//chooChoo();
							tsi.setSpeed(id, speed);
						
						} else {
							Lab1.sem3.release();
						}
					//5,10
					} else if (sensP.equals(Lab1.s16) && status == ACTIVE) {
						if(!movingUp) {		
							//stop();
							tsi.setSpeed(id,0);
							Lab1.sem5.acquire();
							tsi.setSwitch(4,9,RIGHT);
							//chooChoo();
							tsi.setSpeed(id, speed);
						
						} else {
							Lab1.sem5.release();
							tsi.setSwitch(4,9,LEFT);
						}
					//3,13
					} else if (sensP.equals(Lab1.s17) && status == ACTIVE) {
						if(movingUp) {		
							//stop();
							tsi.setSpeed(id,0);
							Lab1.sem5.acquire();
							//chooChoo();
							tsi.setSpeed(id, speed);
						
						} else {
							Lab1.sem5.release();
						}
					//12,14
					} else if (sensP.equals(Lab1.s18) && status == ACTIVE) {
						if(!movingUp) {		
							//stop();
							tsi.setSpeed(id, 0);
							wait(Math.min(1000, 1000 * Math.abs(speed)));
							speed = -speed;
							//chooChoo();
							tsi.setSpeed(id, speed);
							firstEvent = true;
						
						} else {
							movingUp = true;
						}
					//14,5
					} else if (sensP.equals(Lab1.s19) && status == ACTIVE) {
						if(movingUp) {		
							//stop();
							tsi.setSpeed(id,0);
							wait(Math.min(1000, 1000 * Math.abs(speed)));
							speed = -speed;
							//chooChoo();
							tsi.setSpeed(id, speed);
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
