package com.starton.entities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import com.starton.main.Game;
import com.starton.world.Camera;
import com.starton.world.World;

public class Sword extends Entity{
		
	private double dx, dy;
	private double spd = 0;
	private int life = 40, curLife = 0;
	
	//VARIAVEIS PARA SWORD & PLAYER
	public int SwordHeight = 15;
	public int SwordWidth = 2;
	public int SwordDesloc = 7;
	
	private BufferedImage sprites;
	
	public Sword(int x, int y, int width, int height, BufferedImage sprite, double dx, double dy) {
		super(x, y, width, height, sprite);
		
		sprites = Game.spritesheet.getSprite(112, 64, 32, 16);
		
		this.dx = dx;
		this.dy = dy;
	}
	private BufferedImage image;
	
	public void tick() {
		
		if(World.isFreeDynamic((int)(x+(dx*spd)), (int)(y+(dy*spd)), 2, 20)) {//insere metodo de colisão com parede
			x+= dx * spd;
			y+= dy * spd;
		}else {
			Game.sword.remove(this);
			return;
		}
		//Remove a bala para não ficar se movendo infinitamente após sair do mapa
		curLife++;
		if(curLife == life) {
			Game.sword.remove(this);
			return;
		}
		
	}
	
	public void colidingSword() { //está colidindo com wall
		for(int i = 0; i < Game.sword.size(); i++) {
			Entity e = Game.sword.get(i);
			if(e instanceof Sword) {
				if(Entity.isColidding(this,e)) {
					Game.sword.remove(i);
					return;
				}
			}
		}
	}
	
	
	public void render(Graphics g) {
		

		
		//g.setColor(Color.RED);
		//g.fillRect(this.getX() - Camera.x, this.getY() - Camera.y, 2, 20);
		//Graphics2D g2 = (Graphics2D) g;
		//g2.rotate(0,this.getX() - Camera.x, this.getY() - Camera.y);
		//g.drawImage(sprites, this.getX() - Camera.x ,this.getY() - Camera.y,null);
		//g.drawImage(sprites, this.getX() - Camera.x, this.getY() - Camera.y, null);
		
		//EXEMPLO ROTATE
		//Graphics2D g2 = (Graphics2D) g;
		//double angleMouse = Math.atan2(200 + 25 - Game.my, 200+25 - Game.mx);
		//g2.rotate(angleMouse, 200 + 25, 200 + 25);
		//g.setColor(Color.RED);
		//g.fillRect(200, 200, 50, 50);
		
		//Graphics2D g2 = (Graphics2D) g;
		//double angleMouse = Math.atan2((Game.player.getY() - Camera.y + SwordDesloc) - Game.my + SwordWidth/2, (Game.player.getX() - Camera.x + SwordDesloc) - Game.mx + SwordWidth/2);
		//g2.rotate(angleMouse + 3.14, (Game.player.getX() - Camera.x + SwordDesloc) + SwordWidth/2, (Game.player.getY() - Camera.y + SwordDesloc) + SwordWidth/2);
		//g.setColor(Color.RED);
		//g.fillRect((Game.player.getX() - Camera.x + SwordDesloc), (Game.player.getY() - Camera.y), SwordHeight, SwordWidth);
		
	}
	
}
