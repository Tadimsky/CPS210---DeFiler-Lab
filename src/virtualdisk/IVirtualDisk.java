package virtualdisk;

import java.io.IOException;
import common.Constants.DiskOperationType;
import dblockcache.DBuffer;


public interface IVirtualDisk {

    /**
     * Start an asynchronous request to the underlying device/disk/volume.
     * 
     * @param buf a DBuffer object that needs to be read/write from/to the
     *        volume
     * @param operation either READ or WRITE
     */
    public void startRequest (DBuffer buf, DiskOperationType operation)
            throws IllegalArgumentException, IOException;
}