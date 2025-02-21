import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

public class PlataformaGame extends JPanel implements KeyListener, Runnable {
    private int playerX = 50;
    private int playerY = 300;
    private int playerWidth = 50;
    private int playerHeight = 50;
    private int playerSpeed = 5;
    private boolean jumping = false;
    private int jumpHeight = 110; 
    private int initialY;
    private boolean movingLeft = false;
    private boolean movingRight = false;
    private Thread gameThread;

    private List<Rectangle> platforms; // Lista de plataformas
    private Image background; // Imagem de fundo

    public PlataformaGame() {
        setPreferredSize(new Dimension(800, 600));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);

        // Carrega o fundo (substitua pelo caminho da sua imagem)
        try {
            background = new ImageIcon("italy.png").getImage();
        } catch (Exception e) {
            System.err.println("Error loading background image: " + e.getMessage());
            background = null; // Set background to null if loading fails
        }

        // Cria as plataformas
        platforms = new ArrayList<>();
        platforms.add(new Rectangle(0, 550, 800, 50)); // Chão
        platforms.add(new Rectangle(200, 400, 200, 20)); // Plataforma 1
        platforms.add(new Rectangle(500, 450, 200, 20)); // Plataforma 2

        // Inicia a thread do jogo
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Desenha o fundo
        if (background != null) {
            g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
        }

        // Desenha as plataformas
        g.setColor(Color.GREEN);
        for (Rectangle platform : platforms) {
            g.fillRect(platform.x, platform.y, platform.width, platform.height);
        }

        // Desenha o jogador
        g.setColor(Color.BLUE);
        g.fillRect(playerX, playerY, playerWidth, playerHeight);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_A) {
            movingLeft = true;
        } else if (key == KeyEvent.VK_D) {
            movingRight = true;
        } else if (key == KeyEvent.VK_SPACE && !jumping && isOnPlatform()) {
            jumping = true;
            initialY = playerY;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_A) {
            movingLeft = false;
        } else if (key == KeyEvent.VK_D) {
            movingRight = false;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    private boolean isOnPlatform() {
        Rectangle playerRect = new Rectangle(playerX, playerY, playerWidth, playerHeight);
        for (Rectangle platform : platforms) {
            if (playerRect.intersects(platform) && playerY + playerHeight <= platform.y + 10) {
                return true;
            }
        }
        return false;
    }

    private void update() {
        // Movimento horizontal
        if (movingLeft) {
            playerX -= playerSpeed;
        }
        if (movingRight) {
            playerX += playerSpeed;
        }

        // Pulo
        if (jumping) {
            int deltaY = initialY - playerY;
            if (deltaY < jumpHeight) {
                playerY -= 5; // Subindo
            } else {
                jumping = false;
            }
        } else if (!isOnPlatform()) {
            playerY += 5; // Gravidade (caindo)
        }

        // Limita o jogador dentro da tela
        playerX = Math.max(0, Math.min(playerX, getWidth() - playerWidth));
        playerY = Math.max(0, Math.min(playerY, getHeight() - playerHeight));

        // Verifica colisão com as plataformas
        if (isOnPlatform()) {
            jumping = false;
        }
    }

    @Override
    public void run() {
        // Game loop
        while (true) {
            update(); // Atualiza a lógica do jogo
            repaint(); // Redesenha a tela

            try {
                Thread.sleep(16); // ~60 FPS (1000ms / 60 = ~16ms por frame)
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Jogo de Plataforma");
        PlataformaGame game = new PlataformaGame();
        frame.add(game);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
