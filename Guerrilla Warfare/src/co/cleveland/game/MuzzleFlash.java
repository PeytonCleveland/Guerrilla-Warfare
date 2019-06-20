package co.cleveland.game;

import java.awt.Graphics;

public class MuzzleFlash extends Sprite{
	
	public double timer;
	public double diff;
	public int period;

	public MuzzleFlash(int x, int y, int w, int h, ImageLoader imsLd, boolean right, int period) {
		super(x, y, w, h, imsLd, "MuzzleRight");
		this.period = period;
		if(right){
			setImage("MuzzleRight");
		}else{
			setImage("MuzzleLeft");
		}
		loopImage(period, .5, false);
		timer = System.currentTimeMillis();
	}
	
	
	public void updateSprite(){
		diff = System.currentTimeMillis() - timer;
		if(diff > period / 100){
			stopLooping();
			setActive(false);
			}
		super.updateSprite();
	}
	
	public void display(Graphics g){
		super.drawSprite(g);
	}

}
