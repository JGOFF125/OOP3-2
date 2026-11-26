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

    /** Start with 10 spaces and 25 percent growth */
    public MyArrayList()
    {
        this( 10, 0.25 );
    }

    /**
     * Choose the starting space and growth rate
     *
     * @param initialCapacity the starting capacity
     * @param growthRate the positive decimal growth rate
     * @throws IllegalArgumentException if the settings are invalid
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
     * Count the stored elements
     * @return the number of elements
     */
    @Override
    public int size()
    {
        return size;
    }

    /** Release the stored elements and reset the count */
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
     * Insert an element at the chosen index
     * @param index where to insert it, from zero through size
     * @param toAdd the non-null value to add
     * @return true when added
     * @throws NullPointerException if null is passed
     * @throws IndexOutOfBoundsException if the index is invalid
     */
    @Override
    public boolean add( int index, String toAdd ) throws NullPointerException, IndexOutOfBoundsException
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

        // Make room for the value
        System.arraycopy( elements, index, elements, index + 1, size - index );
        elements[ index ] = toAdd;
        size++;
        return true;
    }

    /**
     * Add an element at the end
     * @param toAdd the non-null value to add
     * @return true when added
     * @throws NullPointerException if null is passed
     */
    @Override
    public boolean add( String toAdd ) throws NullPointerException
    {
        return add( size, toAdd );
    }

    /**
     * Append the other list in iterator order
     * @param toAdd the list to copy from
     * @return true if any elements are added
     * @throws NullPointerException if the list or an element is null
     */
    @Override
    public boolean addAll( ListADT toAdd ) throws NullPointerException
    {
        if( toAdd == null )
        {
            throw new NullPointerException( "Cannot add a null list" );
        }

        // Copy first so the list can add itself
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
     * Read an element without removing it
     * @param index the existing position to read
     * @return the value at the index
     * @throws IndexOutOfBoundsException if the index is invalid
     */
    @Override
    public String get( int index ) throws IndexOutOfBoundsException
    {
        checkIndex( index );
        return elements[ index ];
    }

    /**
     * Remove the element at an index
     * @param index the existing position to remove
     * @return the removed value
     * @throws IndexOutOfBoundsException if the index is invalid
     */
    @Override
    public String remove( int index ) throws IndexOutOfBoundsException
    {
        checkIndex( index );
        String removed = elements[ index ];
        System.arraycopy( elements, index + 1, elements, index, size - index - 1 );
        size--;

        // Clear the old last slot
        elements[ size ] = null;
        return removed;
    }

    /**
     * Remove the first matching value
     * @param toRemove the non-null value to find
     * @return the removed value, or null if no match is found
     * @throws NullPointerException if null is passed
     */
    @Override
    public String remove( String toRemove ) throws NullPointerException
    {
        int index = indexOf( toRemove );
        return index == -1 ? null : remove( index );
    }

    /**
     * Replace an element without changing the size
     * @param index the existing position to replace
     * @param toChange the non-null value to store
     * @return the previous value
     * @throws NullPointerException if null is passed
     * @throws IndexOutOfBoundsException if the index is invalid
     */
    @Override
    public String set( int index, String toChange ) throws NullPointerException, IndexOutOfBoundsException
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
     * Check whether the list is empty
     * @return true if empty
     */
    @Override
    public boolean isEmpty()
    {
        return size == 0;
    }

    /**
     * Check whether a value is present
     * @param toFind the non-null value to find
     * @return true if a match exists
     * @throws NullPointerException if null is passed
     */
    @Override
    public boolean contains( String toFind ) throws NullPointerException
    {
        return indexOf( toFind ) != -1;
    }

    /**
     * Find the first matching index
     * @param toFind the non-null value to find
     * @return the zero-based index, or -1 if absent
     * @throws NullPointerException if null is passed
     */
    @Override
    public int indexOf( String toFind ) throws NullPointerException
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
     * Copy the elements into an array in list order
     * @param toHold the array to reuse if it has enough space
     * @return the supplied array or a larger copy
     * @throws NullPointerException if the array is null
     */
    @Override
    public String[] toArray( String[] toHold ) throws NullPointerException
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
     * Create an independent iterator over the current elements
     * @return the iterator to traverse from first to last
     */
    @Override
    public Iterator iterator()
    {
        return new ArrayListIterator( elements, size );
    }

    /** Grow the array by the chosen rate, with at least one extra slot */
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
     * Check that an element already exists at the index
     * @param index the position to check
     * @throws IndexOutOfBoundsException if the index is invalid
     */
    private void checkIndex( int index )
    {
        if( index < 0 || index >= size )
        {
            throw new IndexOutOfBoundsException( "Invalid index: " + index );
        }
    }

    /** Keep the iterator's copy separate from later list changes */
    private static class ArrayListIterator implements Iterator
    {
        private final String[] snapshot;
        private int position;

        /**
         * Save the current values in a private array
         * @param source the array to copy
         * @param size number of elements to copy
         */
        private ArrayListIterator( String[] source, int size )
        {
            snapshot = Arrays.copyOf( source, size );
        }

        /**
         * Check the copy for another element
         * @return true if another value remains
         */
        @Override
        public boolean hasNext()
        {
            return position < snapshot.length;
        }

        /**
         * Read the next value from the copy
         * @return the next value
         * @throws NoSuchElementException if no elements remain
         */
        @Override
        public String next() throws NoSuchElementException
        {
            if( !hasNext() )
            {
                throw new NoSuchElementException( "No more elements" );
            }
            return snapshot[ position++ ];
        }
    }
}
