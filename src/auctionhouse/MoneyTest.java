/**
 *
 */
package auctionhouse;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 * @author pbj
 *
 */
public class MoneyTest {

    @Test
    public void testAdd() {
        Money val1 = new Money("12.34");
        Money val2 = new Money("0.66");
        Money result = val1.add(val2);
        assertEquals("13.00", result.toString());
    }

    /*
     ***********************************************************************
     * BEGIN MODIFICATION AREA
     ***********************************************************************
     * Add all your JUnit tests for the Money class below.
     */
    @Test
    public void testSubtract() {
        Money val1 = new Money("12.34");
        Money val2 = new Money("0.34");
        Money result = val1.subtract(val2);
        assertEquals("12.00", result.toString());
    }

    @Test
    public void testAddPercent() {
        Money val1 = new Money("12.00");
        Money result = val1.addPercent(50.0);
        assertEquals("18.00", result.toString());
    }

    @Test
    public void testCompareTo() {
        Money val1 = new Money("12.10");
        Money val2 = new Money("12.10");
        Money val3 = new Money("12.00");
        Money val4 = new Money("12.20");
        assertEquals(0, val1.compareTo(val2));
        assertEquals(-1, val1.compareTo(val4));
        assertEquals(1, val1.compareTo(val3));

    }

    @Test
    public void testLessEqual() {
        Money val1 = new Money("12.10");
        Money val2 = new Money("12.10");
        Money val3 = new Money("12.00");
        Money val4 = new Money("12.20");
        assertEquals(false, val1.lessEqual(val3));
        assertEquals(true, val1.lessEqual(val2));
        assertEquals(true, val1.lessEqual(val4));

    }

    @Test
    public void testEquals() {
        Money val1 = new Money("12.10");
        Money val2 = new Money("12.10");
        Money val3 = new Money("12.00");
        assertEquals(true, val1.equals(val2));
        assertEquals(false, val1.equals(val3));

    }




    /*
     * Put all class modifications above.
     ***********************************************************************
     * END MODIFICATION AREA
     ***********************************************************************
     */


}