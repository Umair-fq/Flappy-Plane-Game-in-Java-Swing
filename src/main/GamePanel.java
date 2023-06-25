package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.Random;

import javax.swing.JPanel;

import entity.Apple;
import entity.Map;
import entity.Player;

public class GamePanel extends JPanel implements Runnable{
	
	//Screen settigs
	final int originalTileSize = 16;
	final int scale = 3;
	
	public	final int tileSize = originalTileSize * scale; // 48
	public final int maxScreenCol = 12;
	public final int maxScreenRow = 6;
	public final int screenWidth = 800; 
	public final int screenHeight = 500; 
	
	// FPS
	int FPS = 60;   //FPS can be chnaged from here
	int score = 0;
	KeyHandler keyH = new KeyHandler();
	Thread gameThread;
	Map map = new Map(this);
	Apple apple = new Apple(this);
	Player player = new Player(this, keyH);
	
	public GamePanel() {            // This is to add the basic functionality of window size
		this.setPreferredSize(new Dimension(screenWidth, screenHeight));
		this.setBackground(Color.black);
		this.setDoubleBuffered(true);
		this.addKeyListener(keyH);
		this.setFocusable(true);
	}
	
	public void startGameThread() {     //Creating game thread here to give it an infinite loop
		
		gameThread = new Thread(this);
		gameThread.start();
	}
	
	@Override
	public void run() {
		
		double drawInterval = 1000000000/FPS;        //Updating changes after every single time
		double nextDrawTime = System.nanoTime() + drawInterval;
		while(gameThread != null) {
			long currentTime = System.nanoTime();
			update();
			
			repaint();
			//System.out.println("This game loop is running!\n");
			
			try {
				double remainingTime = nextDrawTime - System.nanoTime();
				remainingTime = remainingTime/1000000;
				if(remainingTime < 0) {
					remainingTime = 0;
				}
				Thread.sleep((long) remainingTime);
				
				nextDrawTime += drawInterval;
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
	}
	public void update() {
		player.update();
	}
	
	public void updateCollision(int [][]details, int x, int y) {    //Checking the collision between Apple and Player
		int temp1 = 0, temp2 = 0;
		for(int i = 0; i < 1; i++) {
			temp1 = details[i][0] - x;
			if(temp1 < 0) {
				temp1 = temp1 * -1;
			}
			temp2 = details[i][1] - y;
			if(temp2 < 0) {
				temp2 = temp2 * -1;
			}
			if(temp1 < 55 && temp2 < 20 && details[i][2] == 0) {
				details[i][2] = 1;
				Random m = new Random();
				int num = m.nextInt() % 300;
				details[0][1] = num + 60;
				player.goldCheck = false;
				score++;
			}
		}
		if(player.goldCheck == true) {
			
			details[0][2] = 0;
		}
	}
	
	public void paintComponent(Graphics g) {     //Repainting/Redrawing the whole game after every fps
		super.paintComponent(g);
		
		Graphics2D g2 = (Graphics2D)g;
		map.draw(g2);
		if(player.planeCrash == false) {
			apple.draw(g2);
		}
		player.draw(g2);
		updateCollision(apple.details, player.x, player.y);
		g2.setFont(new Font("Times New Roman", Font.BOLD, 20));
		g2.drawString("Score: ", 350, 40);
		g2.drawString("Gold Collected: ", 300, 80);
		g2.drawString(String.valueOf(score), 440, 80);
		g2.drawString(String.valueOf(player.playerScore), 410, 40);
		g2.dispose();
	}
}
