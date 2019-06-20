package co.cleveland.game;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Background {
	
	private BufferedImage im;
	private int width;
	private int pWidth, pHeight;
	
	private int moveSize;
	private boolean movingRight;
	private boolean movingLeft;
	
	private int xImHead;
	
	public Background(int w, int h, BufferedImage im, int moveSize){
		pWidth = w;
		pHeight = h;
		
		this.im = im;
		width = im.getWidth();
		this.moveSize = moveSize;
		movingRight = false;
		movingLeft = false;
		xImHead = 0;
	}
	
	public void moveRight(){
		movingRight = true;
		movingLeft = false;
	}
	
	public void moveLeft(){
		movingLeft = true;
		movingRight = false;
	}
	
	public void stayStill(){
		movingRight = false;
		movingLeft = false;
	}

	public void update(){
		if (movingRight)
	      xImHead = (xImHead + moveSize) % width;
	    else if (movingLeft)
	      xImHead = (xImHead - moveSize) % width;
	} 
	
	public void display(Graphics g){
	    if (xImHead == 0){ 
	      draw(g, im, 0, pWidth, 0, pWidth);
	    }
	    else if ((xImHead > 0) && (xImHead < pWidth)) {  
	      draw(g, im, 0, xImHead, width-xImHead, width); 
	      draw(g, im, xImHead, pWidth, 0, pWidth-xImHead); 
	    }
	    else if (xImHead >= pWidth){  
	      draw(g, im, 0, pWidth, width-xImHead, width-xImHead+pWidth);  
	    }
	    else if ((xImHead < 0) && (xImHead >= pWidth-width)){
	      draw(g, im, 0, pWidth, -xImHead, pWidth-xImHead);
	    }
	    else if (xImHead < pWidth-width) {
	      draw(g, im, 0, width+xImHead, -xImHead, width);  
	      draw(g, im, width+xImHead, pWidth, 0, pWidth-width-xImHead);  
	    }
	  }
	
	private void draw(Graphics g, BufferedImage im, int scrX1, int scrX2, int imX1, int imX2){
		g.drawImage(im, scrX1, 0, scrX2, pHeight, imX1, 0,  imX2, pHeight, null);
	}
}
