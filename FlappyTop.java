import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.Timer;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class FlappyTop extends JPanel implements ActionListener, KeyListener {
	//set variables
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    private static final int BIRD_SIZE = 120;
    private static final int PIPE_WIDTH = 100;
    private static final int PIPE_GAP = 250;
    private static final int PIPE_SPEED = 5;
    private static final int GRAVITY = 1;
    //images
    private BufferedImage birdImageOpenKeyless;
    private BufferedImage birdImageClosedKeyless;
    private BufferedImage backgroundImage;
    private BufferedImage winImage;
    //non final variables
    private int birdY;
    private int birdVelocity;
    private ArrayList<Rectangle> pipes;
    private int score;
    private boolean gameOver;
    private Timer timer;
    private char currentLetter;
    private Set<Character> letters;
    private boolean gameStarted;
	private boolean spaceKeyPressed;
	private boolean gameWon;
	private ArrayList<Character> remainingLetters;
    
    //methods/ constructor
    public FlappyTop() {
    	//load images
    	try {
    		birdImageClosedKeyless = ImageIO.read( new File("/Users/vanditasoni/Downloads/closednokeys.png"));
    		birdImageOpenKeyless = ImageIO.read( new File("/Users/vanditasoni/Downloads/openflapnokeys.png"));
    		backgroundImage = ImageIO.read( new File("/Users/vanditasoni/Downloads/background.jpg"));
    		winImage = ImageIO.read(new File("/Users/vanditasoni/Downloads/winner.png"));
    	}catch (IOException e) {
    		e.printStackTrace();
    	}
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setFocusable(true);
        addKeyListener(this);

        birdY = HEIGHT / 2;
        birdVelocity = 0;
        pipes = new ArrayList<>();
        score = 0;
        gameOver = false;
        gameStarted = false;

        timer = new Timer(20, this);
        letters = new HashSet<>();
        //letters.add('A'); //test case
       for (char c = 'A'; c <= 'Z'; c++) {
           letters.add(c);
        }

        generatePipe();
        remainingLetters = new ArrayList<>(letters);
        generateLetter();
    }

    public void generatePipe() {
        int pipeX = WIDTH;
        int gapPosition = new Random().nextInt(HEIGHT - PIPE_GAP);
        Rectangle topPipe = new Rectangle(pipeX, 0, PIPE_WIDTH, gapPosition);
        Rectangle bottomPipe = new Rectangle(pipeX, gapPosition + PIPE_GAP, PIPE_WIDTH, HEIGHT - gapPosition - PIPE_GAP);
        pipes.add(topPipe);
        pipes.add(bottomPipe);
    }

    public void movePipes() {
        for (Rectangle pipe : pipes) {
            pipe.setLocation(pipe.x - PIPE_SPEED, pipe.y);
        }
        if (pipes.get(0).x + pipes.get(0).width < 0) {
            pipes.remove(0);
            pipes.remove(0);
            generatePipe();
            score++; // Increment score when a new pipe is generated
        }
    }

//    public void generateLetter() {
//        remainingLetters = new ArrayList<>(letters);
//        remainingLetters.remove(Character.valueOf(currentLetter)); // Remove the current letter
//        currentLetter = remainingLetters.get(new Random().nextInt(remainingLetters.size())); // Select a new letter randomly
//    }
    public void generateLetter() {
    	if (remainingLetters.isEmpty()) {
    		gameWon = true;
    	    return;
    	    }
    	remainingLetters.remove(Character.valueOf(currentLetter)); // Remove the current letter
        currentLetter = remainingLetters.get(new Random().nextInt(remainingLetters.size())); // Select a new letter randomly
    }

    public void jump() {
        birdVelocity = -12;
        gameStarted = true; // Start the game when the player jumps
        timer.start(); // Start the timer when the player jumps
    }


    public void updateBird() {
        birdY += birdVelocity;
        birdVelocity += GRAVITY;

        if (birdY < 0 || birdY + BIRD_SIZE > HEIGHT) {
            gameOver = true;
        }
        
        //collision
        for (Rectangle pipe : pipes) {
            if (pipe.intersects(new Rectangle(WIDTH / 2 - BIRD_SIZE/ 2, birdY, BIRD_SIZE, BIRD_SIZE))) {
                gameOver = true;
            }
        }
    }
    
    private void checkForWin() {
    	if(remainingLetters.size()== 0) {
    		gameWon= true;
    		timer.stop();
    	}
    }
    
    //interface stuff
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        if (gameWon) {
        	g.drawImage(winImage, 0, 0, WIDTH, HEIGHT, null);
        	g.setFont(new Font("Arial", Font.BOLD, 45));
        	g.drawString("Final Score: " + (score), 20, 40);
        } else {
	        //background
	        g.drawImage(backgroundImage, 0, 0, WIDTH, HEIGHT, null);
	        if (spaceKeyPressed && remainingLetters.size() > 0){
	            g.drawImage(birdImageOpenKeyless, WIDTH / 2 - BIRD_SIZE / 2, birdY, BIRD_SIZE, BIRD_SIZE, null);
	        }
	        if (!spaceKeyPressed && remainingLetters.size() > 0){
	            g.drawImage(birdImageClosedKeyless, WIDTH / 2 - BIRD_SIZE / 2, birdY, BIRD_SIZE, BIRD_SIZE, null);
	        }
	        
	        //pipes
	        g.setColor(Color.BLUE);
	        for (Rectangle pipe : pipes) {
	            g.fillRect(pipe.x, pipe.y, pipe.width, pipe.height);
	        }
	
	        g.setColor(Color.BLACK);
	        g.setFont(new Font("Arial", Font.BOLD, 30));
	        g.drawString("Score: " + (score), 20, 40);
	
	        g.setFont(new Font("Arial", Font.BOLD, 50));
	        g.drawString(String.valueOf(currentLetter), WIDTH - 100, 50); // Display current letter
	
	        if (gameOver) {
	            g.setFont(new Font("Arial", Font.BOLD, 50));
	            g.drawString("Game Over", WIDTH / 2 - 150, HEIGHT / 2);
	        }
	        if (!gameOver && !gameStarted) {
	            g.setFont(new Font("Arial", Font.BOLD, 50));
	            g.drawString("Press Space to Start", WIDTH / 5, HEIGHT / 2 - 50);
	            g.setFont(new Font("Arial", Font.BOLD, 20));
	            g.drawString("Get all the letters to complete your keyboard", WIDTH / 23, HEIGHT -50);
	            g.drawString("The lower your score the better", WIDTH / 23, HEIGHT -20);
	        }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!gameOver && gameStarted) {
            movePipes();
            updateBird();
            checkForWin();
        }
        repaint();
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            jump();
            spaceKeyPressed = true;
        } else {
        	char pressedKey = Character.toUpperCase(e.getKeyChar());
        	if (Character.toUpperCase(e.getKeyChar()) == currentLetter && !gameOver && gameStarted) {
        		generateLetter();
        	} else if (Character.toUpperCase(e.getKeyChar()) != currentLetter && !gameOver && gameStarted) {
        		gameOver = true;
        	}
        	if (e.getKeyCode() == KeyEvent.VK_ENTER && gameOver) {
        		restartGame();
        	}
        }
    }
    
    @Override
    public void keyReleased(KeyEvent e) {
    	if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            spaceKeyPressed = false;
        }
    }
    //reset everything 
    public void restartGame() {
        birdY = HEIGHT / 2;
        birdVelocity = 0;
        pipes.clear();
        score = 0;
        gameOver = false;
        gameStarted = false;
        timer.stop();
        generatePipe();
        generateLetter();
    }	

    public static void main(String[] args) {
        JFrame frame = new JFrame("FlappyTop");
        FlappyTop game = new FlappyTop();
        frame.add(game);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}