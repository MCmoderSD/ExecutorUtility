package de.MCmoderSD.executor;

import java.util.concurrent.TimeUnit;

/**
 * NanoLoop is a utility class that allows scheduling of tasks to run at a specified interval
 * with precision down to nanoseconds. This loop can be started, stopped, and modified in real-time.
 */
@SuppressWarnings("ALL")
public class NanoLoop {

    // Flags
    private volatile boolean running;

    // Attributes
    private Runnable task;
    private Thread thread;
    private long intervalNanos;

    // Variables
    private long period;
    private float modifier;

    /**
     * Constructs a NanoLoop with a specified task and frequency.
     *
     * @param task      the task to be executed in the loop
     * @param frequency the frequency in Hz at which to run the task
     */
    public NanoLoop(Runnable task, long frequency) {
        this(task, 1_000_000_000 / frequency, TimeUnit.NANOSECONDS, 1);
    }

    /**
     * Constructs a NanoLoop with a specified task, frequency, and modifier.
     *
     * @param task      the task to be executed in the loop
     * @param frequency the frequency in Hz at which to run the task
     * @param modifier  a multiplier to adjust the task execution interval
     */
    public NanoLoop(Runnable task, long frequency, float modifier) {
        this(task, 1_000_000_000 / frequency, TimeUnit.NANOSECONDS, modifier);
    }

    /**
     * Constructs a NanoLoop with a specified task, period, and time unit.
     *
     * @param task     the task to be executed in the loop
     * @param period   the interval period between executions of the task
     * @param timeUnit the time unit of the period
     */
    public NanoLoop(Runnable task, long period, TimeUnit timeUnit) {
        this(task, period, timeUnit, 1);
    }

    /**
     * Constructs a NanoLoop with a specified task, period, time unit, and modifier.
     *
     * @param task     the task to be executed in the loop
     * @param period   the interval period between executions of the task
     * @param timeUnit the time unit of the period
     * @param modifier a multiplier to adjust the task execution interval
     */
    public NanoLoop(Runnable task, long period, TimeUnit timeUnit, float modifier) {
        this.task = task;
        this.period = period;
        this.intervalNanos = timeUnit.toNanos(period);
        this.modifier = modifier;
        this.running = false;
    }

    /**
     * Starts the execution loop. If already running, this method does nothing.
     */
    public void start() {
        if (running) return;
        running = true;

        thread = new Thread(() -> {
            var nextTime = System.nanoTime();

            while (running) {
                var now = System.nanoTime();

                if (now >= nextTime) {
                    task.run();
                    nextTime += intervalNanos;
                }

                var sleepTime = (nextTime - System.nanoTime()) / 1_000_000;
                if (sleepTime > 0) {
                    try {
                        Thread.sleep(sleepTime);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        System.err.println("Thread was interrupted");
                        break;
                    }
                }
            }
        });

        thread.start();
    }

    /**
     * Stops the execution loop and interrupts the running thread if active.
     */
    public void stop() {
        running = false;
        if (thread != null) thread.interrupt();
    }

    /**
     * Updates the period of the loop's execution interval.
     *
     * @param period   the new interval period between executions of the task
     * @param timeUnit the time unit of the new period
     */
    public void setPeriod(long period, TimeUnit timeUnit) {
        this.period = period;
        var interval = Math.round(period / modifier);
        intervalNanos = timeUnit.toNanos(interval);
    }

    /**
     * Sets a modifier to adjust the interval period dynamically.
     *
     * @param modifier a new multiplier to adjust the task execution interval
     */
    public void setModifier(float modifier) {
        this.modifier = modifier;
        intervalNanos = Math.round(period / modifier);
    }

    /**
     * Resets the modifier to its default value of 1.
     */
    public void resetModifier() {
        setModifier(1);
    }

    /**
     * Sets a new task to be executed by the loop.
     *
     * @param task the new task to be executed in the loop
     */
    public void setTask(Runnable task) {
        this.task = task;
    }

    /**
     * Checks if the loop is currently running.
     *
     * @return {@code true} if the loop is running, {@code false} otherwise
     */
    public boolean isRunning() {
        return running;
    }

    /**
     * Gets the current interval period between task executions.
     *
     * @return the interval period
     */
    public long getPeriod() {
        return period;
    }

    /**
     * Gets the current modifier value used to adjust the interval period.
     *
     * @return the modifier
     */
    public float getModifier() {
        return modifier;
    }

    /**
     * Gets the current task assigned to the loop.
     *
     * @return the task being executed in the loop
     */
    public Runnable getTask() {
        return task;
    }
}