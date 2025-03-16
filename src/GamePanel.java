
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;


public class GamePanel extends JPanel implements ActionListener {
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    private static final int PIPE_WIDTH = 80;
    private static final int BIRD_SIZE = 60;
    private static final int PIPE_GAP = 300; 
    private static final int GRAVITY = 1;
    private static final int JUMP_STRENGTH = -8;

    private int birdY = HEIGHT / 2;
    private int birdX = WIDTH / 2 - BIRD_SIZE / 2;
    private int birdVelocity = 0;
    private int score = 0;
    private int deaths = 0;
    private boolean gameoverAnimation = false;
    private boolean gameOver = true;

    private Timer timer;
    private ArrayList<Rectangle> pipes;
    private Random random;

    private Image birdImage;

    private void loadImages() {
        ImageIcon iib = new ImageIcon("resources/bird.png");
        birdImage = iib.getImage();
    }

    public GamePanel() {
        setBackground(Color.cyan);
        setFocusable(true);
        addKeyListener(new TAdapter());
        loadImages();

        pipes = new ArrayList<>();
        random = new Random();

        timer = new Timer(20, this);

    


        
    }




    private class TAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                jump();
                if (gameOver && gameoverAnimation == false) {
                    gameOver = false;
                    startGame();
                    jump();
                }
            }

            
        }
    }

    public void startGame() {
        loadImages();
        if (!gameOver) {
            birdY = HEIGHT / 2;
            birdVelocity = 0;
            score = 0;
            pipes.clear();
            

            addPipe(true);
            addPipe(true);
            addPipe(true);
            addPipe(true);

            timer.start();
        }
    }

    private void addPipe(boolean start) {
        int space = HEIGHT / 4;
        int height = 50 + random.nextInt(300);

        if (start) {
            pipes.add(new Rectangle(WIDTH + PIPE_WIDTH + pipes.size() * 200, HEIGHT - height - 120, PIPE_WIDTH, height));
            pipes.add(new Rectangle(WIDTH + PIPE_WIDTH + (pipes.size() - 1) * 200, 0, PIPE_WIDTH, HEIGHT - height - PIPE_GAP));
        } else {
            pipes.add(new Rectangle(pipes.get(pipes.size() - 1).x + 400, HEIGHT - height - 120, PIPE_WIDTH, height));
            pipes.add(new Rectangle(pipes.get(pipes.size() - 1).x, 0, PIPE_WIDTH, HEIGHT - height - PIPE_GAP));
        }
    }

    private void paintPipe(Graphics g, Rectangle pipe) {
        g.setColor(Color.green);
        g.fillRect(pipe.x, pipe.y, pipe.width, pipe.height);
    }

    private void jump() {
        if (!gameOver) {
            birdVelocity = JUMP_STRENGTH;
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!gameOver) {
            ArrayList<Rectangle> toRemove = new ArrayList<>();

            for (Rectangle pipe : pipes) {
                pipe.x -= 5;


                if (pipe.x + pipe.width < 0) {
                    toRemove.add(pipe);
                }
            }

            pipes.removeAll(toRemove);

            if (pipes.size() < 8) {
                addPipe(false);
            }

            birdVelocity += GRAVITY;
            birdY += birdVelocity;

            for (Rectangle pipe : pipes) {
                if (pipe.intersects(new Rectangle(WIDTH / 2 - BIRD_SIZE / 2, birdY, BIRD_SIZE, BIRD_SIZE))) {
                    deaths++;
                    gameOver = true;
                    
                    
                }
            }

            if (birdY > HEIGHT - 120 || birdY < 0) {
                deaths++;
                gameOver = true;
                
                

            }


            for (Rectangle pipe : pipes) {
                if (pipe.y == 0 && pipe.x + PIPE_WIDTH < birdX) {
                    score++;
                    pipe.y = -1;
                }
            }

        }

        if (gameOver) {
            gameoverAnimation = true;
            birdVelocity += GRAVITY;
            birdY += birdVelocity;
            if (birdY > HEIGHT - 120) {
                gameoverAnimation = false;
            }
        }

        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;

        g2d.setColor(Color.cyan);
        g2d.fillRect(0, 0, WIDTH, HEIGHT);

        g2d.setColor(Color.orange);
        g2d.fillRect(0, HEIGHT - 120, WIDTH, 120);

        g2d.setColor(Color.green);
        g2d.fillRect(0, HEIGHT - 120, WIDTH, 20);

        for (Rectangle pipe : pipes) {
            paintPipe(g2d, pipe);
        }

        if (birdImage != null) {
            g2d.drawImage(birdImage, birdX, birdY, BIRD_SIZE, BIRD_SIZE * 3/4, this);
        } else {
            g2d.setColor(Color.yellow);
            g2d.fillOval(birdX, birdY, BIRD_SIZE, BIRD_SIZE);
        }

        g2d.setColor(Color.white);
        g2d.setFont(new Font("Arial", Font.BOLD, 80));

        if (gameOver && deaths > 0) {
            g2d.drawString("Game Over", 100, HEIGHT / 2 - 50);
            g2d.setFont(new Font("Arial", Font.BOLD, 40));
            g2d.drawString("Your Final Score Was: " + score, 100, HEIGHT / 2 + 50);
        }

        if (!gameOver) {
            g2d.setFont(new Font("Arial", Font.BOLD, 50));
            g2d.drawString("Score: " + score, 10, 50);
        }

        Toolkit.getDefaultToolkit().sync();
    }


}
