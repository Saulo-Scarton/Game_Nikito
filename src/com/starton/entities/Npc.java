package com.starton.entities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import com.starton.main.Game;

public class Npc extends Entity{

	public String[] dialogs = new String[5];
	
	public boolean showMessage = false;
	public boolean show = false;
	public int curIndex = 0;
	
	public Npc(int x, int y, int width, int height, BufferedImage sprite) {
		super(x, y, width, height, sprite);
		dialogs[0] = "Hey! quer comprar um...  matinho?";
	}
	
	public void tick() {
		int xPlayer = Game.player.getX();
		int yPlayer = Game.player.getY();
		
		int xNpc = (int)x;
		int yNpc = (int)y;
		
		if(Math.abs(xPlayer - xNpc)< 10 && Math.abs(yPlayer - yNpc) < 10) {
			if(show == false) {
				showMessage = true;
				show = true;
			}
			
		}else {
			showMessage = false;
			show = false;
			curIndex = 0;
		}
		
		if(showMessage) {
			if(curIndex < dialogs[0].length())
			curIndex++;
		}
	}
	
	public void render(Graphics g) {
		super.render(g);
		if(showMessage) {
			g.setColor(Color.white);
			g.fillRect(0, Game.HEIGHT-Game.HEIGHT/4-2, Game.WIDTH, Game.HEIGHT/4+2);
			g.setColor(Color.blue);
			g.fillRect(1, Game.HEIGHT-Game.HEIGHT/4-1, Game.WIDTH-2, Game.HEIGHT/4);
			g.setColor(Color.white);
			g.drawString(dialogs[0].substring(0, curIndex), 3, Game.HEIGHT/6*5);
		}
	}

}
