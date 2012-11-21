package dfs;
import java.util.List;
import common.INode;


public class DFS {
    
    private List<DFile> _dFiles;
    private List<INode> _allocatedBlocks;
    private List<INode> _freeBlocks;
    
    public DFS() {
        // TODO
    }

    public void init() {
        // TODO
    }
    
    public DFileID createDFile() {
        // TODO
        return null;
    }
    
    public void destroyFile(DFileID dFID) {
        // TODO
    }
    
    public int read(DFileID dFID, byte[] ubuffer, int startOffset, int count) {
        // TODO
        return 0;
    }
    
    public int write(DFileID dFID, byte[] ubuffer, int startOffset, int count) {
        // TODO
        return 0;
    }
    
    public List<DFileID> listAllDFiles() {
        // TODO
        return null;
    }
    
    public void sync() {
        // TODO
    }
}
