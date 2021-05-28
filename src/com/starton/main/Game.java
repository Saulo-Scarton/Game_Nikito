package com.starton.main;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import java.awt.image.DataBufferInt;
import com.starton.entities.Boss;
import com.starton.entities.Enemy;
import com.starton.entities.Entity;
import com.starton.entities.Flower;
import com.starton.entities.Player;
import com.starton.entities.Shot;
import com.starton.graphics.Spritesheet;
import com.starton.graphics.UI;
import com.starton.world.World;

public class Game extends Canvas implements Runnable,KeyListener,MouseListener,MouseMotionListener{
	
	private static final long serialVersionUID = 1L;
	public static JFrame frame;
	private Thread thread;
	private boolean isRunning = true;
	public static final int WIDTH = 240;
	public static final int HEIGHT = 160;
	public static final int SCALE = 5;
	
	public static int FPS = 0;
	
	public static int CUR_LEVEL = 1,MAX_LEVEL = 3; //quantidade de "fases"
	private BufferedImage image;
	
	public static List<Entity> entities;
	public static List<Enemy> enemies;
	public static List<Boss> boss1;
	public static List<Flower> flower;
	public static List<Shot> shot;
	public static Spritesheet spritesheet;
	
	public static World world;
	
	public static Player player;
	
	public static Random random;
	
	public UI ui;
	
	//public int xx, yy;
	
	//public InputStream stream = ClassLoader.getSystemClassLoader().getResourceAsStream("pixelfont.ttf"); //fonte importada
	//public Font newFont;
	
	public static String gameState = "MENU";
	private boolean showMessageGameOver = true;
	private int framesGameOver = 0;
	public static boolean restartGame = false;
	public boolean saveGame = false;
	public Menu menu;
	
	public int[] pixels;
	public BufferedImage lightmap;
	public int[] lightmapPixels;
	
	public int mx,my;
	
	public Game() {
		random = new Random();
		addKeyListener(this); //precisa informar que estará usando o keylistenner para funcionar
		addMouseListener(this); //precisa informar que estará usando o Mouselistenner para funcionar
		addMouseMotionListener(this);
		setPreferredSize(new Dimension(WIDTH*SCALE,HEIGHT*SCALE));
		initFrame();
		//Inicializando objetos
		ui = new UI();
		image = new BufferedImage(WIDTH,HEIGHT,BufferedImage.TYPE_INT_RGB);
		try {
			lightmap = ImageIO.read(getClass().getResource("/lightmap.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		lightmapPixels = new int[lightmap.getWidth()*lightmap.getHeight()];
		lightmap.getRGB(0, 0, lightmap.getWidth(), lightmap.getHeight(), lightmapPixels, 0, lightmap.getWidth());
		pixels = ((DataBufferInt)image.getRaster().getDataBuffer()).getData(); //manipular pixel da imagem
		entities = new ArrayList<Entity>();
		enemies = new ArrayList<Enemy>();
		boss1 = new ArrayList<Boss>();
		flower = new ArrayList<Flower>();
		shot = new ArrayList<Shot>();
		spritesheet = new Spritesheet("/spritesheet.png");
		player = new Player(0,0,16,16,spritesheet.getSprite(32, 0, 16, 16));
		entities.add(player);
		world = new World("/level1.png"); //World precisa ser carregado depois do spritesheet
		
		/*
		try {
			newFont = Font.createFont(Font.TRUETYPE_FONT, stream).deriveFont(36f);
		} catch (FontFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		*/
		
		menu = new Menu();
	}

	private void initFrame() {
		frame = new JFrame("Game #1");
		frame.add(this);
		frame.setResizable(false);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		
	}

	public synchronized void start() {
		thread = new Thread(this);
		isRunning = true;
		thread.start();
	}
	
	public synchronized void stop() {
		isRunning = false;
		try {
			thread.join();
		}catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		Game game = new Game();
		game.start();
	}
	
	public void tick() { //toda logica do game fica em tick
		if(Game.CUR_LEVEL == 3) {
			Sound.bombastic1.playLoop();
		}else {
		Sound.music.playLoop();
		}
		if(gameState == "NORMAL") {
			if(this.saveGame) {
				this.saveGame = false;
				String[] opt1 = {"level","life"}; //parametros que serão salvos
				int[] opt2 = {CUR_LEVEL, player.Life}; //parametros que serão salvos
				Menu.saveGame(opt1,opt2,10);
				System.out.println("Jogo foi salvo com sucesso!");
			}
			Game.restartGame = false; //para prevenir que o usuario aperte ENTER e reinicie o jogo
			for(int i = 0; i < entities.size(); i++) {
				Entity e = entities.get(i);
				e.tick();
			}
			
			for(int i = 0; i < shot.size(); i++) {
				shot.get(i).tick();
			}
			
			if(enemies.size() == 0 && boss1.size() == 0) {//se todos inimigos forem eliminados
				//próximo level
				CUR_LEVEL++;
				if(CUR_LEVEL > MAX_LEVEL) {
					CUR_LEVEL = 1;
				}
				String newWorld = "level" + CUR_LEVEL + ".png";
				World.restartGame(newWorld);
			}
		}else if(gameState == "GAME_OVER") {
			//faz piscar a mensagem "pressione enter para continuar"
			framesGameOver++;
			if(framesGameOver == 30) {
				framesGameOver = 0;
				if(showMessageGameOver)
					showMessageGameOver = false;
				else
					showMessageGameOver = true;
			}
			
			if(restartGame) {
				restartGame = false;
				gameState = "NORMAL";
				CUR_LEVEL = 1;
				String newWorld = "level" + CUR_LEVEL + ".png";
				World.restartGame(newWorld);
			}
		}else if(gameState == "MENU") {//Mostra Menu
			player.updateCamera();
			menu.tick();
		}
	}
	
	/*
	public void drawRectangleExample(int xoff, int yoff) {
		for(int xx =0; xx < 32; xx++) {
			for(int yy = 0; yy < 32; yy++) {
				int xOff = xx + xoff;
				int yOff = yy + yoff;
				if(xOff < 0 || yOff < 0 || xOff >= WIDTH || yOff >= HEIGHT) {// verifica se saiu do mapa e não da erro
					continue;
				}
				pixels[xOff + (yOff*WIDTH)] = 0xFF0000;
			}
		}
	}
	*/
	
	public void applyLight() {
		for(int xx = 0; xx < Game.WIDTH; xx++) {
			for(int yy = 0; yy < Game.HEIGHT; yy++) {
				if(lightmapPixels[xx+(yy* Game.WIDTH)] == 0xffffffff) {
					pixels[xx+(yy*Game.WIDTH)] =0;
				}
			}
		}
	}
	
	public void render() {
		BufferStrategy bs = this.getBufferStrategy();
		if(bs == null){
			this.createBufferStrategy(3);
			return;
		}
		
		Graphics g = image.getGraphics();
		g.setColor(new Color(0,0,0));
		g.fillRect(0, 0, WIDTH, HEIGHT);

		
		world.render(g); //renderizar mundo
		
		for(int i = 0; i < entities.size(); i++) {
			Entity e = entities.get(i);
			e.render(g);
		}
		for(int i = 0; i < shot.size(); i++) {
			shot.get(i).render(g);
		}
		
		applyLight();
		ui.render(g);
		/***/
		g.dispose();
		g = bs.getDrawGraphics();
		
		
		g.drawImage(image, 0, 0, WIDTH*SCALE, HEIGHT*SCALE, null);
		
		//mostrar FPS
		g.setFont(new Font("arial",Font.BOLD,20));
		g.setColor(Color.black);
		g.drawString("FPS: " + FPS, (WIDTH*SCALE)+3 - 85, (HEIGHT*SCALE)-7);
		g.setFont(new Font("arial",Font.BOLD,20));
		g.setColor(Color.white);
		g.drawString("FPS: " + FPS, (WIDTH*SCALE) - 85, (HEIGHT*SCALE)-5);
		
		//mostrar qtd ammo
		g.setFont(new Font("arial",Font.BOLD,20));
		g.setColor(Color.black);
		g.drawString("Ammo: " + player.Ammo, (WIDTH*SCALE)+3 - 120, 30-1);
		g.setFont(new Font("arial",Font.BOLD,20));
		g.setColor(Color.white);
		g.drawString("Ammo: " + player.Ammo, WIDTH*SCALE - 120, 30);
		//g.setFont(newFont);
		//g.drawString("Teste", 20, 100);
		
		
		if(gameState == "GAME_OVER") { //se der game over tela fica escura
			Graphics2D g2 = (Graphics2D) g;
			g2.setColor(new Color(0,0,0,100));
			g2.fillRect(0, 0, WIDTH * SCALE, HEIGHT * SCALE);
			g.setFont(new Font("arial",Font.BOLD,100));
			g.setColor(Color.black);
			g.drawString("GAME OVER",(WIDTH*SCALE)/2 - 295, (HEIGHT*SCALE)/2 + 25);
			g.setFont(new Font("arial",Font.BOLD,100));
			g.setColor(Color.red);
			g.drawString("GAME OVER",(WIDTH*SCALE)/2 - 300, (HEIGHT*SCALE)/2 + 30);
			if(showMessageGameOver) {
				g.setFont(new Font("arial",Font.BOLD,60));
				g.setColor(Color.black);
				g.drawString(">Press ENTER to continue<",(WIDTH*SCALE)/2 - 375, (HEIGHT*SCALE)/2 + 95);
				g.setFont(new Font("arial",Font.BOLD,60));
				g.setColor(Color.white);
				g.drawString(">Press ENTER to continue<",(WIDTH*SCALE)/2 - 380, (HEIGHT*SCALE)/2 + 100);
			}
		}else if(gameState == "MENU") {
			menu.render(g);
		}
		
		/*inserir quadrado que rotaciona "seguindo o cursor"
		Graphics2D g2 = (Graphics2D) g;
		double angleMouse = Math.atan2(200+25 - my, 200+25 - mx);
		g2.rotate(angleMouse, 200+25, 200+25);
		g.setColor(Color.orange);
		g.fillRect(200, 200, 50, 50);
		*/
		
		bs.show();
	}

	public void run() {
		long lastTime = System.nanoTime();
		double amountOfTicks = 60.0;
		double ns = 1000000000 / amountOfTicks;
		double delta = 0;
		int frames = 0;
		double timer = System.currentTimeMillis();
		requestFocus(); //para o jogo iniciar selecionado (não precisa clicar na tela pra começar a jogar)
		while(isRunning) {
			long now = System.nanoTime();
			delta+= (now - lastTime) / ns;
			lastTime = now;
			if(delta >= 1) {
				tick();
				render();
				frames++;
				delta--;
			}
			
			if(System.currentTimeMillis() - timer >= 1000) {
				FPS = frames;
				frames = 0;
				timer+=1000;
			}
		}
		stop();
	}

	@Override
	public void keyTyped(KeyEvent e) {

		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D) {
			player.right = true;
		}else if(e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A) {
			player.left = true;
		}
		
		if(e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_W) {
			player.up = true;
		}else if(e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_S) {
			player.down = true;
		}
		
		//Shift para aumentar a velocidade (correr)
		if(e.getKeyCode() == KeyEvent.VK_SHIFT) {
			player.run = true;
		}
		
		//Atirar
		if(e.getKeyCode() == KeyEvent.VK_X) {
			player.shoot = true;
		}
		
		//ENTER para "CONTINUE"
		if(e.getKeyCode() == KeyEvent.VK_ENTER) {
			restartGame = true;
			menu.enter = true;
		}
		
		//ESC para pausar
		if(e.getKeyCode() == KeyEvent.VK_ESCAPE) {
				Menu.pause = true;
			if(gameState == "NORMAL") {
				Menu.menuOptionPause = "Resume";
				gameState = "MENU";
			}else if(gameState == "MENU" && Menu.menuOptionPause == "Resume") {
				gameState = "NORMAL";
			}
		}
		
		if(e.getKeyCode() == KeyEvent.VK_SPACE) {
			if(gameState == "NORMAL") {
				this.saveGame = true;
			}
		}
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D) {
			player.right = false;
		}else if(e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A) {
			player.left = false;
		}
		
		if(e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_W) {
			player.up = false;
			if(gameState == "MENU") {
				menu.up = true;
			}
		}else if(e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_S) {
			player.down = false;
			if(gameState == "MENU") {
				menu.down = true;
			}
		}
		
		//Shift para aumentar a velocidade (correr)
		if(e.getKeyCode() == KeyEvent.VK_SHIFT) {
			player.run = false;
		}
		
		//Atirar
		if(e.getKeyCode() == KeyEvent.VK_X) {
			player.shoot = false;
		}
		
		//ENTER para "CONTINUE"
		if(e.getKeyCode() == KeyEvent.VK_ENTER) {
			restartGame = false;
		}
		
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		player.mouseShoot = true;
		player.mx = (e.getX() / SCALE);
		player.my = (e.getY() / SCALE);
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		this.mx = e.getX();
		this.my = e.getY();
		
	}
}




