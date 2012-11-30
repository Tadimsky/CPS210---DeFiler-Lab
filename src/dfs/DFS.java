package dfs;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import virtualdisk.VirtualDisk;

import common.Constants;
import common.INode;
import dblockcache.DBuffer;
import dblockcache.DBufferCache;


public class DFS {
    
    private Map<DFileID, DFile> _dFiles;
    private SortedSet<byte[]> _allocatedBlocks;
    private SortedSet<byte[]> _freeBlocks;
    
    DBufferCache _cache;
    
    public DFS() {
    	_dFiles = new HashMap<DFileID, DFile>();

    	// Cache Size
    	_cache = new DBufferCache(1024, new VirtualDisk());
    }
    
    private void LoadDFileList()
    {
    	for (int i = 1; i <= Constants.MAX_FILES; i++)
    	{
    		// Get the INodes from the Disk
    		DBuffer block = _cache.getBlock(i);
    		if (!block.checkValid())
    		{
    			block.waitValid();
    		}    		
    		DFileID dfid = new DFileID(i);
    		
    		
    		DFile dfile = new DFile(dfid, ubuffer, blockmap);
    		
    	}
    }

    /**
     * Called by test programs.
     * Check that each DFile has exactly one INode
     * Check that the size of each DFile is a legal value
     * Check that the block maps of all DFiles have a valid block number for every block in the DFile
     * Check that no data block is listed for more than one DFile
     * Build the list of DFiles on the disk by scanning the INode region
     * Build a list of all allocated and free blocks on the VirtualDisk
     */
    public void init() {
        for(DFileID id : _dFiles.keySet()) {
            DFile d = _dFiles.get(id);
            // TODO Check that each DFile has exactly one INode
            if(d.getSize() > common.Constants.MAX_FILE_BLOCKS) return;
            // TODO Check that the block maps of all DFiles have a valid block number for every block in the DFile
            // TODO Check that no data block is listed for more than one DFile
        }
        // TODO Build the list of DFiles on the disk by scanning the INode region
        // TODO Build a list of all allocated and free blocks on the VirtualDisk
    }
    
    /**
     * Creates a new DFile and returns the DFileID.
     */
    public DFileID createDFile() {
        byte[] fileStart = _freeBlocks.first();
        _allocatedBlocks.add(_freeBlocks.first());
        _freeBlocks.remove(_freeBlocks.first());
        int dFID = 0;
        while(_dFiles.containsKey(dFID)) dFID++;
        DFileID fID = new DFileID(dFID);
        DFile newFile = new DFile(fID, fileStart);
        _dFiles.put(fID, newFile);
        return fID;
    }
    
    /**
     * Destroys the DFile named by the DFileID
     * @param dFID names the DFile
     */
    public void destroyFile(DFileID dFID) {
        // TODO
    }
    
    /**
     * Reads contents of the DFile named by DFileID into the ubuffer
     * @param dFID names the DFile
     * @param ubuffer destination for the data
     * @param startOffset place to start the transfer
     * @param count at most count bytes are transferred
     */
    public int read(DFileID dFID, byte[] ubuffer, int startOffset, int count) {
        // TODO
        return 0;
    }
    
    /**
     * Writes to the file named by DFileID from the ubuffer
     * @param dFID names the DFile
     * @param ubuffer destination for the data
     * @param startOffset place to start the transfer
     * @param count a most count bytes are transferred
     * @return
     */
    public int write(DFileID dFID, byte[] ubuffer, int startOffset, int count) {
        // TODO
        return 0;
    }
    
    /**
     * List DFileIDs for all existing DFiles in the volume.
     */
    public List<DFileID> listAllDFiles() {
        List<DFileID> fileList = new ArrayList<DFileID>();
        fileList.addAll(_dFiles.keySet());
        return fileList;
    }

    /**
     * Write back all dirty blocks to the volume and wait for completion
     */
    public void sync() {
        _cache.sync();
    }
}

