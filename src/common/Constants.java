package common;

public class Constants {
	public static final int NUM_OF_BLOCKS = 32768; // 2^15
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
	
	/**
	 * Returns the number of blocks that are needed to store the given file size
	 * @param size The size of the file
	 * @return The number of blocks required to store a file of size 
	 */
	public static int BlocksRequired(int size)
	{
		return (int)Math.ceil((double)size / (double)Constants.BLOCK_SIZE);
	}
}
