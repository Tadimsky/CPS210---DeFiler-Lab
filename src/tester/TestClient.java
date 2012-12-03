package tester;
import java.awt.print.Printable;
import java.util.Random;

import dfs.DFS;
import dfs.DFile;
import dfs.DFileID;


public class TestClient implements Runnable{
	private static final int NUM_WORKERS = 10;
	
	DFS dfiler;
	DFileID conc;
	int clientID;
	/**
	 * @param args
	 */
	
	public TestClient(DFS d, DFileID c, int client)
	{
		dfiler = d;
		// File to try concurrent access on
		conc = c;
		clientID = client;
	}
	
	private void Print(String op, String mes)
	{
		System.out.println("Client #" + clientID + "\t Op: " + op + "\t \t " + mes);
	}
	
	private void WriteTest(DFileID f, String t)
	{
		byte[] data = t.getBytes();
		dfiler.write(f, data, 0, data.length);
	}
	
	private String ReadTest(DFileID f)
	{
		byte[] read = new byte[100];
		int bytes = dfiler.read(f, read, 0, 100);
		Print("Read bytes", Integer.toString(bytes));
		return new String(read);
	}
	
	private String ReadTestPartial(DFileID f, int index, int count)
	{
		byte[] read = new byte[100];
		int bytes = dfiler.read(f, read, 0, 100);	
		dfiler.read(f, read, index, count);
		Print("Read bytes", Integer.toString(bytes));
		return new String(read);
	}

	@Override
	public void run() {
		WriteTest(conc, "INTIAL");
		Print("Read", ReadTest(conc));		
		synchronized (this) {
			// Wait for a bit
			try {
				wait(new Random().nextInt(1000));
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		}		
		System.out.println(ReadTest(conc));
		DFileID nf = dfiler.createDFile();
		Print("Created DFile", Integer.toString(nf.get_dFID()));		
		WriteTest(nf, "TEST TWO");
		Print("Writing", "Test Two");
		Print("Read", ReadTest(nf));
		
		WriteTest(nf, "TEST THREE");
		Print("Read", ReadTestPartial(nf, 6, 4)); // Should be TEST TEST		
		
		// Test concurrent access 3 times
		for (int i = 0; i < 3; i++)
		{
			Print("Read Concurrent" + i, ReadTest(conc));
			WriteTest(conc, "SHUT DOWN"+ clientID);
			Print("Read Concurrent" + i, ReadTest(conc));
		}
		
		// Sync files to disk
		dfiler.sync();
	}
	
	public static void main(String[] args) 
	{
		
		DFS dfiler = new DFS();
		dfiler.init();
		DFileID file = dfiler.createDFile();
		// Run NUM_WORKERS threads 
		for (int i = 0; i < NUM_WORKERS; i++)
		{
			TestClient tc = new TestClient(dfiler, file, i);
			Thread f = new Thread(tc);
			f.run();
		}
	}
}
