
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/*
 * random start location
 * random walk, carving passages to unvisted neighbours untill current cell has no more un visited neighbours
 * hunt, scan grid for unvisted cell with adjacent cell that is visited, create passage between the two, set unvisted cell as new start location
 * loop until no more unvisted cells
 */

class HuntKillMaze {
	
	private int width;
	private int height;
	private char map[][];
	//private int weightMap[][];
	public static final char NORTH = 'n';
	public static final char WEST = 'w';
	public static final char SOUTH = 's';
	public static final char EAST = 'e';
	public static final char EMPTY_CELL_CHAR = '0';
	
	HuntKillMaze(int width, int height) {
		this.width = width;
		this.height = height;
		//create array of unvisted cells to set dimensions
		//[rows][columns]
		this.map = new char[height][width];
		char empty = EMPTY_CELL_CHAR;
		//fill map with unvisted cells
		for (char[] row: map) {
			Arrays.fill(row, empty);
		}
		
		//get a random point to start the algorithm
		int[] point = randomPoint(width, height);
		int x = point[0];
		int y = point[1];
		//set start point as x
		map[y][x] = 'x';
		//loop until all cells have been visited
		while(containsUnvisited()) {
			//do a random walk until it reaches dead end
			point = walk(x,y);
			point = hunt();
			if (point[0] < 0 || point[1] < 0) {
				break;
			}
			//connect new path to a previous path
			x = point[0];
			y = point[1];
			map[y][x] = neighbourDirection(x, y);
		}
	}
	
	int[] walk(int sx, int sy) { //preform random walk until it hits a dead end and can not proceed
		int x = sx;
		int y = sy;
		int[] endCell = new int[3];
		//int weight = 0;
		while (hasAdjacent(x,y)) {
			//++weight;
			ArrayList<int[]> adjacentCells = new ArrayList<>(); //list of adjacent cells
			//check left
			if(x - 1 >= 0) {
				if (map[y][x - 1] == EMPTY_CELL_CHAR) {
					int[] adjacentCell = new int[3];
					adjacentCell[0] = x - 1;
					adjacentCell[1] = y;
					adjacentCell[2] = 0;
					adjacentCells.add(adjacentCell);
				}
			}
			//check right
			if(x + 1 < width)  {
				if (map[y][x + 1] == EMPTY_CELL_CHAR) {
					int[] adjacentCell = new int[3];
					adjacentCell[0] = x + 1;
					adjacentCell[1] = y;
					adjacentCell[2] = 1;
					adjacentCells.add(adjacentCell);
				}
			}
			//check up
			if(y - 1 >= 0) {
				if (map[y - 1][x] == EMPTY_CELL_CHAR) {
					int[] adjacentCell = new int[3];
					adjacentCell[0] = x;
					adjacentCell[1] = y - 1;
					adjacentCell[2] = 2;
					adjacentCells.add(adjacentCell);
				}
			}
			//check down
			if(y + 1 < height) {
				if (map[y + 1][x] == EMPTY_CELL_CHAR) {
					int[] adjacentCell = new int[3];
					adjacentCell[0] = x;
					adjacentCell[1] = y + 1;
					adjacentCell[2] = 3;
					adjacentCells.add(adjacentCell);
				}
			}			

			if(adjacentCells.size() > 1) { //if there are multiple adjacent cells, choose one at random
				Random ran = new Random();
				int num;
				num = ran.nextInt(adjacentCells.size());
				endCell = adjacentCells.get(num);
				
			} else {
				endCell = adjacentCells.get(0);
			}
			x = endCell[0];
			y = endCell[1];
			map[y][x] = intDirectionToCharDirection(endCell[2]);
			//weightMap[y][x] = weight;
		}
		return endCell;
	}
	
	int[] hunt() { //search each row, one by one for an unvisited cell with an adjacent neighbour that is visited
		for(int y = 0; y < this.height; ++y) {
			for(int x = 0; x < this.width; ++x) {
				if(map[y][x] == EMPTY_CELL_CHAR && hasNeighbour(x,y)) {
					//if cell is found that meets the parameters, return its position
					int[] pos = {x,y};
					return pos;
				}
			}
		}	
		int[] empty = {-1,-1};
		return empty;
	}
	
	char intDirectionToCharDirection(int direction) { //convert integer direction character
		switch (direction) {
			case 0:
				return EAST;
			case 1:
				return WEST;
			case 2:
				return SOUTH;
			case 3:
				return NORTH;
		}
		return 'a';
	}
	
	Boolean hasAdjacent(int x, int y) { //check if a cell has unvisted neighbours
		//check left
		if(x - 1 >= 0) {
			if (map[y][x - 1] == EMPTY_CELL_CHAR) {
				return true;
			}
		}
		//check right
		if(x + 1 < width)  {
			if (map[y][x + 1] == EMPTY_CELL_CHAR) {
				return true;
			}
		}
		//check up
		if(y - 1 >= 0) {
			if (map[y - 1][x] == EMPTY_CELL_CHAR) {
				return true;
			}
		}
		//check down
		if(y + 1 < height) {
			if (map[y + 1][x] == EMPTY_CELL_CHAR) {
				return true;
			}
		}
		return false;
	}
	
	char neighbourDirection(int x, int y) { //return direction of neighbour that is part of maze and adjacent
		Character direction = null;
		ArrayList<Character> directions = new ArrayList<>();
		if(x - 1 >= 0) {
			if (map[y][x - 1] != EMPTY_CELL_CHAR) {
				directions.add('w');
			}
		}
		//check right
		if(x + 1 < width)  {
			if (map[y][x + 1] != EMPTY_CELL_CHAR) {
				directions.add('e');
			}
		}
		//check up
		if(y - 1 >= 0) {
			if (map[y - 1][x] != EMPTY_CELL_CHAR) {
				directions.add('n');
			}
		}
		//check down
		if(y + 1 < height) {
			if (map[y + 1][x] != EMPTY_CELL_CHAR) {
				directions.add('s');
			}
		}
		
		if(directions.size() > 1) { //check if there are multiple adjacent cells, if so return direction of one at random
			Random ran = new Random();
			int num;
			num = ran.nextInt(directions.size());
			direction = directions.get(num);			
		} else {
			direction = directions.get(0);
		}
		return direction;
	}
	
	Boolean hasNeighbour(int x, int y) { //check if a cell has a neighbour that is part of the maze
		//check left
		if(x - 1 >= 0) {
			if (map[y][x - 1] != EMPTY_CELL_CHAR) {
				return true;
			}
		}
		//check right
		if(x + 1 < width)  {
			if (map[y][x + 1] != EMPTY_CELL_CHAR) {
				return true;
			}
		}
		//check up
		if(y - 1 >= 0) {
			if (map[y - 1][x] != EMPTY_CELL_CHAR) {
				return true;
			}
		}
		//check down
		if(y + 1 < height) {
			if (map[y + 1][x] != EMPTY_CELL_CHAR) {
				return true;
			}
		}
		return false;
	}
	
	Boolean containsUnvisited() { //check if there are any unvisited cells in map
		for(int y = 0; y < this.height; ++y) {
			for(int x = 0; x < this.width; ++x) {
				if(map[y][x] == EMPTY_CELL_CHAR) {
					return true;
				}
			}
		}
		return false;
	}
	
	int[] randomPoint(int width, int height) { //get random point within bounds of maze dimensions
		Random ran = new Random();
		int x;
		int y;
		x = ran.nextInt(width);
		y = ran.nextInt(height);
		int[] point = {x,y};
		return point;
	}
	
	public char[][] getMap() {
		return map;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public String toString() { //turn map into string
		String str = "";
		for(int y = 0; y < this.height; ++y) {
			for(int x = 0; x < this.width; ++x) {
				str += map[y][x];
			}
			str += "\n";
		}
		return str;
	}

}