package implementations;

import java.util.EmptyStackException;
import utilities.Iterator;
import utilities.StackADT;

/**
 * An array-backed implementation of the {@link StackADT} interface,
 * for use in CPRG 304 at SAIT.
 * <p>
 * This class delegates all storage to an internal {@link MyArrayList},
 * treating the end of the list as the top of the stack. {@link #push(String)}
 * appends to the end; {@link #pop()} and {@link #peek()} access the last
 * element. A stack follows a <strong>Last-In, First-Out (LIFO)</strong>
 * ordering policy: the last element pushed is the first one returned.
 * </p>
 *
 * <p><strong>Design Note – String-Specific Implementation:</strong><br>
 * This class is intentionally designed to store {@link String} elements only.
 * A fully reusable, type-safe design would use Java generics (e.g.,
 * {@code MyStack<E>}), allowing any object type to be stored in the stack.
 * Generic programming is outside the scope of CPRG 304 and has therefore
 * been omitted. All element fields, parameters, and return types are fixed
 * as {@link String} throughout this class and its implementations.
 * </p>
 */
public class MyStack implements StackADT
{
    private final MyArrayList elements;

    /** Start with an empty array list for storage */
    public MyStack()
    {
        elements = new MyArrayList();
    }

    /**
     * Push a value onto the top
     * @param toAdd the non-null value to push
     * @throws NullPointerException if null is passed
     */
    @Override
    public void push( String toAdd ) throws NullPointerException
    {
        elements.add( toAdd );
    }

    /**
     * Remove the top value
     * @return the popped value
     * @throws EmptyStackException if empty
     */
    @Override
    public String pop() throws EmptyStackException
    {
        checkEmpty();
        return elements.remove( elements.size() - 1 );
    }

    /**
     * Read the top without removing it
     * @return the top value read
     * @throws EmptyStackException if empty
     */
    @Override
    public String peek() throws EmptyStackException
    {
        checkEmpty();
        return elements.get( elements.size() - 1 );
    }

    /** Clear the stack through the array list */
    @Override
    public void clear()
    {
        elements.clear();
    }

    /**
     * Check whether the stack is empty
     * @return true if empty
     */
    @Override
    public boolean isEmpty()
    {
        return elements.isEmpty();
    }

    /**
     * Copy the stack from bottom to top
     * @param holder the array to reuse if it has enough space
     * @return the supplied array or a larger copy
     * @throws NullPointerException if the array is null
     */
    @Override
    public String[] toArray( String[] holder ) throws NullPointerException
    {
        return elements.toArray( holder );
    }

    /**
     * Check whether the stack contains a value
     * @param toFind the non-null value to find
     * @return true if a match exists
     * @throws NullPointerException if null is passed
     */
    @Override
    public boolean contains( String toFind ) throws NullPointerException
    {
        return elements.contains( toFind );
    }

    /**
     * Search from the top for the nearest match
     * @param toFind the non-null value to find
     * @return the one-based position, or -1 if absent
     * @throws NullPointerException if null is passed
     */
    @Override
    public int search( String toFind )
    {
        if( toFind == null )
        {
            throw new NullPointerException( "Cannot search for null" );
        }

        // Count from the top for duplicates
        for( int i = elements.size() - 1; i >= 0; i-- )
        {
            if( toFind.equals( elements.get( i ) ) )
            {
                return elements.size() - i;
            }
        }
        return -1;
    }

    /**
     * Create an independent iterator from bottom to top
     * @return the array list iterator
     */
    @Override
    public Iterator iterator()
    {
        return elements.iterator();
    }

    /**
     * Compare both stacks in bottom-to-top order
     * @param that the stack to compare against
     * @return true if both stacks contain the same values in the same order
     */
    @Override
    public boolean equals( StackADT that )
    {
        if( that == null || size() != that.size() )
        {
            return false;
        }
        Iterator other = that.iterator();
        for( int i = 0; i < elements.size(); i++ )
        {
            if( !elements.get( i ).equals( other.next() ) )
            {
                return false;
            }
        }
        return true;
    }

    /**
     * Count the stack elements
     * @return the number of elements
     */
    @Override
    public int size()
    {
        return elements.size();
    }

    /**
     * Guard the operations that need a top element
     * @throws EmptyStackException if empty
     */
    private void checkEmpty()
    {
        if( isEmpty() )
        {
            throw new EmptyStackException();
        }
    }
}
