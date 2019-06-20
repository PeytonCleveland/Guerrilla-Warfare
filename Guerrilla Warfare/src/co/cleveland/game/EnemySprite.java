package co.cleveland.game;

import java.awt.Graphics;

public class EnemySprite extends Sprite {
	
	private static double DURATION = 0.5;
	
	private static final int NOT_JUMPING = 0;
	private static final int RISING = 1;
	private static final int FALLING = 2;
	
	private static int MAX_UP_STEPS = 12;
	
	private int bulletOffset;
	
	private int period;
	
	private boolean facingRight, still, playerFacingRight, playerFacingLeft, playerStill;
	
	private int vertMoveMode;
	private int vertStep;
	private int upCount;
	private int w, h;
	private int health = 5;
	
	private BlockManager bm;
	private ImageLoader imsLd;
	private BulletManager bulletManager;
	private int moveSize;
	
	private int xWorld, yWorld;

	public EnemySprite(int x, int y, int w, int h, int blockMoveSize, BulletManager bulletManager, BlockManager bm, ImageLoader imsLd, int p) {
		super(x, y, w, h, imsLd, "runningLeft");
		
		moveSize = blockMoveSize - 4;
		bulletOffset = 1;
		this.bm = bm;
		this.bulletManager = bulletManager;
		this.imsLd = imsLd;
		this.w = w;
		this.h = h;
		period = p;
		
		facingRight = false;
		still = true;
		
		locy = bm.findFloor(locx + getWidth() / 2) - getHeight();
		xWorld = locx;
		yWorld = locy;
		
		vertMoveMode = NOT_JUMPING;
		vertStep = bm.getBrickHeight() / 2;
		
		upCount = 0;
	}
	
	public void moveRight(boolean playerFacingRight, boolean playerFacingLeft, boolean playerStill){
		setImage("runningRight");
		loopImage(50, DURATION, true);
		facingRight = true;
		still = false;
		bulletOffset = 35;
		this.playerFacingRight = playerFacingLeft;
		this.playerFacingRight = playerFacingRight;
		this.playerStill = playerStill;
	}
	
	public void moveLeft(boolean playerFacingRight, boolean playerFacingLeft, boolean playerStill){
		setImage("runningLeft");
		loopImage(period, DURATION, true);
		facingRight = false;
		still = false;
		bulletOffset = 5;
		this.playerFacingRight = playerFacingLeft;
		this.playerFacingRight = playerFacingRight;
		this.playerStill = playerStill;
	}
	
	public void stayStill(boolean playerMovingRight, boolean playerMovingLeft, boolean playerStill){
		this.playerFacingRight = playerMovingRight;
		this.playerFacingRight = playerMovingLeft;
		this.playerStill = playerStill;
		stopLooping();
		still = true;
	}
	
	public void jump(){
		if(vertMoveMode == NOT_JUMPING){
			vertMoveMode = RISING;
			upCount = 0;
			if(still){
				if(facingRight){
					setImage("jumpingRight");
				}else{
					setImage("jumpingLeft");
				}
			}
		}
	}
	
	public void shoot(){
		bulletManager.createBullet(locx + bulletOffset, locy + 10, w, h, imsLd, facingRight);
	}
	
	public boolean willHitBrick(){
	    if (still)
	      return false;   

	    int xTest;   
	    if (facingRight)  
	      xTest = xWorld + moveSize;
	    else 
	      xTest = xWorld - moveSize;
	    
        System.out.println("will hit");
	    int xMid = xTest + getWidth()/2;
	    int yMid = yWorld + (int)(getHeight()*0.8);   

	    return bm.insideBlock(xMid,yMid);  
	  }  


	  public void updateSprite(){
	    if (!still) {   
	      if (facingRight){  
	        if(playerStill){
	        	locx += moveSize;
	        }else if(playerFacingLeft && !playerStill){
	        	locx += 2 * moveSize;
	        }
	      }else{ 
	    	  if(playerFacingRight && !playerStill){ 
	    		  locx -= 2 * moveSize;
	  	      }else if(playerStill){
	  	    	  locx -= moveSize;
	  	      }
	      }
	      if (vertMoveMode == NOT_JUMPING) 
	        checkIfFalling();   
	    }else if(still){
	    	if(playerFacingRight && !playerStill){
	    		locx -= moveSize;
	    	}else if(playerFacingLeft && !playerStill){
	    		locx += moveSize;
	    	}
	    }

	    if (vertMoveMode == RISING)
	      updateRising();
	    else if (vertMoveMode == FALLING)
	      updateFalling();

	    super.updateSprite();
	  }  
	  
	  private void checkIfFalling(){
	    int yTrans = bm.checkBlockTop( xWorld+(getWidth()/2), yWorld+getHeight()+vertStep, vertStep);
	    if (yTrans != 0)  
	      vertMoveMode = FALLING;   
	  }  



	  private void updateRising(){
		  if (upCount == MAX_UP_STEPS){
			  vertMoveMode = FALLING;   
			  upCount = 0;
	    }else{
	      int yTrans = bm.checkBlockBase(xWorld+(getWidth()/2), yWorld-vertStep, vertStep);
	      if (yTrans == 0) {   
	        vertMoveMode = FALLING;   
	        upCount = 0;
	      }else{  
	        translate(0, -yTrans);
	        yWorld = (yWorld - yTrans);  
	        upCount++;
	      }
	    }
	  }  


	  private void updateFalling(){
	    int yTrans = bm.checkBlockTop(xWorld+(getWidth()/2), yWorld+getHeight()+vertStep, vertStep);
	    if (yTrans == 0)   
	      finishJumping();
	    else {    
	      translate(0, yTrans);
	      yWorld += yTrans;   
	    }
	  }  

	  private void finishJumping(){
	    vertMoveMode = NOT_JUMPING;
	    upCount = 0;

	    if (still) {   
	      if (facingRight)
	        setImage("runningRight");
	      else    
	        setImage("runningLeft");
	    }
	  }  
	  
	  public void display(Graphics g){
		  super.drawSprite(g);
	  }
	  
	  public void takeDamage(){
		  health--;
		  if(health == 0){
			  die();
		  }
	  }

	private void die() {
		this.setActive(false);
	}
}
