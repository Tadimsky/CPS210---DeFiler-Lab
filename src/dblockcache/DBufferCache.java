package dblockcache;

import java.util.TreeSet;


public class DBufferCache {
    private int _cacheSize;
    private SortedDBuffer _bufferList;

    public DBufferCache (int cachesize) {
        _cacheSize = cachesize;
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
        DBuffer buf = new DBuffer(common.Constants.BLOCK_SIZE, blockID);
        _bufferList.addNode(buf);
        return buf;
    }

    /**
     * Release the buffer so that it may be eligible for eviction.
     * 
     * @param dbuf buffer to be released
     */
    public void releaseBlock (DBuffer dbuf) {
        // TODO
    }

    /**
     * Write back all dirty blocks tot he volume, and wait for completion.
     */
    public void sync () {
        // TODO
    }
}
