import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;

public class SnakeGame extends JPanel implements ActionListener {

    private final int B_WIDTH = 300;
    private final int B_HEIGHT = 300;
    private final int DOT_SIZE = 10;
    private final int ALL_DOTS = (B_WIDTH * B_HEIGHT) / (DOT_SIZE * DOT_SIZE);
    private final int RAND_POS = B_WIDTH / DOT_SIZE;
    private final int DELAY = 140;

    private final List<Point> snake;
    private Point apple;
    private int score = 0;
    private boolean inGame = true;

    private Timer timer;
    private int direction = KeyEvent.VK_RIGHT;

    private JButton newGameButton;

    public SnakeGame() {
        setPreferredSize(new Dimension(B_WIDTH, B_HEIGHT));
        setBackground(Color.black);
        setFocusable(true);
        addKeyListener(new TAdapter());

        snake = new ArrayList<>();
        snake.add(new Point(50, 50));
        snake.add(new Point(40, 50));
        snake.add(new Point(30, 50));

        locateApple();

        timer = new Timer(DELAY, this);
        timer.start();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (inGame) {
            drawSnake(g);
            drawApple(g);
            drawScore(g);
        } else {
            gameOver(g);
        }
    }

    private void drawSnake(Graphics g) {
        g.setColor(Color.green);
        for (Point point : snake) {
            g.fillRect(point.x, point.y, DOT_SIZE, DOT_SIZE);
        }
    }

    private void drawApple(Graphics g) {
        g.setColor(Color.red);
        g.fillRect(apple.x, apple.y, DOT_SIZE, DOT_SIZE);
    }

    private void drawScore(Graphics g) {
        g.setColor(Color.white);
        g.setFont(new Font("Arial", Font.BOLD, 12));
        FontMetrics metrics = g.getFontMetrics();
        String scoreMsg = "Score: " + score;
        g.drawString(scoreMsg, B_WIDTH - metrics.stringWidth(scoreMsg) - 10, 20);
    }

    private void gameOver(Graphics g) {
        String msg = "Game Over";
        Font font = new Font("Arial", Font.BOLD, 16);
        FontMetrics metrics = getFontMetrics(font);

        g.setColor(Color.white);
        g.setFont(font);
        g.drawString(msg, (B_WIDTH - metrics.stringWidth(msg)) / 2, B_HEIGHT / 2);

        if (newGameButton == null) {
            createNewGameButton();
        }
    }

    private void createNewGameButton() {
        newGameButton = new JButton("New Game");
        newGameButton.setFocusable(false);
        newGameButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                resetGame();
            }
        });
        add(newGameButton);
        revalidate();
        repaint();
    }

    private void resetGame() {
        remove(newGameButton);
        newGameButton = null;

        snake.clear();
        snake.add(new Point(50, 50));
        snake.add(new Point(40, 50));
        snake.add(new Point(30, 50));

        score = 0;
        inGame = true;

        locateApple();

        timer.restart();

        requestFocus();
    }

    private void checkCollision() {
        Point head = snake.get(0);

        if (head.x < 0 || head.x >= B_WIDTH || head.y < 0 || head.y >= B_HEIGHT) {
            inGame = false;
        }

        for (int i = 1; i < snake.size(); i++) {
            if (head.equals(snake.get(i))) {
                inGame = false;
            }
        }

        if (!inGame) {
            timer.stop();
        }
    }

    private void move() {
        Point head = snake.get(0);
        Point newHead = new Point(head.x, head.y);

        switch (direction) {
            case KeyEvent.VK_UP:
                newHead.y -= DOT_SIZE;
                break;
            case KeyEvent.VK_DOWN:
                newHead.y += DOT_SIZE;
                break;
            case KeyEvent.VK_LEFT:
                newHead.x -= DOT_SIZE;
                break;
            case KeyEvent.VK_RIGHT:
                newHead.x += DOT_SIZE;
                break;
        }

        snake.add(0, newHead);

        if (newHead.equals(apple)) {
            score++;
            locateApple();
        } else {
            snake.remove(snake.size() - 1);
        }
    }

    private void locateApple() {
        int maxX = B_WIDTH / DOT_SIZE;
        int maxY = B_HEIGHT / DOT_SIZE;

        int appleX = (int) (Math.random() * maxX) * DOT_SIZE;
        int appleY = (int) (Math.random() * maxY) * DOT_SIZE;

        apple = new Point(appleX, appleY);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (inGame) {
            checkCollision();
            move();
        }

        repaint();
    }

    private class TAdapter extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent e) {
            int key = e.getKeyCode();

            if ((key == KeyEvent.VK_LEFT || key == KeyEvent.VK_A) && direction != KeyEvent.VK_RIGHT) {
                direction = KeyEvent.VK_LEFT;
            } else if ((key == KeyEvent.VK_RIGHT || key == KeyEvent.VK_D) && direction != KeyEvent.VK_LEFT) {
                direction = KeyEvent.VK_RIGHT;
            } else if ((key == KeyEvent.VK_UP || key == KeyEvent.VK_W) && direction != KeyEvent.VK_DOWN) {
                direction = KeyEvent.VK_UP;
            } else if ((key == KeyEvent.VK_DOWN || key == KeyEvent.VK_S) && direction != KeyEvent.VK_UP) {
                direction = KeyEvent.VK_DOWN;
            }
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Snake Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.add(new SnakeGame());
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
