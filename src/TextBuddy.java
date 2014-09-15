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
	private static String MESSAGE_SEARCH_CONTAINING_WORD = "%d. %s \n";
	private static String MESSGAE_SEARCH_NOT_CONTAINING_WORD = "No items contain word searched \n";
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
	private static int SEARCH_TEXT_STARTING_INDEX = 7;
	
	// These is used to check if word exists in line
	private static int SEARCH_TEXT_DO_NOT_EXIST = -1;

	// These are the possible command types
	private enum Command {
		ADD, DISPLAY, DELETE, CLEAR, EXIT, INVALID, SORT, SEARCH
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
	 * @param commandType The command word entered in by the user
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
		} else if (commandType.equals("sort")) {
			return Command.SORT;
		} else if (commandType.equals("search")) {
			return Command.SEARCH;
		} else {
			return Command.INVALID;
		}
	}

	/**
	 *  A method to execute a command based on the command type. This method 
	 * calls method determineCommandType to handle each of the command type.
	 * 
	 * @param details Task details keyed in by users
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
				
			case SORT:
				sort();
				break;
				
			case SEARCH:
				processSearchWord(details);
				break;
				
			default:
				showToUser(MESSGAE_INVALID_COMMAND);
		}
	}
	
	/**
	 * Method to add an item to the list.
	 * The method lists down the procedures taken to add an item to the list.
	 * 
	 * @param task The add command and details of what to be added that are key in by users
	 */
	private static void processAddTask(String task) {
		task = task.substring(ADD_TEXT_STARTING_INDEX);
		if (isValidAddTask(task)) {
			taskList.addLast(task);
			showToUser(String.format(MESSAGE_ADDED, filename, task));
		} else {
			throw new Error(MESSAGE_ADDED_ERROR);
		}	
	}
	
	/**
	 * Method checks if task to be added is a valid
	 * @param task checks if task details length is more than 1 character
	 * @return true if is it valid task, false otherwise
	 */
	private static boolean isValidAddTask(String task) {
		if (task.length() >= 1) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Method deletes an item from the list, taskList.
	 * The method lists down the procedures taken to delete an item on the list.
	 * @param task string primitive number. The task number in taskList to be deleted.
	 */
	private static void processDeleteTask(String task) {
		task = task.substring(DELETE_TEXT_STARTING_INDEX);
		if (isValidDeleteTask(task)) {
			int taskNumber = Integer.parseInt(task);
			String removedTask = taskList.get(taskNumber - 1);
			taskList.remove(taskNumber - 1);
			showToUser(String.format(MESSAGE_DELETE_TASK, filename, removedTask));
		} else {
			throw new Error(MESSAGE_DELETE_ERROR);
		}
	}
	
	/**
	 * Method checks whether task is a valid task
	 * @param task a string primitive number
	 * @return true if it is a valid task existing in list, false otherwise
	 */
	private static boolean isValidDeleteTask(String task) {
		if (isInteger(task) && 
			Integer.parseInt(task) > 0 &&
			Integer.parseInt(task) <= taskList.size()) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Method displays all tasks in taskList in a listed order
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
	 * Method clears taskList
	 */
	private static void clear() {
		taskList = new LinkedList<String>();
		showToUser(String.format(MESSAGE_CLEARED, filename));
	}
	
	/**
	 * Method exits programme
	 */
	private static void exit() {
		save();
		sc.close();
		System.exit(1);
	}
	
	/**
	 * Method saves information into a file
	 */
	private static void save() {
		if (fileExists(fileReference)) {
			saveTempFile(fileReference);
		} else {
			createNewFile(fileReference);
		}

	}
	
	/**
	 * Method sorts tasks keyed in by user alphabetically
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
	 * Method displays the lines containing the word searched
	 * @param details input keyed in by users on what word to search
	 */
	private static void processSearchWord(String details) {
		LinkedList <String> searchedTasks = search(details);
		if (searchedTasks.size() == 0) {
			showToUser(MESSGAE_SEARCH_NOT_CONTAINING_WORD);
			
		} else {
			for (int index = 0; index < searchedTasks.size(); index++) {
				showToUser(String.format(MESSAGE_SEARCH_CONTAINING_WORD, 
								index + 1, searchedTasks.get(index)));
			}
		}
	}
	
	/**
	 * Method searches for word in the file and stores all lines 
	 * containing the word to be searched in a linkedList.
	 * @param details input keyed in by users on what word to search
	 * @return A linkedList with lines containing the searched word
	 */
	private static LinkedList<String> search(String details) {
		String wordToSearch = details.substring(SEARCH_TEXT_STARTING_INDEX);
		LinkedList <String> searchedTasks = new LinkedList<String>();
		
		if (validSearchWord(wordToSearch)) {
			for (int index = 0; index < taskList.size(); index++) {
				int intIndex = taskList.get(index).indexOf(wordToSearch); //naming issue
				if(intIndex == SEARCH_TEXT_DO_NOT_EXIST){ // can refactor
					
				}else{
					searchedTasks.add(taskList.get(index));
				}
			}
		}
		return searchedTasks;
	}
	
	/**
	 * Method checks whether input word to be search is a valid word, or a word to begin with
	 * @param wordToSearch a word to be searched
	 * @return true if word is a valid word, false otherwise
	 */
	private static boolean validSearchWord(String wordToSearch) {
		if (wordToSearch != null && 
			wordToSearch.length() > 0 &&
			!wordToSearch.contains(" ")) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Method checks if file exists
	 * @param givenFile file to be checked for existance
	 * @return true if file exists, false otherwise
	 */
	private static boolean fileExists(File givenFile) {
		if (givenFile.isFile()) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Method saves all information into the file provided
	 * @param existingFile file to save all tasks in
	 */
	private static void saveTempFile(File existingFile) {
		try {
			File tempFile = new File(fileReference.getAbsolutePath() + ".tmp");
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
		
	/**
	 * Creates a new file with the filename provided. 
	 * File will save all current task in taskList.
	 * @param filename name of file
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
	 * Method retrieves the first word in the line keyed in by user
	 * @param details the line keyed in by user
	 * @return the first word in the line keyed in by user
	 */
	private static String getFirstWord(String details) {
		String[] parts = details.split(" ");
		String userCommand = parts[0];
		return userCommand;
	}
	
	/**
	 * Method shows user messages
	 * @param toShow message to be printed in command line
	 */
	private static void showToUser(String toShow) {
		System.out.print(toShow);
	}
	
	/**
	 * Method checks whether input parsed in is an integer
	 * @param input a string primitive number
	 * @return true if input is an integer, false otherwise
	 */
	private static boolean isInteger( String input ) {
	    try {
	        Integer.parseInt(input);
	        return true;
	    } catch( Exception e ) {
	        return false;
	    }
	}
	
	/**
	 * Method retrieves name of file parsed into argument
	 * @param str string array of name of file parsed into argument
	 * @return name of file parsed into argument
	 */
	private static String getFileName(String[] str) {
		StringBuilder name = new StringBuilder();
		for (int i = 0; i < str.length; i++) {
			name.append(str[i]);
		}
		return name.toString();
	}
	
	/**
	 * Method reads existing data from file and adds them into taskList
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
	
	/**
	 * Method updates file by adding text parsed into this file's taskList
	 * @param line read in by bufferRead in the sequence of "index. task"
	 */
	private static void updateTaskList(String line) {
		int textStartingPoint = line.indexOf(".");
		line = line.substring(textStartingPoint + 2);
		taskList.add(line);
	}
}