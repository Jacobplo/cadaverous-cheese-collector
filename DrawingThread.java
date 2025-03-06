import java.util.ArrayList;

// Creates a thread used for drawing the tiles of the maze.
public class DrawingThread extends Thread{
	int index;

	ArrayList<Wall> walls = new ArrayList<Wall>();
	ArrayList<Floor> floors = new ArrayList<Floor>();
	ArrayList<Objective> cheeses = new ArrayList<Objective>();
	Start start = null;
	End end = null;
	Player player = null;

    // Constructor.
	DrawingThread(ArrayList<Wall> walls, ArrayList<Floor> floors, ArrayList<Objective> cheeses, Start start, End end, Player player, int index, ThreadGroup group, String name){
		super(group, name);
		this.walls = walls;
		this.floors = floors;
		this.cheeses = cheeses;
		this.index = index;
		this.start = start;
		this.end = end;
		this.player = player;
	}
}