import java.util.Arrays;
import java.util.Random;
/*
 * 0 = floor
 * 1 = top wall
 * 2 = front wall
 * 3 = start
 * 4 = end
 *
 */
public class MazeExpanding {
	public final int FLOOR_TILE = 0;
	public final int TOP_WALL_TILE = 1;
	public final int FRONT_WALL_TILE = 2;
	public final int START_TILE = 3;
	public final int END_TILE = 4;
	public final int OBJECTIVE_TILE = 5;
	private char charMap[][];
	private int gridMaze[][];
	private int finalMaze[][];
	private HuntKillMaze maze;

	public int totalObjectives;

	MazeExpanding(HuntKillMaze maze) { //get maze to be expanded
		this.maze = maze;
		this.charMap = maze.getMap();
		this.gridMaze = new int[maze.getHeight()*2][maze.getWidth()*2];
		this.finalMaze = new int[maze.getHeight()*2 + 1][maze.getWidth()*2 + 1];
		for(int[] row:gridMaze) {
			Arrays.fill(row, TOP_WALL_TILE);
		}

	}

	void buildGrid() { //builds grid, removes walls in set positions
		for(int y = 0; y < charMap.length; ++y) {
			for(int x = 0; x < charMap[0].length; ++x) {
				gridMaze[y*2][x*2] = FLOOR_TILE;
				removeWall(x, y, charMap[y][x]);

			}
		}

	}

	void removeWall(int x, int y, char direction) { //remove wall in the given direction relative to cell
		if(direction == 'n') {
			gridMaze[y*2 - 1][x*2] = FLOOR_TILE;
		}

		if(direction == 's') {
			gridMaze[y*2 + 1][x*2] = FLOOR_TILE;
		}

		if(direction == 'w') {
			gridMaze[y*2][x*2 - 1] = FLOOR_TILE;
		}

		if(direction == 'e') {
			gridMaze[y*2][x*2 + 1] = FLOOR_TILE;
		}

		if(direction == 'x') {
			//do nothing
		}

	}

	void randomEnd() { //set the end point to a random location along right side or bottom
		while (true) {
			Random ran = new Random();
			boolean right = ran.nextBoolean(); // random choice of which side end point will be present
			if(right) { //if right side is chosen
				int y = ran.nextInt((finalMaze[0].length - 1) - 1) + 1;
				if (finalMaze[y][finalMaze[0].length - 2] != FLOOR_TILE){
					randomEnd();
				}
				else{
					finalMaze[y][finalMaze[0].length - 1] = END_TILE;
					finalMaze[y - 1][finalMaze[0].length - 1] = FRONT_WALL_TILE;
				}
				return;
			}
			int x = ran.nextInt((finalMaze.length - 1) - 1) + 1;
			if(finalMaze[finalMaze.length - 2][x] != FLOOR_TILE) {
				randomEnd();
			} else {
				finalMaze[finalMaze.length - 1][x] = END_TILE;
			}
			return;
		}
	}

	void placeObjectives() { //place objectives throughout the maze, number of objectives scales with size of maze
		Random ran = new Random();
		int numOfObjectives = (int)Math.ceil(((double)(maze.getWidth() + maze.getHeight())/2)/3); //number of objectives to place
		while(numOfObjectives > 0) {
			//get random position on maze
			int y = ran.nextInt((finalMaze.length - 1) - 1) + 1;
			int x = ran.nextInt((finalMaze[0].length - 1) - 1) + 1;
			//check if random position falls on a valid spot
			if(finalMaze[y][x] == FLOOR_TILE && (y >= finalMaze.length/2 || x >= finalMaze[0].length/2)){
				finalMaze[y][x] = OBJECTIVE_TILE;
				++totalObjectives;
				--numOfObjectives;
			}
		}
		
	}

	void addBorder() {
		//create top row
		int topBorder[];
		topBorder = new int[gridMaze[0].length + 1];
		topBorder[0] = TOP_WALL_TILE;
		topBorder[1] = START_TILE; //set position of start to corner
		for(int i = 0; i < gridMaze[0].length - 1; ++i) {
			topBorder[i+2] = TOP_WALL_TILE;
		}

		//set top row of maze
		finalMaze[0] = topBorder;

		//add border to sides
		for(int y = 0; y < gridMaze.length; ++y) {
			int[] row = new int[gridMaze[y].length + 1];
			row[0] = 1;
			for(int x = 0; x < gridMaze[0].length - 1; ++x) {
				row[x + 1] = gridMaze[y][x];
			}
			row[gridMaze[y].length] = TOP_WALL_TILE;
			finalMaze[y + 1] = row;
		}
	}
	
	void setWalls() {

		//create bottom row, it will always be front wall tiles
		int[] bottomRow = new int[finalMaze[0].length];
		Arrays.fill(bottomRow, FRONT_WALL_TILE);

		//set bottom row of maze
		finalMaze[finalMaze.length - 1] = bottomRow;

		//check which walls need to be front walls based on what is in front
		for(int y = 0; y < finalMaze.length - 1; ++y) {
			for(int x = 0; x < finalMaze[0].length; ++x) {
				if((finalMaze[y][x] == TOP_WALL_TILE || finalMaze[y][x] == FRONT_WALL_TILE) && finalMaze[y + 1][x] == FLOOR_TILE) {
					finalMaze[y][x] = FRONT_WALL_TILE;
				}
			}
		}
	}
	
	public int[][] getFinalMaze() {
		return finalMaze;
	}
	
	public String finalToString() {
		String str = "";
		for(int y = 0; y < finalMaze.length; ++y) {
			for (int[] element : finalMaze) {
				str += element[y];
			}
			str += "\n";
		}
		return str;
	}

	@Override
	public String toString() { // turn grid into string
		String str = "";
		for(int y = 0; y < gridMaze[0].length; ++y) {
			for (int[] element : gridMaze) {
				str += element[y];
			}
			str += "\n";
		}
		return str;
	}
}
