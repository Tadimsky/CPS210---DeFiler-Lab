package common;

public class Constants {
	public static final int NUM_OF_BLOCKS = 16384; // 2^14
	public static final int BLOCK_SIZE = 1024; // 1kB
	public static final int MAX_FILES = 512; // 2^9
	public static final int MAX_FILE_BLOCKS = 64; // 2^6 

	public enum DiskOperationType {
		READ, WRITE
	};
	
	public  enum DBufferState {
		CLEAN, DIRTY
	};

	public static final String vdiskName = "DSTORE.dat";
}
