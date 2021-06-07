package com.starton.entities;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import com.starton.main.Game;
import com.starton.world.Camera;

public class Flower extends Entity{

	private int frames = 0,maxFrames = 25 /*max frames para reduzir a velocidade da animação do inimigo*/,index = 0,maxIndex = 5;
	
	private BufferedImage[] sprites;
	
	
	public Flower(int x, int y, int width, int height, BufferedImage sprite) {
		super(x, y, width, height, sprite);
		sprites = new BufferedImage[6];
		sprites[0] = Game.spritesheet.getSprite(0, 64, 16, 16);
		sprites[1] = Game.spritesheet.getSprite(32, 64, 16, 16);
		sprites[2] = Game.spritesheet.getSprite(16, 64, 16, 16);
		frames++;
		if(frames == maxFrames) {
			frames = 0;
			index++;
			if(index > maxIndex) {
				index = 0;
			}
		}
	}
	
	public void render(Graphics g) { //trocar o tamanho da mascara (para colidirem com o tamanho correto)
		g.drawImage(sprites[index], this.getX() - Camera.x,this.getY() - Camera.y,null);
	}
}
