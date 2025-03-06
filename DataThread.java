import java.util.ArrayList;

import javax.swing.JFrame;

// Creates a thread used for creating the maze tiles based on input data.
public class DataThread extends Thread{
	int rowIndex;
	int[] row = null;

	ArrayList<Wall> rowWalls = new ArrayList<Wall>();
	ArrayList<Floor> rowFloors = new ArrayList<Floor>();
	ArrayList<Objective> rowCheeses = new ArrayList<Objective>();

	static ArrayList<Wall> walls = new ArrayList<Wall>();
	static ArrayList<Floor> floors = new ArrayList<Floor>();
	static ArrayList<Objective> cheeses = new ArrayList<Objective>();
	static Start start = null;
	static End end = null;
	static Player player = null;
    
    static JFrame staticFrame;

    // Constructor.
	DataThread(int rowIndex, int[] row, ThreadGroup group, String name){
		super(group, name);
		this.rowIndex = rowIndex;
		this.row = row;
	}

	static void resetTiles(){
		walls = new ArrayList<Wall>();
		floors = new ArrayList<Floor>();
		cheeses = new ArrayList<Objective>();
		start = null;
		end = null;
		player = null;
	}
}