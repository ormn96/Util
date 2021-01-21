package runner;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * class to run task in random intervals
 * @author Or Man
 * @version 1.1
 * @since 21/12/2020
 */
public class RandomRunner {

	private Runnable task;
	private TimeUnit unit;
	private int lowerDelta;
	private int upperDelta;
	private Random r = new Random();
	private ScheduledExecutorService scheduler;
	
	/**
	 * creates taskRunner that execute the task every random intervals
	 * @param task {@link Runnable} that sets the task to run
	 * @param unit {@link TimeUnit} - unit of the intervals
	 * @param lowerDelta - lower bound of the delay
	 * @param upperDelta - upper bound of the delay
	 */
	public RandomRunner(Runnable task, TimeUnit unit, int lowerDelta, int upperDelta) {
		this.task = task;
		this.unit = unit;
		this.lowerDelta = lowerDelta;
		this.upperDelta = upperDelta;
		scheduler = Executors.newScheduledThreadPool(1);
		start();
	}

	/**
	 * start the task in random time between the delta boundaries
	 */
	private void start() {
		Runnable next =new Runnable() {
			
			@Override
			public void run() {
				task.run();
				start();
			}
		};
		scheduler.scheduleWithFixedDelay(next, 0, calculateNextTime(), unit);
	}

	/**
	 * stop the execution of the job, waits to the execution to end, if already started 
	 */
	public void stop()
    {
		scheduler.shutdown();
        try {
        	scheduler.awaitTermination(1, TimeUnit.DAYS);
        } catch (InterruptedException ex) {
        }
    }


	private int calculateNextTime() {
		return r.nextInt(upperDelta-lowerDelta) + lowerDelta;
		
	}
}
