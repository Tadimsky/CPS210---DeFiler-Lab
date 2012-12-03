package dblockcache;

import java.io.IOException;
import common.Constants.DiskOperationType;
import virtualdisk.VirtualDisk;

/**
 * This class implements a binary search tree for DBuffers that handles the
 * heuristic algorithm for finding the least-recently-used buffer.
 * 
 */
public class SortedDBuffer {

    private DBuffer _buffer;
    private SortedDBuffer _left;
    private SortedDBuffer _right;
    private boolean _direction;
    private int _size;

    /**
     * Constructor for sorteddbuffer
     * 
     * @param buffer the actual data
     * @param direction a bit indicating whether the LRU item is to the left or
     *        the right
     */
    public SortedDBuffer (DBuffer buffer, boolean direction) {
        _buffer = buffer;
        _direction = direction;
        _size = 1;
    }

    /**
     * Empty constructor for sorteddbuffer
     */
    public SortedDBuffer () {
        _direction = false;
        _size = 0;
    }

    /**
     * Adds a node to the proper location in the binary search tree
     * 
     * @param buffer data for the new node
     */
    public void addNode (DBuffer buffer) {
        _size++;
        if (_buffer == null) {
            _buffer = buffer;
            return;
        }
        SortedDBuffer currentNode = this;
        if (buffer.getBlockID() < currentNode.getBuffer().getBlockID()) {
            if (currentNode.getLeft() == null) {
                currentNode.setLeft(buffer);
                return;
            }
            currentNode.getLeft().addNode(buffer);
        }
        else if (buffer.getBlockID() > currentNode.getBuffer().getBlockID()) {
            if (currentNode.getRight() == null) {
                currentNode.setRight(buffer);
                return;
            }
            currentNode.getRight().addNode(buffer);
        }
    }

    /**
     * Finds and removes a good approximation of the least-recently-used buffer.
     * As it traverses the tree, it flips the booleans associated with each node
     * it passes so that the next access will yield a different buffer. It then
     * returns the head of the tree again, which may or may not have changed
     * (depending on whether it was the LRU buffer).
     */
    public SortedDBuffer getLRUNode () {
        if (getNext() == null) {
            _size--;
            return getOther();
        }
        if (getNext().getNext() == null) {
            setNext(getNext().getOther());
            _size--;
            return this;
        }
        getNext().getLRUNode();
        _direction = !_direction;
        return this;
    }
    
    /**
     * Writes all dirty buffers back to the disk.
     * @param d destination for the writes
     */
    public void sync(VirtualDisk d) {
        if(getLeft() != null) getLeft().sync(d);
        if(getRight() != null) getRight().sync(d);
        if(!_buffer.checkClean()) try {
            d.startRequest(_buffer, DiskOperationType.WRITE);
        }
        catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Returns the data associated with a given SortedDBuffer.
     */
    public DBuffer getBuffer () {
        return _buffer;
    }

    private SortedDBuffer getLeft () {
        return _left;
    }

    private SortedDBuffer getRight () {
        return _right;
    }

    private SortedDBuffer getNext () {
        if (!_direction) return _left;
        return _right;
    }

    private SortedDBuffer getOther () {
        if (!_direction) return _right;
        return _left;
    }

    private void setNext (SortedDBuffer buf) {
        if (!_direction)
            _left = buf;
        else _right = buf;
    }

    private void setLeft (DBuffer buffer) {
        _left = new SortedDBuffer(buffer, true);
    }

    private void setRight (DBuffer buffer) {
        _right = new SortedDBuffer(buffer, false);
    }

    /**
     * Returns the number of nodes in the (sub)tree.
     */
    public int getSize () {
        return _size;
    }

    public boolean contains(int blockID) {
        if(_buffer.getBlockID() == blockID) {
            return true;
        }
        if(_left == null && _right == null) return false;
        if(_left == null) return _right.contains(blockID);
        if(_right == null) return _left.contains(blockID);
        return _left.contains(blockID) || _right.contains(blockID);
    }
    
    public SortedDBuffer get(int blockID) {
        if(_buffer.getBlockID() == blockID) {
            return this;
        }
        if(_left == null && _right == null) return null;
        if(_left == null) return _right.get(blockID);
        if(_right == null) return _left.get(blockID);
        if(_left.get(blockID) != null) return _left.get(blockID);
        if(_right.get(blockID) != null) return _right.get(blockID);
        return null;
    }
}
