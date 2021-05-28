package com.starton.entities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import com.starton.main.Game;
import com.starton.world.Camera;
import com.starton.world.World;

public class Shot extends Entity{
		
	private double dx, dy;
	private double spd = 4;
	private int life = 40, curLife = 0;
	
	public Shot(int x, int y, int width, int height, BufferedImage sprite, double dx, double dy) {
		super(x, y, width, height, sprite);
		this.dx = dx;
		this.dy = dy;
	}
	
	public void tick() {
		if(World.isFreeDynamic((int)(x+(dx*spd)), (int)(y+(dy*spd)), 3, 3)) {//insere metodo de colisão com parede
			x+= dx * spd;
			y+= dy * spd;
		}else {
			Game.shot.remove(this);
			return;
		}
		//Remove a bala para não ficar se movendo infinitamente após sair do mapa
		curLife++;
		if(curLife == life) {
			Game.shot.remove(this);
			return;
		}
		
	}
	
	public void colidingShot() { //está colidindo com wall
		for(int i = 0; i < Game.shot.size(); i++) {
			Entity e = Game.shot.get(i);
			if(e instanceof LifePack) {
				if(Entity.isColidding(this,e)) {
					Game.shot.remove(i);
					return;
				}
			}
		}
	}
	
	public void render(Graphics g) {
		g.setColor(Color.RED);
		g.fillOval(this.getX() - Camera.x,this.getY() - Camera.y, 4, 4);
	}
}
