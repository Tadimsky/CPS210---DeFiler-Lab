public class DBuffer {

	private byte[] _buffer;
	private int _blockid;
	
	private Constants.DBufferState _state;
	private boolean _busy;
	private boolean _isvalid;
	
	
	public DBuffer(int size, int blockid)
	{
		_buffer = new byte[size];
		_blockid = blockid;
		_state = Constants.DBufferState.CLEAN;
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
    
    public int read(byte[] ubuffer, int startOffset, int count) {
        // TODO
        return 0;
    }
    
    public int write(byte[] ubuffer, int startOffset, int count) {
        // TODO
        return 0;
    }
    
    public void ioComplete() {
        // TODO
    }
    
    public int getBlockID() {
        // TODO
        return 0;
    }
    
    public byte[] getBuffer() {
        // TODO
        return null;
    }
}
