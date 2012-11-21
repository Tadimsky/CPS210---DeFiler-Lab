package dblockcache;

import common.Constants;


public class DBuffer {

    private byte[] _buffer;
    private int _blockid;

    private Constants.DBufferState _state;
    private boolean _busy;
    private boolean _isvalid;

    public DBuffer (int size, int blockid) {
        _buffer = new byte[size];
        _blockid = blockid;
        _state = Constants.DBufferState.CLEAN;
        _busy = false;
        _isvalid = false;
    }

    /**
     * Start an asychronous fetch of associated block from this volume.
     */
    public void startFetch () {
        // TODO
    }

    /**
     * Start an asynchronous write of buffer contents to block on volume.
     */
    public void startPush () {
        // TODO
    }

    /**
     * Check whether the buffer has valid data.
     */
    public boolean checkValid () {
        // TODO
        return false;
    }

    /**
     * Wait until the buffer has valid data (i.e., wait for fetch to complete).
     * @return
     */
    public boolean waitValid () {
        // TODO
        return false;
    }

    /**
     * Check whether the buffer is dirty, i.e., has modified data to be written back
     */
    public boolean checkClean () {
        // TODO
        return false;
    }

    /**
     * Wait until the buffer is clean (i.e., wait for push to complete).
     */
    public boolean waitClean () {
        // TODO
        return false;
    }

    /**
     * Check if the buffer is evictable: not evictable if I/O in progress, or buffer is held.
     */
    public boolean isBusy () {
        // TODO
        return false;
    }

    /**
     * Reads into the ubuffer[] from the contents of this Dbuffer dbuf.
     * Check first that the dbuf has a valid copy of the data!
     * @param ubuffer destination
     * @param startOffset for the ubuffer, not for dbuf
     * @param count reads begin at offset 0 and move at most count bytes
     */
    public int read (byte[] ubuffer, int startOffset, int count) {
        // Need the Thread This
    	
    	
    	if (_state == Constants.DBufferState.DIRTY)
    		return -1;
   
    	// make sure startOffset does not exceed bounds
    	if (startOffset < ubuffer.length || startOffset >= ubuffer.length)
    		return -1;
    	// number of bytes to copy
    	int numcopy = count;
    	if (count > _buffer.length)
    		numcopy = _buffer.length;
    	
    	// make sure does not exceed bounds
    	if (startOffset + numcopy >= ubuffer.length)
    		return -1;
    	
    	// copy every byte from the ubuffer to the _buffer
    	for (int i = startOffset; i < startOffset + numcopy; i++)
    	{
    		ubuffer[i] = _buffer[i-startOffset];
    	}
    	
    	return numcopy;

    }

    /**
     * Writes into this Dbuffer dbuf from the contents of ubuffer[].
     * Mark dbuf dirty!
     * @param ubuffer source
     * @param startOffset for the ubuffer, not for dbuf
     * @param count writes begin at offset 0 in dbuf and move at most count bytes
     */
    public int write (byte[] ubuffer, int startOffset, int count) {
    	// Need the Thread This
    	
    	
    	if (_state == Constants.DBufferState.DIRTY)
    		return -1;
   
    	// make sure startOffset does not exceed bounds
    	if (startOffset < ubuffer.length || startOffset >= ubuffer.length)
    		return -1;
    	// number of bytes to copy
    	int numcopy = count;
    	if (count > _buffer.length)
    		numcopy = _buffer.length;
    	
    	// make sure does not exceed bounds
    	if (startOffset + numcopy >= ubuffer.length)
    		return -1;
    	
    	// copy every byte from the ubuffer to the _buffer
    	for (int i = startOffset; i < startOffset + numcopy; i++)
    	{
    		ubuffer[i] = _buffer[i-startOffset];
    	}
   	
    	return numcopy;
    }

    /**
     * Called by VirtualDisk when it finishes working with the file.
     */
    public void ioComplete () {
        // TODO
    }

    /**
     * Called by VirtualDisk to get a reference point for the block
     */
    public int getBlockID () {
        // TODO
        return 0;
    }

    /**
     * Called by VirtualDisk to get the contents of the buffer.
     */
    public byte[] getBuffer () {
        // TODO
        return null;
    }
}
