package dfs;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import common.Constants;

import dblockcache.DBuffer;

public class INode {
	
	/***********************************************\
	| DFILEID | SIZE |           BLOCK MAP          |
	\***********************************************/
	
	public static DFile createDFile(DBuffer block)
    {
    	if (!block.checkValid())
    	{
    		block.waitValid();
    	}
    	
    	byte[] data = block.getBuffer();
    	try {
    		DataInputStream dis = new DataInputStream(new ByteArrayInputStream(data));
    		
    		// Read the DFileID from the INode
    		int fid = dis.readInt();
			DFileID dfid = new DFileID(fid);
			// Read the Size from the INode
			int size = dis.readInt();
			
			// The rest of the block contains the block map			
			int[] blockmap = new int[Constants.NUM_OF_BLOCKS];
			for (int i = 0; i < blockmap.length; i++)
			{
				// read the next index of the block
				// TODO: error checking?
				blockmap[i] = dis.readInt();
			}
			// initialize the buffer array for the file
			byte[] buff = new byte[size];
			
			return new DFile(dfid, buff, size, blockmap);
		} 
    	catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	return null;
    }
	
	public static byte[] createINode(DFile file)
	{
		byte[] result = new byte[Constants.BLOCK_SIZE];
		try
		{
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			DataOutputStream dos = new DataOutputStream(bos);
			dos.writeInt(file.get_dFID().get_dFID());
			dos.writeInt(file.getSize());			
			
			for (int i = 0; i < Constants.NUM_OF_BLOCKS; i++)
			{
				dos.writeInt(file.getMappedBlock(i));
			}			
			dos.close();
			// Create a new byte array of the correct size
			ByteBuffer bb = ByteBuffer.wrap(result);
			bb.put(bos.toByteArray());
			
			// Return the array of INode
			return bb.array();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		return null;
	}
}
