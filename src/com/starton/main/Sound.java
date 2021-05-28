package com.starton.main;

import java.io.*;
import javax.sound.sampled.*;

public class Sound {
	
	public static class Clips{
		
		public Clip[] clips;
		private int p;
		private int count;
		
		public Clips(byte[] buffer, int count) throws/*throws serve para jogar o erro, como se fosse try/catch*/LineUnavailableException, IOException, UnsupportedAudioFileException {//"byte" é menor que "int"
			if(buffer == null) return;
			
			clips = new Clip[count];
			this.count = count;
			
			for(int i = 0; i < count; i++) {
				clips[i] = AudioSystem.getClip();
				clips[i].open(AudioSystem.getAudioInputStream(new ByteArrayInputStream(buffer)));
			}
		}
		
		public void playOnce() {//para audios que tocam apenas uma vez
			if(clips == null) return;
			clips[p].stop();
			clips[p].setFramePosition(0);
			clips[p].start();
			p++;
			if(p >= count) p = 0;
		}
		
		public void playLoop() {//para audios que tocam repetitivamente
			if(clips == null) return;
			clips[p].loop(300);
		}
	}
	
	public static Clips music = load("/music1.wav",1); //carregar musica de fundo
	public static Clips hurtPlayer = load("/hurtPlayer.wav",1); //carregar som de hurt player
	public static Clips hurtEnemy = load("/hurtEnemy.wav",1); //carregar som de hurt enemy
	public static Clips playerShot = load("/playerShot.wav",1); //carregar som de hurt enemy
	public static Clips bombastic1 = load("/bombastic1.wav",1); //carregar som de hurt enemy
	
	
	private static Clips load(String name,int count) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			DataInputStream dis = new DataInputStream(Sound.class.getResourceAsStream(name));
			
			byte[] buffer = new byte[1024];
			int read = 0;
			
			while((read = dis.read(buffer)) >= 0) {//laço de repetição até terminar de ler o arquivo
				baos.write(buffer,0,read);
			}
			dis.close();
			byte[] data = baos.toByteArray();
			return new Clips(data,count);
			
		}catch(Exception e){//segurança a mais, para que evite falhas
			try {
				return new Clips(null,0);
			}catch(Exception ee) {
				return null;
			}
		}
	}
}
