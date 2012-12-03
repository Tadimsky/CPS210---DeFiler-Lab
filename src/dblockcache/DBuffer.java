package dblockcache;

import java.io.IOException;
import virtualdisk.VirtualDisk;
import common.Constants;
import common.Constants.DBufferState;
import common.Constants.DiskOperationType;


public class DBuffer {

    private byte[] _buffer;
    private int _blockid;

    private Constants.DBufferState _state;
    private boolean _busy;
    private boolean _isvalid;
    private VirtualDisk _disk;

    public DBuffer (int size, int blockid, VirtualDisk d) {
        _buffer = new byte[size];
        _blockid = blockid;
        _state = Constants.DBufferState.CLEAN;
        _busy = false;
        _isvalid = false;
        _disk = d;
    }

    /**
     * Start an asynchronous fetch of associated block from this volume.
     */
    public void startFetch () {
        _isvalid = false;
        _busy = true;

        try {
            _disk.startRequest(this, DiskOperationType.READ);
        }
        catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }        
    }

    /**
     * Start an asynchronous write of buffer contents to block on volume.
     */
    public void startPush () {
        if (DBufferState.CLEAN.equals(_state)) { return; }
        _busy = true;
        try {
            _disk.startRequest(this, DiskOperationType.WRITE);
        }
        catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        // Data will be written to the disk - Clean
        synchronized (_state) {
            _state = DBufferState.CLEAN;
            notifyAll();
        }
    }

    /**
     * Check whether the buffer has valid data.
     */
    public boolean checkValid () {
        return _isvalid;
    }

    /**
     * Wait until the buffer has valid data (i.e., wait for fetch to complete).
     * 
     * @return
     */
    public synchronized boolean waitValid () {
        while (!_isvalid)
            try {
                wait();
            }
            catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        return true;
    }

    /**
     * Check whether the buffer is dirty, i.e., has modified data to be written
     * back
     */
    public boolean checkClean () {
        return Constants.DBufferState.CLEAN.equals(_state);
    }

    /**
     * Wait until the buffer is clean (i.e., wait for push to complete).
     */
    public synchronized boolean waitClean () {
        while (!Constants.DBufferState.CLEAN.equals(_state)) {
            try {
                wait();
            }
            catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * Check if the buffer is evictable: not evictable if I/O in progress, or
     * buffer is held.
     */
    public boolean isBusy () {
        return _busy;
    }

    /**
     * Reads into the ubuffer[] from the contents of this DBuffer dbuf.
     * Check first that the dbuf has a valid copy of the data!
     * 
     * @param ubuffer destination
     * @param startOffset for the ubuffer, not for dbuf. The index to start
     *        writing in the ubuffer
     * @param count reads begin at offset 0 and move at most count bytes. Don't
     *        make this bigger than the block size.
     */
    public synchronized int read (byte[] ubuffer, int startOffset, int count) {

        if (_state == DBufferState.DIRTY) return -1;

        // make sure startOffset does not exceed bounds
        if (startOffset < 0 || startOffset >= ubuffer.length) return -1;
        // number of bytes to copy
        int numcopy = count;
        if (count > _buffer.length) numcopy = _buffer.length;

        // make sure does not exceed bounds
        if (startOffset + numcopy >= ubuffer.length) return -1;

        // copy every byte from the ubuffer to the _buffer
        for (int i = startOffset; i < startOffset + numcopy; i++) {
            // if we hit the end of the file
            if (_buffer[i - startOffset] == 0xffffffff) {
                ubuffer[i] = '\0';
                return i;
            }
            else {
                // continue the read
                ubuffer[i] = _buffer[i - startOffset];
            }
        }
        return numcopy;
    }

    /**
     * Writes into this DBuffer dbuf from the contents of ubuffer[].
     * Mark dbuf dirty!
     * 
     * @param ubuffer source
     * @param startOffset for the ubuffer, not for dbuf from which to read from.
     * @param count writes begin at offset 0 in dbuf and move at most count
     *        bytes
     */
    public synchronized int write (byte[] ubuffer, int startOffset, int count) {

        // make sure startOffset does not exceed bounds
        if (startOffset < 0 || startOffset >= ubuffer.length) return -1;
        // number of bytes to copy
        int numcopy = count;
        if (count > _buffer.length) numcopy = _buffer.length;

        // make sure does not exceed bounds
        if (startOffset + numcopy >= ubuffer.length) return -1;

        // Mark this DBuffer as dirty as we've written data to it.
        _state = DBufferState.DIRTY;

        // copy every byte from the ubuffer to the _buffer
        for (int i = startOffset; i < startOffset + numcopy; i++) {
            // if we reach the end of the file
            if (ubuffer[i] == '\0') {
                // mark the buffer as end of file
                _buffer[i - startOffset] = 0xffffffff;
                return i;
            }
            else {
                // continue the read
                _buffer[i - startOffset] = ubuffer[i];
            }            
        }
        return numcopy;
    }

    /**
     * Called by VirtualDisk when it finishes working with the file.
     */
    public synchronized void ioComplete () {
        // The DBuffer is now valid if it is returning from a Read
        _isvalid = true;
        // The DBuffer is no longer busy
        _busy = false;

        // Wake up people who are waiting for the state to change
        notifyAll();
    }

    /**
     * Called by VirtualDisk to get a reference point for the block
     */
    public int getBlockID () {
        return _blockid;
    }

    /**
     * Called by VirtualDisk to get the contents of the buffer.
     */
    public byte[] getBuffer () {
        return _buffer;
    }

    public void setBusy (boolean b) {
        _busy = b;
    }
}
