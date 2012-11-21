package dfs;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import common.INode;


public class DFS {
    
    private Map<DFileID, DFile> _dFiles;
    private SortedSet<byte[]> _allocatedBlocks;
    private SortedSet<byte[]> _freeBlocks;
    
    public DFS() {
        // TODO
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
        // TODO
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
        // TODO
        return null;
    }

    /**
     * Write back all dirty blocks to the volume and wait for completion
     */
    public void sync() {
        // TODO
    }
}
