package com.starton.entities;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.concurrent.TimeUnit;

import com.starton.main.Game;
import com.starton.main.Sound;
import com.starton.world.Camera;
import com.starton.world.World;

public class Player extends Entity{

	public boolean right,up,left,down,run;
	public int right_dir = 0,left_dir = 1;
	public int dir = right_dir;
	public double speed = 1;
	public static int playerFatigue = 0, playerMaxFatigue = 60*2;
	
	
	private int frames = 0,maxFrames = 5,index = 0,maxIndex = 3;
	public boolean moved = false;
	private BufferedImage[] rightPlayer;
	private BufferedImage[] leftPlayer;
	
	private BufferedImage playerDamage;
	
	private int time1 = 0, maxTime1 = 12;
	
	private boolean hasWeapon = false;
	
	public int Ammo = 0, maxAmmo = 100;
	
	public boolean isDamaged = false;
	
	private int damageFrames = 0; //deve criar para após o player sofrer dano voltar a sprite normal
	
	public boolean shoot, mouseShoot, sword = false;
	public int Stamina = 100, maxStamina = 100;  
	public int Life = 100,maxLife=100; //não pode ser static pois quando reinicia o jogo (GAME OVER) a vida deve voltar a ser 100,
	public double mx,my; //posição do mouse
	
	public Player(int x, int y, int width, int height, BufferedImage sprite) {
		super(x, y, width, height, sprite);
		
		rightPlayer = new BufferedImage[4];
		leftPlayer = new BufferedImage[4];
		playerDamage = Game.spritesheet.getSprite(0, 16, 16, 16);
		
		for(int i = 0; i < 4; i++) {
			rightPlayer[i] = Game.spritesheet.getSprite(32 + (i*16), 0, 16, 16);
		}
		
		for(int i = 0; i < 4; i++) {
			leftPlayer[i] = Game.spritesheet.getSprite(32 + (i*16), 16, 16, 16);
		}
	}
	
	public void revealMap() {
		int xx = (int)(x/16);
		int yy = (int)(y/16);
		
		World.tiles[xx-1+yy*World.WIDTH].show = true;
		World.tiles[xx+yy*World.WIDTH].show = true;
		World.tiles[xx+1+yy*World.WIDTH].show = true;
		
		World.tiles[xx+(yy+1)*World.WIDTH].show = true;
		World.tiles[xx+(yy-1)*World.WIDTH].show = true;
		
		World.tiles[xx-1+(yy-1)*World.WIDTH].show = true;
		World.tiles[xx+1+(yy-1)*World.WIDTH].show = true;
		
		World.tiles[xx-1+(yy+1)*World.WIDTH].show = true;
		World.tiles[xx+1+(yy+1)*World.WIDTH].show = true;
		
		
		
	}
	
	public void tick() {
		
		//para fog of war
		revealMap();
		
		if(Game.DEBUG) {
			Stamina = maxStamina;
			Life = maxLife;
			speed = 4;
			Ammo = 50;
		}
		
		depth = 1;
		moved = false;
		if(right && World.isFree((int)(x+speed),this.getY())) {
			moved = true;
			dir = right_dir;
			x+=speed;
		}else if(left && World.isFree((int)(x-speed),this.getY())) {
			moved = true;
			dir = left_dir;
			x-=speed;
		}
		
		if(up && World.isFree(this.getX(),(int)(y-speed))) {
			moved = true;
			y-=speed;
		}else if(down && World.isFree(this.getX(),(int)(y+speed))) {
			moved = true;
			y+=speed;
		}
		
		if(moved && run && Stamina > 0) {
			speed = 2;
			Stamina--;
			playerFatigue = 0;
		}else{
			if(Stamina >= 100) {
				Stamina = 100;
			}
			speed = 1;
			if(playerFatigue != playerMaxFatigue){
				playerFatigue++;
			}else {
				Stamina++;
			}
		}
		
		if(moved) {
			frames++;
			if(frames == maxFrames) {
				frames = 0;
				index++;
				if(index > maxIndex) {
					index = 0;
				}
			}
		}
		
		checkColisionLifePack();
		checkColisionAmmo();
		checkColisionWeapon();
		
		
		if(isDamaged) {//Se sofrer dano muda a frame para isDamaged
			this.damageFrames++;
			if(this.damageFrames == 10) {//aparece por 10ms depois volta
				this.damageFrames = 0;
				isDamaged = false;//sai da sprite isDamaged
				Sound.hurtPlayer.playOnce();
			}
		}
		
		//shoot
		if(shoot && hasWeapon && Ammo > 0) { //atirar com tecla "x"
			Ammo--;
			shoot = false;
			int dx = 0, px = 0, py = 0;
			if(dir == right_dir) {//se estiver apontado para direita
				px = 10;
				py = 5;
				dx = 1;
			}else if(dir == left_dir) {
				px = 2;
				py = 5;
				dx = -1;
			}
				
			Shot shot = new Shot(this.getX() + px, this.getY() + py, 3, 3, null, dx, 0);
			Game.shot.add(shot);
		}
		
		//sword
		if(sword) { //sword com tecla "z"
			sword = false;
			int dx = 0, px = 0, py = 0;
			if(dir == right_dir) {//se estiver apontado para direita
				px = 20;
				py = 0;
				dx = 1;
			}else if(dir == left_dir) {
				px = -5;
				py = 0;
				dx = -1;
			}
				
			Sword sword = new Sword(this.getX() + px, this.getY() + py, 2, 20, null, dx, 0);
			Game.sword.add(sword);
		}
			
		if(mouseShoot) { //atirar com o mouse
			mouseShoot = false;
			
			if(hasWeapon && Ammo > 0) {
				//Sound.playerShot.playOnce();
				Ammo--;
				int px = 0,  py = 8;
				double angle = 0;
				if(dir == right_dir) {//se estiver apontado para direita
					px = 10;
					angle = Math.atan2(my - (this.getY() + py - Camera.y), mx - (this.getX() + px - Camera.x)); //calcula o angulo que está apontado o mouse
				}else if(dir == left_dir) {
					px = 2;
					angle = Math.atan2(my - (this.getY() + py - Camera.y), mx - (this.getX() + px - Camera.x)); //calcula o angulo que está apontado o mouse
				}
				
				double dx = Math.cos(angle);
				double dy = Math.sin(angle);
				
				//PARA SHOT
				Shot shot = new Shot(this.getX() + px, this.getY() + py, 3, 3, null, dx, dy);
				Game.shot.add(shot);
				
				//PARA SWORD
				//Sword sword = new Sword(this.getX() + px, this.getY() + py, 3, 3, null, dx, dy);
				//Game.sword.add(sword);
			}
		}
		
		if(Life<=0) { //Reinicia jogo caso GAME OVER
			//GAME OVER
			Life = 0;
			Game.gameState = "GAME_OVER";
		}
		updateCamera();
	}
	
	public void updateCamera() {
		//Movimentação da camera
		//Limitando até onde pode ir a camera (não aparece fundo preto)
		Camera.x = Camera.clamp(this.getX() - (Game.WIDTH/2),0,World.WIDTH*16 - Game.WIDTH);
		Camera.y = Camera.clamp(this.getY() - (Game.HEIGHT/2),0,World.HEIGHT*16 - Game.HEIGHT);
	}
	
	public void checkColisionWeapon() {
		for(int i = 0; i < Game.entities.size(); i++) { //roda todas entities do jogo (não recomendado quando há muitas)
			Entity e = Game.entities.get(i);
			if(e instanceof Weapon) { //se Entity for = Weapon
				if(Entity.isColidding(this, e)) { //player está colidindo com Weapon?
					hasWeapon = true;
					Game.entities.remove(i); //remove o objeto atual (Weapon)
				}
			}
		}
	}
	
	public void checkColisionAmmo() {
		for(int i = 0; i < Game.entities.size(); i++) { //roda todas entities do jogo (não recomendado quando há muitas)
			Entity e = Game.entities.get(i);
			if(e instanceof Ammo) { //se Entity for = Ammo
				if(Entity.isColidding(this, e)) { //player está colidindo com Ammo?
					if(Ammo != maxAmmo) {
						Ammo+=15;
						Game.entities.remove(i); //remove o objeto atual (Ammo)
						if(Ammo >= maxAmmo) {
							Ammo = maxAmmo;
						}
						return;
					}
				}
			}
		}
	}
	
	public void checkColisionLifePack() {
		for(int i = 0; i < Game.entities.size(); i++) { //roda todas entities do jogo (não recomendado quando há muitas)
			Entity e = Game.entities.get(i);
			if(e instanceof LifePack) { //se Entity for = LifePack
				if(Entity.isColidding(this, e)) { //player está colidindo com LifePack?
					if(Life != maxLife) {
						Life+=100;
						Game.entities.remove(i); //remove o objeto atual (lifePack)
						if(Life >= maxLife) {
							Life = maxLife;
						}
						return;
					}
				}
			}
		}
	}
	
	//Renderiza o player conforme estado (left, right, up, down, damage, dead, shooting)
	public void render(Graphics g) {
		
		
		
		//g.fillRect(this.getX() + maskx - Camera.x , this.getY() + masky - Camera.y ,mwidth,mheight); //para ver a posição da mascara
		if(!isDamaged) {
			if(dir == right_dir) {
				g.drawImage(rightPlayer[index], this.getX() - Camera.x, this.getY() - Camera.y, null);
				if(hasWeapon) {
					//Desenhar arma dir
					g.drawImage(Entity.SLINGSHOT_RIGHT, this.getX() - Camera.x + 6, this.getY() - Camera.y, null);
				}
				if(run && moved) {
					if(time1 > 10) {
						g.drawImage(Entity.RUNDIRT_EN, this.getX() - Camera.x + 5, this.getY() - Camera.y + 14, null);
						g.drawImage(Entity.RUNDIRT_EN, this.getX() - Camera.x + 2, this.getY() - Camera.y + 13, null);
					}else if(time1 > 6) {
						g.drawImage(Entity.RUNDIRT_EN, this.getX() - Camera.x + 6, this.getY() - Camera.y + 13, null);
						g.drawImage(Entity.RUNDIRT_EN, this.getX() - Camera.x + 3, this.getY() - Camera.y + 14, null);
					}else if(time1 > 4) {
						g.drawImage(Entity.RUNDIRT_EN, this.getX() - Camera.x + 7, this.getY() - Camera.y + 14, null);
						g.drawImage(Entity.RUNDIRT_EN, this.getX() - Camera.x + 4, this.getY() - Camera.y + 13, null);
					}else if(time1 > 2) {
						g.drawImage(Entity.RUNDIRT_EN, this.getX() - Camera.x +8, this.getY() - Camera.y + 13, null);
						g.drawImage(Entity.RUNDIRT_EN, this.getX() - Camera.x + 5, this.getY() - Camera.y + 14, null);
					}
					time1++;
					if(time1 == maxTime1) {
						time1 = 0;
					}
					
					
				}
					
					//System.out.println(time1);
					//g.drawImage(Entity.RUNDIRT_EN, this.getX() - Camera.x, this.getY() - Camera.y + 10, null);
					//g.drawImage(Entity.RUNDIRT_EN, this.getX() - Camera.x -3, this.getY() - Camera.y + 8, null);
					//g.drawImage(Entity.RUNDIRT_EN, this.getX() - Camera.x -6, this.getY() - Camera.y + 10, null);
					//g.drawImage(Entity.RUNDIRT_EN, this.getX() - Camera.x -9, this.getY() - Camera.y + 8, null);
				
				
			}else if(dir == left_dir) {
				g.drawImage(leftPlayer[index], this.getX() - Camera.x, this.getY() - Camera.y, null);
				if(hasWeapon) {
					//Desenhar arma esq
					g.drawImage(Entity.SLINGSHOT_LEFT, this.getX() - Camera.x - 6, this.getY() - Camera.y, null);
				}
				if(run && moved) {
					if(time1 > 10) {
						g.drawImage(Entity.RUNDIRT_EN, this.getX() - Camera.x + 11, this.getY() - Camera.y + 14, null);
						g.drawImage(Entity.RUNDIRT_EN, this.getX() - Camera.x + 8, this.getY() - Camera.y + 13, null);
					}else if(time1 > 6) {
						g.drawImage(Entity.RUNDIRT_EN, this.getX() - Camera.x + 10, this.getY() - Camera.y + 13, null);
						g.drawImage(Entity.RUNDIRT_EN, this.getX() - Camera.x + 7, this.getY() - Camera.y + 14, null);
					}else if(time1 > 4) {
						g.drawImage(Entity.RUNDIRT_EN, this.getX() - Camera.x + 9, this.getY() - Camera.y + 14, null);
						g.drawImage(Entity.RUNDIRT_EN, this.getX() - Camera.x + 6, this.getY() - Camera.y + 13, null);
					}else if(time1 > 2) {
						g.drawImage(Entity.RUNDIRT_EN, this.getX() - Camera.x +8, this.getY() - Camera.y + 13, null);
						g.drawImage(Entity.RUNDIRT_EN, this.getX() - Camera.x + 5, this.getY() - Camera.y + 14, null);
					}
					time1++;
					if(time1 == maxTime1) {
						time1 = 0;
					}
					
					
				}
			}
		}else {
			g.drawImage(playerDamage, this.getX() - Camera.x, this.getY() - Camera.y, null);
			if(hasWeapon) {//para arma também trocar sprite quando player sofrer dano
				if(dir == left_dir) {
					g.drawImage(Entity.WEAPON_EN, this.getX() - 4 - Camera.x, this.getY() - Camera.y, null);
				}else {
					g.drawImage(Entity.WEAPON_EN, this.getX() + 4 - Camera.x, this.getY() - Camera.y, null);
				}
			}
		}
	}
	
	

}
