package virtualdisk;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.LinkedList;
import java.util.Queue;
import common.Constants;
import common.Constants.DiskOperationType;
import dblockcache.DBuffer;


public class VirtualDisk implements Runnable {

    private Queue<RequestObject> requestQueue;
    private String _volName;
    private RandomAccessFile _file;
    private int _maxVolSize;
    private boolean running;

    /**
     * VirtualDisk Constructors
     */
    public VirtualDisk (String volName, boolean format)
            throws FileNotFoundException, IOException {

        _volName = volName;
        _maxVolSize = Constants.BLOCK_SIZE * Constants.NUM_OF_BLOCKS;

        /*
         * mode: rws => Open for reading and writing, as with "rw", and also
         * require that every update to the file's content or metadata be
         * written synchronously to the underlying storage device.
         */
        _file = new RandomAccessFile(_volName, "rws");

        /*
         * Set the length of the file to be NUM_OF_BLOCKS with each block of
         * size BLOCK_SIZE. setLength internally invokes ftruncate(2) syscall to
         * set the length.
         */
        _file.setLength(Constants.BLOCK_SIZE * Constants.NUM_OF_BLOCKS);
        if (format) {
            formatStore();
        }
        /* Other methods as required */
        requestQueue = new LinkedList<RequestObject>();
    }

    public VirtualDisk (boolean format) throws FileNotFoundException,
            IOException {
        this(Constants.vdiskName, format);
    }

    public VirtualDisk () throws FileNotFoundException, IOException {
        this(Constants.vdiskName, false);
    }

    /**
     * Start an asynchronous request to the underlying device/disk/volume.
     * -- buf is an DBuffer object that needs to be read/write from/to the
     * volume.
     * -- operation is either READ or WRITE
     */
    public void startRequest (DBuffer buf, Constants.DiskOperationType operation)
            throws IllegalArgumentException, IOException {
        synchronized (requestQueue) {
            // Create the new Item
            RequestObject ro = new RequestObject(buf, operation);
            // Add it to the queue
            requestQueue.add(ro);
            // Let the VirtualDisk know that there is something in the queue.
            requestQueue.notifyAll();
        }
    }

    private void processQueue () {
        RequestObject ro;
        synchronized (requestQueue) {
            ro = requestQueue.poll();
        }

        if (ro == null) return;
        try {
            if (ro.operation == DiskOperationType.READ) {
                readBlock(ro.dBuffer);
            }
            else {
                writeBlock(ro.dBuffer);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            ro.dBuffer.ioComplete();
        }
    }

    /**
     * Clear the contents of the disk by writing 0s to it
     */
    private void formatStore () {
        byte b[] = new byte[Constants.BLOCK_SIZE];
        setBuffer((byte) 0, b, Constants.BLOCK_SIZE);
        for (int i = 0; i < Constants.NUM_OF_BLOCKS; i++) {
            try {
                int seekLen = i * Constants.BLOCK_SIZE;
                _file.seek(seekLen);
                _file.write(b, 0, Constants.BLOCK_SIZE);
            }
            catch (Exception e) {
                System.out
                        .println("Error in format: WRITE operation failed at the device block "
                                + i);
            }
        }
    }

    /**
     * helper function: setBuffer
     */
    private static void setBuffer (byte value, byte b[], int bufSize) {
        for (int i = 0; i < bufSize; i++) {
            b[i] = value;
        }
    }

    /**
     * Reads the buffer associated with DBuffer to the underlying
     * device/disk/volume
     */
    private int readBlock (DBuffer buf) throws IOException {
        int seekLen = buf.getBlockID() * Constants.BLOCK_SIZE;
        /* Boundary check */
        if (_maxVolSize < seekLen + Constants.BLOCK_SIZE) { return -1; }
        _file.seek(seekLen);
        return _file.read(buf.getBuffer(), 0, Constants.BLOCK_SIZE);
    }

    /**
     * Writes the buffer associated with DBuffer to the underlying
     * device/disk/volume
     */
    private void writeBlock (DBuffer buf) throws IOException {
        int seekLen = buf.getBlockID() * Constants.BLOCK_SIZE;
        _file.seek(seekLen);
        _file.write(buf.getBuffer(), 0, Constants.BLOCK_SIZE);
    }

    @Override
    public void run () {
        running = true;
        // Wait until there are items in the queue and then process them.
        while (running) {
            synchronized (requestQueue) {
                try {
                    while (!requestQueue.isEmpty()) {
                        processQueue();
                    }
                    requestQueue.wait();
                }
                catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    public void stopdisk () {
        synchronized (requestQueue) {
            running = false;
            requestQueue.notifyAll();
        }
    }

}
