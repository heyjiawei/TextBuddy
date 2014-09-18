import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;


public class TextBuddyTest {
	
	@Test // test whether task added to TextBuddy task list is a valid task
	public void testIsValidAddTask() {
		String ADDED_TASK = "add William will-i-am";
		TextBuddy.processAddTask(ADDED_TASK);
		assertEquals(TextBuddy.isValidAddTask(ADDED_TASK), true);
	}
	
	@Test
	public void testIsValidDeleteTask() {
		String ADDED_TASK = "add William will-i-am";
		TextBuddy.processAddTask(ADDED_TASK);
		TextBuddy.processAddTask(ADDED_TASK);
		TextBuddy.processAddTask(ADDED_TASK);
		String DELETE_TASK = "3";
		assertEquals(TextBuddy.isValidDeleteTask(DELETE_TASK), true);
	}
	
	@Test
	public void testSearch() {
		String ADDED_TASK = "add William will-i-am";
		TextBuddy.processAddTask(ADDED_TASK);
		TextBuddy.processAddTask(ADDED_TASK);
		TextBuddy.processAddTask(ADDED_TASK);
		assertEquals(TextBuddy.validSearchWord("am"), true);
	}
	
	@Test
	public void testGetFirstWord() {
		String ADDED_TASK = "delete blah blah lala";
		assertEquals(TextBuddy.getFirstWord(ADDED_TASK), "delete");
	}
	
	@Test
	public void testIsInteger() {
		assertEquals(TextBuddy.isInteger("234567890"), true);
	}
	
	@Test
	public void testGetFileName() {
		String[] name = {"t","e","s","t","i","n","g"};
		assertEquals(TextBuddy.getFileName(name), "testing");
	}
	
}
