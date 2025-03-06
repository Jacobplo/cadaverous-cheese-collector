import java.awt.*;

import javax.imageio.ImageIO;
import javax.swing.*;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

public class Scoreboard extends JFrame{

    public static final Color GOLD = new Color(219, 172, 52);

    // Constructor, being used as a secondary main to define the scoreboard.
    public Scoreboard(){
        // Gets the scores from the scoreboard text file and puts them in an array.
        FileMethods methods = new FileMethods();
        String[] stringScores = methods.fileRead(new File("scoreboard.txt")).split(Character.toString((char)10));
        Double[] scores = new Double[stringScores.length - 1];
        for(int i = 1; i < stringScores.length; ++i){
            scores[i - 1] = Double.parseDouble(stringScores[i]);
        }
        // Sort the score array from least time to greatest time.
        ArrayList<Double> sortedScores = new ArrayList<Double>(Arrays.asList(scores));
        Collections.sort(sortedScores, Comparator.comparing(Double::valueOf));

        // Scoreboard frame parameters.
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		setTitle("Scoreboard");
		setResizable(false);
	    setSize(600, 800);
        setVisible(true);

        int numberOfScores;
        // Displays the top 10 lowest times to the frame, or all scores if there are less than 10.
        if(scores.length < 10) numberOfScores = scores.length;
        else numberOfScores = 10;
        for(int i = 0; i < numberOfScores; ++i){
            // Draws a label for each position number on the left of the frame.
            JLabel position = new JLabel();
            position.setText(Integer.toString(i + 1) + ".");
            position.setFont(new Font("Comic Sans MS", Font.BOLD, 50));
            position.setForeground(Color.YELLOW);
            position.setBounds(50, 140 + 60 * i, 200, 50);
            add(position);

            // Draws a label for each time on the right of the frame.
            JLabel time = new JLabel();
            time.setText(sortedScores.get(i) + " s");
            time.setFont(new Font("Comic Sans MS", Font.ITALIC, 50));
            time.setForeground(GOLD);
            time.setBounds(400, 140 + 60 * i, 200, 50);
            add(time);
        }

        JPanel panel = new ScoreWindow();
        add(panel);
        validate();
        repaint();
    }
}// End class.

// Draws the background of the frame.
class ScoreWindow extends JPanel{
    @Override
    public void paintComponent(Graphics graphics) {
		super.paintComponent(graphics);
		Image background = null;
        // Determines runtime environment to decide how to load config file
		
        try{
        	if (System.console() == null) {
    			background = ImageIO.read(new File("resources" + File.separator + "score_screen.png")); //ide
    		} else {
    			URL backgroundURL = GameMain.class.getClassLoader().getResource("resources" + File.separator + "score_screen.png");
    			background = new ImageIcon(backgroundURL).getImage();
    		}
            //background = ImageIO.read(new File("resources" + File.separator + "score_screen.png"));
        }
        catch(IOException e){};
        graphics.drawImage(background, 0, 0, this);
    }
}