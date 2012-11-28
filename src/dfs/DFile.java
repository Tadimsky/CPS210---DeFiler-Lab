package dfs;
import java.io.ByteArrayInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.List;

import common.Constants;
import common.INode;
import dblockcache.DBuffer;
import dblockcache.DBufferCache;


public class DFile {
    private int _size;
    private byte[] _filedata;
    private int[] _blockmap;
    private DFileID _dFID;
    
    public DFile(DFileID dFID, byte[] ubuffer) {
    	_filedata = ubuffer;
    	// NOTE: Fixed Block Map size
    	_blockmap = new int[16];
    	_dFID = dFID;
    }
    
    public DFile(DFileID dFID, byte[] ubuffer, int size, int[] map)
    {
    	this(dFID, ubuffer);
    	_blockmap = map;
    	_size = size;
    }   
    
    private void loadData(DBufferCache cache)
    {
    	for (int i = 0; i < _blockmap.length; i++)
    	{
    		DBuffer db = cache.getBlock(_blockmap[i]);    		
    		// Read the data for all the blocks into the _filedata
    		
    		if (!db.checkValid())
    		{
    			db.waitValid();
    		}
    		db.read(_filedata, i * Constants.BLOCK_SIZE, Constants.BLOCK_SIZE);
    		
    	}
    }
    
    public int[] getBlockMap()
    {
    	return _blockmap;
    }
    
    public int getSize()
    {
    	return _size;
    }

    public DFileID get_dFID () {
        return _dFID;
    }

    public void set_dFID (DFileID _dFID) {
        this._dFID = _dFID;
    }

}
