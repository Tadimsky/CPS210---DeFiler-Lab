package virtualdisk;

import common.Constants.DiskOperationType;
import dblockcache.DBuffer;


public class RequestObject {

    public DBuffer dBuffer;
    public DiskOperationType operation;

    public RequestObject (DBuffer db, DiskOperationType dot) {
        dBuffer = db;
        operation = dot;
    }
}
