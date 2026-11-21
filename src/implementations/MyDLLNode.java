package implementations;

/**
 * A single node in a doubly linked list (DLL), used as the building block
 * for {@link MyDLL} in CPRG 304 at SAIT.
 * <p>
 * Each node stores a {@link String} element and maintains two directional
 * references: a predecessor ({@code pred}) pointing to the node immediately
 * before it in the chain, and a successor ({@code succ}) pointing to the
 * node immediately after it. A {@code null} predecessor indicates the node
 * is the head of the list; a {@code null} successor indicates it is the tail.
 * </p>
 *
 * <p><strong>Design Note – String-Specific Implementation:</strong><br>
 * This class is intentionally designed to store {@link String} elements only.
 * A fully reusable, type-safe design would use Java generics (e.g.,
 * {@code DLLNode<E>}), allowing any object type to be stored in the node.
 * Generic programming is outside the scope of CPRG 304 and has therefore
 * been omitted. All element fields, parameters, and return types are fixed
 * as {@link String} throughout this class and its usages.
 * </p>
 */
public class MyDLLNode
{
    /** The {@link String} element stored in this node. */
    private String element;

    /** The node immediately before this one, or {@code null} if this is the head. */
    private MyDLLNode pred;

    /** The node immediately after this one, or {@code null} if this is the tail. */
    private MyDLLNode succ;

    /**
     * Constructs a new, unlinked node holding the given element.
     * Both {@code pred} and {@code succ} start as {@code null}.
     *
     * @param element the {@link String} element to store in this node
     */
    public MyDLLNode( String element )
    {
        this( element, null, null );
    }

    /**
     * Constructs a new node holding the given element and linked to the
     * given neighbours.
     *
     * @param element the {@link String} element to store in this node
     * @param pred    the node that comes before this one, or {@code null}
     * @param succ    the node that comes after this one, or {@code null}
     */
    public MyDLLNode( String element, MyDLLNode pred, MyDLLNode succ )
    {
        this.element = element;
        this.pred = pred;
        this.succ = succ;
    }

    /**
     * Returns the element stored in this node.
     *
     * @return the stored {@link String} element
     */
    public String getElement()
    {
        return element;
    }

    /**
     * Replaces the element stored in this node.
     *
     * @param element the new {@link String} element to store
     */
    public void setElement( String element )
    {
        this.element = element;
    }

    /**
     * Returns the predecessor of this node.
     *
     * @return the previous node, or {@code null} if this node is the head
     */
    public MyDLLNode getPred()
    {
        return pred;
    }

    /**
     * Sets the predecessor of this node.
     *
     * @param pred the node to place before this one, or {@code null}
     */
    public void setPred( MyDLLNode pred )
    {
        this.pred = pred;
    }

    /**
     * Returns the successor of this node.
     *
     * @return the next node, or {@code null} if this node is the tail
     */
    public MyDLLNode getSucc()
    {
        return succ;
    }

    /**
     * Sets the successor of this node.
     *
     * @param succ the node to place after this one, or {@code null}
     */
    public void setSucc( MyDLLNode succ )
    {
        this.succ = succ;
    }
}
