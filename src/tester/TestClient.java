package tester;

import java.util.ArrayList;
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

    private void WriteLong (DFileID f) {
        byte[] data = new byte[2048];
        for (int i = 0; i < 2048; i++) {
            data[i] = (byte) ('a' + (i % 26));
        }
        dfiler.write(f, data, 0, 2048);
    }

    private String ReadLong (DFileID f) {
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

    private void extTest () {
        Print("Started", "Running");

        Print("Write INITIAL", "Concurrent " + conc.get_dFID());
        WriteTest(conc, "INTIAL");
        Print("Read Concurrent", ReadTest(conc));
        Print("Write INITIALS", "Concurrent " + conc.get_dFID());
        WriteTest(conc, "INTIALS");

        DFileID nf = dfiler.createDFile();

        Print("Created DFile", Integer.toString(nf.get_dFID()));
        Print("Writing", "Test Two");
        WriteTest(nf, "TEST TWO");
        Print("Read", ReadTest(nf));

        WriteTest(nf, "TEST PART");
        Print("Read", ReadTestPartial(nf, 5, 4)); // Should be TEST TEST

        // Test concurrent access 3 times
        for (int i = 0; i < 3; i++) {
            Print("Write", "Concurrent " + i);
            WriteTest(conc, "SHUT DOWN " + clientID + "" + i);
            Print("Read Concurrent " + i, ReadTest(conc));
        }

        WriteLong(nf);
        Print("Read Long", ReadLong(nf));

        WriteLong(conc);
        Print("Read Long Concurrent", ReadLong(conc));
    }

    private void concTest () {
        DFileID file = new DFileID(clientID);
        // DFileID file = dfiler.createDFile();
        WriteTest(file, "CLIENT " + clientID + 1);

        Print("Read", ReadTest(file));

    }

    @Override
    public void run () {
        // concTest();
        extTest();
        dfiler.sync();
    }

    public static void main (String[] args) throws Exception {
        System.out.println("Initializing DFS");
        DFS dfiler = new DFS();
        dfiler.init();
        dfiler.createDFile();
        System.out.println("Initialized");
        // DFileID file = dfiler.createDFile();
        DFileID file = new DFileID(4);

        ArrayList<Thread> clients = new ArrayList<Thread>();
        // Run NUM_WORKERS threads
        for (int i = 0; i < NUM_WORKERS; i++) {
            TestClient tc = new TestClient(dfiler, file, i);
            Thread f = new Thread(tc);
            clients.add(f);
            f.start();
        }
        // Sync files to disk
        for (Thread tc : clients) {
            tc.join();
        }
        System.out.println("SHUTTING DOWN");
        dfiler.shutdown();
    }
}
