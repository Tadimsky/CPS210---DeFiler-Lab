
public class Constants {
	public static final int NUM_OF_BLOCKS = 16384; // 2^14
	public static final int BLOCK_SIZE = 1024; // 1kB

	public enum DiskOperationType {
		READ, WRITE
	};
	
	public  enum DBufferState {
		CLEAN, DIRTY
	};

	public static final String vdiskName = "DSTORE.dat";
}
