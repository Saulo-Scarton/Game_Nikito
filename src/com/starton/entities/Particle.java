package com.starton.entities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.Random;

import com.starton.main.Game;
import com.starton.world.Camera;

public class Particle extends Entity{
	public int lifeTime = 15, curLife = 0, spd = 2;
	public double dx = 0, dy = 0;
	
	public Particle(int x, int y, int width, int height, BufferedImage sprite) {
		super(x, y, width, height, sprite);
		
		dx = new Random().nextGaussian();
		dy = new Random().nextGaussian();
	}

	public void tick() {
		x+=dx*spd;
		y+=dy*spd;
		curLife++;
		if(lifeTime == curLife) {
			Game.entities.remove(this);
		}
	}
	
	public void render(Graphics g) {
		g.setColor(Color.RED);
		g.fillRect(this.getX() - Camera.x, this.getY() - Camera.y, width, height);
	}
	
}
