package com.starton.main;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import java.awt.image.DataBufferInt;
import com.starton.entities.Boss;
import com.starton.entities.Enemy;
import com.starton.entities.Entity;
import com.starton.entities.Flower;
import com.starton.entities.Npc;
import com.starton.entities.Player;
import com.starton.entities.Shot;
import com.starton.entities.Sword;
import com.starton.graphics.Spritesheet;
import com.starton.graphics.UI;
import com.starton.world.Camera;
import com.starton.world.World;

public class Game extends Canvas implements Runnable,KeyListener,MouseListener,MouseMotionListener{
	
	private static final long serialVersionUID = 1L;
	public static JFrame frame;
	private Thread thread;
	private boolean isRunning = true;
	
	//TAMANHO TELA E ESCALA
	public static final int WIDTH = 280;
	public static final int HEIGHT = 200;
	public static final int SCALE = 5;
	
	
	//VARIAVEIS PARA SWORD & PLAYER
	public int SwordHeight = 15 * SCALE;
	public int SwordWidth = 2 * SCALE;
	public int SwordDesloc = 7;
	
	//MINIMAPA
	public static int minimapSize = 25*SCALE;
	
	//FPS
	public static int FPS = 0;
	
	//MODO DEBUG
	public static boolean DEBUG = false;
	
	//LEVEIS
	public static int CUR_LEVEL = 1,MAX_LEVEL = 3; //quantidade de "fases"
	
	
	private BufferedImage image;
	
	//DECL LISTA ENTIDADES
	public static List<Entity> entities;
	public static List<Enemy> enemies;
	public static List<Boss> boss1;
	public static List<Flower> flower;
	public static List<Shot> shot;
	public static List<Sword> sword;
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
	
	//Sistema de CUTSCENE
	public static int entrance1 = 1;
	public static int begin = 2;
	public static int playing = 3;
	public static int finish = 4;
	public static int scene_state = entrance1;
	public int sceneTime = 0, maxSceneTime = 60*5;
	private int countDown = 0;
	private int opac = 255;
	
	public int[] pixels;
	public BufferedImage lightmap;
	public int[] lightmapPixels;
	public static int[] minimapPixels;
	
	public Npc npc;
	
	public static int mx,my;
	
	private static BufferedImage miniMap;
	
	public Game() {
		random = new Random();
		addKeyListener(this); //precisa informar que estar? usando o keylistenner para funcionar
		addMouseListener(this); //precisa informar que estar? usando o Mouselistenner para funcionar
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
		sword = new ArrayList<Sword>();
		spritesheet = new Spritesheet("/spritesheet.png");
		player = new Player(0,0,16,16,spritesheet.getSprite(32, 0, 16, 16));
		entities.add(player);
		world = new World("/level"+CUR_LEVEL+".png"); //World precisa ser carregado depois do spritesheet
		
		//NPC
		npc = new Npc(32,32,16,16,spritesheet.getSprite(80,64,16,16));
		entities.add(npc);
		
		
		miniMap = new BufferedImage(World.WIDTH,World.HEIGHT, BufferedImage.TYPE_INT_RGB);
		minimapPixels = ((DataBufferInt)miniMap.getRaster().getDataBuffer()).getData(); //manipular pixel da imagem
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
		
		//icone da janela
		Image image = null;
		try {
			image = ImageIO.read(getClass().getResource("/icon.png"));
		}catch (IOException e) {
			e.printStackTrace();
		}
		//cursor
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Image cursorImage = toolkit.getImage(getClass().getResource("/icon.png"));
		Cursor c = toolkit.createCustomCursor(cursorImage, new Point(0,0),"img");
		
		frame.setCursor(c);
		frame.setIconImage(image);
		frame.setAlwaysOnTop(true);
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
		Sound.music.playLoop();
		if(gameState == "NORMAL") {
			if(this.saveGame) {
				this.saveGame = false;
				String[] opt1 = {"level","life"}; //parametros que ser?o salvos
				int[] opt2 = {CUR_LEVEL, player.Life}; //parametros que ser?o salvos
				Menu.saveGame(opt1,opt2,10);
				System.out.println("Jogo foi salvo com sucesso!");
			}
			Game.restartGame = false; //para prevenir que o usuario aperte ENTER e reinicie o jogo
			
			//cutscene
			if(scene_state == playing || DEBUG) {
				for(int i = 0; i < entities.size(); i++) {
					Entity e = entities.get(i);
					e.tick();
				}
				
				for(int i = 0; i < shot.size(); i++) {
					shot.get(i).tick();
				}
				for(int i = 0; i < sword.size(); i++) {
					sword.get(i).tick();
				}
			}else if(scene_state == entrance1 ) {
				if(player.getX() < 30) {
					player.x+=0.1;
				}else {
					scene_state = begin;
				}
			}else if(scene_state == finish) {
				sceneTime++;
				if(sceneTime == maxSceneTime) {
					sceneTime = 0;
					scene_state = playing;
				}
			}else if(scene_state == begin) {
				sceneTime++;
				if(sceneTime == maxSceneTime) {
					sceneTime = 0;
					scene_state = playing;
				}
			}
			
			if(enemies.size() == 0 && boss1.size() == 0) {//se todos inimigos forem eliminados
				//pr?ximo level
				scene_state = entrance1;
				
				CUR_LEVEL++;
				if(CUR_LEVEL > MAX_LEVEL) {
					CUR_LEVEL = 1;
					player.updateCamera();
					menu.tick();
				}
				
				String newWorld = "level" + CUR_LEVEL + ".png";
				World.restartGame(newWorld);
				player.updateCamera();
				menu.tick();
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
				if(xOff < 0 || yOff < 0 || xOff >= WIDTH || yOff >= HEIGHT) {// verifica se saiu do mapa e n?o da erro
					continue;
				}
				pixels[xOff + (yOff*WIDTH)] = 0xFF0000;
			}
		}
	}
	*/
	
	public void applyLight() {
		for(int xx = 0; xx < WIDTH; xx++) {
			for(int yy = 0; yy < HEIGHT; yy++) {
				if(lightmapPixels[xx+yy* WIDTH] == 0xff000000) {
					int pixel = Pixel.getLightBlend(pixels[xx+yy*WIDTH], 0xFF808080, 0);
					pixels[xx+yy*WIDTH] = pixel;
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
		
		Collections.sort(entities, Entity.nodeSorter);
		
		for(int i = 0; i < entities.size(); i++) {
			Entity e = entities.get(i);
			e.render(g);
		}
		for(int i = 0; i < shot.size(); i++) {
			shot.get(i).render(g);
		}
		for(int i = 0; i < sword.size(); i++) {
			sword.get(i).render(g);
		}
		
		//applyLight();
		ui.render(g);
		/***/
		g.dispose();
		g = bs.getDrawGraphics();
		
		
		g.drawImage(image, 0, 0, WIDTH*SCALE, HEIGHT*SCALE, null);
		
		//mostrar FPS
		g.setFont(new Font("arial",Font.BOLD,20));
		g.setColor(Color.black);
		g.drawString("FPS: " + FPS, (WIDTH*SCALE)+3 - 85, (HEIGHT*SCALE)-minimapSize-8);
		g.setFont(new Font("arial",Font.BOLD,20));
		g.setColor(Color.white);
		g.drawString("FPS: " + FPS, (WIDTH*SCALE) - 85, (HEIGHT*SCALE)-minimapSize-6);
		
		//mostrar qtd ammo
		g.setFont(new Font("arial",Font.BOLD,20));
		g.setColor(Color.black);
		g.drawString("Ammo: " + player.Ammo, (WIDTH*SCALE)+3 - 120, 30-1);
		g.setFont(new Font("arial",Font.BOLD,20));
		g.setColor(Color.white);
		g.drawString("Ammo: " + player.Ammo, WIDTH*SCALE - 120, 30);
		//g.setFont(newFont);
		//g.drawString("Teste", 20, 100);
		
		
		//Life Bar
		g.setColor(Color.black);
		g.fillRect(7*SCALE,3*SCALE,52*SCALE,10*SCALE);
		g.setColor(Color.red);
		g.fillRect(8*SCALE,4*SCALE,50*SCALE,8*SCALE);
		g.setColor(Color.green);
		g.fillRect(8*SCALE,4*SCALE,(Game.player.Life/2)*SCALE,8*SCALE);
		g.setColor(Color.black);
		g.setFont(new Font("arial",Font.BOLD,8*SCALE));
		g.drawString(Game.player.Life+"/"+ Game.player.maxLife, ((50-8)/2)*SCALE, 11*SCALE);
		
		//Stamina Bar
		g.setColor(Color.black);
		g.fillRect(7*SCALE,14*SCALE,52*SCALE,5*SCALE);
		g.setColor(Color.red);
		g.fillRect(8*SCALE,15*SCALE,50*SCALE,3*SCALE);
		g.setColor(Color.yellow);
		g.fillRect(8*SCALE,15*SCALE,(Game.player.Stamina/2)*SCALE,3*SCALE);
		g.setColor(Color.black);
		
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
		
		World.renderMiniMap();
		g.drawImage(miniMap,WIDTH*SCALE-minimapSize-5,HEIGHT*SCALE-minimapSize-5,minimapSize,minimapSize,null);

		//Contagem para come?ar o estagio
		if(scene_state == entrance1 && gameState != "MENU" && !DEBUG) {
		
			g.setColor(new Color(0,0,0,opac));
			g.fillRect(0,0,Game.WIDTH*Game.SCALE,Game.HEIGHT*Game.SCALE);
			if(opac > 50) {
				opac--;
			}
		}else if(scene_state == begin) {
			g.setColor(new Color(0,0,0,50));
			g.fillRect(0,0,Game.WIDTH*Game.SCALE,Game.HEIGHT*Game.SCALE);

			g.setFont(new Font("arial",Font.BOLD,320));
			g.setColor(Color.white);
			if(sceneTime >= 240) {
				g.drawString("GO!",(WIDTH*SCALE)/2 - 300, (HEIGHT*SCALE)/2+ 100);
				if(countDown == 3) {
					countDown=0;
					Sound.playerShot.playOnce();
				}
			}else if(sceneTime >= 180) {
				g.drawString("1",(WIDTH*SCALE)/2 - 100, (HEIGHT*SCALE)/2+ 100);
				if(countDown == 2) {
					countDown++;
					Sound.playerShot.playOnce();
				}
			}else if(sceneTime >= 120) {
				g.drawString("2",(WIDTH*SCALE)/2 - 100, (HEIGHT*SCALE)/2+ 100);
				if(countDown == 1) {
					countDown++;
					Sound.playerShot.playOnce();
				}
			}else if(sceneTime >= 60) {
				g.drawString("3",(WIDTH*SCALE)/2 - 100, (HEIGHT*SCALE)/2+ 100);
				if(countDown == 0) {
					countDown++;
					Sound.playerShot.playOnce();
					opac = 255;
				}
			}
		}else if(scene_state == finish) {
			g.drawString("NEXT STAGE!",(WIDTH*SCALE)/2 - 300, (HEIGHT*SCALE)/2+ 100);
		}
		
		//EXEMPLO ROTATE
		//Graphics2D g2 = (Graphics2D) g;
		//double angleMouse = Math.atan2(200 + 25 - my, 200+25 - mx);
		//g2.rotate(angleMouse, 200 + 25, 200 + 25);
		//g.setColor(Color.RED);
		//g.fillRect(200, 200, 50, 50);
		
		//TESTE COM PLAYER
		
		//Graphics2D g2 = (Graphics2D) g;
		//double angleMouse = Math.atan2((player.getY() - Camera.y + SwordDesloc) * SCALE - my + SwordWidth/2, (player.getX() - Camera.x + SwordDesloc) * SCALE - mx + SwordWidth/2);
		//g2.rotate(angleMouse + 3.14, (player.getX() - Camera.x + SwordDesloc) * SCALE + SwordWidth/2, (player.getY() - Camera.y + SwordDesloc) * SCALE + SwordWidth/2);
		//g.setColor(Color.RED);
		//g.fillRect((player.getX() - Camera.x + SwordDesloc + 10) * SCALE, (player.getY() - Camera.y + SwordDesloc) * SCALE, SwordHeight, SwordWidth);
		
		//System.out.println(my + " " + mx);
		
		bs.show();
	}

	public void run() {
		long lastTime = System.nanoTime();
		double amountOfTicks = 60.0;
		double ns = 1000000000 / amountOfTicks;
		double delta = 0;
		int frames = 0;
		double timer = System.currentTimeMillis();
		requestFocus(); //para o jogo iniciar selecionado (n?o precisa clicar na tela pra come?ar a jogar)
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
		
		if(e.getKeyCode() == KeyEvent.VK_ENTER) {
			npc.showMessage = false;
		}
		
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
		
		if(e.getKeyCode() == KeyEvent.VK_Z) {
			player.sword = true;
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
		
		if(e.getKeyCode() == KeyEvent.VK_Z) {
			player.sword = false;
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




