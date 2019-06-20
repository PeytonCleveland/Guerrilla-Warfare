package co.cleveland.game;

import java.awt.Graphics;

public class BackgroundManager {

	  private String Images[] = {"background"};
	  private double moveFactors[] = {0.5};  // applied to moveSize
	     // a move factor of 0 would make a ribbon stationary

	  private Background[] backgrounds;
	  private int numBackgrounds;
	  private int moveSize;
	     // standard distance for a ribbon to 'move' each tick


	  public BackgroundManager(int w, int h, int brickMvSz, ImageLoader imsLd)
	  {
	    moveSize = brickMvSz;
	          // the basic move size is the same as the bricks ribbon

	    numBackgrounds = Images.length;
	    backgrounds = new Background[numBackgrounds];

	    for (int i = 0; i < numBackgrounds; i++)
	       backgrounds[i] = new Background(w, h, imsLd.getImage( Images[i] ),
							(int) (moveFactors[i]*moveSize) );
	  }  // end of RibbonsManager()


	  public void moveRight()
	  { for (int i=0; i < numBackgrounds; i++)
	      backgrounds[i].moveRight();
	  }

	  public void moveLeft()
	  { for (int i=0; i < numBackgrounds; i++)
	      backgrounds[i].moveLeft();
	  }

	  public void stayStill()
	  { for (int i=0; i < numBackgrounds; i++)
	      backgrounds[i].stayStill();
	  }


	  public void update()
	  { for (int i=0; i < numBackgrounds; i++)
	      backgrounds[i].update();
	  }

	  public void display(Graphics g)
	  /* The display order is important.
	     Display ribbons from the back to the front of the scene. */
	  { for (int i=0; i < numBackgrounds; i++)
	      backgrounds[i].display(g);
	  }
	
}
