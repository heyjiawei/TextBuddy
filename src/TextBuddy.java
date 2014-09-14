import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Scanner;

/**
 * This class is used to keep track of to-do list.
 * A file is expected to be passed in to TextBuddy. 
 * If the file exists, TextBuddy will retrieve all current information in it.
 * Else, TextBuddy will create a file named with the string passed in.
 * Any changes to the file via TextBuddy command line interface will be saved
 * into the existing file. Changes are saved when the programme exits.
 * The command format is given by the example interaction below:

c:>java TextBuddy myTextFile.txt
Welcome to TextBuddy. myTextFile.txt is ready for use
command: add little brown fox
added to myTextFile.txt: "little brown fox"
command: display
1. little brown fox
command: add jumped over the moon
added to myTextFile.txt: "jumped over the moon"
command: display
1. little brown fox
2. jumped over the moon
command: delete 2
deleted from myTextFile.txt: "jumped over the moon"
command: display
1. little brown fox
command: clear
all content deleted from myTextFile.txt
command: display
myTextFile.txt is empty
command: exit

 * @author Chong Jia Wei
 */

public class TextBuddy {
	
	// Messages to show to user
	private static String MESSAGE_WELCOME = "Welcome to TextBuddy. %s is ready for use\n";
	private static String MESSAGE_WELCOME_COMMAND = "Command: ";
	private static String MESSGAE_INVALID_COMMAND = "Please type in a valid command\n";
	private static String MESSAGE_ADDED = "added to %s: \"%s\"\n";
	private static String MESSAGE_ADDED_ERROR = "This is not a valid task!\n";
	private static String MESSAGE_DISPLAY_ITEMS = "%d. %s\n";
	private static String MESSAGE_DISPLAY_EMPTY = "%s is empty\n";
	private static String MESSAGE_DELETE_TASK = "deleted from %s: \"%s\"\n";
	private static String MESSAGE_DELETE_ERROR = "no content to delete\n";
	private static String MESSAGE_CLEARED = "all content deleted from %s\n";
	private static String MESSGAE_FILE_UNABLE_TO_DELETE = "Could not delete file\n";
	private static String MESSAGE_FILE_UNABLE_TO_RENAME = "Could not rename file\n";

	// This is used to store the list of items added by the user
	private static LinkedList<String> taskList = new LinkedList<String>();

	// This is used to store the name of the file used
	private static String filename;

	// This is used to scan the input from the user
	private static Scanner sc = new Scanner(System.in);

	// This is used to store the file reference 	
	private static File fileReference;
	
	// These are the starting indexes for commands
	private static int DELETE_TEXT_STARTING_INDEX = 7;
	private static int ADD_TEXT_STARTING_INDEX = 4;

	// These are the possible command types
	private enum Command {
		ADD, DISPLAY, DELETE, CLEAR, EXIT, INVALID
	}
	
	public static void main(String[] args) {

		TextBuddy.filename = getFileName(args);
		fileReference = new File(filename);

		if (fileExists(fileReference)) {
			loadDataFromFile();
		}

		showToUser(String.format(MESSAGE_WELCOME, filename));
		askUserForInput(); 
	}
	
	/**
	 * This method continously asking users for inputs.
	 */
	private static void askUserForInput() {
		while(true) {
			showToUser(MESSAGE_WELCOME_COMMAND);
			String input = sc.nextLine();
			executeCommand(input);
		}
	}
	
	/** Determine the command type entered by the user
	 * 
	 * @param commandType 
	 * 		command word entered by the user
	 * @return a command stated in enum Command
	 */
	private static Command determineCommandType(String commandType) {
		if (commandType == null) {
			throw new Error(MESSGAE_INVALID_COMMAND);
		} else if (commandType.equals("add")) {
			return Command.ADD;
		} else if (commandType.equals("delete")) {
			return Command.DELETE;
		} else if (commandType.equals("display")) {
			return Command.DISPLAY;
		} else if (commandType.equals("clear")) {
			return Command.CLEAR;
		} else if (commandType.equals("exit")) {
			return Command.EXIT;
		} else {
			return Command.INVALID;
		}
	}

	/** A method to execute a command based on the command type. This method 
	 * calls method determineCommandType to handle each of the command type.
	 * 
	 * @param details 
	 * 		task details key in by users
	 */
	private static void executeCommand(String details) {
		String userCommand = getFirstWord(details);
		Command commandType = determineCommandType(userCommand);
		
		switch (commandType) {
			case ADD:
				processAddTask(details);
				break;
				
			case DELETE:
				processDeleteTask(details);
				break;
				
			case DISPLAY:
				display();
				break;
				
			case CLEAR:
				clear();
				break;
				
			case EXIT:
				exit();
				break;
				
			default:
				showToUser(MESSGAE_INVALID_COMMAND);
		}
	}
	
	/**
	 * A method to add an item to the list, which will then be stored in taskList
	 * The method lists down the procedures taken to add an item to the list.
	 * 
	 * @param task 
	 * 		Add command and details of what to be added that are key in by users
	 */
	private static void processAddTask(String task) {
		task = removeAddWord(task);
		if (isValidAddTask(task)) {
			addTask(task);
		} else {
			throw new Error(MESSAGE_ADDED_ERROR);
		}	
	}
	
	/**
	 * Method removes command word "add" from task key in by users
	 * @param details
	 * 		input key in by users
	 * @return task details key in by users without command word "add"
	 */
	private static String removeAddWord(String details) {
		details = details.substring(ADD_TEXT_STARTING_INDEX);
		return details;
	}
	
	/**
	 * Method checks if task to be added is a valid
	 * @param task
	 * 		checks if task details length is more than 1 character
	 * @return true if is it valid, false if it is invalid
	 */
	private static boolean isValidAddTask(String task) {
		if (task.length() >= 1) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Method adds task details into taskList and show users that the keyed in task has been added
	 * @param task 
	 * 		input key in by users
	 */
	private static void addTask(String task) {
		taskList.addLast(task);
		showToUser(String.format(MESSAGE_ADDED, filename, task));
	}
	
	/**
	 * 
	 * @param task
	 */
	private static void processDeleteTask(String task) {
		task = removeDeleteWord(task);
		if (isInteger(task) && 
			Integer.parseInt(task) > 0 &&
			Integer.parseInt(task) <= taskList.size()) {
			int taskNumber = Integer.parseInt(task);
			deleteTask(taskNumber);
		} else {
			throw new Error(MESSAGE_DELETE_ERROR);
		}
	}
	
	/**
	 * 
	 * @param details
	 * @return
	 */
	private static String removeDeleteWord(String details) {
		details = details.substring(DELETE_TEXT_STARTING_INDEX);
		return details;
	}
	
	/**
	 * 
	 * @param taskNumber
	 */
	private static void deleteTask(int taskNumber) {
		String removedTask = taskList.get(taskNumber - 1);
		taskList.remove(taskNumber - 1);
		showToUser(String.format(MESSAGE_DELETE_TASK, filename, removedTask));
	}
	
	/**
	 * 
	 */
	private static void display() {
		if (taskList.isEmpty()) {
			showToUser(String.format(MESSAGE_DISPLAY_EMPTY, filename));
		} else {
			String task = null;
			for (int index = 0; index < taskList.size(); index++) {
				task = taskList.get(index);
				showToUser(String.format(MESSAGE_DISPLAY_ITEMS, index + 1, task));
			}
		}
	}

	/**
	 * 
	 */
	private static void clear() {
		taskList = new LinkedList<String>();
		showToUser(String.format(MESSAGE_CLEARED, filename));
	}
	
	/**
	 * 
	 */
	private static void exit() {
		save();
		sc.close();
		System.exit(1);
	}
	
	/**
	 * 
	 */
	private static void save() {
		if (fileExists(fileReference)) {
			saveTempFile(fileReference);
		} else {
			createNewFile(fileReference);
		}

	}
	
	/**
	 * 
	 */
	private static void sort() {
		 String[] taskListArray = taskList.toArray(new String[taskList.size()]);
		 Arrays.sort(taskListArray);
		 taskList = new LinkedList<String>();
		 for (String task : taskListArray) {
			 taskList.add(task);
		 }
	}
	
	/**
	 * 
	 * @param word
	 * @return
	 */
	private static String search(String word) {
		LinkedList<String> searchedTasks = new LinkedList<String>();
		for (int index = 0; index < taskList.size(); index++) {
			int intIndex = taskList.get(index).indexOf(word);
			if(intIndex == - 1){
				//System.out.println("Hello not found");
			}else{
				showToUser(taskList.get(index));
				
			}
		}
		return "";
	}
	
	/**
	 * 
	 * @param givenFile
	 * @return
	 */
	private static boolean fileExists(File givenFile) {
		if (givenFile.isFile()) {
			return true;
		} else {
			return false;
		}
	}
	
	// create a temp file to save all information
	// followed by deleting original file and
	// renaming temp file to original file
	/**
	 * 
	 * @param existingFile
	 */
	private static void saveTempFile(File existingFile) {
		try {
			File tempFile = createTempFile();
			BufferedWriter pw = new BufferedWriter(new FileWriter(tempFile));
			for (int i = 0; i < taskList.size(); i++) {
				pw.write(i + 1 + ". " + taskList.get(i));
				pw.newLine();
			}
			pw.flush();
			pw.close();

			if (!existingFile.delete()) {
				showToUser(MESSGAE_FILE_UNABLE_TO_DELETE);
			}
	
			if (!tempFile.renameTo(existingFile)) {
				showToUser(MESSAGE_FILE_UNABLE_TO_RENAME);
			}

		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
		
	// save all information into a new file
	/**
	 * 
	 * @param filename
	 */
	private static void createNewFile(File filename) {
		try {
			BufferedWriter pw = new BufferedWriter(new FileWriter(filename));
			for (int i = 0; i < taskList.size(); i++) {
				pw.write(i + 1 + ". " + taskList.get(i));
				pw.newLine();
			}
			pw.flush();
			pw.close();

		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @return
	 */
	private static File createTempFile() {
		File tempFile = new File(fileReference.getAbsolutePath() + ".tmp");
		return tempFile;
	}
	
	/**
	 * 
	 * @param details
	 * @return
	 */
	private static String getFirstWord(String details) {
		String[] parts = details.split(" ");
		String userCommand = parts[0];
		return userCommand;
	}
	
	/**
	 * 
	 * @param toShow
	 */
	private static void showToUser(String toShow) {
		System.out.print(toShow);
	}
	
	/**
	 * 
	 * @param input
	 * @return
	 */
	private static boolean isInteger( String input ) {
	    try {
	        Integer.parseInt(input);
	        return true;
	    } catch( Exception e ) {
	        return false;
	    }
	}
	
	// get file name from string array
	/**
	 * 
	 * @param str
	 * @return
	 */
	private static String getFileName(String[] str) {
		StringBuilder name = new StringBuilder();
		for (int i = 0; i < str.length; i++) {
			name.append(str[i]);
		}
		return name.toString();
	}
	
	// uses BufferReader to read given file
	/**
	 * 
	 */
	private static void loadDataFromFile() {
		try {
			BufferedReader br = new BufferedReader(new FileReader(filename));
			String line = null;
			
			while ((line = br.readLine()) != null) {
				updateTaskList(line);
			}
			br.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// add previous text in file into temporary file's list of text
	/**
	 * Method updates file by adding previous text in file into this file's taskList
	 * @param line
	 *		lines read in by bufferReader. In the sequence of "index. task"
	 */
	private static void updateTaskList(String line) {
		int textStartingPoint = line.indexOf(".");
		line = line.substring(textStartingPoint + 2);
		taskList.add(line);
	}
}