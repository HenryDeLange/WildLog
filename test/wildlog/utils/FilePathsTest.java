package wildlog.utils;


import org.junit.Test;
import static org.junit.Assert.*;


public class FilePathsTest {
    
    @Test
    public void testPaths1() {
        String inPrefix = null;
        FilePaths.setWorkspacePrefix(inPrefix);
        String fullPathResult = FilePaths.WILDLOG_EXPORT_HTML.getFullPath();
        assertEquals("C:\\WildLog\\Export\\HTML\\", fullPathResult);
        String relativePathResult = FilePaths.WILDLOG_EXPORT_HTML.getRelativePath();
        assertEquals("\\WildLog\\Export\\HTML\\", relativePathResult);
    }
    
    @Test
    public void testPaths2() {
        String inPrefix = "\\";
        FilePaths.setWorkspacePrefix(inPrefix);
        String fullPathResult = FilePaths.WILDLOG_EXPORT_HTML.getFullPath();
        assertEquals("C:\\WildLog\\Export\\HTML\\", fullPathResult);
        String relativePathResult = FilePaths.WILDLOG_EXPORT_HTML.getRelativePath();
        assertEquals("\\WildLog\\Export\\HTML\\", relativePathResult);
    }
    
    @Test
    public void testPaths3() {        
        String inPrefix = "c:\\";
        FilePaths.setWorkspacePrefix(inPrefix);
        String fullPathResult = FilePaths.WILDLOG_EXPORT_HTML.getFullPath();
        assertEquals("c:\\WildLog\\Export\\HTML\\", fullPathResult);
        String relativePathResult = FilePaths.WILDLOG_EXPORT_HTML.getRelativePath();
        assertEquals("\\WildLog\\Export\\HTML\\", relativePathResult);
    }
    
    @Test
    public void testPaths4() {
        String inPrefix = "SomeWorkspace";
        FilePaths.setWorkspacePrefix(inPrefix);
        String fullPathResult = FilePaths.WILDLOG_EXPORT_HTML.getFullPath();
        assertEquals("C:\\SomeWorkspace\\WildLog\\Export\\HTML\\", fullPathResult);
        String relativePathResult = FilePaths.WILDLOG_EXPORT_HTML.getRelativePath();
        assertEquals("\\WildLog\\Export\\HTML\\", relativePathResult);
    }
    
    @Test
    public void testPaths5() {
        String inPrefix = "C:\\SomeWorkspace";
        FilePaths.setWorkspacePrefix(inPrefix);
        String fullPathResult = FilePaths.WILDLOG_EXPORT_HTML.getFullPath();
        assertEquals("C:\\SomeWorkspace\\WildLog\\Export\\HTML\\", fullPathResult);
        String relativePathResult = FilePaths.WILDLOG_EXPORT_HTML.getRelativePath();
        assertEquals("\\WildLog\\Export\\HTML\\", relativePathResult);
    }
    
    @Test
    public void testPaths6() {
        String inPrefix = "WildLog";
        FilePaths.setWorkspacePrefix(inPrefix);
        String fullPathResult = FilePaths.WILDLOG_EXPORT_HTML.getFullPath();
        assertEquals("C:\\WildLog\\WildLog\\Export\\HTML\\", fullPathResult);
        String relativePathResult = FilePaths.WILDLOG_EXPORT_HTML.getRelativePath();
        assertEquals("\\WildLog\\Export\\HTML\\", relativePathResult);
    }
    
    @Test
    public void testPaths7() {
        String inPrefix = "WildLog\\";
        FilePaths.setWorkspacePrefix(inPrefix);
        String fullPathResult = FilePaths.WILDLOG_EXPORT_HTML.getFullPath();
        assertEquals("C:\\WildLog\\WildLog\\Export\\HTML\\", fullPathResult);
        String relativePathResult = FilePaths.WILDLOG_EXPORT_HTML.getRelativePath();
        assertEquals("\\WildLog\\Export\\HTML\\", relativePathResult);
    }
    
    @Test
    public void testPaths8() {
        String inPrefix = "C:\\WildLog";
        FilePaths.setWorkspacePrefix(inPrefix);
        String fullPathResult = FilePaths.WILDLOG_EXPORT_HTML.getFullPath();
        assertEquals("C:\\WildLog\\WildLog\\Export\\HTML\\", fullPathResult);
        String relativePathResult = FilePaths.WILDLOG_EXPORT_HTML.getRelativePath();
        assertEquals("\\WildLog\\Export\\HTML\\", relativePathResult);
    }
    
    @Test
    public void testPaths9() {
        String inPrefix = "\\WildLog\\SomeWorkspace\\";
        FilePaths.setWorkspacePrefix(inPrefix);
        String fullPathResult = FilePaths.WILDLOG_EXPORT_HTML.getFullPath();
        assertEquals("C:\\WildLog\\SomeWorkspace\\WildLog\\Export\\HTML\\", fullPathResult);
        String relativePathResult = FilePaths.WILDLOG_EXPORT_HTML.getRelativePath();
        assertEquals("\\WildLog\\Export\\HTML\\", relativePathResult);
    }
    
    @Test
    public void testPaths10() {
        String inPrefix = "\\WildLog\\";
        FilePaths.setWorkspacePrefix(inPrefix);
        String fullPathResult = FilePaths.WILDLOG_EXPORT_HTML.getFullPath();
        assertEquals("C:\\WildLog\\WildLog\\Export\\HTML\\", fullPathResult);
        String relativePathResult = FilePaths.WILDLOG_EXPORT_HTML.getRelativePath();
        assertEquals("\\WildLog\\Export\\HTML\\", relativePathResult);
    }
    
    @Test
    public void testConcatPathNulls() {
        String path1 = null;
        String path2 = null;
        String expectedResult = null;
        String result = FilePaths.concatPaths(path1, path2);
        assertEquals(expectedResult, result);
        // Also test
        path1 = "123";
        path2 = null;
        expectedResult = null;
        result = FilePaths.concatPaths(path1, path2);
        assertEquals(expectedResult, result);
        // Also test
        path1 = null;
        path2 = "321";
        expectedResult = null;
        result = FilePaths.concatPaths(path1, path2);
        assertEquals(expectedResult, result);
    }
    
    @Test
    public void testConcatPathEmpty() {
        String path1 = "";
        String path2 = "123";
        String expectedResult = "123";
        String result = FilePaths.concatPaths(path1, path2);
        assertEquals(expectedResult, result);
        // Also test
        path1 = "321";
        path2 = "";
        expectedResult = "321";
        result = FilePaths.concatPaths(path1, path2);
        assertEquals(expectedResult, result);
        // Also test
        path1 = "";
        path2 = "";
        expectedResult = "";
        result = FilePaths.concatPaths(path1, path2);
        assertEquals(expectedResult, result);
    }
    
    @Test
    public void testConcatPathNormal() {
        String path1 = "123";
        String path2 = "321";
        String expectedResult = "123\\321";
        String result = FilePaths.concatPaths(path1, path2);
        assertEquals(expectedResult, result);
        // Also test
        path1 = "\\321";
        path2 = "123\\";
        expectedResult = "\\321\\123\\";
        result = FilePaths.concatPaths(path1, path2);
        assertEquals(expectedResult, result);
        // Also test
        path1 = "\\";
        path2 = "\\";
        expectedResult = "\\";
        result = FilePaths.concatPaths(path1, path2);
        assertEquals(expectedResult, result);
        // Also test
        path1 = "\\123";
        path2 = "\\";
        expectedResult = "\\123\\";
        result = FilePaths.concatPaths(path1, path2);
        assertEquals(expectedResult, result);
        // Also test
        path1 = "\\";
        path2 = "123\\";
        expectedResult = "\\123\\";
        result = FilePaths.concatPaths(path1, path2);
        assertEquals(expectedResult, result);
        // Also test
        path1 = "123\\";
        path2 = "\\";
        expectedResult = "123\\";
        result = FilePaths.concatPaths(path1, path2);
        assertEquals(expectedResult, result);
        // Also test
        path1 = "\\123\\";
        path2 = "\\321\\";
        expectedResult = "\\123\\321\\";
        result = FilePaths.concatPaths(path1, path2);
        assertEquals(expectedResult, result);
        // Also test
        path1 = "C:\\123\\";
        path2 = "\\test\\more\\";
        expectedResult = "C:\\123\\test\\more\\";
        result = FilePaths.concatPaths(path1, path2);
        assertEquals(expectedResult, result);
    }

}
