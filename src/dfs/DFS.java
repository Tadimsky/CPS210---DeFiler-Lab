package dfs;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import virtualdisk.VirtualDisk;
import common.Constants;
import dblockcache.DBuffer;
import dblockcache.DBufferCache;


public class DFS {

    private Map<DFileID, DFile> _dFiles;
    private SortedSet<Integer> _allocatedBlocks;
    private SortedSet<Integer> _freeBlocks;

    DBufferCache _cache;

    public DFS () {
        _dFiles = new HashMap<DFileID, DFile>();

        // Cache Size
        try {
            _cache = new DBufferCache(1024, new VirtualDisk());
        }
        catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Loads a list of all the files from the INodes on the Disk.
     */
    private void LoadDFileList () {
        // Iterate through the blocks from 1 to the number of files
        for (int i = 1; i <= Constants.MAX_FILES; i++) {
            // Get the INode from the Disk
            DBuffer block = _cache.getBlock(i);
            if (!block.checkValid()) {
                block.startFetch();
                block.waitValid();
            }

            DFile dfile = INode.createDFile(block);
            _dFiles.put(dfile.get_dFID(), dfile);
        }
    }

    /**
     * Called by test programs.
     * Check that each DFile has exactly one INode
     * Check that the size of each DFile is a legal value
     * Check that the block maps of all DFiles have a valid block number for
     * every block in the DFile
     * Check that no data block is listed for more than one DFile
     * Build the list of DFiles on the disk by scanning the INode region
     * Build a list of all allocated and free blocks on the VirtualDisk
     */
    public void init () {
        Set<Integer> usedBlocks = new HashSet<Integer>();
        for (DFileID id : _dFiles.keySet()) {
            DFile d = _dFiles.get(id);
            // TODO Check that each DFile has exactly one INode
            if (d.getSize() > common.Constants.MAX_FILE_BLOCKS) return;
            // TODO Check that the block maps of all DFiles have a valid block
            // number for every block in the DFile
            // TODO Check that no data block is listed for more than one DFile
        }
        LoadDFileList();
        for (int i = 0; i < common.Constants.NUM_OF_BLOCKS; i++) {
            if (_dFiles.keySet().contains(new DFileID(i))) {
                _allocatedBlocks.add(i);
            }
            else {
                _freeBlocks.add(i);
            }
        }
    }

    /**
     * Creates a new DFile and returns the DFileID.
     */
    public DFileID createDFile () {
        int fileStart = _freeBlocks.first();
        _allocatedBlocks.add(_freeBlocks.first());
        _freeBlocks.remove(_freeBlocks.first());
        int dFID = 0;
        while (_dFiles.containsKey(dFID))
            dFID++;
        DFileID fID = new DFileID(dFID);
        DFile newFile = new DFile(fID);
        newFile.MapBlock(0, fileStart);
        _dFiles.put(fID, newFile);
        return fID;
    }

    /**
     * Destroys the DFile named by the DFileID
     * 
     * @param dFID names the DFile
     * @throws Exception 
     */
    public void destroyFile (DFileID dFID) throws Exception {
        for (int i = 0; i < _dFiles.get(dFID).getNumBlocks(); i++) {
            int block = _dFiles.get(dFID).getMappedBlock(i);
            _allocatedBlocks.remove(block);
            _freeBlocks.add(block);
        }
        _dFiles.get(dFID).setSize(0);
        _dFiles.remove(dFID);
    }

    /**
     * Reads contents of the DFile named by DFileID into the ubuffer
     * 
     * @param dFID names the DFile
     * @param ubuffer destination for the data
     * @param startOffset place to start the transfer
     * @param count at most count bytes are transferred
     */
    public int read (DFileID dFID, byte[] ubuffer, int startOffset, int count) {
        return 0;
    }

    /**
     * Writes to the file named by DFileID from the ubuffer
     * 
     * @param dFID names the DFile
     * @param ubuffer destination for the data
     * @param startOffset place to start the transfer
     * @param count a most count bytes are transferred
     * @return
     */
    public int write (DFileID dFID, byte[] ubuffer, int startOffset, int count) {
        // TODO
        return 0;
    }

    /**
     * List DFileIDs for all existing DFiles in the volume.
     */
    public List<DFileID> listAllDFiles () {
        List<DFileID> fileList = new ArrayList<DFileID>();
        fileList.addAll(_dFiles.keySet());
        return fileList;
    }

    /**
     * Write back all dirty blocks to the volume and wait for completion
     */
    public void sync () {
        _cache.sync();
    }
}
