import java.awt.*;

import javax.imageio.ImageIO;
import javax.swing.*;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.util.ArrayList;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.util.Properties;

public class GameMain {

	// Constants.
	static int SCREEN_HEIGHT;
	static int SCREEN_WIDTH;
	static int MAZE_WIDTH;
	static int MAZE_HEIGHT;
	static int FPS;
	static boolean objectivesEnabled = true;
	static int drawThreadCount;
	
	static int objectivesLeft;

	static JFrame scoreboardFrame = null;

	static FileMethods methods = new FileMethods();

	//double start = System.nanoTime();

	public static void main(String[] args) throws Exception{

		// Gets the frame and maze constants from the config file.
		Properties prop = new Properties();
		
		// Determines runtime environment to decide how to load config file
		if (System.console() == null) {
			FileInputStream in = new FileInputStream("config.properties"); //ide
			prop.load(in);
		} else {
			InputStream in = GameMain.class.getClassLoader().getResourceAsStream("config.properties"); //JAR
			prop.load(in);
		}
		
		SCREEN_HEIGHT = Integer.parseInt(prop.getProperty("SCREEN_HEIGHT"));
		SCREEN_WIDTH = Integer.parseInt(prop.getProperty("SCREEN_WIDTH"));
		MAZE_WIDTH = Integer.parseInt(prop.getProperty("MAZE_WIDTH"));
		MAZE_HEIGHT = Integer.parseInt(prop.getProperty("MAZE_HEIGHT"));
		FPS = Integer.parseInt(prop.getProperty("FPS"));
		objectivesEnabled = Boolean.parseBoolean(prop.getProperty("OBJECTIVES_ENABLED"));
		drawThreadCount = Integer.parseInt(prop.getProperty("DRAW_THREADS"));

		// Main game JFrame initialization.
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("Cadaverous Cheese Collector (0%)");
		frame.setResizable(false);
		frame.setVisible(true);
		frame.setSize(SCREEN_WIDTH + 14, SCREEN_HEIGHT + 37); // Numbers are added to account for the scaling with a decorated frame.

		// New game loop.
		while(true) {

			// Maze declaration and setup.
			HuntKillMaze maze = new HuntKillMaze(MAZE_WIDTH/2, MAZE_HEIGHT/2);
			MazeExpanding grid = new MazeExpanding(maze);
			grid.buildGrid();
			grid.addBorder();
			grid.setWalls();
			grid.randomEnd();
			if(objectivesEnabled){
				grid.placeObjectives();
			}
			objectivesLeft = grid.totalObjectives;
			
			// Gets the array data of the maze.
			int[][] data;
			data = grid.getFinalMaze();
			
			// Declares variables to store the maze tile data.
			ArrayList<Floor> floors = new ArrayList<Floor>(); 
			ArrayList<Wall> walls = new ArrayList<Wall>(); 
			ArrayList<Objective> cheeses = new ArrayList<Objective>();
			Start start = null;
			End end = null;
			Player player = null;
			
			// Thread managment objects
			ThreadGroup threads = new ThreadGroup("Row Thread Group");
			ArrayList<Thread> threadList = new ArrayList<Thread>();


			// Iterates through the 2D maze data array, creating tile objects to match the data.
			int rowIndex = 0;
			
			double x = System.nanoTime();
			for(int[] row: data) {
				//System.out.println("row index: " + rowIndex);
				Thread dataThread = new DataThread(rowIndex, row, threads, "Thread " + row.toString()){
					@Override
					public void run() {
						staticFrame = frame;
						//System.out.println(getName() + " started");
						int tileIndex = 0;
						for(int tile: row) {
							try {
								if(tile == 0) {
									rowFloors.add(new Floor(SCREEN_WIDTH / row.length * tileIndex, SCREEN_HEIGHT / data.length * rowIndex, SCREEN_WIDTH / row.length, SCREEN_HEIGHT / data.length));
								}
								else if(tile == 1 || tile == 2) {
									rowWalls.add(new Wall(SCREEN_WIDTH / row.length * tileIndex, SCREEN_HEIGHT / data.length * rowIndex, SCREEN_WIDTH / row.length, SCREEN_HEIGHT / data.length, tile));
								}
								else if(tile == 3) {
									start = (new Start(SCREEN_WIDTH / row.length * tileIndex, SCREEN_HEIGHT / data.length * rowIndex, SCREEN_WIDTH / row.length, SCREEN_HEIGHT / data.length));
									player = (new Player(SCREEN_WIDTH / row.length * tileIndex, SCREEN_HEIGHT / data.length * rowIndex, SCREEN_WIDTH / row.length, SCREEN_HEIGHT / data.length));
								}
								else if(tile == 4) {
									end = (new End(SCREEN_WIDTH / row.length * tileIndex, SCREEN_HEIGHT / data.length * rowIndex, SCREEN_WIDTH / row.length, SCREEN_HEIGHT / data.length));
								}
								else if(tile == 5){
									rowFloors.add(new Floor(SCREEN_WIDTH / row.length * tileIndex, SCREEN_HEIGHT / data.length * rowIndex, SCREEN_WIDTH / row.length, SCREEN_HEIGHT / data.length));
									rowCheeses.add(new Objective(SCREEN_WIDTH / row.length * tileIndex, SCREEN_HEIGHT / data.length * rowIndex, SCREEN_WIDTH / row.length, SCREEN_HEIGHT / data.length));
								}
							}
							
							catch(IOException e) {}
							++tileIndex;
						}			
						//System.out.println(getName() + " finished");

						//update progress bar
						GameMain.progressBar(staticFrame);
					}
					
				};
				threadList.add(dataThread);
				dataThread.start();
				++rowIndex;
			}
			while(threads.activeCount() > 0) {} //waits for threads to finish
			
			for(Thread thread: threadList) {
				DataThread.walls.addAll(((DataThread)thread).rowWalls);
				DataThread.floors.addAll(((DataThread)thread).rowFloors);
				DataThread.cheeses.addAll(((DataThread)thread).rowCheeses);
				((DataThread)thread).rowWalls = null;
				((DataThread)thread).rowFloors = null;
				((DataThread)thread).rowCheeses = null;
			}

			floors = DataThread.floors;
			walls = DataThread.walls;
			cheeses = DataThread.cheeses;
			start = DataThread.start;
			end = DataThread.end;
			player = DataThread.player;
			
			System.out.println((System.nanoTime() - x)/1000000 + " milliseconds to load maze objects");
			
			DataThread.resetTiles();

			//set title without loading
			frame.setTitle("Cadaverous Cheese Collector");
			resetFinishedThreads();

			boolean endActivated = false;
			
	        // Main game JPanel initialization.
	        JPanel panel = new Window(data, floors, walls, cheeses, start, end, player);
	        panel.setLayout(null);;
	        frame.add(panel);
	        
			// Adds keyboard input capability to the frame, but not allowing input in the menu.
			KeyListener keyListener = new Keyboard(data, SCREEN_WIDTH, SCREEN_HEIGHT, player, walls, cheeses, end, panel);
	        frame.addKeyListener(keyListener);
	        frame.setFocusable(false);
	        
	        //declare button objects
	        JButton leaveButton;
	        JButton scoreButton;
	        JButton startButton;
	        
	        // Adds a leave button to the frame, allowing the game to be exited.
	        leaveButton = new JButton();
	        leaveButton.setBounds(SCREEN_WIDTH / 12 * 7, SCREEN_HEIGHT / 3 * 2, SCREEN_WIDTH / 4, SCREEN_HEIGHT / 8);
	        
	        Image leaveImage;
	        // Determines runtime environment to decide how to load config file
			if (System.console() == null) {
				leaveImage = ImageIO.read(new File("resources" + File.separator + "leaveButton.png")).getScaledInstance(SCREEN_WIDTH / 4, SCREEN_HEIGHT / 8, Image.SCALE_DEFAULT); //ide
			} else {
				URL leaveURL = GameMain.class.getClassLoader().getResource("resources" + File.separator + "leaveButton.png");
				leaveImage = new ImageIcon(leaveURL).getImage().getScaledInstance(SCREEN_WIDTH / 4, SCREEN_HEIGHT / 8, Image.SCALE_DEFAULT);
			}
			leaveButton.setIcon(new ImageIcon(leaveImage));
			
			leaveButton.addActionListener(new ActionListener() {
	            @Override
	            public void actionPerformed(ActionEvent e) {
	                System.exit(0);
	            }
	        });
			
	        panel.add(leaveButton);
	        
	        
			// Adds a scoreboard button to the frame, which creates a seperate scoreboard frame on click.
	        scoreButton = new JButton();
	        scoreButton.setBounds(SCREEN_WIDTH / 2 - SCREEN_WIDTH / 16, SCREEN_HEIGHT / 6 * 5, SCREEN_WIDTH / 8, SCREEN_HEIGHT / 16);
	        
	        Image scoreImage;
	        // Determines runtime environment to decide how to load config file
			if (System.console() == null) {
				scoreImage = ImageIO.read(new File("resources" + File.separator + "scoreButton.png")).getScaledInstance(SCREEN_WIDTH / 8, SCREEN_HEIGHT / 16, Image.SCALE_DEFAULT); //ide
			} else {
				URL scoreURL = GameMain.class.getClassLoader().getResource("resources" + File.separator + "scoreButton.png");
				scoreImage = new ImageIcon(scoreURL).getImage().getScaledInstance(SCREEN_WIDTH / 8, SCREEN_HEIGHT / 16, Image.SCALE_DEFAULT);
			}
			scoreButton.setIcon(new ImageIcon(scoreImage));
	        
	        scoreButton.addActionListener(new ActionListener() {
	            @Override
	            public void actionPerformed(ActionEvent e) {
	                GameMain.scoreboardFrame = new Scoreboard();
	            }
	        });
	        
	        panel.add(scoreButton);

	        
	        // Adds a start button to the frame, allowing the game to be started, exiting the menu.
	        startButton = new JButton();
	        startButton.setBounds(SCREEN_WIDTH / 6, SCREEN_HEIGHT / 3 * 2, SCREEN_WIDTH / 4, SCREEN_HEIGHT / 8);
	        
	        Image startImage;
	        // Determines runtime environment to decide how to load image file
			if (System.console() == null) {
				startImage = ImageIO.read(new File("resources" + File.separator + "startButton.png")).getScaledInstance(SCREEN_WIDTH / 4, SCREEN_HEIGHT / 8, Image.SCALE_DEFAULT); //ide
			} else {
				URL startURL = GameMain.class.getClassLoader().getResource("resources" + File.separator + "startButton.png");
				startImage = new ImageIcon(startURL).getImage().getScaledInstance(SCREEN_WIDTH / 4, SCREEN_HEIGHT / 8, Image.SCALE_DEFAULT);
			}
			startButton.setIcon(new ImageIcon(startImage));
	        
	        startButton.addActionListener(new ActionListener() {
	            @Override
	            public void actionPerformed(ActionEvent e) {
	                ((Window)panel).windowState = "maze";
	                //randomButton.setVisible(false); //idk how to fix
	                startButton.setVisible(false);
	                leaveButton.setVisible(false);
					scoreButton.setVisible(false);
	                frame.setFocusable(true);
	                frame.requestFocusInWindow();
	            }
	        });
	        panel.add(startButton);

			// Label to display the amount of seconds passed during the maze.
			JLabel time = new JLabel();
			time.setText("0");
			time.setFont(new Font("Comic Sans MS", Font.BOLD, 50));
			time.setForeground(Color.YELLOW);
			time.setBounds(SCREEN_WIDTH - 50, 5, 300, 50);
			time.setVisible(false);
			panel.add(time);

			frame.validate();

			long startTime = 0;
			BigDecimal displayTime = null;
			
			// Main game loop.
	        while(true) {
				// Increments frame count and draws the frame to the JFrame.
				if(((Window)panel).windowState.equals("maze")){
					if(!time.isVisible()) {
						time.setVisible(true);
						startTime = System.nanoTime();
					}

					// Updates the time counter label, adjusting the position for the amount of digits. Rounds to one decimal place.
					double currentSeconds = (double)Math.round((double)(System.nanoTime() - startTime)/1000000000*10)/10;
					displayTime = new BigDecimal(Double.toString((double)currentSeconds));
					displayTime = displayTime.setScale(1, RoundingMode.HALF_UP);
					time.setText(displayTime.toString());
					time.setBounds(SCREEN_WIDTH - (20 + 30 * Double.toString((double)currentSeconds).length()), 5, 200, 50);

					player.animate();
				}
	        	frame.repaint();

				// Activates the end if all objectives have been collected.
				if(objectivesLeft == 0 && !endActivated){
					end.activate();
					endActivated = true;
				}

				// Ends the game and disposes the frame when the player wins. Updates the scoreboard with the time taken.
				if(((Window)panel).finished) {
					methods.fileAppend(new File("scoreboard.txt"), displayTime.toString());
					frame.repaint();
					Thread.sleep(3000);
					frame.getContentPane().removeAll();
					frame.removeKeyListener(keyListener);
	        		break;
	        	}
	        	Thread.sleep(1000/FPS);
	        }// End main game loop.
		}// End new game loop.
	}// End main.
	
	// progress display variables and methods
	static volatile int finishedThreads = 0;

	static void resetFinishedThreads() {
		finishedThreads = 0;
	}

	static void progressBar(JFrame f) { // Set title to percentage based on number of threads finished
		++finishedThreads;
		f.setTitle("Cadaverous Cheese Collector (" + (int)((double)finishedThreads/(MAZE_HEIGHT/2*2)*100) + "%)");
	}
}// End class.

// Modified JPanel used for painting components to the JFrame.
class Window extends JPanel {
	static boolean flashlightEnabled;
	int flashlightRadiusModifier;
	
	int[][] data;
	ArrayList<Floor> floors;
	ArrayList<Wall> walls;
	ArrayList<Objective> cheeses;
	Start start;
	End end;
	Player player;
	
	// Sets default game states.
	String windowState = "menu";
	boolean finished = false;

	// Variables to be used for the flashlight mechanism to remember already visited tiles.
	static ArrayList<Wall> visitedWalls = new ArrayList<Wall>();
	static ArrayList<Floor> visitedFloors = new ArrayList<Floor>();
	static ArrayList<Objective> visitedCheeses = new ArrayList<Objective>();
	static End visitedEnd = null;

	static int flashRadius;

	// menu image
	Image menuBG;
	URL menuBGURL;

	// Constructor.
	public Window(int[][] data, ArrayList<Floor> floors, ArrayList<Wall> walls, ArrayList<Objective> cheeses, Start start, End end, Player player) throws IOException {
		
		this.data = data;
		this.floors = floors;
		this.walls = walls;
		this.cheeses = cheeses;
		this.start = start;
		this.end = end;
		this.player = player;

		// load menu image
		if (System.console() == null) {
			menuBG = ImageIO.read(new File("resources" + File.separator + "start_screen.png")); //ide
		} else {
			menuBGURL = GameMain.class.getClassLoader().getResource("resources" + File.separator + "start_screen.png");
			menuBG = new ImageIcon(menuBGURL).getImage();
		}
		menuBG = menuBG.getScaledInstance(GameMain.SCREEN_WIDTH, GameMain.SCREEN_HEIGHT, Image.SCALE_DEFAULT);

		// Gets the flashlight state from the config file. If true, flashlight radius is 2 tiles. If false, flashlight radius is set to high enough to cover the entire maze.
		Properties prop = new Properties();
		
		// Determines runtime environment to decide how to load config file
		if (System.console() == null) {
			FileInputStream in = new FileInputStream("config.properties"); //ide
			prop.load(in);
		} else {
			InputStream in = GameMain.class.getClassLoader().getResourceAsStream("config.properties"); //JAR
			prop.load(in);
		}

		flashlightEnabled = Boolean.parseBoolean(prop.getProperty("FLASHLIGHT_ENABLED"));
		if (!flashlightEnabled) {
			flashlightRadiusModifier = 1000000;
		} else {
			flashlightRadiusModifier = 1600;
		}
		
		flashRadius = flashlightRadiusModifier/GameMain.MAZE_WIDTH;
		visitedWalls = new ArrayList<Wall>();
		visitedFloors = new ArrayList<Floor>();
		visitedCheeses = new ArrayList<Objective>();
	}
	
	// Paints necessary information to the JFrame, depending on the game state.
	
    @Override
    public void paintComponent(Graphics graphics) {
		super.paintComponent(graphics);
		
		// Switch statement for the game statement.
		switch(windowState) {
		// Case for drawing the menu screen.
		case "menu":
			try {
				graphics.drawImage(menuBG, 0, 0, null);
			} 
			catch (Exception e) {}
			break;

		// Case for drawing the maze game based on tile data.
		case "maze":
			// Resets the frame so that it can be drawn again.
			graphics.setColor(Color.BLACK);
			graphics.fillRect(0, 0, GameMain.SCREEN_WIDTH, GameMain.SCREEN_HEIGHT);
			try {
				//double x = System.nanoTime();
				
				// Determine if wall tiles have been visited using the flashlight radius. Adds them to the accompanying variables if so.
				// Done by multihreading.
				ThreadGroup checkThreads = new ThreadGroup("check Thread Group");
				for(int i = GameMain.drawThreadCount; i < walls.size() + GameMain.drawThreadCount; i += GameMain.drawThreadCount){
					Thread wallCheck = new DrawingThread(walls, floors, cheeses, start, end, player, i, checkThreads, "wallCheck-" + i){
						@Override
						public void run() {
							for(int i = index - GameMain.drawThreadCount; i < index; ++i){
								try {
									if(!(walls.get(i).xPos - flashRadius > player.xPos || player.xPos - flashRadius > walls.get(i).xPos || walls.get(i).yPos - flashRadius > player.yPos || player.yPos - flashRadius > walls.get(i).yPos) && !Window.visitedWalls.contains(walls.get(i))){
										Window.visitedWalls.add(walls.get(i));
									}
								}
								catch(Exception e){};
							}
						}
					};
					wallCheck.start();
				}
				
				// Determine if floor tiles have been visited using the flashlight radius. Adds them to the accompanying variables if so.
				// Done by multihreading.
				for(int i = GameMain.drawThreadCount; i < floors.size() + GameMain.drawThreadCount; i += GameMain.drawThreadCount){
					Thread floorCheck = new DrawingThread(walls, floors, cheeses, start, end, player, i, checkThreads, "floorCheck-" + i){
						@Override
						public void run() {
							for(int i = index - GameMain.drawThreadCount; i < index; ++i){
								try {
									if(!(floors.get(i).xPos - flashRadius > player.xPos || player.xPos - flashRadius > floors.get(i).xPos || floors.get(i).yPos - flashRadius > player.yPos || player.yPos - flashRadius > floors.get(i).yPos) && !Window.visitedFloors.contains(floors.get(i))){
										Window.visitedFloors.add(floors.get(i));
									}
								}
								catch(Exception e){};
							}
						}
					};
					floorCheck.start();
				}
				
				// Determine if cheese tiles have been visited using the flashlight radius. Adds them to the accompanying variables if so.
				for(int i = 0; i < cheeses.size(); ++i) {
					if(!(cheeses.get(i).xPos - flashRadius > player.xPos || player.xPos - flashRadius > cheeses.get(i).xPos || cheeses.get(i).yPos - flashRadius > player.yPos || player.yPos - flashRadius > cheeses.get(i).yPos) && !Window.visitedCheeses.contains(cheeses.get(i))){
						Window.visitedCheeses.add(cheeses.get(i));
					}
				}
				// Determine if the end have been visited using the flashlight radius. Adds them to the accompanying variables if so.
				if(!(end.xPos - flashRadius > player.xPos || player.xPos - flashRadius > end.xPos || end.yPos - flashRadius > player.yPos || player.yPos - flashRadius > end.yPos) && visitedEnd != end){
					visitedEnd = end;
				}
				
				// Waits for all threads to finish.
				while(checkThreads.activeCount() > 0) {}

				//double x = System.nanoTime();
				
				// Draws all visited wall tiles to the frame.
				// Done by multithreading.
				ThreadGroup visitedThreads = new ThreadGroup("vistitedThread Thread Group");
				for(int i = GameMain.drawThreadCount; i < visitedWalls.size() + GameMain.drawThreadCount; i += GameMain.drawThreadCount){
					Thread wallThread = new DrawingThread(visitedWalls, visitedFloors, visitedCheeses, start, visitedEnd, player, i, visitedThreads, "wall-" + i){
						@Override
						public void run() {
							for(int i = index - GameMain.drawThreadCount; i < index; ++i){
								try {
									walls.get(i).draw(graphics);
								}
								catch(Exception e){};
								
							}
						}
					};
					wallThread.start();
				}
				
				// Draws all visited floor tiles to the frame.
				// Done by multithreading.
				for(int i = GameMain.drawThreadCount; i < visitedFloors.size() + GameMain.drawThreadCount; i += GameMain.drawThreadCount){
					Thread floorThread = new DrawingThread(visitedWalls, visitedFloors, visitedCheeses, start, visitedEnd, player, i, visitedThreads, "floor-" + i){
						@Override
						public void run() {
							for(int i = index - GameMain.drawThreadCount; i < index; ++i){
								try {
									visitedFloors.get(i).draw(graphics);
								}
								catch(Exception e){};
								
							}
						}
					};
					floorThread.start();
				}

				// Waits for all active threads to finish before drawing the cheeses because cheeses must be drawn on top of floors.
				while(visitedThreads.activeCount() > 0) {}

				// Draws all visited cheese tiles to the frame.
				// Done by multithreading.
				for(int i = 10; i < visitedCheeses.size() + 10; i += 10){
					Thread cheeseThread = new DrawingThread(visitedWalls, visitedFloors, visitedCheeses, start, visitedEnd, player, i, visitedThreads, "cheese-" + i){
						@Override
						public void run() {
							for(int i = index - 10; i < index; ++i){
								try {
									visitedCheeses.get(i).draw(graphics);
								}
								catch(Exception e){};
								
							}
						}
					};
					cheeseThread.start();
				}
				
				// Waits for all active threads to finish.
				while(visitedThreads.activeCount() > 0) {}
				
				// Draws the start, player, and end tile if visited to the frame..
				start.draw(graphics);
				if(visitedEnd == end){
					visitedEnd.draw(graphics);
				}
				player.draw(graphics);

				//System.out.println((System.nanoTime() - x)/1000);
			}
			catch(Exception e) {}
			break;

		// Case for drawing the end screen.
		case "finish":
			try{
				// load finish screen image
				Image finishBG = ImageIO.read(new File("resources" + File.separatorChar + "end_screen.png"));
				finishBG = finishBG.getScaledInstance(GameMain.SCREEN_WIDTH, GameMain.SCREEN_HEIGHT, Image.SCALE_DEFAULT);
				graphics.drawImage(finishBG, 0, 0, null);
			}
			catch(Exception e){}	
			break;
		}// End switch case.
    }// End paintComponent.
}// End class.
