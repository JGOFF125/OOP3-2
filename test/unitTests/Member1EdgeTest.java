package unitTests;

import static org.junit.Assert.*;

import java.util.NoSuchElementException;
import org.junit.Test;
import implementations.MyArrayList;
import implementations.MyStack;
import utilities.Iterator;

/** Check resizing and iterator cases beyond the supplied tests */
public class Member1EdgeTest
{
    /** Keep every value through repeated resizing */
    @Test
    public void listKeepsValuesAfterGrowing()
    {
        MyArrayList list = new MyArrayList( 1, 0.25 );
        for( int i = 0; i < 10000; i++ )
        {
            list.add( String.valueOf( i ) );
        }
        assertEquals( 10000, list.size() );
        for( int i = 0; i < list.size(); i++ )
        {
            assertEquals( String.valueOf( i ), list.get( i ) );
        }
    }

    /** Make room at the front when the array is full */
    @Test
    public void insertIntoFullArrayKeepsOrder()
    {
        MyArrayList list = new MyArrayList( 2, 0.25 );
        list.add( "B" );
        list.add( "C" );
        list.add( 0, "A" );
        assertArrayEquals( new String[] { "A", "B", "C" }, list.toArray( new String[ 0 ] ) );
        assertEquals( "B", list.remove( 1 ) );
        assertArrayEquals( new String[] { "A", "C" }, list.toArray( new String[ 0 ] ) );
    }

    /** Add the same list once without looping forever */
    @Test( timeout = 2000 )
    public void addAllWithSameListAppendsOneCopy()
    {
        MyArrayList list = new MyArrayList();
        list.add( "A" );
        list.add( "B" );
        assertTrue( list.addAll( list ) );
        assertArrayEquals( new String[] { "A", "B", "A", "B" }, list.toArray( new String[ 0 ] ) );
    }

    /** Report no change when the source is empty */
    @Test
    public void addAllEmptyListReturnsFalse()
    {
        MyArrayList list = new MyArrayList();
        list.add( "A" );
        assertFalse( list.addAll( new MyArrayList() ) );
        assertEquals( 1, list.size() );
    }

    /** Keep the original iterator values after changing the list */
    @Test
    public void listIteratorKeepsItsSnapshot()
    {
        MyArrayList list = new MyArrayList( 2, 0.25 );
        list.add( "A" );
        list.add( "B" );
        Iterator iterator = list.iterator();
        list.set( 0, "changed" );
        list.remove( 1 );
        list.add( "C" );
        list.add( "D" );
        list.clear();
        assertEquals( "A", iterator.next() );
        assertEquals( "B", iterator.next() );
        assertFalse( iterator.hasNext() );
    }

    /** Keep the stack iterator after popping and clearing */
    @Test
    public void stackIteratorKeepsItsSnapshot()
    {
        MyStack stack = new MyStack();
        stack.push( "A" );
        stack.push( "B" );
        Iterator iterator = stack.iterator();
        stack.pop();
        stack.push( "C" );
        stack.clear();
        assertEquals( "A", iterator.next() );
        assertEquals( "B", iterator.next() );
        assertFalse( iterator.hasNext() );
    }

    /** Reject reads past an empty iterator */
    @Test( expected = NoSuchElementException.class )
    public void emptyIteratorThrowsAtEnd()
    {
        new MyArrayList().iterator().next();
    }

    /** Reject reads after the final stack iterator value */
    @Test( expected = NoSuchElementException.class )
    public void exhaustedStackIteratorThrowsAtEnd()
    {
        MyStack stack = new MyStack();
        stack.push( "A" );
        Iterator iterator = stack.iterator();
        iterator.next();
        iterator.next();
    }

    /** Keep returned arrays separate from list storage */
    @Test
    public void arrayCopiesDoNotExposeStorage()
    {
        MyArrayList list = new MyArrayList();
        list.add( "A" );
        String[] copy = list.toArray( new String[ 0 ] );
        copy[ 0 ] = "changed";
        assertEquals( "A", list.get( 0 ) );
        String[] holder = { "old", "old", "keep" };
        assertSame( holder, list.toArray( holder ) );
        assertArrayEquals( new String[] { "A", null, "keep" }, holder );
        holder[ 0 ] = "changed again";
        assertEquals( "A", list.get( 0 ) );
    }

    /** Check LIFO order after growing the stack */
    @Test
    public void largeStackPopsInReverseOrder()
    {
        MyStack stack = new MyStack();
        for( int i = 0; i < 10000; i++ )
        {
            stack.push( String.valueOf( i ) );
        }
        for( int i = 9999; i >= 0; i-- )
        {
            assertEquals( String.valueOf( i ), stack.pop() );
        }
        assertTrue( stack.isEmpty() );
    }

    /** Compare String contents and leave both stacks unchanged */
    @Test
    public void equalityUsesValuesWithoutChangingStacks()
    {
        MyStack first = new MyStack();
        MyStack second = new MyStack();
        first.push( new String( "same" ) );
        second.push( new String( "same" ) );
        assertTrue( first.equals( second ) );
        assertFalse( first.equals( null ) );
        assertEquals( 1, first.size() );
        assertEquals( 1, second.size() );
        assertEquals( "same", first.peek() );
        assertEquals( "same", second.peek() );
    }

    /** Keep growing even when the chosen rate rounds below one slot */
    @Test
    public void tinyGrowthStillAddsSpace()
    {
        MyArrayList list = new MyArrayList( 1, 0.001 );
        for( int i = 0; i < 20; i++ )
        {
            list.add( "value" );
        }
        assertEquals( 20, list.size() );
        assertEquals( "value", list.get( 19 ) );
    }
}
