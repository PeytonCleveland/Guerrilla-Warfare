package co.cleveland.game;

import java.awt.Graphics;

public class BulletManager {
	
	private int size = 15;
	private int index = 0;
	private Bullet[] bullets = new Bullet[size];
	public EnemySprite enemy;

	public void createBullet(int x, int y, int w, int h, ImageLoader imsLd, boolean facingRight){
		if(index < size){
			bullets[index] = new Bullet(x, y, w, h, imsLd, "Bullet", facingRight);
		}else{
			index = 0;
			bullets[index] = new Bullet(x, y, w, h, imsLd, "Bullet", facingRight);
		}
		index++;
	}
	
	public void display(Graphics g){
		for(int i = 0; i < size; i++){
			if(bullets[i] != null && bullets[i].active() == true){
				bullets[i].drawSprite(g);
			}
		}
	}
	
	public void update(){
		for(int i = 0; i < size; i++){
			if(bullets[i] != null && bullets[i].active() == true){
				bullets[i].updateSprite();
				checkCollision(bullets[i]);
			}
		}
	}

	public void checkCollision(Bullet bullet) {
		if(bullet.locx >= enemy.locx && bullet.locx <= enemy.locx + 60 && bullet.locy >= enemy.locy && bullet.locy <= enemy.locy + enemy.getHeight()){
			enemy.takeDamage();
			bullet.setActive(false);
		}
	}

	public void setEnemy(EnemySprite enemy) {
		this.enemy = enemy;
		
	}

}
