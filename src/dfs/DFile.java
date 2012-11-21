package dfs;
import java.util.List;
import common.INode;


public class DFile {
    
    private List<INode> _blocks;
    private DFileID _dFID;
    
    public DFile(DFileID dFID, byte[] ubuffer) {
        // TODO
    }

    public DFileID get_dFID () {
        return _dFID;
    }

    public void set_dFID (DFileID _dFID) {
        this._dFID = _dFID;
    }

}
