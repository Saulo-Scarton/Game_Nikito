package com.starton.world;

public class Camera {

	public static int x,y;
	
	//Limitando at� onde pode ir a camera (n�o aparece fundo preto)
	public static int clamp(int Now,int Min,int Max) {
		if(Now < Min) {
			Now = Min;
		}
		
		if(Now > Max) {
			Now = Max;
		}
		
		return Now;
	}
}
