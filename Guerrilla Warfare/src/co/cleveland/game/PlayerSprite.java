package co.cleveland.game;

import java.awt.Graphics;

public class PlayerSprite extends Sprite{
	
	private static double DURATION = .85;
	
	private static final int NOT_JUMPING = 0;
	private static final int RISING = 1;
	private static final int FALLING = 2;
	
	private int ammo = 15;
	
	private static int MAX_UP_STEPS = 12;
	
	private int bulletOffset, bulletYOffset;
	
	private int period;
	
	private boolean facingRight, still;
	
	private int vertMoveMode;
	private int vertStep;
	private int upCount;
	private int w, h;
	
	private BlockManager bm;
	private ImageLoader imsLd;
	private BulletManager bulletManager;
	private ArtificialIntelligence ai;
	private MuzzleFlash flash;
	private int moveSize;
	
	private int xWorld, yWorld;

	public PlayerSprite(int w, int h, int blockMoveSize, BulletManager bulletManager, BlockManager bm, ImageLoader imsLd, int p) {
		super(w/2, h/2, w, h, imsLd, "runningRight");
		
		moveSize = blockMoveSize;
		bulletOffset = 1;
		this.bm = bm;
		this.bulletManager = bulletManager;
		this.imsLd = imsLd;
		this.w = w;
		this.h = h;
		period = p;
		
		facingRight = true;
		still = true;
		
		locy = bm.findFloor(locx + getWidth() / 2) - getHeight();
		xWorld = locx;
		yWorld = locy;
		
		vertMoveMode = NOT_JUMPING;
		vertStep = bm.getBrickHeight() / 2;
		
		upCount = 0;
	}
	
	public void setAI(ArtificialIntelligence ai){
		this.ai = ai;
	}
	
	public void moveRight(){
		setImage("runningRight");
		loopImage(50, DURATION, true);
		facingRight = true;
		still = false;
		bulletOffset = 82;
		bulletYOffset = 27;
		ai.setPlayerFacingRight(true);
		ai.setPlayerFacingLeft(false);
		ai.setStill(false);
	}
	
	public void moveLeft(){
		setImage("runningLeft");
		loopImage(period, DURATION, true);
		facingRight = false;
		still = false;
		bulletOffset = -22;
		bulletYOffset = 39;
		ai.setPlayerFacingLeft(true);
		ai.setPlayerFacingRight(false);
		ai.setStill(false);
	}
	
	public void stayStill(){
		if(facingRight){
			setImage("stillRight");
		}else{
			setImage("stillLeft");
		}
		ai.setStill(true);
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
		if(facingRight){
			if(still){
				setImage("shootRightStill");
			}else{
				setImage("shootRightRunning");
				loopImage(period, DURATION, true);
			}
		}else{
			if(still){
				setImage("shootLeftStill");
			}else{
				setImage("shootLeftRunning");
				loopImage(period, DURATION, true);
			}
		}
		if(ammo > 0){
			bulletManager.createBullet(locx + bulletOffset, locy + 51, w, h, imsLd, facingRight);
			flash = new MuzzleFlash(locx + bulletOffset, locy + bulletYOffset, w, h, imsLd, facingRight, period);
			ammo--;
		}else{
			System.out.println("reload");
		}
	}
	
	public void reload(){
		ammo = 15;
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
	      if (facingRight)  
	        xWorld += moveSize;
	      else 
	        xWorld -= moveSize;
	      if (vertMoveMode == NOT_JUMPING) 
	        checkIfFalling();   
	    }

	    if (vertMoveMode == RISING)
	      updateRising();
	    else if (vertMoveMode == FALLING)
	      updateFalling();
	    
	    if(flash != null && flash.active()){
	    	if(vertMoveMode == 1)
	    		flash.locy -= vertStep;
	    	else if(vertMoveMode == 2)
	    		flash.locy += vertStep;
	    	flash.updateSprite();
	    }

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
		  if(flash != null && flash.active()){
			  flash.display(g);
		  }
	  }
}
