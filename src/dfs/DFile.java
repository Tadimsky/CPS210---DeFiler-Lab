package dfs;
import java.util.List;
import common.INode;


public class DFile {
    
    private byte[] _filedata;
    private int[] _blockmap;
    private DFileID _dFID;
    
    public DFile(DFileID dFID, byte[] ubuffer, int[] blockmap) {
    	_filedata = ubuffer;
    	_blockmap = blockmap;
    	_dFID = dFID;
    }

    public DFileID get_dFID () {
        return _dFID;
    }

    public void set_dFID (DFileID _dFID) {
        this._dFID = _dFID;
    }

}
