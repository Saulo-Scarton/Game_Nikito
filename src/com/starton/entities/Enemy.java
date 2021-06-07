package com.starton.entities;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import com.starton.main.Game;
import com.starton.main.Sound;
import com.starton.world.AStar;
import com.starton.world.Camera;
import com.starton.world.Vector2i;

public class Enemy extends Entity{
	
	//private double speed = 0.1;
	
	private int frames = 0,maxFrames = 17 /*max frames para reduzir a velocidade da animação do inimigo*/,index = 0,maxIndex = 1;
	
	private BufferedImage[] sprites;
	
	private int life = 10;
	
	private boolean isDamaged = false;
	private int damageFrames = 10, damageCurrent = 0;
	
	public Enemy(int x, int y, int width, int height, BufferedImage sprite) {
		super(x, y, width, height, null);
		sprites = new BufferedImage[2];
		sprites[0] = Game.spritesheet.getSprite(112, 16, 16, 16);
		sprites[1] = Game.spritesheet.getSprite(128, 16, 16, 16);
	}

	
	public void tick() {
		
		//Algoritimo de perseguição 1
		/*
		if(this.calculateDistance(this.getX(), this.getY(), Game.player.getX(), Game.player.getY()) < 70) {
			if(isColiddingWithPlayer() == false) {
			if((int)x < Game.player.getX() && World.isFree((int)(x+speed), this.getY()) && !isColiding((int)(x+speed), this.getY())) {
				x+=speed;
			}else if((int)x > Game.player.getX()&& World.isFree((int)(x-speed), this.getY()) && !isColiding((int)(x-speed), this.getY())) {
				x-=speed;
			}
			if((int)y < Game.player.getY()&& World.isFree(this.getX(), (int)(y+speed)) && !isColiding(this.getX(), (int)(y+speed))) {
				y+=speed;
			}else if((int)y > Game.player.getY()&& World.isFree(this.getX(), (int)(y-speed)) && !isColiding(this.getX(), (int)(y-speed))) {
				y-=speed;
			}
			}else {
				//está colidindo com player
				if(Game.random.nextInt(100) < 5) {
					Game.player.Life--;
					Game.player.isDamaged = true;
				}
			}
		}else {
			
		}
		*/
		
		//Algoritimo de perseguição 2 (A*)
		maskx = 5;
		masky = 5;
		mwidth = 7;
		mheight = 11;
		depth = 0;
		if(!isColiddingWithPlayer()) {
			if(path == null || path.size() == 0) {
				Vector2i start = new Vector2i((int)(x/16),(int)(y/16));
				Vector2i end = new Vector2i((int)(Game.player.x/16),(int)(Game.player.y/16));
				path = AStar.findPath(Game.world, start, end);
			}
		}else{
			//está colidindo com player
			if(Game.random.nextInt(100) < 5) {
				//Game.player.Life--;
				Game.player.isDamaged = true;
			}
		}
		if(Game.random.nextInt(100) < 90) {
			followPath(path);
		}
		if(Game.random.nextInt(100) < 90) {
			Vector2i start = new Vector2i((int)(x/16),(int)(y/16));
			Vector2i end = new Vector2i((int)(Game.player.x/16),(int)(Game.player.y/16));
			path = AStar.findPath(Game.world, start, end);
		}
		frames++;
		if(frames == maxFrames) {
			frames = 0;
			index++;
			if(index > maxIndex) {
				index = 0;
			}
		}
		colidingShot();
		if(life <= 0) {
			destroySelf();
			return;
		}
		if(isDamaged){
			this.damageCurrent++;
			if(this.damageCurrent == this.damageFrames) {
				this.damageCurrent = 0;
				this.isDamaged = false;
			}
		}
	}
	
	public void destroySelf() {
		Game.enemies.remove(this);
		Game.entities.remove(this);
	}
	
	public void colidingShot() { //está colidindo com shot
		for(int i = 0; i < Game.shot.size(); i++) {
			Entity e = Game.shot.get(i);
			if(e instanceof Shot) {
				if(Entity.isColidding(this,e)) {
					Sound.hurtEnemy.playOnce();
					isDamaged = true;
					life--;
					Game.shot.remove(i);
					return;
				}
			}
		}
	}
	
	public boolean isColiddingWithPlayer() { //testa colisão do inimigo com o player
		Rectangle enemyCurrent = new Rectangle(this.getX() + maskx,this.getY() + masky,mwidth,mheight);
		Rectangle player = new Rectangle(Game.player.getX(),Game.player.getY(),16,16);
		
		return enemyCurrent.intersects(player);
	}
	

	
	public void render(Graphics g) { //trocar o tamanho da mascara (para colidirem com o tamanho correto)
		if(!isDamaged)
			g.drawImage(sprites[index], this.getX() - Camera.x,this.getY() - Camera.y,null);
		else
			g.drawImage(Entity.ENEMY_DAMAGED, this.getX() - Camera.x,this.getY() - Camera.y,null);
			//g.fillRect(this.getX() + maskx - Camera.x , this.getY() + masky - Camera.y ,mwidth,mheight); //para ver a posição da mascara
	}
	
}
