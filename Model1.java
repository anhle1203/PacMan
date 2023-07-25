import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Stroke;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Model1 extends JPanel implements ActionListener{
	
	private Dimension d;
	private final Font smallFont = new Font("Arial", Font.BOLD, 12); //Text display font

	//Pacman settings
	boolean living = false;
	private boolean dying = false;
	private int pacman_x, pacman_y, pacman_dx, pacman_dy;
	private int[] dx,dy; //What is this?
	private int pacman_speed;
	
	//screen and block settings
	private final int block_size = 24; //block size 
	private final int num_block = 15;
	private final int screen_size = num_block * block_size;
	
	//ghosts settings
	private final int max_ghost = 12;
	private int num_ghost = 6;
	private int[] ghost_x, ghost_y, ghost_dx, ghost_dy, ghostSpeed;

	//image settings
	private Image heart, ghost1, ghost2, ghost3;
	private Image up, down, left, right;
	
	//others settings
	private int req_dx; //Keyboard arrow left-right
	private int req_dy; //Keyboard arrow up-down
	private int lives, score;
	private final int validSpeed[] = {1,2,3,4,6,8};
	private final int maxSpeed = 6;
	private int currentSpeed = 3;
	private short [] screenData;
	private Timer timer;
	
	//level settings
	private final short levelData[] = {
		25, 21, 21, 21, 17, 21, 17, 21, 21, 21, 17, 21, 17, 21, 19,
		26, 0,  0,  0,  26, 0,  26, 0,  0,  0,  26, 0,  26, 0,  26,
		26, 0,  25, 21, 22, 0,  28, 21, 17, 21, 16, 21, 18, 0,  26,
		26, 0,  26, 0,  0,  0,  0,  0,  26, 0,  26, 0,  26, 0,  26,
		24, 17, 20, 17, 19, 0,  25, 21, 22, 0,  24, 21, 20, 21, 18,
		24, 22, 0,  28, 18, 0,  26, 0,  0,  0,  26, 0,  0,  0,  26,
		26, 0,  0,  0,  24, 21, 20, 17, 21, 21, 16, 21, 17, 21, 18,
		24, 19, 0,  25, 18, 0,  0,  26, 0,  0,  26, 0,  26, 0,  26,
		24, 18, 0,  24, 18, 0,  29, 20, 23, 0,  26, 0,  30, 0,  26,
		24, 18, 0,  24, 18, 0,  0,  0,  0,  0,  26, 0,  0,  0,  26,
		24, 20, 17, 20, 20, 21, 21, 21, 21, 21, 16, 21, 17, 21, 18,
		26, 0,  26, 0,  0,  0,  0,  0,  0,  0,  26, 0,  26, 0,  26,
		26, 0,  28, 21, 19, 0,  0,  0,  25, 21, 22, 0,  26, 0,  26,
		26, 0,  0,  0,  24, 19, 0,  25, 18, 0,  0,  0,  26, 0,  26,
		28, 21, 21, 21, 20, 20, 21, 20, 20, 21, 21, 21, 20, 21, 22
	};
	
	public Model1() {
		loadImages();
		initVariables();
//		addKeyListener(new TAdapter());
		setFocusable(true);
		initGame();
	}
	
	private void loadImages() {
		down = new ImageIcon("/Users/dale/Downloads/New Stuff/Pacman/Images/down.gif").getImage();
		up = new ImageIcon("/Users/dale/Downloads/New Stuff/Pacman/Images/up.gif").getImage();
		right = new ImageIcon("/Users/dale/Downloads/New Stuff/Pacman/Images/right.gif").getImage();
		left = new ImageIcon("/Users/dale/Downloads/New Stuff/Pacman/Images/left.gif").getImage();
		ghost1 = new ImageIcon("/Users/dale/Downloads/New Stuff/Pacman/Images/ghost1.gif").getImage();
		ghost2 = new ImageIcon("/Users/dale/Downloads/New Stuff/Pacman/Images/ghost2.gif").getImage();
		ghost3 = new ImageIcon("/Users/dale/Downloads/New Stuff/Pacman/Images/ghost3.gif").getImage();
		heart = new ImageIcon("/Users/dale/Downloads/New Stuff/Pacman/Images/heart.gif").getImage();
	}
	
	
	public void showIntroScreen(Graphics2D g2d) {
		String start = "Press SPACE to start";
		g2d.setColor(Color.yellow);
		g2d.drawString(start, screen_size/3+5, 197);
	}
	
	public void drawScore(Graphics2D g2d) {
		g2d.setFont(new Font("Arial", Font.BOLD, 12));
		g2d.setColor(Color.green);
		String Score = "Score:" + score;
		g2d.drawString(Score, screen_size-60, screen_size + 16);
		
		for (int i=0; i<lives;i++) {
			g2d.drawImage(heart,i*28+8,screen_size+1,this);
			
		}
	}
	
	private void initVariables() {
		screenData = new short[num_block * num_block];
		d = new Dimension(400,400);
		ghost_x = new int[max_ghost];
		ghost_y = new int[max_ghost];
		ghost_dx = new int[max_ghost];
		ghost_dy = new int[max_ghost];
		dx = new int[4];
		dy= new int[4];
		
		timer = new Timer(40, this); //How often the images are redrawn
		timer.restart();
	}
	
	
	private void initGame() {
		lives = 3;
		score = 0;
		initLevel();
		num_ghost = 3;
		currentSpeed = 3;
	}
	
	private void initLevel() {
		for (int i = 0; i< num_block * num_block; i++) {
			screenData[i] = levelData[i];
		}
	}
	
	private void playGame(Graphics2D g2d) {
		if (dying) { //when death dies the function is called
			death();
		}
		else {
			movePacman();
			drawPacman(g2d);
			moveGhosts(g2d);
			checkMaze();
		}
	}
	
	public void movePacman() {
		int pos;
		short ch;
		
		//Determine Pacman position
		if (pacman_x % block_size == 0 && pacman_y % block_size == 0) {
			pos = pacman_x/block_size + num_block * (int) (pacman_y/block_size); //make sure it does not collide with walls
			ch = screenData[pos];
		
		if ((ch & 16) != 0) {
		screenData[pos] = (short) (ch & 15);
		score++;
			}
		
		if(req_dx != 0 || req_dy != 0) {
			if (!((req_dx == -1 && req_dy == 0 && (ch&1) != 0)
				|| (req_dy == 1 && req_dy == 0 && (ch&4) != 0)
				|| (req_dx == 0 && req_dy == -1 && (ch&2) != 0)
				|| (req_dx == 0 && req_dy == 1 && (ch&8) != 0))){
					pacman_dx = req_dx;
					pacman_dy = req_dy;
			}
		}
		
			if (!((pacman_dx == -1 && pacman_dy == 0 && (ch&1) != 0)
				|| (pacman_dx== 1 && pacman_dy == 0 && (ch&4) != 0)
				|| (pacman_dx == 0 && pacman_dy == -1 && (ch&2) != 0)
				|| (pacman_dx == 0 && pacman_dy == 1 && (ch&8) != 0))){
					pacman_dx = 0;
					pacman_dy = 0;
			}
		}
		
		pacman_x = pacman_x + pacman_speed * pacman_dx;
		pacman_y = pacman_y + pacman_speed * pacman_dy;
		
	}
	
	
	public void drawPacman(Graphics2D g2d) {
		if (req_dx == -1) {
			//left image is loaded, 
			g2d.drawImage(left,pacman_x+1, pacman_y+1,this);
		}
		else if (req_dx == 1) {
			g2d.drawImage(right,pacman_x+1, pacman_y+1,this);
		}
		else if (req_dy == 1) {
			g2d.drawImage(up,pacman_x+1, pacman_y+1,this);
		}
		else if (req_dy == -1) {
			g2d.drawImage(down,pacman_x+1, pacman_y+1,this);
		}
	}
	
	public void moveGhosts(Graphics2D g2d) {
		int pos;
		int count;
		for (int i=0; i<num_ghost;i++) {
			if (ghost_x[i] % block_size == 0 && ghost_y[i] % block_size == 0 ) {
				pos = ghost_x[i] / block_size + num_block * (int) (ghost_y[i] / block_size);
				
				count = 0;
				//cannot go up
				if ((screenData[pos] & 1) == 0 && ghost_dy[i] != 1) {
					dx[count] = 0;
					dy[count] = -1;
					count++;
				}
				//cannot go right, dy instead?
				if ((screenData[pos] & 2) == 0 && ghost_dx[i] != 1) {
					dx[count] = -1;
					dy[count] = 0;
					count++;
				}
				//cannot go down
				if ((screenData[pos] & 4) == 0 && ghost_dx[i] != -1) {
					dx[count] = 0;
					dy[count] = 1;
					count++;
				}
				//cannot go left
				if ((screenData[pos] & 8) == 0 && ghost_dx[i] != -1) {
					dx[count] = 1;
					dy[count] = 0;
					count++;
				}
				
				
				if (count == 0) {
					if ((screenData[pos] & 15) == 15){
						ghost_dy[i] = 0;
						ghost_dx[i] = 0;
					} else {
						ghost_dy[i] = - ghost_dy[i];
						ghost_dx[i] = - ghost_dx[i];
					}
				}
				else {
					count = (int) Math.random() * count;
					
					if (count >3) {
						count = 3;
					}
					
					ghost_dx[i] = dx[count];
					ghost_dy[i] = dy[count];
					}
				}
			
			ghost_x[i] = ghost_x[i] + (ghost_dx[i] * ghostSpeed[i]);
			ghost_y[i] = ghost_y[i] + (ghost_dx[i] * ghostSpeed[i]);
			drawGhost(g2d,ghost_x[i], ghost_y[i] +1);
			
			if (pacman_x > (ghost_x[i] - 12) && pacman_x < (ghost_x[i] +12)
			 && pacman_y > (ghost_y[i] - 12) && pacman_y < (ghost_y[i] + 12)
			 && living) {
				dying = true;
			}
		}
	}
	
	public void drawGhost(Graphics2D g2d, int x, int y) {
		g2d.drawImage(ghost1, x, y, this);
	}
	
	
	public void checkMaze() {
		int i = 0;
		boolean finished = true;
		
		while(i<num_block * num_block && finished) {
			if ((screenData[i] & 48) != 0) {
				finished = false;
			}
		} i++;
	
	
	if (finished) {
		score += 50;
		
		if (num_ghost < max_ghost) {
			num_ghost++;
		}
		if (currentSpeed < maxSpeed) {
			currentSpeed++;
		}
	} initLevel();
	
	}
	
	private void death() {
		lives--;
		if (lives ==0) {
			living = false;
		}
	
		continueLevel();
	}
	
	private void continueLevel() {
		int dx = 1;
		int random;
		for (int i =0; i<num_ghost; i++) {
				ghost_y[i] = 9 * block_size;
				ghost_x[i] = 7 * block_size;
				ghost_dy[i] = 0;
				ghost_dx[i] = dx;
				dx= -dx;
				random = (int) (Math.random() * (currentSpeed +1));
				
				if (random > currentSpeed) {
					random = currentSpeed;
				}
				
				ghostSpeed[i] = validSpeed[random];
		}
		
		pacman_x = 8 * block_size;
		pacman_y = 9 * block_size;
		pacman_dx = 0;
		pacman_dy = 0;
		req_dx = 0;
		req_dy = 0;
	}
	
	public void drawMaze(Graphics2D g2d) {
		short i = 0;
		int x,y;
		for (y =0; y<screen_size; y+= block_size) {
			for (x=0; x<screen_size; x+= block_size) {
				g2d.setColor(new Color(70,26,245)); //RGB Code
				g2d.setStroke(new BasicStroke(5));
				
				if (screenData[i] == 0) {
					g2d.fillRect(x, y, block_size, block_size);
				}
				if ((screenData[i] & 1) != 0) {
					g2d.drawLine(x, y, x+ block_size - 1, y);
				}
				if ((screenData[i] & 2) != 0) {
					g2d.drawLine(x+ block_size -1, y, x+block_size-1, y+block_size-1);
				}
				if ((screenData[i] & 4) != 0) {
					g2d.drawLine(x, y+block_size-1, x+ block_size -1, y+block_size-1);
				}
				if ((screenData[i] & 8) != 0) {
					g2d.drawLine(x, y, x, y+ block_size - 1);
				}
				if ((screenData[i] & 16) != 0) {
					g2d.setColor(Color.white);
					g2d.fillOval(x+10,y+10,6,6);
				}
				
				i++;
			}
		}
	}
	

	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g); //parent class
		
		Graphics2D g2d = (Graphics2D) g;
		g2d.setColor(Color.black);
		g2d.fillRect(0, 0, d.width, d.height);
		
		drawMaze(g2d);
		drawScore(g2d);
		
		if (living) {
			playGame(g2d);
		}
		else {
			showIntroScreen(g2d);
		}
		
		Toolkit.getDefaultToolkit().sync(); //make sure the visual is updating well
	}
	
	
	
	
	
	
	
	
	
	
	
	class keyClicked extends KeyAdapter{
	public void keyPressed(KeyEvent e) {
		int key = e.getKeyCode();
		if (living == true) {
			if (key == KeyEvent.VK_LEFT) {
				System.out.println("Check");
				req_dx = -1;
				req_dy = 0;
			}
			else if (key == KeyEvent.VK_RIGHT) {
				req_dx = 1;
				req_dy = 0;
			}
			else if (key == KeyEvent.VK_UP) {
				req_dx = 0;
				req_dy = 1;
			}
			else if (key == KeyEvent.VK_DOWN) {
				req_dx = 0;
				req_dy = -1;
			}
			else if (key == KeyEvent.VK_ESCAPE && timer.isRunning()) {
				living = false;
			}
		}
		else {
			if(key == KeyEvent.VK_SPACE) {
			living = true;
			initGame();
		}
		}
	}
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	
	
}
