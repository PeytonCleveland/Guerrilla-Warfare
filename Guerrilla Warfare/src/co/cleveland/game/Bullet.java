package co.cleveland.game;

public class Bullet extends Sprite {
	
	private static int xStep = 15;
	private int w;
	
	private boolean facingRight;
	
	public Bullet(int x, int y, int w, int h, ImageLoader imsLd, String name, boolean right) {
		super(x, y, w, h, imsLd, name);
		facingRight = right;
		this.w = w;
	}

	public void updateSprite() {
		if(facingRight){
			locx += xStep;
			if(locx > w){
				setActive(false);
			}
		}else{
			locx -= xStep;
			if(locx < 0){
				setActive(false);
			}
		}
		super.updateSprite();
	}

}
