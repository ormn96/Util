package runner;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Class that execute specific "job" at specific time, and run it every "delta" time
 * @author Or Man
 * @version 1.2
 * @since 21/01/2021
 */
public class PeriodicallyRunner {
	private ScheduledExecutorService scheduler;

	/**
	 * Creates {@link PeriodicallyRunner} that execute specific <b>"job"</b> at specific <b>time(targetHour,targrtMin)</b>, and run it every <b>"delta"</b> time
	 * @param target {@link LocalTime} to start
	 * @param deltaUnit {@link TimeUnit} the unit of the delta 
	 * @param delta the time between runs in <b>deltaUnit</b>
	 * @param job runnable to execute 
	 */
	public PeriodicallyRunner(LocalTime target,TimeUnit deltaUnit,long delta, Runnable job) {
		LocalDateTime localNow = LocalDateTime.now();
		ZoneId currentZone = ZoneId.systemDefault();
		ZonedDateTime zonedNow = ZonedDateTime.of(localNow, currentZone);
		ZonedDateTime zonedNextTarget = ZonedDateTime.of(LocalDateTime.of(LocalDate.now(),target), currentZone);
		if (zonedNow.compareTo(zonedNextTarget) > 0)
			zonedNextTarget = zonedNextTarget.plusDays(1);

		Duration duration = Duration.between(zonedNow, zonedNextTarget);
		long initalDelay = duration.getSeconds();

		scheduler = Executors.newScheduledThreadPool(1);
		scheduler.scheduleAtFixedRate(job, initalDelay, deltaUnit.toSeconds(delta), TimeUnit.SECONDS);
	}
	
	/**
	 * Creates {@link PeriodicallyRunner} that execute specific <b>"job"</b> at specific <b>time(targetHour,targrtMin)</b>, and run it every 1 day
	 * @param target {@link LocalTime} to start
	 * @param job runnable to execute 
	 * @return the {@link PeriodicallyRunner}
	 */
	public static PeriodicallyRunner runEveryDayAt(LocalTime target, Runnable job) {
		return new PeriodicallyRunner(target, TimeUnit.DAYS, 1, job);
	}

	
	/**
	 * stop the execution of the job, waits to the execution to end, if already started 
	 */
	public void stop() {
		scheduler.shutdown();
		try {
			scheduler.awaitTermination(1, TimeUnit.DAYS);
		} catch (InterruptedException ex) {

		}
	}


}
