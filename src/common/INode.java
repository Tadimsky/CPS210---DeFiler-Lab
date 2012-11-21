package common;

public class INode {
    public INode next;
    public INode prev;
    public byte[] data;

    public INode(INode p, INode n, byte[] d) {
        next = n;
        prev = p;
        data = d;
    }
}
