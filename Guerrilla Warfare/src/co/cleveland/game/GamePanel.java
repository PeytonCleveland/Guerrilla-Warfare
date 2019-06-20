package co.cleveland.game;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.*;

@SuppressWarnings("serial")
public class GamePanel extends JPanel implements Runnable, KeyListener {
	
	private static final int WIDTH = 1100;
	private static final int HEIGHT = WIDTH / 16 * 9;
	
	private Graphics dbg;
	private Image dbImage = null;
	
	private Thread thread;
	private volatile boolean running = false;
	private volatile boolean gameOver = false;
	private volatile boolean paused = false;
	
	private final double FPS = 60.0;
	private final double FRAME_PERIOD = 1000000000 / FPS;
	
	private final static String IMS_INFO = "imsInfo.txt";
	private final static String BLOCKS_INFO = "blocksInfo2.txt";
	
	ImageLoader imsLoader = new ImageLoader(IMS_INFO);
	BlockManager bm = new BlockManager(WIDTH, HEIGHT, BLOCKS_INFO, imsLoader);
	BulletManager bulletManager = new BulletManager();
	int blockMoveSize = bm.getMoveSize();
	
	BackgroundManager backgroundManager = new BackgroundManager(WIDTH, HEIGHT, blockMoveSize, imsLoader);
	
	private PlayerSprite player = new PlayerSprite(WIDTH, HEIGHT, blockMoveSize, bulletManager, bm, imsLoader, (int) (FRAME_PERIOD / 1000));
	private EnemySprite enemy = new EnemySprite(575, 0, WIDTH, HEIGHT, blockMoveSize, bulletManager, bm, imsLoader, (int) (FRAME_PERIOD / 1000));
	private ArtificialIntelligence ai = new ArtificialIntelligence(enemy, player);
	
	public GamePanel(){
		
		JFrame frame = new JFrame();
		frame.setTitle("Guerrilla Warfare");
		
		frame.setPreferredSize(new Dimension(WIDTH, HEIGHT));
		frame.setMaximumSize(new Dimension(WIDTH, HEIGHT));
		frame.setMinimumSize(new Dimension(WIDTH, HEIGHT));
		frame.addKeyListener(this);
		
		frame.setLayout(new BorderLayout());
		frame.add(this, BorderLayout.CENTER);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		frame.setFocusable(true);
		frame.requestFocus();
		player.setAI(ai);
		bulletManager.setEnemy(enemy);
		
	}
	
	public void addNotify(){
		
		super.addNotify();
		startGame();
		
	}
	
	private void startGame(){
		
		if(thread == null || !running){
			thread = new Thread(this);
			thread.start();
		}
	}
	
	public void stopGame(){
		
		running = false;
		
	}
	
	public void run(){
		
		running = true;
		
		double beginTime = System.nanoTime();
		double delta = 0;
		int updates = 0;
		int frames = 0;
		double timer = System.nanoTime();
		
		while(running){
			double currentTime = System.nanoTime();
			delta += (currentTime - beginTime) / FRAME_PERIOD;
			beginTime = currentTime;
			if(delta >= 1){
				update();
				updates++;
				delta--;
			}
			render();
			paintScreen();
			try {
				Thread.sleep(5);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			frames++;
		
		if(System.nanoTime() - timer > 1000000000){
			timer += 1000000000;
			System.out.println(updates + " Updates, Fps " + frames);
			updates = 0;
			frames = 0;
			}
		}
		System.exit(0);
	}
	
	private void update(){
		backgroundManager.update();
		bulletManager.update();
		player.updateSprite();
		ai.update();
		enemy.updateSprite();
		bm.update();
	}
	
	private void render(){
		if(dbImage == null){
			dbImage = createImage(WIDTH, HEIGHT);
			if(dbImage == null){
				System.out.println("dbImage is null");
				return;
			}else{
				dbg = dbImage.getGraphics();
			}
		}
			backgroundManager.display(dbg);
			bulletManager.display(dbg);
			player.display(dbg);
			enemy.display(dbg);
			bm.display(dbg);
	}
	
	private void paintScreen(){
		Graphics g;
		try{
			g = this.getGraphics();
			if((g != null) && (dbImage != null))
				g.drawImage(dbImage, 0, 0, null);
			Toolkit.getDefaultToolkit().sync();
			g.dispose();
		}
		catch(Exception e){
			System.out.println("Graphics context error: " + e);
		}
	}

	public void keyPressed(KeyEvent e) {
		int keyCode = e.getKeyCode();
		
		if(keyCode == KeyEvent.VK_ESCAPE){
			running = false;
		}
		
		if((!paused) && (!gameOver)){
			if(keyCode == KeyEvent.VK_RIGHT){
				player.moveRight();
				bm.moveLeft();
				backgroundManager.moveLeft();
			}else if(keyCode == KeyEvent.VK_LEFT){
				player.moveLeft();
				bm.moveRight();
				backgroundManager.moveRight();
			}else if(keyCode == KeyEvent.VK_UP){
				player.jump();
			}else if(keyCode == KeyEvent.VK_S){
				player.shoot();
			}else if(keyCode == KeyEvent.VK_R){
				player.reload();
			}
		}
	}
	
	public void keyReleased(KeyEvent e) {
		int keyCode = e.getKeyCode();
		if((keyCode == KeyEvent.VK_RIGHT) || (keyCode == KeyEvent.VK_LEFT)){
			player.stayStill();
			bm.stayStill();
			backgroundManager.stayStill();
		}
	}

	public void keyTyped(KeyEvent arg0) {
	}
}
