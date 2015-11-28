package bikescheme;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.util.Date;
import java.util.List;

import org.junit.Test;

public class EventTest {

    @Test
    public void test1() {
        assertEquals(
                new Event("1 00:00,C,i,m, a, b, c, d"),
                new Event("1 00:00,C,i,m, a, b, c, d") );
    }
    @Test
    public void test2() {
        assertNotEquals(
                new Event("1 00:00,C,i,m, a, b, c, d"),
                new Event("1 00:00,C,i,m, a, b, c, e") );
    }
    @Test
    public void equals_testDateNotEqual() {
    	assertNotEquals(
    			new Event("31 22:10,C,i,m, a, b, c, d"),
    			new Event("29 22:10,C,i,m, a, b, c, d") );
    }
    @Test
    public void equals_testDeviceClassNotEqual() {
    	assertNotEquals(
    			new Event("31 22:10,C,i,m, a, b, c, d"),
    			new Event("31 22:10,D,k,m, a, b, c, d") );
    }
    @Test
    public void equals_testMessageArgsNotEqual() {
    	assertNotEquals(
    			new Event("31 22:10,C,i,m, a, b, c, d, e"),
    			new Event("31 22:10,C,i,m, a, b, c, d") );
    }
    @Test
    public void equals_ignoreWhiteSpace() {
        assertEquals(
                new Event("1 00:00,C,i,m, a, b, c, d"),
                new Event("1 00:00,C,i,m, a,b,c,d") );
    }
    @Test
    public void equals_testMessageArgs() {
    	assertNotEquals(
    			new Event("31 22:10,C,i,m, c, b, a, d"),
    			new Event("31 22:10,C,i,m, a, b, c, d") );
    }
    @Test
    public void equals_testMessageArgs2() {
    	assertEquals(
    			new Event("31 22:10,C,i,m, unordered-tuples,1,first,second,c, b, a, d"),
    			new Event("31 22:10,C,i,m, unordered-tuples,1,first,second,a, b, c, d") );
    }
    @Test
    public void equals_testMessageArgs3() {
    	assertNotEquals(
    			new Event("31 22:10,C,i,m, unordered-tuples,1,first,second,f, b, a, d"),
    			new Event("31 22:10,C,i,m, unordered-tuples,1,first,second,a, b, c, d") );
    }
    @Test
    public void equals_testMessageArgs4() {
    	assertNotEquals(
    			new Event("31 22:10,C,i,m, unordered-tuples,first,second,2,a,b,c,d,e,f"),
    			new Event("31 22:10,C,i,m, unordered-tuples,first,second,1,a,b,c,d,e,f") );
    }
    @Test
    public void equals_testMessageArgs5() {
    	assertEquals(
    			new Event("1 00:00,C,i,m, unordered-tuples,2,first,second,a,b,c,d,e,f"),
    			new Event("1 00:00,C,i,m, unordered-tuples,2,first,second,a,b,e,f,c,d") );
    }
    @Test
    public void equals_testMessageArgs6() {
    	assertNotEquals(
    			new Event("31 22:10,C,i,m, unordered-tuples,2,first,second,a,c,b,d,e,f"),
    			new Event("31 22:10,C,i,m, unordered-tuples,2,first,second,a,b,e,f,c,d") );
    }
    @Test
    public void equals_testHeaderTuples() {
    	assertNotEquals(
    			new Event("31 22:10,C,i,m, unordered-tuples,2,one,two,a,b,c,d"),
    			new Event("31 22:10,C,i,m, unordered-tuples,2,first,second,a,b,c,d") );
    }
    
}
