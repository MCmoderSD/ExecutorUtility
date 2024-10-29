import de.MCmoderSD.executor.NanoLoop;

import javax.swing.*;
import java.awt.*;

public class BouncingBall {

    // NanoLoops
    private NanoLoop renderLoop;
    private NanoLoop moveLoop;

    // Variables
    private float y = 0;
    private float ySpeed = 0.1f;

    public BouncingBall() {

        JFrame frame = new JFrame("Bouncing Ball");

        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(Color.RED);
                g.fillOval(125, (int) y, 50, 50);
            }
        };

        panel.setPreferredSize(new Dimension(300, 600));

        frame.add(panel);





        renderLoop = new NanoLoop(panel::repaint, 240, 1);
        moveLoop = new NanoLoop(() -> {
            y += ySpeed;
            if (y >= 550 || y <= 0) ySpeed *= -1;
        }, 2000);

        renderLoop.start();
        moveLoop.start();

        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension screenSize = tk.getScreenSize();
        frame.setLocation(screenSize.width / 2 - frame.getWidth() / 2, screenSize.height / 2 - frame.getHeight() / 2);

        frame.setVisible(true);


        System.out.println("Start Testing");

        try {
            test();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


    private void test() throws InterruptedException {

        Thread.sleep(1000);
        System.out.println("Test 1");
        System.out.println("Setting modifier to 2.5");
        moveLoop.setModifier(2.5f);

        Thread.sleep(2000);
        System.out.println("Test 2");
        System.out.println("Setting modifier to 0.5");
        moveLoop.setModifier(0.5f);

        Thread.sleep(2000);
        System.out.println("Test 3");
        System.out.println("Stopping moveLoop");
        moveLoop.stop();
        Thread.sleep(2000);

        System.out.println("Test 4");
        System.out.println("Starting moveLoop");
        moveLoop.start();
        Thread.sleep(2000);

        System.out.println("Test 5");
        System.out.println("Setting period to modify to 10k");
        moveLoop.setPeriod(10_000);
        Thread.sleep(2000);
    }


    public static void main(String[] args) {
        new BouncingBall();
    }
}
