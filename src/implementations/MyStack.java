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

    /** I start with an empty array list for storage */
    public MyStack()
    {
        elements = new MyArrayList();
    }

    /**
     * I push a value onto the top
     * @param toAdd the non-null value I push
     * @throws NullPointerException if I get null
     */
    @Override
    public void push( String toAdd )
    {
        elements.add( toAdd );
    }

    /**
     * I remove the top value
     * @return the value I popped
     * @throws EmptyStackException if I have no elements
     */
    @Override
    public String pop()
    {
        checkEmpty();
        return elements.remove( elements.size() - 1 );
    }

    /**
     * I read the top without removing it
     * @return the top value I read
     * @throws EmptyStackException if I have no elements
     */
    @Override
    public String peek()
    {
        checkEmpty();
        return elements.get( elements.size() - 1 );
    }

    /** I clear the stack through my array list */
    @Override
    public void clear()
    {
        elements.clear();
    }

    /**
     * I check whether the stack is empty
     * @return true if I have no elements
     */
    @Override
    public boolean isEmpty()
    {
        return elements.isEmpty();
    }

    /**
     * I copy the stack from bottom to top
     * @param holder the array I reuse if it has enough space
     * @return the supplied array or a larger copy I create
     * @throws NullPointerException if I get a null array
     */
    @Override
    public String[] toArray( String[] holder )
    {
        return elements.toArray( holder );
    }

    /**
     * I check whether the stack contains a value
     * @param toFind the non-null value I look for
     * @return true if I find a match
     * @throws NullPointerException if I get null
     */
    @Override
    public boolean contains( String toFind )
    {
        return elements.contains( toFind );
    }

    /**
     * I search from the top for the nearest match
     * @param toFind the non-null value I look for
     * @return the one-based position I find, or -1 if absent
     * @throws NullPointerException if I get null
     */
    @Override
    public int search( String toFind )
    {
        if( toFind == null )
        {
            throw new NullPointerException( "Cannot search for null" );
        }

        // Count from my top for duplicates
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
     * I create an independent iterator from bottom to top
     * @return the iterator I get from my array list
     */
    @Override
    public Iterator iterator()
    {
        return elements.iterator();
    }

    /**
     * I compare both stacks in bottom-to-top order
     * @param that the stack I compare against
     * @return true if I find the same values in the same order
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
     * I count the stack elements
     * @return how many elements I have
     */
    @Override
    public int size()
    {
        return elements.size();
    }

    /**
     * I guard the operations that need a top element
     * @throws EmptyStackException if I have no elements
     */
    private void checkEmpty()
    {
        if( isEmpty() )
        {
            throw new EmptyStackException();
        }
    }
}
