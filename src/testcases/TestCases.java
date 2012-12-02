package testcases;

import java.util.ArrayList;
import org.junit.Test;
import dfs.DFS;
import dfs.DFile;
import dfs.DFileID;
import junit.framework.TestCase;

public class TestCases extends TestCase {
    DFS d = new DFS();
    
    @Test
    public void testFileCreation() {
        d.init();
        assertEquals(d.createDFile().get_dFID(), 0);
        assertEquals(d.createDFile().get_dFID(), 1);
        d.destroyFile(new DFileID(0));
        ArrayList<DFile> files = new ArrayList<DFile>();
        files.add(new DFile(new DFileID(1)));
        assertEquals(d.listAllDFiles(), files);
    }
    
    @Test
    public void testReadWrite() {
        d.init();
        d.createDFile();
        byte[] ubuffer = new byte[10];
        ubuffer[0] = 4;
        ubuffer[1] = 7;
        ubuffer[2] = 6;
        d.write(new DFileID(0), ubuffer, 0, 3);
        ubuffer[0] = 0;
        ubuffer[1] = 0;
        ubuffer[2] = 0;
        d.read(new DFileID(0), ubuffer, 0, 3);
        assertEquals(ubuffer[0], 4);
        assertEquals(ubuffer[1], 7);
        assertEquals(ubuffer[2], 6);
    }
}
