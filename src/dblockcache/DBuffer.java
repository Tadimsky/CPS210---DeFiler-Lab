package dblockcache;
import common.Constants;
import common.Constants.DBufferState;

public class DBuffer {

	private byte[] _buffer;
	private int _blockid;
	
	private DBufferState _state;
	private boolean _busy;
	private boolean _isvalid;
	
	
	public DBuffer(int size, int blockid)
	{
		_buffer = new byte[size];
		_blockid = blockid;
		_state = DBufferState.CLEAN;
		_busy = false;
		_isvalid = false;
	}
	
    public void startFetch() {
        // TODO
    }
    
    public void startPush() {
        // TODO
    }
    
    public boolean checkValid() {
        //TODO
        return false;
    }
    
    public boolean waitValid() {
        // TODO
        return false;
    }
    
    public boolean checkClean() {
        // TODO
        return false;
    }
    
    public boolean waitClean() {
        // TODO
        return false;
    }
    
    public boolean isBusy() {
        // TODO
        return false;
    }
    
    // reads the data from the ublock into our block starting at the startOffset    
    public int read(byte[] ubuffer, int startOffset, int count) {
    	// Need the Thread This
    	
    	
    	if (_state == DBufferState.DIRTY)
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
    
    public int write(byte[] ubuffer, int startOffset, int count) {
    	// Need the Thread This
    	
        // writes the data from the ublock into our block starting at the startOffset
   
    	// make sure startOffset does not exceed bounds
    	if (startOffset < ubuffer.length || startOffset >= ubuffer.length)
    		return -1;
    	// number of bytes to copy
    	int numcopy = count;
    	if (count > _buffer.length)
    		numcopy = _buffer.length;
    	
    	// make sure does not exceeed bounds
    	if (startOffset + numcopy >= ubuffer.length)
    		return -1;
    	
    	_state = DBufferState.DIRTY;
    	// copy every byte from the ubuffer to the _buffer
    	for (int i = startOffset; i < startOffset + numcopy; i++)
    	{
    		_buffer[i-startOffset] = ubuffer[i];
    	}
    	
    	return numcopy;
    }
    
    public void ioComplete() {
        // TODO
    }
    
    public int getBlockID() {
    	// Do we need to have a mutex for this?
        return _blockid;
    }
    
    public byte[] getBuffer() {
    	// We need a Mutex for this I think
    	return _buffer;
    }
}