package dblockcache;

public class DBufferCache {
	private int _cacheSize;
	
	public DBufferCache(int cachesize) {
		_cacheSize = cachesize;
	}

	/**
	 * Get buffer for block specified by blockID
	 * The buffer is "held" until the caller releases it
	 * A "held" buffer cannot be evicted; its block ID cannot change.
	 * @param BlockID name of block
	 */
    public DBuffer getBlock(int blockID) {
        // TODO
        return null;
    }
    
    /**
     * Release the buffer so that it may be eligible for eviction.
     * @param dbuf buffer to be released
     */
    public void releaseBlock(DBuffer dbuf) {
        // TODO
    }
    
    /**
     * Write back all dirty blocks tot he volume, and wait for completion.
     */
    public void sync() {
        // TODO
    }
    
    /**
     * Called by releaseBlock to determine heuristically which block to release.
     */
    public DBuffer getLRUBlock() {
        // TODO
        return null;
    }
}
