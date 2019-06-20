package co.cleveland.game;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class Block {

	private int mapX, mapY;   
	  private int imageID;

	  private BufferedImage image;
	  private int height;   

	  private int locY;   

	  public Block(int id, int x, int y)
	  { mapX = x;  mapY = y;
	    imageID = id;
	  }

	  public int getMapX()
	  {  return mapX;  }

	  public int getMapY()
	  {  return mapY;  }

	  public int getImageID()
	  {  return imageID;  }


	  public void setImage(BufferedImage im)
	  { image = im;
	    height = im.getHeight();
	  } 


	  public void setLocY(int pHeight, int maxYBlocks)
	  {  locY = pHeight - ((maxYBlocks-mapY) * height);  }

	  public int getLocY()
	  {  return locY;  }

	  public void display(Graphics g, int xScr)
	  {  g.drawImage(image, xScr, locY, null);  }
	
}
