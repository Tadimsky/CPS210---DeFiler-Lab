package tester;

import dfs.DFS;
import dfs.DFileID;


public class TestClient implements Runnable {
    private static final int NUM_WORKERS = 10;

    DFS dfiler;
    DFileID conc;
    int clientID;    

    /**
     * @param args
     */

    public TestClient (DFS d, DFileID c, int client) {
        dfiler = d;
        // File to try concurrent access on
        conc = c;
        clientID = client;
    }

    private void Print (String op, String mes) {
        System.out.println("Client #" + clientID + "\t Op: " + op + "\t \t "
                + mes);
    }

    private void WriteTest (DFileID f, String t) {
        byte[] data = t.getBytes();
        dfiler.write(f, data, 0, data.length);
    }
    
    private void WriteLong(DFileID f)
    {
    	byte[] data = new byte[2048];
    	for (int i = 0; i < 2048; i++)
    	{
    		data[i] = (byte) ('a' + (i%26));
    	}
    	dfiler.write(f, data, 0, 2048);
    }
    
    private String ReadLong(DFileID f)
    {
    	byte[] data = new byte[2048];
    	dfiler.read(f, data, 0, 2048);
    	return new String(data).trim();
    }

    private String ReadTest (DFileID f) {
        byte[] read = new byte[100];
        dfiler.read(f, read, 0, 50);
        // Print("Read bytes", Integer.toString(bytes));
        return new String(read).trim();
    }

    private String ReadTestPartial (DFileID f, int index, int count) {
        byte[] read = new byte[100];
        dfiler.read(f, read, 0, 100);
        dfiler.read(f, read, index, count);
        // Print("Read bytes", Integer.toString(bytes));
        return new String(read).trim();
    }

    @Override
    public void run () {
        Print("Started", "Running");
        WriteTest(conc, "INTIAL");
        Print("Read", ReadTest(conc));
        WriteTest(conc, "INTIALS");
        System.out.println(ReadTest(conc));
        DFileID nf = dfiler.createDFile();
        Print("Created DFile", Integer.toString(nf.get_dFID()));
        Print("Writing", "Test Two");
        WriteTest(nf, "TEST TWO");
        Print("Read", ReadTest(nf));

        WriteTest(nf, "TEST THREE");
        Print("Read", ReadTestPartial(nf, 5, 4)); // Should be TEST TEST

        // Test concurrent access 3 times
        for (int i = 0; i < 3; i++) {
            Print("Read Concurrent" + i, ReadTest(conc));
            WriteTest(conc, "SHUT DOWN " + clientID);
            Print("Read Concurrent " + i, ReadTest(conc));
        }    
        
        WriteLong(nf);
        Print("Read Long", ReadLong(nf));
    }

    public static void main (String[] args) {
        System.out.println("Initializing DFS");
        DFS dfiler = new DFS();
        dfiler.init();
        System.out.println("Initialized");
        DFileID file = dfiler.createDFile();
        // Run NUM_WORKERS threads
        for (int i = 0; i < NUM_WORKERS; i++) {
            TestClient tc = new TestClient(dfiler, file, i);
            Thread f = new Thread(tc);
            f.start();
        }
        // Sync files to disk
        dfiler.sync();
    }
}
