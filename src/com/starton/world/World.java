package com.starton.world;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import com.starton.entities.Ammo;
import com.starton.entities.Boss;
import com.starton.entities.Enemy;
import com.starton.entities.Entity;
import com.starton.entities.Flower;
import com.starton.entities.LifePack;
import com.starton.entities.Particle;
import com.starton.entities.Player;
import com.starton.entities.Weapon;
import com.starton.graphics.Spritesheet;
import com.starton.main.Game;

public class World {
	
	public static Tile[] tiles;
	public static int WIDTH,HEIGHT;
	public static final int TILE_SIZE = 16;
	
	public World(String path) {
		try {
			BufferedImage map = ImageIO.read(getClass().getResource(path));
			//leitura pixel mapa
			int[] pixels = new int[map.getWidth() * map.getHeight()]; //para saber tamanho do mapa
			WIDTH = map.getWidth();
			HEIGHT = map.getHeight();
			tiles = new Tile[map.getWidth() * map.getHeight()];
			map.getRGB(0, 0,map.getWidth(),map.getHeight(),pixels,0,map.getWidth());
			for(int xx = 0; xx < map.getWidth();xx++) {
				for(int yy = 0; yy < map.getHeight();yy++) {
					
					int pixelNow = pixels[xx + (yy*map.getWidth())];
					tiles[xx+ (yy*WIDTH)] = new FloorTile(xx*16,yy*16,Tile.TILE_FLOOR);
					if(pixelNow == 0xFF000000) {
						//Floor
						tiles[xx+ (yy*WIDTH)] = new FloorTile(xx*16,yy*16,Tile.TILE_FLOOR);
					}else if(pixelNow == 0xFFFFFFFF) {
						//Wall
						tiles[xx+ (yy*WIDTH)] = new WallTile(xx*16,yy*16,Tile.TILE_WALL);
					}else if(pixelNow == 0xFF4CFF00) {
						//Flower
						Flower flower = new Flower(xx*16,yy*16,16,16,Entity.FLOWER_EN);
						Game.entities.add(flower);
					}else if(pixelNow == 0xFF0026FF) {
						//Player
						Game.player.setX(xx*16);
						Game.player.setY(yy*16);
					}else if(pixelNow == 0xFFFF0000) {
						//Enemy
						Enemy en = new Enemy(xx*16,yy*16,16,16,Entity.ENEMY_EN);
						Game.entities.add(en);
						Game.enemies.add(en);
					}else if(pixelNow == 0xFFFF00DC) {
						//Ammo
						//Game.entities.add(new Ammo(xx*16,yy*16,16,16,Entity.AMMO_EN);
						Ammo ammo = new Ammo(xx*16,yy*16,16,16,Entity.AMMO_EN);
						Game.entities.add(ammo);
					}else if(pixelNow == 0xFFFF6A00) {
						//LifePack
						LifePack pack = new LifePack(xx*16,yy*16,16,16,Entity.LIFEPACK_EN);
						Game.entities.add(pack);
					}else if(pixelNow == 0xFFFFD800) {
						//Weapon
						Game.entities.add(new Weapon(xx*16,yy*16,16,16,Entity.WEAPON_EN));
					}else if(pixelNow == 0xFF00FF21) {
						//Boss
						Boss en = new Boss(xx*16,yy*16,16,16,Entity.ENEMY_EN);
						Game.entities.add(en); //para colisao entre si
						Game.boss1.add(en); //para colisao entre si

					}
					
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void generateParticles(int amount, int x, int y) {
		for(int i = 0; i < amount; i++) {
			Game.entities.add(new Particle(x,y,1,1,null));
		}
	}
	
	//Testando colisões entre varios objetos (ex: shot x wall)
		public static boolean isFreeDynamic(int xnext,int ynext, int width, int height) {
			int x1 = xnext / TILE_SIZE;
			int y1 = ynext / TILE_SIZE;
			
			int x2 = (xnext+width-1) / TILE_SIZE;
			int y2 = ynext / TILE_SIZE;
			
			int x3 = xnext / TILE_SIZE;
			int y3 = (ynext+height-1) / TILE_SIZE;
			
			int x4 = (xnext+width-1) / TILE_SIZE;
			int y4 = (ynext+height-1) / TILE_SIZE;
			
			return !((tiles[x1 + (y1*World.WIDTH)] instanceof WallTile) ||
					(tiles[x2 + (y2*World.WIDTH)] instanceof WallTile) ||
					(tiles[x3 + (y3*World.WIDTH)] instanceof WallTile) ||
					(tiles[x4 + (y4*World.WIDTH)] instanceof WallTile));
		}
	
	//Testando colisões
	public static boolean isFree(int xnext,int ynext) {
		int x1 = xnext / TILE_SIZE;
		int y1 = ynext / TILE_SIZE;
		
		int x2 = (xnext+TILE_SIZE-1) / TILE_SIZE;
		int y2 = ynext / TILE_SIZE;
		
		int x3 = xnext / TILE_SIZE;
		int y3 = (ynext+TILE_SIZE-1) / TILE_SIZE;
		
		int x4 = (xnext+TILE_SIZE-1) / TILE_SIZE;
		int y4 = (ynext+TILE_SIZE-1) / TILE_SIZE;
		
		return !((tiles[x1 + (y1*World.WIDTH)] instanceof WallTile) ||
				(tiles[x2 + (y2*World.WIDTH)] instanceof WallTile) ||
				(tiles[x3 + (y3*World.WIDTH)] instanceof WallTile) ||
				(tiles[x4 + (y4*World.WIDTH)] instanceof WallTile));
	}
	
	public static void restartGame(String level) {
		Game.entities.clear(); //limpa entities
		Game.enemies.clear(); //limpa enemies
		Game.shot.clear(); //limpa shot
		Game.sword.clear();
		Game.entities = new ArrayList<Entity>();
		Game.enemies = new ArrayList<Enemy>();
		Game.boss1 = new ArrayList<Boss>();
		Game.spritesheet = new Spritesheet("/spritesheet.png");
		Game.player = new Player(0,0,16,16,Game.spritesheet.getSprite(32, 0, 16, 16));
		Game.entities.add(Game.player);
		Game.world = new World("/" + level); //World precisa ser carregado depois do spritesheet
		return; //pois o codigo nao pode continuar
	}
	
	public void render(Graphics g) {
		
		//renderizar apenas o que está sendo mostrado na tela
		int xstart = Camera.x >> 4;
		int ystart = Camera.y >> 4;
		
		int xfinal = xstart + (Game.WIDTH >> 4);
		int yfinal = ystart + (Game.HEIGHT >> 4);
		
		for(int xx = xstart; xx <= xfinal; xx++) {
			for(int yy = ystart; yy <= yfinal; yy++) {
				if(xx < 0 || yy < 0 || xx >= WIDTH || yy >= HEIGHT)
					continue;
				Tile tile = tiles[xx + (yy*WIDTH)];
				tile.render(g);
			}
			
		}
	}
	
	public static void renderMiniMap() {
		for(int i = 0; i < Game.minimapPixels.length; i++) {
			Game.minimapPixels[i] = 0;
		}
		for(int xx = 0; xx < WIDTH; xx++) {
			for(int yy = 0; yy < HEIGHT; yy++) {
				if(tiles[xx + (yy*WIDTH)] instanceof WallTile) {
					Game.minimapPixels[xx + (yy*WIDTH)] = 0xecf0f1;
				}
			}
		}
		
		int xPlayer = Game.player.getX()/16;
		int yPlayer = Game.player.getY()/16;
		Game.minimapPixels[xPlayer + (yPlayer*WIDTH)] = 0x3498db;
	}
	
}
