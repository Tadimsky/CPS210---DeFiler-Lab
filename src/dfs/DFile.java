package dfs;
import common.Constants;

public class DFile {
    private int _size;
    private int[] _blockmap;
    private DFileID _dFID;
    
    public DFile(DFileID dFID) {    	
    	// NOTE: Fixed Block Map size
    	_blockmap = new int[Constants.MAX_FILE_BLOCKS];
    	_dFID = dFID;
    }
    
    public DFile(DFileID dFID, int size, int[] map)
    {
    	this(dFID);
    	_blockmap = map;
    	_size = size;
    }
    
    /**
     * Returns the Physical Block number for a given Logical Block Number 
     * @param block The logical block number
     * @return The physical block number which can be used to get the block out of the VirtualDisk
     */
    public int getMappedBlock(int block)
    {
    	if (block < 0 || block >= _blockmap.length)
    		return -1;
    	
    	return _blockmap[block];
    }
    
    /**
     * Maps a Logical Block Number to a specified Physical Block Number
     * @param index The logical block number
     * @param block The physical block number
     * @return true if it was successful
     */
    public boolean MapBlock(int index, int block)
    {
    	if (index< 0 || index >= _blockmap.length)
    		return false;    	
    	
    	_blockmap[index] = block;
    	return true;
    }
    
    /**
     * Returns the number of blocks the DFile is actually using, based on the size of the DFile
     * @return
     */
    public int getNumBlocks()
    {
    	return Constants.BlocksRequired(_size);
    }
    
    /**
     * Returns the change in the number of blocks required if a new file size is set.
     * This will be used by the DFS to check if it should allocate new blocks or free blocks that will no longer be needed.
     * @param newsize The new size that the file will be.
     * @return The change in blocks. A negative number means that there is a reduction in blocks and a positive number means that more blocks are required.
     */
    public int changeinBlocks(int newsize)
    {
    	int oldb = getNumBlocks();
    	int newb = Constants.BlocksRequired(newsize);
    	return newb - oldb;    			
    }
    
    /**
     * Returns the current size of the file
     * @return The size of the file
     */
    public int getSize()
    {
    	return _size;
    }
    
    /**
     * Sets the new size of this file. Removes blocks from the map if they are no longer used. 
     * Make sure that the DFS marks the blocks that will no longer be needed as free.
     * Make sure that the correct number of blocks have been allocated to the DFile
     * @param newsize
     * @throws Exception
     */
    public void setSize(int newsize) throws Exception
    {
    	int oldblocks = getNumBlocks();
    	_size = newsize;
    	
    	// the size has changed, want to make sure we have correct number of blocks    	
    	int newblocks = getNumBlocks();
    	
    	// if the size has decreased, we want to free the rest of the blocks from the map
    	// The DFS should move the blocks to the Free List
    	if (newblocks < oldblocks)
    	{
    		for (int i = newblocks; i < oldblocks; i++)
    		{
    			_blockmap[i] = -1;
    		}
    	}
    	
    	// Ensure that the DFile has the correct number of blocks assigned to it in order to store the information
    	for (int i = 0; i < newblocks; i++)
    	{
    		if (_blockmap[i] == -1)
    			throw new Exception("Not enough blocks allocated. Please ensure you have allocated the correct number of blocks to the DFile (" + newblocks + ").");
    	}
    }    

    public DFileID get_dFID () {
        return _dFID;
    }

    public void set_dFID (DFileID _dFID) {
        this._dFID = _dFID;
    }

}
