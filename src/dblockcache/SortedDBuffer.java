package dblockcache;

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
     * Adds a node to the proper location in the binary search tree
     * 
     * @param buffer data for the new node
     */
    public void addNode (DBuffer buffer) {
        _size++;
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
     * Finds and returns a good approximation of the least-recently-used buffer.
     * As it traverses the tree, it flips the booleans associated with each node
     * it passes so that the next access will yield a different buffer.
     */
    public SortedDBuffer getLRUNode () {
        if(getNext() == null) {
            _size--;
            return getOther();
        }
        if(getNext().getNext() == null) {
            setNext(getNext().getOther());
            _size--;
            return this;
        }
        getNext().getLRUNode();
        _direction = !_direction;
        return this;
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
        if(!_direction) return _left;
        return _right;
    }
    
    private SortedDBuffer getOther () {
        if(!_direction) return _right;
        return _left;
    }

    private void setNext (SortedDBuffer buf) {
        if(!_direction) _left = buf;
        else _right = buf;
    }
    
    private void setLeft (DBuffer buffer) {
        _left = new SortedDBuffer(buffer, true);
    }

    private void setRight (DBuffer buffer) {
        _right = new SortedDBuffer(buffer, false);
    }
    
    public int getSize() {
        return _size;
    }
}
