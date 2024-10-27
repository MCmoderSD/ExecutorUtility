# ExecutorUtility

## Description

A simple utility to run tasks in a loop with a specific period.


## Usage

### Maven
```xml
<dependencies>
    <dependency>
        <groupId>de.MCmoderSD</groupId>
        <artifactId>executor</artifactId>
        <version>1.0.1</version>
    </dependency>
</dependencies>
```

### Usage Example
```java
import de.MCmoderSD.executor.NanoLoop;

import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) {

        // Runnable task = () -> {
        Runnable task = () -> {
            System.out.println("Hello World");
        };

        // This will print "Hello World" every 2 seconds
        NanoLoop nanoLoop = new NanoLoop(() -> System.out.println("Hello World"), 2, TimeUnit.SECONDS, 1);

        nanoLoop = new NanoLoop(task, 3, TimeUnit.SECONDS);         // Run the task every 3 seconds
        nanoLoop = new NanoLoop(task, 60, 2);                       // Run the task 120 times per second
        nanoLoop = new NanoLoop(task, 60);                          // Run the task 60 times per second

        nanoLoop.start();                                           // Start the loop
        nanoLoop.setModifier(2);                                    // Make the loop run twice as fast
        nanoLoop.setModifier(0.5f);                                 // Make the loop run half as fast
        nanoLoop.setPeriod(100, TimeUnit.MILLISECONDS);             // Change the period to 100 milliseconds
        nanoLoop.resetModifier();                                   // Reset the modifier to 1
        nanoLoop.setTask(() -> System.out.println("New Task"));     // Change the task
        nanoLoop.stop();                                            // Stop the loop
    }
}
```