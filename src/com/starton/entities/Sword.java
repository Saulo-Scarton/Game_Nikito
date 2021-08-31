package com.starton.entities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import com.starton.main.Game;
import com.starton.world.Camera;
import com.starton.world.World;

public class Sword extends Entity{
		
	private double dx, dy;
	private double spd = 1;
	private int life = 40, curLife = 0;
	
	private BufferedImage sprites;
	
	public Sword(int x, int y, int width, int height, BufferedImage sprite, double dx, double dy) {
		super(x, y, width, height, sprite);
		
		sprites = Game.spritesheet.getSprite(112, 64, 32, 16);
		
		this.dx = dx;
		this.dy = dy;
	}
	
	public void tick() {
		if(World.isFreeDynamic((int)(x+(dx*spd)), (int)(y+(dy*spd)), 3, 3)) {//insere metodo de colisão com parede
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
		//g.setColor(Color.WHITE);
		//g.fillRect(this.getX() - Camera.x,this.getY() - Camera.y, 2, 20);
		//Graphics2D g2 = (Graphics2D) g;
		//g2.rotate(45,50,50);
		g.drawImage(sprites, this.getX() - Camera.x,this.getY() - Camera.y,null);
		
	}
}
