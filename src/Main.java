import java.awt.*;
import javax.swing.JFrame;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

public class WindowTest extends Canvas implements Runnable {

    public static JFrame frame;
    private Thread thread;
    private boolean isRunning = true;
    private final int WIDTH = 800;
    private final int HEIGHT = 600;
    private final int SCALE = 1;

    private BufferedImage image;

    public WindowTest () {
        setPreferredSize(new Dimension(WIDTH*SCALE, HEIGHT*SCALE));
        initFrame();
        image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
    }

    public void initFrame () {
        frame = new JFrame("Snake WindowTest");
        frame.add(this);
        frame.setResizable(false);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    public synchronized void start () {
        thread = new Thread(this);
        isRunning = true;
        thread.start();
    }
    public synchronized void stop () {
        isRunning = false;
        try {
            thread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        WindowTest windowTest = new WindowTest();
        windowTest.start();
    }

    public void tick () {}

    public void render () {
        BufferStrategy bs = this.getBufferStrategy();
        if (bs == null) {
            this.createBufferStrategy(3);
            return;
        }
        Graphics g = image.getGraphics();
        g.setColor(Color.BLUE);
        g.fillRect(25, 25, WIDTH-50, HEIGHT-50);
        g = bs.getDrawGraphics();
        g.drawImage(image, 0, 0, WIDTH*SCALE, HEIGHT*SCALE, null);
        bs.show();
    }

    @Override
    public void run() {
        long lastTime = System.nanoTime();
        double amountOfTicks = 60.0;
        double ns = 1000000000 / amountOfTicks;
        double delta = 0;
        int frames = 0;
        double timer = System.currentTimeMillis();
        while (isRunning) {
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;
            if (delta >= 1) {
                tick();
                render();
                frames++;
                delta--;
            }

            if (System.currentTimeMillis() - timer > 1000) {
                System.out.println("FPS: " + frames);
                frames = 0;
                timer += 1000;
            }
        }
        stop();
    }
}
