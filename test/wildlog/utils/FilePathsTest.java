package wildlog.utils;


import org.junit.Test;
import static org.junit.Assert.*;


public class FilePathsTest {
    // WARNING: Some of these tests are dependant on what files are on the file system. To work correctly C:\WildLog\Data must already exist.

    @Test
    public void testPaths1() {
        String inPrefix = null;
        WildLogPaths.setWorkspacePrefix(inPrefix);
        String fullPathResult = WildLogPaths.WILDLOG_EXPORT_HTML.getFullPath();
        assertEquals("C:\\WildLog\\Export\\HTML\\", fullPathResult);
        String relativePathResult = WildLogPaths.WILDLOG_EXPORT_HTML.getRelativePath();
        assertEquals("\\WildLog\\Export\\HTML\\", relativePathResult);
    }

    @Test
    public void testPaths2() {
        String inPrefix = "\\";
        WildLogPaths.setWorkspacePrefix(inPrefix);
        String fullPathResult = WildLogPaths.WILDLOG_EXPORT_HTML.getFullPath();
        assertEquals("C:\\WildLog\\Export\\HTML\\", fullPathResult);
        String relativePathResult = WildLogPaths.WILDLOG_EXPORT_HTML.getRelativePath();
        assertEquals("\\WildLog\\Export\\HTML\\", relativePathResult);
    }

    @Test
    public void testPaths3() {
        String inPrefix = "c:\\";
        WildLogPaths.setWorkspacePrefix(inPrefix);
        String fullPathResult = WildLogPaths.WILDLOG_EXPORT_HTML.getFullPath();
        assertEquals("c:\\WildLog\\Export\\HTML\\", fullPathResult);
        String relativePathResult = WildLogPaths.WILDLOG_EXPORT_HTML.getRelativePath();
        assertEquals("\\WildLog\\Export\\HTML\\", relativePathResult);
    }

    @Test
    public void testPaths4() {
        String inPrefix = "SomeWorkspace";
        WildLogPaths.setWorkspacePrefix(inPrefix);
        String fullPathResult = WildLogPaths.WILDLOG_EXPORT_HTML.getFullPath();
        assertEquals("C:\\SomeWorkspace\\WildLog\\Export\\HTML\\", fullPathResult);
        String relativePathResult = WildLogPaths.WILDLOG_EXPORT_HTML.getRelativePath();
        assertEquals("\\WildLog\\Export\\HTML\\", relativePathResult);
    }

    @Test
    public void testPaths5() {
        String inPrefix = "C:\\SomeWorkspace";
        WildLogPaths.setWorkspacePrefix(inPrefix);
        String fullPathResult = WildLogPaths.WILDLOG_EXPORT_HTML.getFullPath();
        assertEquals("C:\\SomeWorkspace\\WildLog\\Export\\HTML\\", fullPathResult);
        String relativePathResult = WildLogPaths.WILDLOG_EXPORT_HTML.getRelativePath();
        assertEquals("\\WildLog\\Export\\HTML\\", relativePathResult);
    }

    @Test
    public void testPaths6() {
        String inPrefix = "WildLog";
        WildLogPaths.setWorkspacePrefix(inPrefix);
        String fullPathResult = WildLogPaths.WILDLOG_EXPORT_HTML.getFullPath();
        assertEquals("C:\\WildLog\\Export\\HTML\\", fullPathResult);
        String relativePathResult = WildLogPaths.WILDLOG_EXPORT_HTML.getRelativePath();
        assertEquals("\\WildLog\\Export\\HTML\\", relativePathResult);
    }

    @Test
    public void testPaths7() {
        String inPrefix = "WildLog\\";
        WildLogPaths.setWorkspacePrefix(inPrefix);
        String fullPathResult = WildLogPaths.WILDLOG_EXPORT_HTML.getFullPath();
        assertEquals("C:\\WildLog\\Export\\HTML\\", fullPathResult);
        String relativePathResult = WildLogPaths.WILDLOG_EXPORT_HTML.getRelativePath();
        assertEquals("\\WildLog\\Export\\HTML\\", relativePathResult);
    }

    @Test
    public void testPaths8() {
        String inPrefix = "C:\\WildLog";
        WildLogPaths.setWorkspacePrefix(inPrefix);
        String fullPathResult = WildLogPaths.WILDLOG_EXPORT_HTML.getFullPath();
        assertEquals("C:\\WildLog\\Export\\HTML\\", fullPathResult);
        String relativePathResult = WildLogPaths.WILDLOG_EXPORT_HTML.getRelativePath();
        assertEquals("\\WildLog\\Export\\HTML\\", relativePathResult);
    }

    @Test
    public void testPaths9() {
        String inPrefix = "\\WildLog\\SomeWorkspace\\";
        WildLogPaths.setWorkspacePrefix(inPrefix);
        String fullPathResult = WildLogPaths.WILDLOG_EXPORT_HTML.getFullPath();
        assertEquals("C:\\WildLog\\SomeWorkspace\\WildLog\\Export\\HTML\\", fullPathResult);
        String relativePathResult = WildLogPaths.WILDLOG_EXPORT_HTML.getRelativePath();
        assertEquals("\\WildLog\\Export\\HTML\\", relativePathResult);
    }

    @Test
    public void testPaths10() {
        String inPrefix = "\\WildLog\\";
        WildLogPaths.setWorkspacePrefix(inPrefix);
        String fullPathResult = WildLogPaths.WILDLOG_EXPORT_HTML.getFullPath();
        assertEquals("C:\\WildLog\\Export\\HTML\\", fullPathResult);
        String relativePathResult = WildLogPaths.WILDLOG_EXPORT_HTML.getRelativePath();
        assertEquals("\\WildLog\\Export\\HTML\\", relativePathResult);
    }

    @Test
    public void testConcatPathNulls() {
        String path1 = null;
        String path2 = null;
        String expectedResult = null;
        String result = WildLogPaths.concatPaths(true, path1, path2);
        assertEquals(expectedResult, result);
        // Also test
        path1 = "123";
        path2 = null;
        expectedResult = null;
        result = WildLogPaths.concatPaths(true, path1, path2);
        assertEquals(expectedResult, result);
        // Also test
        path1 = null;
        path2 = "321";
        expectedResult = null;
        result = WildLogPaths.concatPaths(true, path1, path2);
        assertEquals(expectedResult, result);
    }

    @Test
    public void testConcatPathEmpty() {
        String path1 = "";
        String path2 = "123";
        String expectedResult = "123";
        String result = WildLogPaths.concatPaths(true, path1, path2);
        assertEquals(expectedResult, result);
        // Also test
        path1 = "321";
        path2 = "";
        expectedResult = "321";
        result = WildLogPaths.concatPaths(true, path1, path2);
        assertEquals(expectedResult, result);
        // Also test
        path1 = "";
        path2 = "";
        expectedResult = "";
        result = WildLogPaths.concatPaths(true, path1, path2);
        assertEquals(expectedResult, result);
    }

    @Test
    public void testConcatPathNormal1() {
        String path1 = "123";
        String path2 = "321";
        String expectedResult = "123\\321";
        String result = WildLogPaths.concatPaths(true, path1, path2);
        assertEquals(expectedResult, result);
        // Also test
        path1 = "\\321";
        path2 = "123\\";
        expectedResult = "\\321\\123";
        result = WildLogPaths.concatPaths(true, path1, path2);
        assertEquals(expectedResult, result);
        // Also test
        path1 = "";
        path2 = "";
        expectedResult = "";
        result = WildLogPaths.concatPaths(true, path1, path2);
        assertEquals(expectedResult, result);
        // Also test
        path1 = "\\321";
        path2 = "123\\\\";
        expectedResult = "\\321\\123";
        result = WildLogPaths.concatPaths(true, path1, path2);
        assertEquals(expectedResult, result);
        // Also test
        path1 = "\\321\\\\";
        path2 = "123";
        expectedResult = "\\321\\\\123";
        result = WildLogPaths.concatPaths(true, path1, path2);
        assertEquals(expectedResult, result);
        // Also test
        path1 = "\\";
        path2 = "\\";
        expectedResult = "\\";
        result = WildLogPaths.concatPaths(true, path1, path2);
        assertEquals(expectedResult, result);
        // Also test
        path1 = "\\123";
        path2 = "\\";
        expectedResult = "\\123";
        result = WildLogPaths.concatPaths(true, path1, path2);
        assertEquals(expectedResult, result);
        // Also test
        path1 = "\\\\\\123";
        path2 = "\\\\\\";
        expectedResult = "\\\\\\123";
        result = WildLogPaths.concatPaths(true, path1, path2);
        assertEquals(expectedResult, result);
        // Also test
        path1 = "\\";
        path2 = "123\\";
        expectedResult = "\\123";
        result = WildLogPaths.concatPaths(true, path1, path2);
        assertEquals(expectedResult, result);
        // Also test
        path1 = "123\\";
        path2 = "\\";
        expectedResult = "123";
        result = WildLogPaths.concatPaths(true, path1, path2);
        assertEquals(expectedResult, result);
        // Also test
        path1 = "\\123\\";
        path2 = "\\321\\";
        expectedResult = "\\123\\321";
        result = WildLogPaths.concatPaths(true, path1, path2);
        assertEquals(expectedResult, result);
        // Also test
        path1 = "C:\\123\\";
        path2 = "\\test\\more\\";
        expectedResult = "C:\\123\\test\\more";
        result = WildLogPaths.concatPaths(true, path1, path2);
        assertEquals(expectedResult, result);
    }

    @Test
    public void testConcatPathNormal2() {
        String path1 = "123";
        String path2 = "321";
        String expectedResult = "123\\321";
        String result = WildLogPaths.concatPaths(false, path1, path2);
        assertEquals(expectedResult, result);
        // Also test
        path1 = "\\321";
        path2 = "123\\";
        expectedResult = "\\321\\123\\";
        result = WildLogPaths.concatPaths(false, path1, path2);
        assertEquals(expectedResult, result);
        // Also test
        path1 = "";
        path2 = "";
        expectedResult = "";
        result = WildLogPaths.concatPaths(false, path1, path2);
        assertEquals(expectedResult, result);
        // Also test
        path1 = "\\321";
        path2 = "123\\\\";
        expectedResult = "\\321\\123\\\\";
        result = WildLogPaths.concatPaths(false, path1, path2);
        assertEquals(expectedResult, result);
        // Also test
        path1 = "\\321\\\\";
        path2 = "123";
        expectedResult = "\\321\\\\123";
        result = WildLogPaths.concatPaths(false, path1, path2);
        assertEquals(expectedResult, result);
        // Also test
        path1 = "\\";
        path2 = "\\";
        expectedResult = "\\";
        result = WildLogPaths.concatPaths(false, path1, path2);
        assertEquals(expectedResult, result);
        // Also test
        path1 = "\\123";
        path2 = "\\";
        expectedResult = "\\123\\";
        result = WildLogPaths.concatPaths(false, path1, path2);
        assertEquals(expectedResult, result);
        // Also test
        path1 = "\\\\\\123";
        path2 = "\\\\\\";
        expectedResult = "\\\\\\123\\\\\\";
        result = WildLogPaths.concatPaths(false, path1, path2);
        assertEquals(expectedResult, result);
        // Also test
        path1 = "\\";
        path2 = "123\\";
        expectedResult = "\\123\\";
        result = WildLogPaths.concatPaths(false, path1, path2);
        assertEquals(expectedResult, result);
        // Also test
        path1 = "123\\";
        path2 = "\\";
        expectedResult = "123\\";
        result = WildLogPaths.concatPaths(false, path1, path2);
        assertEquals(expectedResult, result);
        // Also test
        path1 = "\\123\\";
        path2 = "\\321\\";
        expectedResult = "\\123\\321\\";
        result = WildLogPaths.concatPaths(false, path1, path2);
        assertEquals(expectedResult, result);
        // Also test
        path1 = "C:\\123\\";
        path2 = "\\test\\more\\";
        expectedResult = "C:\\123\\test\\more\\";
        result = WildLogPaths.concatPaths(false, path1, path2);
        assertEquals(expectedResult, result);
    }

    @Test
    public void testStripRootFromPath() {
        String inPath = "123456789";
        String inRoot = "123";
        String expResult = "456789";
        String result = WildLogPaths.stripRootFromPath(inPath, inRoot);
        assertEquals(expResult, result);
        // Also test
        inPath = "123456789";
        inRoot = "";
        expResult = "123456789";
        result = WildLogPaths.stripRootFromPath(inPath, inRoot);
        assertEquals(expResult, result);
    }

}
