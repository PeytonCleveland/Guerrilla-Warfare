package co.cleveland.game;

public class ArtificialIntelligence {
	
	private EnemySprite enemy;
	private PlayerSprite player;
	
	private boolean playerFacingRight = false;
	private boolean playerFacingLeft = false;
	private boolean playerStill = true;

	public ArtificialIntelligence(EnemySprite enemy, PlayerSprite player){
		this.enemy = enemy;
		this.player = player;
	}
	
	public void update(){
		enemy.stayStill(playerFacingRight, playerFacingLeft, playerStill);
	}

	public void setPlayerFacingRight(boolean b) {
		this.playerFacingRight = b;
	}

	public void setPlayerFacingLeft(boolean b) {
		this.playerFacingLeft = b;
	}

	public void setStill(boolean b) {
		this.playerStill = b;
	}
	
}
