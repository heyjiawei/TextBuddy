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
	
	@Test // test whether parsed index of deleted task is valid
	public void testIsValidDeleteTask() {
		String ADDED_TASK = "add William will-i-am";
		TextBuddy.processAddTask(ADDED_TASK);
		TextBuddy.processAddTask(ADDED_TASK);
		TextBuddy.processAddTask(ADDED_TASK);
		String DELETE_TASK = "3";
		assertEquals(TextBuddy.isValidDeleteTask(DELETE_TASK), true);
	}
	
	@Test // test whether search function works
	public void testSearch() {
		String ADDED_TASK = "add William will-i-am";
		TextBuddy.processAddTask(ADDED_TASK);
		TextBuddy.processAddTask(ADDED_TASK);
		TextBuddy.processAddTask(ADDED_TASK);
		assertEquals(TextBuddy.validSearchWord("am"), true);
	}
	
	@Test // test whether getFirtWord works
	public void testGetFirstWord() {
		String ADDED_TASK = "delete blah blah lala";
		assertEquals(TextBuddy.getFirstWord(ADDED_TASK), "delete");
	}
	
	@Test // test whether isInteger works
	public void testIsInteger() {
		assertEquals(TextBuddy.isInteger("234567890"), true);
	}
	
	@Test // test whether getFileName converts filename correctly
	public void testGetFileName() {
		String[] name = {"t","e","s","t","i","n","g"};
		assertEquals(TextBuddy.getFileName(name), "testing");
	}
	
}
