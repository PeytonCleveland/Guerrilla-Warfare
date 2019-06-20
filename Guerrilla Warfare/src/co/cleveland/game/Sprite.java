package co.cleveland.game;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

public class Sprite {
	
	private static final int xStep = 5;
	private static final int yStep = 5;
	
	private ImageLoader imsLoader;
	private String imageName;
	private BufferedImage image;
	private int width, height;
	
	private ImagePlayer player;
	private boolean isLooping;
	
	private int pWidth, pHeight;
	
	private boolean active = true;
	
	protected int locx, locy;
	protected int dx, dy;
	
	  public Sprite(int x, int y, int w, int h, ImageLoader imsLd, String name) 
	  { 
	    locx = x; locy = y;
	    pWidth = w; pHeight = h;
	    dx = xStep; dy = yStep;

	    imsLoader = imsLd;
	    setImage(name);    
	  } 


	  public void setImage(String name)
	  {
	    imageName = name;
	    image = imsLoader.getImage(imageName);
	    if (image == null) {    
	      System.out.println("No sprite image for " + imageName);
	    }
	    else {
	      width = image.getWidth();
	      height = image.getHeight();
	    }
	    player = null;
	    isLooping = false;
	  }  


	  public void loopImage(int animPeriod, double seqDuration, boolean loop){
	    if (imsLoader.numImages(imageName) > 1) {
	      player = null;   
	      player = new ImagePlayer(imageName, animPeriod, seqDuration, loop, imsLoader);
	      isLooping = true;
	    }
	    else
	      System.out.println(imageName + " is not a sequence of images");
	  }  


	  public void stopLooping()
	  {
	    if (isLooping) {
	      player.stop();
	      isLooping = false;
	    }
	  }  


	  public int getWidth()  
	  {  return width;  }

	  public int getHeight()  
	  {  return height;  }

	  public int getPWidth()   
	  {  return pWidth;  }

	  public int getPHeight()  
	  {  return pHeight;  }


	  public boolean active() 
	  {  return active;  }

	  public void setActive(boolean a) 
	  {  active = a;  }

	  public void setPosition(int x, int y)
	  {  locx = x; locy = y;  }

	  public void translate(int xDist, int yDist)
	  {  locx += xDist;  locy += yDist;  }

	  public int getXPosn()
	  {  return locx;  }

	  public int getYPosn()
	  {  return locy;  }


	  public void setStep(int dx, int dy)
	  {  this.dx = dx; this.dy = dy; }

	  public int getXStep()
	  {  return dx;  }

	  public int getYStep()
	  {  return dy;  }


	  public Rectangle getMyRectangle()
	  {  return  new Rectangle(locx, locy, width, height);  }


	  public void updateSprite(){
	    if (active()) {
	      if (isLooping)
	        player.update();  
	    }
	  } 
	  



	  public void drawSprite(Graphics g) 
	  {
	    if (active()) {
	      if (image == null) {   
	    	  System.out.println("Image is null");
	      }
	      else {
	        if (isLooping)
	          image = player.getCurrentImage();
	        g.drawImage(image, locx, locy, null);
	      }
	    }
	  } 

}
