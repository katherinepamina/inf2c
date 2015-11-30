package bikescheme;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
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
    	Event e1 = new Event("31 22:10,C,i,m, a, b, c, d");
    	Event e2 = new Event("29 22:10,C,i,m, a, b, c, d");
    	assertFalse(e1.equals(e2));
    }
    @Test
    public void equals_testDeviceClassNotEqual() {
    	Event e1 = new Event("31 22:10,C,i,m, a, b, c, d");
    	Event e2 = new Event("31 22:10,D,i,m, a, b, c, d");
    	assertFalse(e1.equals(e2));
    }
    @Test
    public void equals_testInstanceNameNotEqual() {
    	Event e1 = new Event("31 22:10,C,i,m, a, b, c, d");
    	Event e2 = new Event("31 22:10,C,k,m, a, b, c, d");
    	assertFalse(e1.equals(e2));
    }
    @Test
    public void equals_testMessageArgsNotEqual() {
    	Event e1 = new Event("31 22:10,C,i,m, a, b, c, d, e");
    	Event e2 = new Event("31 22:10,C,i,m, a, b, c, d");
    	assertFalse(e1.equals(e2));
    }
    @Test
    public void equals_testIgnoreWhiteSpace() {
        Event e1 = new Event("1 00:00,C,i,m, a, b, c, d");
        Event e2 = new Event("1 00:00,C,i,m, a,b,c,d");
        assertTrue(e1.equals(e2));
    }
    @Test
    public void equals_testMessageArgs() {
    	Event e1 = new Event("31 22:10,C,i,m, c, b, a, d");
    	Event e2 = new Event("31 22:10,C,i,m, a, b, c, d");
    	assertFalse(e1.equals(e2));
    }
    @Test
    public void equals_testMessageArgs2() {
    	Event e1 = new Event("31 22:10,C,i,m, unordered-tuples,1,first,second,c, b, a, d");
    	Event e2 = new Event("31 22:10,C,i,m, unordered-tuples,1,first,second,a, b, c, d");
    	assertTrue(e1.equals(e2));
    }
    @Test
    public void equals_testMessageArgs3() {
    	Event e1 = new Event("31 22:10,C,i,m, unordered-tuples,1,first,second,f, b, a, d");
    	Event e2 = new Event("31 22:10,C,i,m, unordered-tuples,1,first,second,a, b, c, d");
    	assertFalse(e1.equals(e2));
    }
    @Test
    public void equals_testMessageArgs4() {
    	Event e1 = new Event("31 22:10,C,i,m, unordered-tuples,first,second,2,a,b,c,d,e,f");
    	Event e2 = new Event("31 22:10,C,i,m, unordered-tuples,first,second,1,a,b,c,d,e,f");
    	assertFalse(e1.equals(e2));
    }
    @Test
    public void equals_testMessageArgs5() {
    	Event e1 = new Event("1 00:00,C,i,m, unordered-tuples,2,first,second,a,b,c,d,e,f");
    	Event e2 = new Event("1 00:00,C,i,m, unordered-tuples,2,first,second,a,b,e,f,c,d");
    	assertTrue(e1.equals(e2));
    }
    @Test
    public void equals_testMessageArgs6() {
    	Event e1 = new Event("31 22:10,C,i,m, unordered-tuples,2,first,second,a,c,b,d,e,f");
    	Event e2 = new Event("31 22:10,C,i,m, unordered-tuples,2,first,second,a,b,e,f,c,d");
    	assertFalse(e1.equals(e2));
    }
    @Test
    public void equals_testHeaderTuples() {
    	Event e1 = new Event("31 22:10,C,i,m, unordered-tuples,2,one,two,a,b,c,d");
    	Event e2 = new Event("31 22:10,C,i,m, unordered-tuples,2,first,second,a,b,c,d");
    	assertFalse(e1.equals(e2));
    }
    
    //tests that 2 lists of diff lengths are not equal
    @Test
    public void listEqualsDiffLength() {
        String eventString = "1 00:00,Class,instance,message, a1, a2, a3, a4";
        Event e1 = new Event(eventString);
        Event e2 = new Event(eventString);
        Event e3 = new Event(eventString);
        ArrayList<Event> listone = new ArrayList<Event>();
        ArrayList<Event> listtwo = new ArrayList<Event>();
        listone.add(e1);
        listtwo.add(e2);
        listtwo.add(e3);

        assertFalse(Event.listEqual(listone, listtwo));
    }


    //tests that two singleton lists with different events are different 
    @Test
    public void listEqualsSingleDiffEvents() {
        String eventString = "1 00:00,Class,instance,message, a1, a2, a3, a4";
        Event e1 = new Event(eventString);
        eventString = "1 00:00,Class1,instance,message, a1, a2, a3, a4";
        Event e2 = new Event(eventString);
        ArrayList<Event> listone = new ArrayList<Event>();
        ArrayList<Event> listtwo = new ArrayList<Event>();
        listone.add(e1);
        listtwo.add(e2);

        assertFalse(Event.listEqual(listone, listtwo));
    }

    //tests that two singleton lists with same events are equal
    @Test
    public void listEqualsDiffEvents() {
        String eventString = "1 00:00,Class,instance,message, a1, a2, a3, a4";
        Event e1 = new Event(eventString);
        eventString = "1 00:00,Class,instance,message, a1, a2, a3, a4";
        Event e2 = new Event(eventString);
        ArrayList<Event> listone = new ArrayList<Event>();
        ArrayList<Event> listtwo = new ArrayList<Event>();
        listone.add(e1);
        listtwo.add(e2);

        assertTrue(Event.listEqual(listone, listtwo));
    }

    @Test
    public void listEqualsDiffEventsFalse() {
        String eventString = "1 00:00,Class,instance,message, a1, a2, a3, a4";
        Event e1 = new Event(eventString);
        eventString = "1 00:00,Class1,instance,message, a1, a2, a3, a4";
        Event e2 = new Event(eventString);
        ArrayList<Event> listone = new ArrayList<Event>();
        ArrayList<Event> listtwo = new ArrayList<Event>();
        listone.add(e1);
        listtwo.add(e2);

        assertFalse(Event.listEqual(listone, listtwo));
    }

    //test that list equals works when all events are the same and there's no same-time events
    @Test
    public void listEqualsSameEventsNoSameTimes() {
        String eventString = "1 00:00,Class,instance,message, a1, a2, a3, a4";
        Event e1 = new Event(eventString);
        eventString = "1 00:01,Class1,instance,message, a1, a2, a3, a4";
        Event e2 = new Event(eventString);
        eventString = "1 00:02,Class1,instance,message, a1, a2, a3, a4";
        Event e3 = new Event(eventString);
        eventString = "1 00:03,Class1,instance,message, a1, a2, a3, a4";
        Event e4 = new Event(eventString);

        eventString = "1 00:00,Class,instance,message, a1, a2, a3, a4";
        Event e5 = new Event(eventString);
        eventString = "1 00:01,Class1,instance,message, a1, a2, a3, a4";
        Event e6 = new Event(eventString);
        eventString = "1 00:02,Class1,instance,message, a1, a2, a3, a4";
        Event e7 = new Event(eventString);
        eventString = "1 00:03,Class1,instance,message, a1, a2, a3, a4";
        Event e8 = new Event(eventString);

        ArrayList<Event> listone = new ArrayList<Event>();
        ArrayList<Event> listtwo = new ArrayList<Event>();
        listone.add(e1);
        listone.add(e2);
        listone.add(e3);
        listone.add(e4);
        listtwo.add(e5);
        listtwo.add(e6);
        listtwo.add(e7);
        listtwo.add(e8);

        assertTrue(Event.listEqual(listone, listtwo));
    }


    //test that list equals works when all events are the same and there are same-time events in same order
    @Test
    public void listEqualsSameEventsSameTimes() {
        String eventString = "1 00:00,Class,instance,message, a1, a2, a3, a4";
        Event e1 = new Event(eventString);
        eventString = "1 00:01,Class1,instance,message, a1, a2, a3, a4";
        Event e2 = new Event(eventString);
        eventString = "1 00:01,Class1,instance,message, a1, a2, a3, a4";
        Event e3 = new Event(eventString);
        eventString = "1 00:03,Class1,instance,message, a1, a2, a3, a4";
        Event e4 = new Event(eventString);

        eventString = "1 00:00,Class,instance,message, a1, a2, a3, a4";
        Event e5 = new Event(eventString);
        eventString = "1 00:01,Class1,instance,message, a1, a2, a3, a4";
        Event e6 = new Event(eventString);
        eventString = "1 00:01,Class1,instance,message, a1, a2, a3, a4";
        Event e7 = new Event(eventString);
        eventString = "1 00:03,Class1,instance,message, a1, a2, a3, a4";
        Event e8 = new Event(eventString);

        ArrayList<Event> listone = new ArrayList<Event>();
        ArrayList<Event> listtwo = new ArrayList<Event>();
        listone.add(e1);
        listone.add(e2);
        listone.add(e3);
        listone.add(e4);
        listtwo.add(e5);
        listtwo.add(e6);
        listtwo.add(e7);
        listtwo.add(e8);

        assertTrue(Event.listEqual(listone, listtwo));
    }

    //test that listEqual returns true when lists are the same but there are same-time events in diff order
    @Test
    public void listEqualsDiffEventsSameTimesRearranged() {
        String eventString = "1 00:00,Class,instance,message, a1, a2, a3, a4";
        Event e1 = new Event(eventString);
        eventString = "1 00:01,Class1,instance,message, a1, a2, a3, a4";
        Event e2 = new Event(eventString);
        eventString = "1 00:01,Class1,instance,message, a1, a2, a3, a4";
        Event e3 = new Event(eventString);
        eventString = "1 00:01,Class2,instance,message, a1, a2, a3, a4";
        Event e4 = new Event(eventString);
        eventString = "1 00:03,Class1,instance,message, a1, a2, a3, a4";
        Event e5 = new Event(eventString);

        eventString = "1 00:00,Class,instance,message, a1, a2, a3, a4";
        Event e6 = new Event(eventString);
        eventString = "1 00:01,Class1,instance,message, a1, a2, a3, a4";
        Event e7 = new Event(eventString);
        eventString = "1 00:01,Class2,instance,message, a1, a2, a3, a4";
        Event e8 = new Event(eventString);
        eventString = "1 00:01,Class1,instance,message, a1, a2, a3, a4";
        Event e9 = new Event(eventString);
        eventString = "1 00:03,Class1,instance,message, a1, a2, a3, a4";
        Event e10 = new Event(eventString);

        ArrayList<Event> listone = new ArrayList<Event>();
        ArrayList<Event> listtwo = new ArrayList<Event>();
        listone.add(e1);
        listone.add(e2);
        listone.add(e3);
        listone.add(e4);
        listone.add(e5);
        listtwo.add(e6);
        listtwo.add(e7);
        listtwo.add(e8);
        listtwo.add(e9);
        listtwo.add(e10);

        assertTrue(Event.listEqual(listone, listtwo));
    }

    //test that listEqual returns false when lists are different but there are same-time events
    //differences are in Class
    @Test
    public void listEqualsDiffEventsSameTimesRearrangedFalse() {
        String eventString = "1 00:00,Class,instance,message, a1, a2, a3, a4";
        Event e1 = new Event(eventString);
        eventString = "1 00:01,Class1,instance,message, a1, a2, a3, a4";
        Event e2 = new Event(eventString);
        eventString = "1 00:01,Class1,instance,message, a1, a2, a3, a4";
        Event e3 = new Event(eventString);
        eventString = "1 00:01,Class2,instance,message, a1, a2, a3, a4";
        Event e4 = new Event(eventString);
        eventString = "1 00:03,Class1,instance,message, a1, a2, a3, a4";
        Event e5 = new Event(eventString);

        eventString = "1 00:00,Class,instance,message, a1, a2, a3, a4";
        Event e6 = new Event(eventString);
        eventString = "1 00:01,Class1,instance,message, a1, a2, a3, a4";
        Event e7 = new Event(eventString);
        eventString = "1 00:01,Class3,instance,message, a1, a2, a3, a4";
        Event e8 = new Event(eventString);
        eventString = "1 00:01,Class1,instance,message, a1, a2, a3, a4";
        Event e9 = new Event(eventString);
        eventString = "1 00:03,Class1,instance,message, a1, a2, a3, a4";
        Event e10 = new Event(eventString);

        ArrayList<Event> listone = new ArrayList<Event>();
        ArrayList<Event> listtwo = new ArrayList<Event>();
        listone.add(e1);
        listone.add(e2);
        listone.add(e3);
        listone.add(e4);
        listone.add(e5);
        listtwo.add(e6);
        listtwo.add(e7);
        listtwo.add(e8);
        listtwo.add(e9);
        listtwo.add(e10);

        assertFalse(Event.listEqual(listone, listtwo));
    }
    
}
