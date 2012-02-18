package wildlog.utils.ui;


import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Henry
 */
public class UtilsTest {

    @Test
    public void testStripRootFromPath() {
        String inPath = "123456789";
        String inRoot = "123";
        String expResult = "456789";
        String result = Utils.stripRootFromPath(inPath, inRoot);
        assertEquals(expResult, result);
        // Also test
        inPath = "123456789";
        inRoot = "";
        expResult = "123456789";
        result = Utils.stripRootFromPath(inPath, inRoot);
        assertEquals(expResult, result);
    }
    
}
