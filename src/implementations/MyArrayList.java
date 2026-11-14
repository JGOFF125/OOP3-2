package implementations;

import java.util.Arrays;
import java.util.NoSuchElementException;
import utilities.Iterator;
import utilities.ListADT;

/**
 * A dynamic array-based implementation of the {@link ListADT} interface,
 * for use in CPRG 304 at SAIT.
 * <p>
 * This class stores {@link String} elements in an internally managed array
 * that grows automatically when capacity is reached. The growth amount is
 * determined by a configurable growth rate expressed as a decimal percentage
 * of the current array length (e.g., {@code 0.25} means 25% growth).
 * All index parameters are zero-based unless otherwise noted.
 * </p>
 *
 * <p><strong>Design Note – String-Specific Implementation:</strong><br>
 * This class is intentionally designed to store {@link String} elements only.
 * A fully reusable, type-safe design would use Java generics (e.g.,
 * {@code MyArrayList<E>}), allowing any object type to be stored in the list.
 * Generic programming is outside the scope of CPRG 304 and has therefore
 * been omitted. All element fields, parameters, and return types are fixed
 * as {@link String} throughout this class and its implementations.
 * </p>
 */
public class MyArrayList implements ListADT
{
    private String[] elements;
    private int size;
    private final double growthRate;

    /** I start with 10 spaces and 25 percent growth */
    public MyArrayList()
    {
        this( 10, 0.25 );
    }

    /**
     * I choose the starting space and growth rate
     *
     * @param initialCapacity how many spaces I start with
     * @param growthRate the positive decimal rate I grow by
     * @throws IllegalArgumentException if I get invalid settings
     */
    public MyArrayList( int initialCapacity, double growthRate )
    {
        if( initialCapacity <= 0 || !Double.isFinite( growthRate ) || growthRate <= 0 )
        {
            throw new IllegalArgumentException( "Capacity and growth rate must be positive" );
        }
        elements = new String[ initialCapacity ];
        this.growthRate = growthRate;
    }

    /**
     * I count the stored elements
     * @return how many elements I have
     */
    @Override
    public int size()
    {
        return size;
    }

    /** I release the stored elements and reset the count */
    @Override
    public void clear()
    {
        for( int i = 0; i < size; i++ )
        {
            elements[ i ] = null;
        }
        size = 0;
    }

    /**
     * I insert an element at the chosen index
     * @param index where I insert it, from zero through size
     * @param toAdd the non-null value I add
     * @return true when I add it
     * @throws NullPointerException if I get null
     * @throws IndexOutOfBoundsException if I get an invalid index
     */
    @Override
    public boolean add( int index, String toAdd )
    {
        if( toAdd == null )
        {
            throw new NullPointerException( "Cannot add null" );
        }
        if( index < 0 || index > size )
        {
            throw new IndexOutOfBoundsException( "Invalid index: " + index );
        }
        if( size == elements.length )
        {
            grow();
        }

        // I make room before inserting
        System.arraycopy( elements, index, elements, index + 1, size - index );
        elements[ index ] = toAdd;
        size++;
        return true;
    }

    /**
     * I add an element at the end
     * @param toAdd the non-null value I add
     * @return true when I add it
     * @throws NullPointerException if I get null
     */
    @Override
    public boolean add( String toAdd )
    {
        return add( size, toAdd );
    }

    /**
     * I append the other list in iterator order
     * @param toAdd the list I copy from
     * @return true if I add any elements
     * @throws NullPointerException if I get a null list or element
     */
    @Override
    public boolean addAll( ListADT toAdd )
    {
        if( toAdd == null )
        {
            throw new NullPointerException( "Cannot add a null list" );
        }

        // I copy first so adding my own list still ends
        Iterator iterator = toAdd.iterator();
        boolean changed = false;
        while( iterator.hasNext() )
        {
            add( iterator.next() );
            changed = true;
        }
        return changed;
    }

    /**
     * I read an element without removing it
     * @param index the existing position I read
     * @return the value I find
     * @throws IndexOutOfBoundsException if I get an invalid index
     */
    @Override
    public String get( int index )
    {
        checkIndex( index );
        return elements[ index ];
    }

    /**
     * I remove the element at an index
     * @param index the existing position I remove
     * @return the value I removed
     * @throws IndexOutOfBoundsException if I get an invalid index
     */
    @Override
    public String remove( int index )
    {
        checkIndex( index );
        String removed = elements[ index ];
        System.arraycopy( elements, index + 1, elements, index, size - index - 1 );
        size--;

        // I clear the old last slot after shifting
        elements[ size ] = null;
        return removed;
    }

    /**
     * I remove the first matching value
     * @param toRemove the non-null value I look for
     * @return the stored value I removed, or null if I find no match
     * @throws NullPointerException if I get null
     */
    @Override
    public String remove( String toRemove )
    {
        int index = indexOf( toRemove );
        return index == -1 ? null : remove( index );
    }

    /**
     * I replace an element without changing the size
     * @param index the existing position I replace
     * @param toChange the non-null value I store
     * @return the previous value I replaced
     * @throws NullPointerException if I get null
     * @throws IndexOutOfBoundsException if I get an invalid index
     */
    @Override
    public String set( int index, String toChange )
    {
        if( toChange == null )
        {
            throw new NullPointerException( "Cannot store null" );
        }
        checkIndex( index );
        String previous = elements[ index ];
        elements[ index ] = toChange;
        return previous;
    }

    /**
     * I check whether the list is empty
     * @return true if I have no elements
     */
    @Override
    public boolean isEmpty()
    {
        return size == 0;
    }

    /**
     * I check whether a value is present
     * @param toFind the non-null value I look for
     * @return true if I find a match
     * @throws NullPointerException if I get null
     */
    @Override
    public boolean contains( String toFind )
    {
        return indexOf( toFind ) != -1;
    }

    /**
     * I find the first matching index
     * @param toFind the non-null value I look for
     * @return the zero-based index I find, or -1 if absent
     * @throws NullPointerException if I get null
     */
    @Override
    public int indexOf( String toFind )
    {
        if( toFind == null )
        {
            throw new NullPointerException( "Cannot search for null" );
        }
        for( int i = 0; i < size; i++ )
        {
            if( toFind.equals( elements[ i ] ) )
            {
                return i;
            }
        }
        return -1;
    }

    /**
     * I copy the elements into an array in list order
     * @param toHold the array I reuse if it has enough space
     * @return the supplied array or a larger copy I create
     * @throws NullPointerException if I get a null array
     */
    @Override
    public String[] toArray( String[] toHold )
    {
        if( toHold == null )
        {
            throw new NullPointerException( "Cannot copy into null" );
        }
        if( toHold.length < size )
        {
            return Arrays.copyOf( elements, size );
        }
        System.arraycopy( elements, 0, toHold, 0, size );
        if( toHold.length > size )
        {
            toHold[ size ] = null;
        }
        return toHold;
    }

    /**
     * I create an independent iterator over the current elements
     * @return the iterator I can traverse from first to last
     */
    @Override
    public Iterator iterator()
    {
        return new ArrayListIterator( elements, size );
    }

    /** I grow the array by the chosen rate, with at least one extra slot */
    private void grow()
    {
        long extra = Math.max( 1L, (long) Math.ceil( elements.length * growthRate ) );
        if( extra > Integer.MAX_VALUE - elements.length )
        {
            throw new OutOfMemoryError( "List capacity is too large" );
        }
        elements = Arrays.copyOf( elements, elements.length + (int) extra );
    }

    /**
     * I check that an element already exists at the index
     * @param index the position I check
     * @throws IndexOutOfBoundsException if I get an invalid index
     */
    private void checkIndex( int index )
    {
        if( index < 0 || index >= size )
        {
            throw new IndexOutOfBoundsException( "Invalid index: " + index );
        }
    }

    /** I keep my iterator's copy separate from later list changes */
    private static class ArrayListIterator implements Iterator
    {
        private final String[] snapshot;
        private int position;

        /**
         * I save the current values in a private array
         * @param source the array I copy
         * @param size how many elements I copy
         */
        private ArrayListIterator( String[] source, int size )
        {
            snapshot = Arrays.copyOf( source, size );
        }

        /**
         * I check my copy for another element
         * @return true if I have another value
         */
        @Override
        public boolean hasNext()
        {
            return position < snapshot.length;
        }

        /**
         * I read the next value from my copy
         * @return the next value I read
         * @throws NoSuchElementException if I reach the end
         */
        @Override
        public String next()
        {
            if( !hasNext() )
            {
                throw new NoSuchElementException( "No more elements" );
            }
            return snapshot[ position++ ];
        }
    }
}
