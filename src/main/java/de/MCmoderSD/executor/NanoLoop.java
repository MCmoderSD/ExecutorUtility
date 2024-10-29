package de.MCmoderSD.executor;

import java.util.concurrent.TimeUnit;

/**
 * A utility class for creating a precise loop that executes a given task
 * at a specified frequency, adjustable with a modifier.
 */
@SuppressWarnings("ALL")
public class NanoLoop {

    // Thread Loop
    private final Runnable loop;

    // Flags
    private volatile boolean running;

    // Attributes
    private Thread thread;
    private long intervalNanos;

    // Variables
    private Runnable task;
    private long period;
    private float modifier;

    /**
     * Constructs a NanoLoop with the specified task and frequency.
     *
     * @param task      the Runnable task to execute
     * @param frequency the frequency in Hz
     */
    public NanoLoop(Runnable task, long frequency) {
        this(task, 1_000_000_000 / frequency, TimeUnit.NANOSECONDS, 1);
    }

    /**
     * Constructs a NanoLoop with the specified task, frequency, and modifier.
     *
     * @param task      the Runnable task to execute
     * @param frequency the frequency in Hz
     * @param modifier  a modifier to adjust the execution period
     */
    public NanoLoop(Runnable task, long frequency, float modifier) {
        this(task, 1_000_000_000 / frequency, TimeUnit.NANOSECONDS, modifier);
    }

    /**
     * Constructs a NanoLoop with the specified task and period in the given time unit.
     *
     * @param task      the Runnable task to execute
     * @param period    the period for task execution
     * @param timeUnit  the TimeUnit of the specified period
     */
    public NanoLoop(Runnable task, long period, TimeUnit timeUnit) {
        this(task, period, timeUnit, 1);
    }

    /**
     * Constructs a NanoLoop with the specified task, period, time unit, and modifier.
     *
     * @param task      the Runnable task to execute
     * @param period    the period for task execution
     * @param timeUnit  the TimeUnit of the specified period
     * @param modifier  a modifier to adjust the execution period
     */
    public NanoLoop(Runnable task, long period, TimeUnit timeUnit, float modifier) {

        // Init Variables
        this.task = task;
        this.period = period;
        this.modifier = modifier;

        // Init Attributes
        intervalNanos = timeUnit.toNanos(period);
        running = false;

        // Create the loop
        loop = () -> {

            // Calculate the next execution time
            var nextTime = System.nanoTime();
            while (running) { // Loop until stopped

                // Execute the task if the next time has been reached
                if (System.nanoTime() >= nextTime) {
                    task.run();
                    nextTime += intervalNanos;
                }

                // Sleep until the next execution time
                var sleepTime = (nextTime - System.nanoTime()) / 1_000_000;
                if (sleepTime > 0) {
                    try {
                        Thread.sleep(sleepTime);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
        };
    }

    /**
     * Starts the NanoLoop. If it's already running, it does nothing.
     */
    public void start() {

        // Return if the loop is already running
        if (running) return;

        // Set the running flag to true
        running = true;

        // Create a new thread for the loop
        thread = new Thread(loop);

        // Start the thread
        thread.start();
    }

    /**
     * Stops the NanoLoop. It sets the running flag to false and interrupts the thread.
     */
    public void stop() {

        // Set the running flag to false
        running = false;

        // Interrupt the thread
        if (thread != null) thread.interrupt();
    }

    /**
     * Sets a new task for the NanoLoop.
     *
     * @param task the Runnable task to execute
     */
    public void setTask(Runnable task) {
        this.task = task;
    }

    /**
     * Sets a new frequency for the NanoLoop.
     *
     * @param frequency the frequency in Hz
     */
    public void setPeriod(long frequency) {
        this.period = 1_000_000_000 / frequency;
        intervalNanos = 1_000_000_000 / Math.round(period / modifier);
    }

    /**
     * Sets a new period for the NanoLoop in the specified time unit.
     *
     * @param period    the period for task execution
     * @param timeUnit  the TimeUnit of the specified period
     */
    public void setPeriod(long period, TimeUnit timeUnit) {
        this.period = period;
        intervalNanos = timeUnit.toNanos(Math.round(period / modifier));
    }

    /**
     * Sets a new modifier for the NanoLoop.
     *
     * @param modifier the new modifier value
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
     * Checks if the NanoLoop is currently running.
     *
     * @return true if running, false otherwise
     */
    public boolean isRunning() {
        return running;
    }

    /**
     * Gets the current period of the NanoLoop.
     *
     * @return the current period in nanoseconds
     */
    public long getPeriod() {
        return period;
    }

    /**
     * Gets the current modifier of the NanoLoop.
     *
     * @return the current modifier
     */
    public float getModifier() {
        return modifier;
    }

    /**
     * Gets the current task of the NanoLoop.
     *
     * @return the Runnable task currently set
     */
    public Runnable getTask() {
        return task;
    }
}