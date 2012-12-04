package dfs;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import sun.rmi.runtime.NewThreadAction;
import virtualdisk.VirtualDisk;
import common.Constants;
import dblockcache.DBuffer;
import dblockcache.DBufferCache;


public class DFS {

    private Map<Integer, DFile> _dFiles;
    private SortedSet<Integer> _allocatedBlocks;
    private SortedSet<Integer> _freeBlocks;

    DBufferCache _cache;

    public DFS () {
        _dFiles = new HashMap<Integer, DFile>();

        _allocatedBlocks = new TreeSet<Integer>();
        _freeBlocks = new TreeSet<Integer>();

        // Cache Size
        try {
            _cache = new DBufferCache(1024, new VirtualDisk());
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
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
            if (dfile != null) _dFiles.put(dfile.get_dFID().get_dFID(), dfile);
        }
    }

    /**
     * Called by test programs.
     * Check that each DFile has exactly one INode (handled in DFile creation)
     * Check that the size of each DFile is a legal value
     * Check that the block maps of all DFiles have a valid block number for
     * every block in the DFile
     * Check that no data block is listed for more than one DFile
     * Build the list of DFiles on the disk by scanning the INode region
     * Build a list of all allocated and free blocks on the VirtualDisk
     * 
     * @throws Exception
     */
    public void init () throws Exception {
        // Build the list of DFiles on the disk by scanning the INode region
        LoadDFileList();
        for (int id : _dFiles.keySet()) {
            DFile d = _dFiles.get(id);
            // Check that the size of each DFile is a legal value
            if (d.getSize() > common.Constants.MAX_FILE_BLOCKS
                    * Constants.BLOCK_SIZE) throw new Exception();
            // Check that the block maps of all DFiles have a valid block
            // number for every block in the DFile
            for (int i = 0; i < d.getNumBlocks(); i++) {
                if (d.getMappedBlock(i) > common.Constants.NUM_OF_BLOCKS)
                    throw new Exception();
            }
        }
        // Build a list of all allocated and free blocks on the VirtualDisk
        for (DFile file : _dFiles.values()) {
            for (int j = 0; j < Constants.MAX_FILE_BLOCKS; j++) {
                int blockid = file.getMappedBlock(j);
                if (blockid == -1) break;
                // Check that no data block is listed for more than one DFile
                if (blockid != 0) {
                    if (_allocatedBlocks.contains(blockid)) { throw new Exception(
                            "Invalid Block Allocation"); }
                    _allocatedBlocks.add(blockid);
                }
            }
        }

        for (int i = Constants.MAX_FILES + 1; i < common.Constants.NUM_OF_BLOCKS; i++) {
            if (!_allocatedBlocks.contains(i)) {
                _freeBlocks.add(i);
            }
        }
    }

    /**
     * Creates a new DFile and returns the DFileID.
     */
    public synchronized DFileID createDFile () {
        int fileStart = _freeBlocks.first();
        _allocatedBlocks.add(_freeBlocks.first());
        _freeBlocks.remove(_freeBlocks.first());
        int dFID = 0;
        while (_dFiles.containsKey(dFID))
            dFID++;
        DFile newFile = new DFile(new DFileID(dFID));
        newFile.MapBlock(0, fileStart);
        _dFiles.put(dFID, newFile);

        updateINode(newFile);

        return new DFileID(dFID);
    }

    private synchronized void updateINode (DFile file) {
        byte[] info = INode.createINode(file);
        DBuffer inode = _cache.getBlock(file.get_dFID().get_dFID());
        if (!inode.checkValid()) {
            inode.startFetch();
            inode.waitValid();
        }
        inode.write(info, 0, Constants.BLOCK_SIZE);
        inode.startPush();
    }

    /**
     * Destroys the DFile named by the DFileID
     * 
     * @param dFID names the DFile
     * @throws Exception
     */
    public void destroyFile (DFileID dFID) {
        DFile file = _dFiles.get(dFID.get_dFID());
        if (file == null) return;

        for (int i = 0; i < file.getNumBlocks(); i++) {
            int block = file.getMappedBlock(i);
            synchronized (_allocatedBlocks) {
                _allocatedBlocks.remove(block);
            }
            synchronized (_freeBlocks) {
                _freeBlocks.add(block);
            }
        }
        try {
            file.setSize(0);
        }
        catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        synchronized (_dFiles) {
            _dFiles.remove(dFID.get_dFID());
        }
        // Size is 0 so it should not be read again
        updateINode(file);
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
        DFile file = _dFiles.get(dFID.get_dFID());
        if (file == null) return -1;
        file.lockRead();
        int nb = file.getNumBlocks();
        int s = startOffset;
        int done = count;

        for (int i = 0; i < nb; i++) {
            DBuffer d = _cache.getBlock(file.getMappedBlock(i));
            if (!d.checkValid()) {
                d.startFetch();
                d.waitValid();
            }

            int read = d.read(ubuffer, s, done);
            done -= read;
            s += read;
        }
        file.unlockRead();
        return count;
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
        DFile file = _dFiles.get(dFID.get_dFID());
        if (file == null) return -1;

        file.lockWrite();
        int delta = file.changeinBlocks(count);
        if (delta < 0) {
            // free blocks
            delta *= -1;
            for (int i = file.getNumBlocks(); i > file.getNumBlocks() - delta; i--) {
                // free these blocks
                synchronized (_freeBlocks) {
                    _freeBlocks.add(file.getMappedBlock(i - 1));
                }
                synchronized (_allocatedBlocks) {
                    _allocatedBlocks.remove(file.getMappedBlock(i - 1));
                }
            }
        }
        else {
            // Adding blocks
            for (int i = file.getNumBlocks(); i < file.getNumBlocks() + delta; i++) {
                if (_freeBlocks.size() > 0) {
                    int newblock = _freeBlocks.first();
                    synchronized (_freeBlocks) {
                        _freeBlocks.remove(newblock);
                    }
                    synchronized (_allocatedBlocks) {
                        _allocatedBlocks.add(newblock);
                    }

                    file.MapBlock(i, newblock);
                }
            }
        }
        // Set the new size, make sure enough blocks were added
        try {
            file.setSize(count);
        }
        catch (Exception e) {
            // Not enough blocks allocated
            e.printStackTrace();
        }

        int nb = file.getNumBlocks();
        int s = startOffset;
        int done = count;

        for (int i = 0; i < nb; i++) {
            DBuffer d = _cache.getBlock(file.getMappedBlock(i));

            if (!d.checkValid()) {
                d.startFetch();
                d.waitValid();
            }

            int wrote = d.write(ubuffer, s, done);
            done -= wrote;
            s += wrote;
        }

        updateINode(file);
        file.unlockWrite();
        return count;
    }

    /**
     * List DFileIDs for all existing DFiles in the volume.
     */
    public List<DFileID> listAllDFiles () {
        List<DFileID> fileList = new ArrayList<DFileID>();
        for (int i : _dFiles.keySet()) {
            fileList.add(new DFileID(i));
        }
        return fileList;
    }

    /**
     * Write back all dirty blocks to the volume and wait for completion
     */
    public void sync () {
        _cache.sync();
    }

    /**
     * Stop the Threads
     */
    public void shutdown () {
        _cache.shutdown();
    }
}
