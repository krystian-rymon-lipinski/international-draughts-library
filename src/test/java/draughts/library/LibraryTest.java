/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package draughts.library;

import org.junit.Test;
import org.mockito.Mock;

import static org.junit.Assert.*;

public class LibraryTest {
	
	@Mock
	Library library;
    @Test 
    public void testSomeLibraryMethod() {
        Library classUnderTest = new Library();
        assertTrue("someLibraryMethod should return 'true'", classUnderTest.someLibraryMethod());
    }
}
