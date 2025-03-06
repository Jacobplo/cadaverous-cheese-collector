import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.InputMismatchException;
import java.util.Scanner;

public class FileMethods {
	// Writes a given line of text to a given text document file, replacing all file contents.
	public void fileWrite(File file, String text) {
		try {
			FileOutputStream output = new FileOutputStream(file, false);
			output.write(text.getBytes());
			output.close();
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}// End method.
	
	// Appends a given line of text to a given text document file, as a new line.
	public void fileAppend(File file, String text) {
		try {
			FileOutputStream output = new FileOutputStream(file, true);
			output.write(((char)10 + text).getBytes());
			output.close();
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}// End method.
	
	// Appends a given line of text to a given line in a given text document file.
	public void fileAppendToLine(File file, String text, int line) {
		try {
			String test = "";
			int lineCount = 1;
			FileOutputStream output = new FileOutputStream(file, true);
			FileInputStream input = new FileInputStream(file);
			
			// Iterates through each character in a given text file.
			while(input.available() > 0) {
				char chr = (char)input.read();
				
				// Increments a line counter if a new line character (10) is found.
				if(chr == (char)10) {
					++lineCount;
					test += chr;
				}
				
				// Keeps all text in the file before and after the input line number.
				else if(lineCount != line) {
					test += chr;
				}
				
				// Appends the input text to the input line.
				else {
					test += text + (char)10 + chr;
					++lineCount;
				}
			}
			
			// Case for if the line to be appended to is greater than the number of lines in the file.
			if(lineCount < line) {
				// Creates new lines until the input line is reached, and then writes the text to the file.
				for(int i = 0; i < line - lineCount; ++i) {
					test += (char)10;
				}
				test += text;
			}
			output.close();
			output = new FileOutputStream(file, false);
			output.write(test.getBytes());
			output.close();
			input.close();
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}// End method.
	
	// Replace a given line within a given text document file with text the given text.
	public void fileReplaceLine(File file, String text, int line) {
		try {
			String test = "";
			int lineCount = 1;
			FileOutputStream output = new FileOutputStream(file, true);
			FileInputStream input = new FileInputStream(file);
			
			// Iterates through each character in a given text file.
			while(input.available() > 0) {
				char chr = (char)input.read();
				
				// Increments a line counter if a new line character (10) is found.
				if(chr == (char)10) {
					++lineCount;
					test += chr;
				}
				else if(lineCount == line+1) {
					continue;
				}
				
				// Keeps all text in the file before and after the input line number.
				else if(lineCount != line) {
					test += chr;
				}
				
				// Writes the input text to the input line.
				else {
					test += text;
					++lineCount;
				}
			}
			// Case for if the line to be replaced to is greater than the number of lines in the file.
			if(lineCount < line) {
				// Creates new lines until the input line is reached, and then writes the text to the file.
				for(int i = 0; i < line - lineCount; ++i) {
					test += (char)10;
				}
				test += text;
			}
			output.close();
			output = new FileOutputStream(file, false);
			output.write(test.getBytes());
			output.close();
			input.close();
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}// End method.
	
	// Reads the text from a given text document file.
	public String fileRead(File file) {
		String text = "";
		try {
			
			FileInputStream input = new FileInputStream(file);
			
			// Iterates through each character in the file, and prints them to the console.
			while(input.available() > 0) {
				text += (char)input.read();
			}
			input.close();
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		
		return text;
	}// End method.
	
	// Reads the text from a given line of a given text document file.
	public String fileReadLine(File file, int line) {
		String textOnLine = "";
		try {
			int lineCount = 1;
			
			FileInputStream input = new FileInputStream(file);
			
			// Iterates through each character in a given text file.
			while(input.available() > 0) {
				char chr = (char)input.read();
				
				// Increments a line counter if a new line character (10) is found.
				if(chr == (char)10) {
					++lineCount;
				}
				
				// Adds the text from the input line to a string.
				if(lineCount == line) {
					textOnLine += chr;
				}
				else if(lineCount > line) {
					break;
				}
			}
			input.close();
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		if(textOnLine.isEmpty()) {
			return null;
		}
		return textOnLine;
	}// End method.
	
	// Deletes a given file.
	public boolean fileDelete(File file) {
		file.delete();
		return false;
	}// End method.
	
	// Gets the user's choice for an action menu.
	public int getChoice(int min, int max) {
		@SuppressWarnings("resource")
		Scanner scanner = new Scanner(System.in);
		while(true) {
			try {
				int choice = scanner.nextInt();
				if(choice < min || choice > max) {
					throw new ArithmeticException();
				}
				return choice;
			}
			catch(InputMismatchException e) {
				System.out.print("You must enter a valid choice. Try again: ");
				scanner.next();
			}
			catch(ArithmeticException e) {
				System.out.print("You must enter a valid choice. Try again: ");
			}
		}
	}// End method.
}// End class.
