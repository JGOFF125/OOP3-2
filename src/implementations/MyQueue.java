package implementations;

import exceptions.EmptyQueueException;
import utilities.Iterator;
import utilities.QueueADT;

/**
 * A doubly linked list-backed implementation of the {@link QueueADT} interface,
 * for use in CPRG 304 at SAIT.
 * <p>
 * This class delegates all storage to an internal {@link MyDLL}, treating
 * index {@code 0} as the front of the queue and the last index as the back.
 * {@link #enqueue(String)} appends to the back; {@link #dequeue()} and
 * {@link #peek()} access the front. A queue follows a
 * <strong>First-In, First-Out (FIFO)</strong> ordering policy: the first
 * element enqueued is the first one returned.
 * </p>
 *
 * <p><strong>Design Note – String-Specific Implementation:</strong><br>
 * This class is intentionally designed to store {@link String} elements only.
 * A fully reusable, type-safe design would use Java generics (e.g.,
 * {@code MyQueue<E>}), allowing any object type to be stored in the queue.
 * Generic programming is outside the scope of CPRG 304 and has therefore
 * been omitted. All element fields, parameters, and return types are fixed
 * as {@link String} throughout this class and its implementations.
 * </p>
 */
public class MyQueue implements QueueADT
{
    /** The doubly linked list that stores the elements, front at index 0. */
    private final MyDLL list;

    /**
     * Constructs a new, empty queue.
     */
    public MyQueue()
    {
        list = new MyDLL();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void enqueue( String toAdd ) throws NullPointerException
    {
        list.add( toAdd );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String dequeue() throws EmptyQueueException
    {
        if( list.isEmpty() )
        {
            throw new EmptyQueueException( "Cannot dequeue from an empty queue." );
        }
        return list.remove( 0 );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String peek() throws EmptyQueueException
    {
        if( list.isEmpty() )
        {
            throw new EmptyQueueException( "Cannot peek at an empty queue." );
        }
        return list.get( 0 );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void dequeueAll()
    {
        list.clear();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEmpty()
    {
        return list.isEmpty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean contains( String toFind ) throws NullPointerException
    {
        return list.contains( toFind );
    }

    /**
     * {@inheritDoc}
     * <p>
     * A {@code null} argument can never be in the queue, so {@code -1} is
     * returned for it.
     * </p>
     */
    @Override
    public int search( String toFind )
    {
        if( toFind == null )
        {
            return -1;
        }
        int index = list.indexOf( toFind );   // 0-based, or -1 if not found
        if( index == -1 )
        {
            return -1;
        }
        return index + 1;                     // queue positions start at 1
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterator iterator()
    {
        return list.iterator();
    }

    /**
     * {@inheritDoc}
     * <p>
     * A {@code null} argument is never equal to a queue, so {@code false}
     * is returned for it.
     * </p>
     */
    @Override
    public boolean equals( QueueADT that )
    {
        if( that == null )
        {
            return false;
        }
        if( that == this )
        {
            return true;
        }
        if( this.size() != that.size() )
        {
            return false;
        }

        // Same size, so compare the elements one by one from front to back
        Iterator mine = this.iterator();
        Iterator theirs = that.iterator();
        while( mine.hasNext() )
        {
            if( !mine.next().equals( theirs.next() ) )
            {
                return false;
            }
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String[] toArray( String[] holder ) throws NullPointerException
    {
        return list.toArray( holder );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int size()
    {
        return list.size();
    }
}
