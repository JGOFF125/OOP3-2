package implementations;

import java.util.Arrays;
import java.util.NoSuchElementException;
import utilities.Iterator;
import utilities.ListADT;

/**
 * A doubly linked list (DLL) implementation of the {@link ListADT} interface,
 * for use in CPRG 304 at SAIT.
 * <p>
 * This class stores {@link String} elements in a chain of {@link MyDLLNode}
 * objects, each of which holds a reference to both its predecessor ({@code pred})
 * and its successor ({@code succ}). The list maintains explicit {@code head}
 * and {@code tail} references for O(1) access to both ends of the chain.
 * Insertions and removals at arbitrary positions require O(n) traversal.
 * All index parameters are zero-based unless otherwise noted.
 * </p>
 *
 * <p><strong>Design Note – String-Specific Implementation:</strong><br>
 * This class is intentionally designed to store {@link String} elements only.
 * A fully reusable, type-safe design would use Java generics (e.g.,
 * {@code MyDLL<E>}), allowing any object type to be stored in the list.
 * Generic programming is outside the scope of CPRG 304 and has therefore
 * been omitted. All element fields, parameters, and return types are fixed
 * as {@link String} throughout this class and its implementations.
 * </p>
 */
public class MyDLL implements ListADT
{
    /** The first node of the list, or {@code null} when the list is empty. */
    private MyDLLNode head;

    /** The last node of the list, or {@code null} when the list is empty. */
    private MyDLLNode tail;

    /** The number of elements currently stored in the list. */
    private int size;

    /**
     * Constructs a new, empty doubly linked list.
     */
    public MyDLL()
    {
        head = null;
        tail = null;
        size = 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int size()
    {
        return size;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clear()
    {
        head = null;
        tail = null;
        size = 0;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Handles the empty list, insertion at the head, insertion at the tail
     * and insertion in the middle as separate cases.
     * </p>
     */
    @Override
    public boolean add( int index, String toAdd ) throws NullPointerException, IndexOutOfBoundsException
    {
        if( toAdd == null )
        {
            throw new NullPointerException( "Cannot add a null element" );
        }
        if( index < 0 || index > size )
        {
            throw new IndexOutOfBoundsException( "Index: " + index + ", Size: " + size );
        }

        if( index == size )
        {
            // Adding at the end (this also covers an empty list)
            linkLast( toAdd );
        }
        else if( index == 0 )
        {
            // Adding at the front of a list that already has nodes:
            // the new node becomes the head
            MyDLLNode node = new MyDLLNode( toAdd, null, head );
            head.setPred( node );
            head = node;
            size++;
        }
        else
        {
            // Adding in the middle: the new node goes between prev and next
            MyDLLNode next = nodeAt( index );
            MyDLLNode prev = next.getPred();
            MyDLLNode node = new MyDLLNode( toAdd, prev, next );
            prev.setSucc( node );   // prev now points forward to the new node
            next.setPred( node );   // next now points back to the new node
            size++;
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean add( String toAdd ) throws NullPointerException
    {
        if( toAdd == null )
        {
            throw new NullPointerException( "Cannot add a null element" );
        }
        linkLast( toAdd );
        return true;
    }

    /**
     * {@inheritDoc}
     * <p>
     * The elements are read through the other list's iterator, which works
     * on a copy, so adding a list to itself is safe.
     * </p>
     */
    @Override
    public boolean addAll( ListADT toAdd ) throws NullPointerException
    {
        if( toAdd == null )
        {
            throw new NullPointerException( "Cannot add a null list" );
        }

        boolean changed = false;
        Iterator it = toAdd.iterator();
        while( it.hasNext() )
        {
            linkLast( it.next() );
            changed = true;
        }
        return changed;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String get( int index ) throws IndexOutOfBoundsException
    {
        checkElementIndex( index );
        return nodeAt( index ).getElement();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String remove( int index ) throws IndexOutOfBoundsException
    {
        checkElementIndex( index );
        return unlink( nodeAt( index ) );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String remove( String toRemove ) throws NullPointerException
    {
        if( toRemove == null )
        {
            throw new NullPointerException( "Cannot remove a null element" );
        }

        // Walk from the head until we find the first match
        MyDLLNode current = head;
        while( current != null )
        {
            if( current.getElement().equals( toRemove ) )
            {
                return unlink( current );
            }
            current = current.getSucc();
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String set( int index, String toChange ) throws NullPointerException, IndexOutOfBoundsException
    {
        if( toChange == null )
        {
            throw new NullPointerException( "Cannot set a null element" );
        }
        checkElementIndex( index );

        MyDLLNode node = nodeAt( index );
        String old = node.getElement();
        node.setElement( toChange );
        return old;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEmpty()
    {
        return size == 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean contains( String toFind ) throws NullPointerException
    {
        return indexOf( toFind ) != -1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int indexOf( String toFind ) throws NullPointerException
    {
        if( toFind == null )
        {
            throw new NullPointerException( "Cannot search for a null element" );
        }

        int index = 0;
        MyDLLNode current = head;
        while( current != null )
        {
            if( current.getElement().equals( toFind ) )
            {
                return index;
            }
            current = current.getSucc();
            index++;
        }
        return -1;
    }

    /**
     * {@inheritDoc}
     * <p>
     * If the supplied array is too small, a new array of the same runtime
     * type is allocated. If it is larger than needed, the slot right after
     * the last element is set to {@code null}.
     * </p>
     */
    @Override
    public String[] toArray( String[] toHold ) throws NullPointerException
    {
        if( toHold == null )
        {
            throw new NullPointerException( "Holder array cannot be null" );
        }

        // Use the holder if it is big enough, otherwise make a bigger array
        String[] result = toHold;
        if( result.length < size )
        {
            result = Arrays.copyOf( toHold, size );
        }

        // Copy the elements from head to tail
        int i = 0;
        MyDLLNode current = head;
        while( current != null )
        {
            result[ i++ ] = current.getElement();
            current = current.getSucc();
        }

        // If the array has extra room, mark where the list's elements end
        if( result.length > size )
        {
            result[ size ] = null;
        }
        return result;
    }

    /**
     * {@inheritDoc}
     * <p>
     * The returned iterator works on its own copy of the elements, so later
     * changes to this list do not affect it.
     * </p>
     */
    @Override
    public Iterator iterator()
    {
        // toArray gives us a new array holding a copy of the elements,
        // so the iterator is not affected by later changes to the list
        String[] copy = toArray( new String[ 0 ] );
        return new DLLIterator( copy );
    }

    // -----------------------------------------------------------------------
    // Private helpers
    // -----------------------------------------------------------------------

    /**
     * Appends a new node holding the element at the tail in O(1).
     *
     * @param element the non-null element to append
     */
    private void linkLast( String element )
    {
        MyDLLNode node = new MyDLLNode( element, tail, null );
        if( tail == null )
        {
            // The list was empty, so this node is both head and tail
            head = node;
        }
        else
        {
            // Link the old tail forward to the new node
            tail.setSucc( node );
        }
        tail = node;
        size++;
    }

    /**
     * Removes the given node from the chain, fixing the head, tail and
     * neighbour links, and returns its element.
     *
     * @param node a node that is currently part of this list
     * @return the element that was stored in the node
     */
    private String unlink( MyDLLNode node )
    {
        MyDLLNode prev = node.getPred();
        MyDLLNode next = node.getSucc();

        // Make the node before it skip over it (or move head if it was first)
        if( prev == null )
        {
            head = next;
        }
        else
        {
            prev.setSucc( next );
        }

        // Make the node after it skip back over it (or move tail if it was last)
        if( next == null )
        {
            tail = prev;
        }
        else
        {
            next.setPred( prev );
        }

        // Clear the removed node's own links and update the size
        String element = node.getElement();
        node.setPred( null );
        node.setSucc( null );
        size--;
        return element;
    }

    /**
     * Finds the node at the given index, walking from whichever end of the
     * list is closer. The index must already be validated.
     *
     * @param index a valid index in {@code [0, size - 1]}
     * @return the node at that index
     */
    private MyDLLNode nodeAt( int index )
    {
        MyDLLNode current;
        if( index < size / 2 )
        {
            // Closer to the head: walk forward from the head
            current = head;
            for( int i = 0; i < index; i++ )
            {
                current = current.getSucc();
            }
        }
        else
        {
            // Closer to the tail: walk backward from the tail
            current = tail;
            for( int i = size - 1; i > index; i-- )
            {
                current = current.getPred();
            }
        }
        return current;
    }

    /**
     * Checks that an index refers to an existing element.
     *
     * @param index the index to check
     * @throws IndexOutOfBoundsException if {@code index < 0} or {@code index >= size}
     */
    private void checkElementIndex( int index )
    {
        if( index < 0 || index >= size )
        {
            throw new IndexOutOfBoundsException( "Index: " + index + ", Size: " + size );
        }
    }

    // -----------------------------------------------------------------------
    // Iterator
    // -----------------------------------------------------------------------

    /**
     * Iterator over a private copy of the list's elements, taken when the
     * iterator is created, as required by {@link Iterator}.
     */
    private static class DLLIterator implements Iterator
    {
        /** The private copy of the elements. */
        private final String[] copy;

        /** The index of the next element to return. */
        private int position;

        /**
         * Creates an iterator over the given array, which must not be shared.
         *
         * @param copy a freshly made array of the list's elements
         */
        private DLLIterator( String[] copy )
        {
            this.copy = copy;
            this.position = 0;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean hasNext()
        {
            return position < copy.length;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String next() throws NoSuchElementException
        {
            if( !hasNext() )
            {
                throw new NoSuchElementException( "No more elements" );
            }
            return copy[ position++ ];
        }
    }
}
