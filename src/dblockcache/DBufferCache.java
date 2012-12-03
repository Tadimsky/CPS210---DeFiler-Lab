package dblockcache;

import virtualdisk.VirtualDisk;


public class DBufferCache {
    private int _cacheSize;
    private SortedDBuffer _bufferList;
    private VirtualDisk _disk;

    public DBufferCache (int cachesize, VirtualDisk vD) {
        _disk = vD;
        Thread virtualDisk = new Thread(_disk);
        virtualDisk.start();
        _cacheSize = cachesize;
        _bufferList = new SortedDBuffer();
    }

    /**
     * Get buffer for block specified by blockID
     * The buffer is "held" until the caller releases it
     * A "held" buffer cannot be evicted; its block ID cannot change.
     * 
     * @param BlockID name of block
     */
    public DBuffer getBlock (int blockID) {
        if(_bufferList.getSize() >= _cacheSize) {
            _bufferList = _bufferList.getLRUNode();
        }
        if(_bufferList.contains(blockID)) {
            return _bufferList.get(blockID).getBuffer();
        }
        DBuffer buf = new DBuffer(common.Constants.BLOCK_SIZE, blockID, _disk);
        buf.setBusy(true);
        _bufferList.addNode(buf);
        return buf;
    }

    /**
     * Release the buffer so that it may be eligible for eviction.
     * 
     * @param dbuf buffer to be released
     */
    public void releaseBlock (DBuffer dbuf) {
        dbuf.setBusy(false);
    }

    /**
     * Write back all dirty blocks to the volume, and wait for completion.
     */
    public void sync () {
        _bufferList.sync(_disk);
    }
}
