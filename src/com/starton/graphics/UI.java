package com.starton.graphics;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import com.starton.entities.Player;
import com.starton.main.Game;

public class UI {

	public void render(Graphics g) {
		
		//Life Bar
		g.setColor(Color.black);
		g.fillRect(7,3,52,10);
		g.setColor(Color.red);
		g.fillRect(8,4,50,8);
		g.setColor(Color.green);
		g.fillRect(8,4,(Game.player.Life/2),8);
		g.setColor(Color.black);
		g.setFont(new Font("arial",Font.BOLD,8));
		g.drawString(Game.player.Life+"/"+ Game.player.maxLife, (50-8)/2, 11);
		
		//Stamina Bar
		g.setColor(Color.black);
		g.fillRect(7,14,52,5);
		g.setColor(Color.red);
		g.fillRect(8,15,50,3);
		g.setColor(Color.yellow);
		g.fillRect(8,15,(Game.player.Stamina/2),3);
		g.setColor(Color.black);
	}
	
}
