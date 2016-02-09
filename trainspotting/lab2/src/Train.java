import TSim.*;
import java.awt.Point;
import java.lang.Math;
import java.util.concurrent.locks.*;

public class Train implements Runnable {

	private final int LEFT = TSimInterface.SWITCH_LEFT, RIGHT = TSimInterface.SWITCH_RIGHT;
	private final int ACTIVE = SensorEvent.ACTIVE, INACTIVE = SensorEvent.INACTIVE;
	private final static TrainMonitor xSectionMonitor = new TrainMonitor();
	private final static TrainMonitor nStationMonitor = new TrainMonitor();
	private final static TrainMonitor eastTurnMonitor = new TrainMonitor();
	private final static TrainMonitor mSectionMonitor = new TrainMonitor();
	private final static TrainMonitor westTurnMonitor = new TrainMonitor();
	private final static TrainMonitor sStationMonitor = new TrainMonitor();
	private boolean onMainTrack = false;
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
					//(15,3)
					if(sensP.equals(Lab2.s0)) {
						movingUp = false;
						//Lab2.sem2.acquire();
						nStationMonitor.enter();
					} else {
						movingUp = true;
						//Lab2.sem6.acquire();
						sStationMonitor.enter();
						
					}
					firstEvent = false;
				} else {
					//(15,3)
					if(sensP.equals(Lab2.s0) && status == ACTIVE) {
						if(movingUp) {		
							tsi.setSpeed(id, 0);
							Thread.sleep(Math.min(1000, 1000 * Math.abs(speed)));
							speed = -speed;
							tsi.setSpeed(id,speed);							
							movingUp = false;
						} else {
							movingUp = false;
						}
					//(6,6)
					} else if(sensP.equals(Lab2.s1) && status == ACTIVE) {
						if(!movingUp) {
							tsi.setSpeed(id, 0);
							//Lab2.sem1.acquire();
							xSectionMonitor.enter();
							tsi.setSpeed(id, speed);
						} else {
							//Lab2.sem1.release();
							xSectionMonitor.leave();
						}
					//(11,7)
					} else if (sensP.equals(Lab2.s2) && status == ACTIVE) {
						if(movingUp) {
							tsi.setSpeed(id, 0);
							xSectionMonitor.enter();
							//Lab2.sem1.acquire();
							tsi.setSpeed(id, speed);
						} else {
							//Lab2.sem1.release();
							xSectionMonitor.leave();
						}
					//(15,7)
					} else if (sensP.equals(Lab2.s3) && status == ACTIVE) {
						if(!movingUp) {
							tsi.setSpeed(id, 0);							
							//Lab2.sem3.acquire();
							eastTurnMonitor.enter();							
							tsi.setSwitch(17,7,RIGHT);
							tsi.setSpeed(id, speed);
							//Lab2.sem2.release();
							nStationMonitor.leave();
							
						} else {
							//Lab2.sem3.release();
							eastTurnMonitor.leave();
						}
					//(19,9)
					} else if (sensP.equals(Lab2.s4) && status == ACTIVE) {
						if(movingUp) {
							if(onMainTrack) {
								mSectionMonitor.leave();
								onMainTrack = false;
							}
							if(nStationMonitor.tryEnter()) {
								tsi.setSwitch(17,7,RIGHT);
							} else {
								tsi.setSwitch(17,7,LEFT);
							}
						} else {
							if(mSectionMonitor.tryEnter()) {
								tsi.setSwitch(15,9, RIGHT);
								onMainTrack = true;
							} else {
								tsi.setSwitch(15,9, LEFT);
							}
						}
					//(12,9)
					} else if (sensP.equals(Lab2.s6) && status == ACTIVE) {
						if(movingUp) {		
							tsi.setSpeed(id, 0);
							//Lab2.sem3.acquire();
							eastTurnMonitor.enter();
							tsi.setSwitch(15,9,RIGHT);
							tsi.setSpeed(id, speed);
							
						} else {
							//Lab2.sem3.release();
							eastTurnMonitor.leave();
						}
					//(7,9)
					} else if (sensP.equals(Lab2.s7) && status == ACTIVE) {
						if(!movingUp) {		
							tsi.setSpeed(id,0);
							//Lab2.sem5.acquire();
							westTurnMonitor.enter();
							tsi.setSwitch(4,9, LEFT);
							tsi.setSpeed(id, speed);
							
						
						} else {
							//Lab2.sem5.release();
							westTurnMonitor.leave();
						}
					//(1,10)
					} else if (sensP.equals(Lab2.s8) && status == ACTIVE) {
						if(movingUp) {		
							if(mSectionMonitor.tryEnter()) {
								onMainTrack = true;
								tsi.setSwitch(4,9,LEFT);
							} else {
								tsi.setSwitch(4,9,RIGHT);
							}		
						}else {
							if(onMainTrack) {
								mSectionMonitor.leave();
								onMainTrack = false;
							}
							if(sStationMonitor.tryEnter()) {
								tsi.setSwitch(3,11,LEFT);
							} else {
								tsi.setSwitch(3,11,RIGHT);
							}		
						}
					//(6,11)
					} else if (sensP.equals(Lab2.s10) && status == ACTIVE) {
						if(movingUp) {		
							tsi.setSpeed(id, 0);
							//Lab2.sem5.acquire();
							westTurnMonitor.enter();
							tsi.setSwitch(3,11,LEFT);
							tsi.setSpeed(id, speed);
							//Lab2.sem6.release();
							sStationMonitor.leave();
						} else {
							//Lab2.sem5.release();
							westTurnMonitor.leave();
						}
					//(15,11)
					} else if (sensP.equals(Lab2.s11) && status == ACTIVE) {
						if(!movingUp) {		
							tsi.setSpeed(id, 0);
							Thread.sleep(Math.min(1000, 1000 * Math.abs(speed)));
							speed = -speed;
							tsi.setSpeed(id, speed);
							movingUp = true;
							
						} else {
							movingUp = true;
							
						}	
					//(8,5)
					} else if (sensP.equals(Lab2.s12) && status == ACTIVE) {
						if(!movingUp) {		
							tsi.setSpeed(id, 0);
							//Lab2.sem1.acquire();
							xSectionMonitor.enter();
							tsi.setSpeed(id, speed);
						
						} else {
							//Lab2.sem1.release();
							xSectionMonitor.leave();
						}
					//(10,8)
					} else if (sensP.equals(Lab2.s13) && status == ACTIVE) {
						if(movingUp) {		
							tsi.setSpeed(id, 0);
							//Lab2.sem1.acquire();
							xSectionMonitor.enter();
							tsi.setSpeed(id, speed);
						
						} else {
							//Lab2.sem1.release();
							xSectionMonitor.leave();
						}
					//(15,8)
					} else if (sensP.equals(Lab2.s14) && status == ACTIVE) {
						if(!movingUp) {		
							tsi.setSpeed(id, 0);
							//Lab2.sem3.acquire();
							eastTurnMonitor.enter();
							tsi.setSwitch(17,7, LEFT);							
							tsi.setSpeed(id, speed);
						
						} else {
							//Lab2.sem3.release();
							eastTurnMonitor.leave();
						}
					//(13,10)
					} else if (sensP.equals(Lab2.s15) && status == ACTIVE) {
						if(movingUp) {		
							tsi.setSpeed(id,0);
							//Lab2.sem3.acquire();
							eastTurnMonitor.enter();
							tsi.setSwitch(15,9, LEFT);
							tsi.setSpeed(id, speed);
						
						} else {
							//Lab2.sem3.release();
							eastTurnMonitor.leave();
						}
					//6,10
					} else if (sensP.equals(Lab2.s16) && status == ACTIVE) {
						if(!movingUp) {		
							tsi.setSpeed(id,0);
							//Lab2.sem5.acquire();
							westTurnMonitor.enter();
							tsi.setSwitch(4,9,RIGHT);
							tsi.setSpeed(id, speed);
						
						} else {
							//Lab2.sem5.release();
							westTurnMonitor.leave();
							tsi.setSwitch(4,9,LEFT);
						}
					//4,13
					} else if (sensP.equals(Lab2.s17) && status == ACTIVE) {
						if(movingUp) {		
							tsi.setSpeed(id,0);
							//Lab2.sem5.acquire();
							westTurnMonitor.enter();
							tsi.setSwitch(3, 11, RIGHT);
							tsi.setSpeed(id, speed);
						
						} else {
							//Lab2.sem5.release();
							westTurnMonitor.leave();
						}
					//15,13
					} else if (sensP.equals(Lab2.s18) && status == ACTIVE) {
						if(!movingUp) {		
							tsi.setSpeed(id, 0);
							Thread.sleep(Math.min(1000, 1000 * Math.abs(speed)));
							speed = -speed;
							tsi.setSpeed(id, speed);
							movingUp = true;
							
						} else {
							movingUp = true;
						}
						System.out.println("Train: " + id + " movingUp: " + movingUp);
					//15,5
					} else if (sensP.equals(Lab2.s19) && status == ACTIVE) {
						if(movingUp) {		
							tsi.setSpeed(id,0);
							Thread.sleep(Math.min(1000, 1000 * Math.abs(speed)));
							speed = -speed;
							tsi.setSpeed(id, speed);
							movingUp = false;
						
						} else {
							movingUp = false;
						}
						System.out.println("Train: " + id + " movingUp: " + movingUp);
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
	
private	static class TrainMonitor {

	
	private Lock lock = new ReentrantLock();
	private Condition allowedEnter = lock.newCondition();	
	private int permits = 1;
	
	public void enter() throws InterruptedException {
		lock.lock();
		try {		
			if(permits == 0) {
				allowedEnter.await();
			}
		} finally {
			permits = permits - 1;
			lock.unlock();
		}

	}
		
	public void leave() {
		lock.lock();
		try {
			allowedEnter.signal();
		} finally {
			permits = permits + 1;
			lock.unlock();
		}
		
	}

	public boolean tryEnter() throws InterruptedException{
		lock.lock();
		if(permits == 0) {
			lock.unlock();
			return false;	
		}
		//permits = permits - 1;
		enter();
		lock.unlock();
		return true;
	}
}	
	
}


