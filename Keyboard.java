
// Used for detecting key inputs and acting upon those inputs.

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

import javax.swing.JPanel;

public class Keyboard implements KeyListener{
	int[][] data;
	int SCREEN_WIDTH;
	int SCREEN_HEIGHT;
	Player player;
	ArrayList<Wall> walls;
	ArrayList<Objective> cheeses;
	End end;
	JPanel panel;
	
	// Variables used for player movement.
	int xChange;
	int yChange;

	boolean keyPressed = false;

	Thread moveThread;
	
	// Constructor.
	public Keyboard(int[][] data, int SCREEN_WIDTH, int SCREEN_HEIGHT, Player player, ArrayList<Wall> walls, ArrayList<Objective> cheeses, End end, JPanel panel) {
		this.data = data;
		this.SCREEN_WIDTH = SCREEN_WIDTH;
		this.SCREEN_HEIGHT = SCREEN_HEIGHT;
		this.player = player;
		this.walls = walls;
		this.cheeses = cheeses;
		this.end = end;
		this.panel = panel;
	}

	@Override
	public void keyTyped(KeyEvent event) {}

	// Detects key input.
	@Override
	public void keyPressed(KeyEvent event) {
		// Prevents the user from holding down a key.
		if(keyPressed) return;
		keyPressed = true;

		moveThread = new DrawingThread(walls, null, cheeses, null, end, player, 0, null, "null"){
			@Override
			public void run() {
				while(keyPressed){
					xChange = 0;
					yChange = 0;
			
					// Determine player movement based on key direction pressed.
					if (event.getKeyCode() == KeyEvent.VK_W || event.getKeyCode() == KeyEvent.VK_UP) {
						yChange = -(SCREEN_HEIGHT / data.length);
						player.move(xChange, yChange, false);
						collideWall();
						collideEnd();
						collideCheese();
					}
					if(event.getKeyCode() == KeyEvent.VK_A || event.getKeyCode() == KeyEvent.VK_LEFT){
						xChange = -(SCREEN_WIDTH / data[0].length);
						player.move(xChange, yChange, false);
						collideWall();
						collideEnd();
						collideCheese();
					}
					if(event.getKeyCode() == KeyEvent.VK_S || event.getKeyCode() == KeyEvent.VK_DOWN){
						yChange = SCREEN_HEIGHT / data.length;
						player.move(xChange, yChange, false);
						collideWall();
						collideEnd();
						collideCheese();
					}
					if(event.getKeyCode() == KeyEvent.VK_D || event.getKeyCode() == KeyEvent.VK_RIGHT){
						xChange = SCREEN_WIDTH / data[0].length;
						player.move(xChange, yChange, false);
						collideWall();
						collideEnd();
						collideCheese();
					}
					
					try{
						Thread.sleep(100);
					}
					catch(Exception e){}
				}
			}
				
		};
		moveThread.start();
	}

	@Override
	public void keyReleased(KeyEvent event) {
		keyPressed = false;
		try{
			moveThread.join();
		}
		catch(Exception e){}
	}
	
	// Checks for wall collisions to prevent player movement.
	public void collideWall() {
		// Check wall collision.
		for(int i = 0; i < walls.size(); ++i) {
			if(player.collides(walls.get(i))) {
				player.move(-xChange, -yChange, true);
				return;
			}
		}
		// Check for edge of frame collision.
		if(player.xPos > SCREEN_WIDTH - (SCREEN_WIDTH / data[0].length) || player.xPos < 0 || player.yPos > SCREEN_HEIGHT - (SCREEN_HEIGHT / data.length)|| player.yPos < 0) {
			player.move(-xChange, -yChange, true);
		}
	}

	// Checks for collision with the end tile to determine if the game has ended.
	public void collideEnd(){
		if(player.collides(end) && end.activated) {
			((Window)panel).finished = true;
			((Window)panel).windowState = "finish";
		}
	}

	// Checks for collision with objectives so that they can be collected.
	public void collideCheese(){
		if(cheeses.isEmpty()) return;
		for(int i = 0; i < cheeses.size(); ++i) {
			if(player.collides(cheeses.get(i))) {
				Window.visitedCheeses.remove(cheeses.get(i));
				cheeses.remove(cheeses.get(i));
				--GameMain.objectivesLeft;
				return;
			}
		}
	}
}// End class.