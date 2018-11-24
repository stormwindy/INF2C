/**
 * 
 */
package auctionhouse;

/**
 * @author pbj
 */

/**
* <h1>Money implementation</h1>
* This class implements all necessary methods for money transactions in the course work.
* The Money class also implements the comparable interface which allows for comparisons between instances of the Money class.
*/

public class Money implements Comparable<Money> {
 
	/** Represents the monetary value of the particular instance of Money.
	*/
	private double value;
    
	private static long getNearestPence(double pounds) {
        return Math.round(pounds * 100.0);
    }
 
    private static double normalise(double pounds) {
        return getNearestPence(pounds)/100.0;
        
    }
 
    public Money(String pounds) {
        value = normalise(Double.parseDouble(pounds));
    }
    
    private Money(double pounds) {
        value = pounds;
    }
    
    /** Adds the value of one instance of Money to another.
	 * @param m The instance of Money to be added to the current instance of Money.
	 * @return The current instance of Money to which the value of the other instance of Money is added to.
	*/
    public Money add(Money m) {
        return new Money(value + m.value);
    }
    
    /** Subtracts the value of one instance of Money from the another.
	 * @param m The instance of Money to be subtracted from the current instance of Money.
	 * @return The current instance of Money to which the value of the other instance of Money has been subtracted from.
	*/
    public Money subtract(Money m) {
        return new Money(value - m.value);
    }
 
    /** Adds a percentage of the current instance of Money's value to itself..
	 * @param percent A double that entails the percentage of Money's value to be added to itself.
	 * @return The current instance of Money but with a larger value field.
	*/
    public Money addPercent(double percent) {
        return new Money(normalise(value * (1 + percent/100.0)));
    }
     
    @Override
    public String toString() {
        return String.format("%.2f", value);
        
    }
    /** Compares the values of two different instances of Money.
	 * @param m The instance of Money to be compared to the current instance of Money.
	 * @return 1 if the current instance of Money' value is larger than m's value, 0 if they are equal and -1 if m's value is larger than the current instance value.
	*/
    public int compareTo(Money m) {
        return Long.compare(getNearestPence(value),  getNearestPence(m.value)); 
    }
    
    /** Tests whether the value of the current instance of Money is less than or equal to the value of some supplied instance of Money.
	 * @param m The instance of Money to be compared to the current instance of Money.
	 * @return 1 if the current instance of Money's value is smaller or equal to m's value.
	*/
    public Boolean lessEqual(Money m) {
        return compareTo(m) <= 0;
    }
    
    /** Tests whether the value of the current instance of Money equal to another instance of Money.
     * ALso overrides the "equals" method of the comparable interface.
	 * @param o The object (instance of Money) to be compared to the current object.
	 * @return 1 if the instances of the Money object are equal.
	*/
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Money)) return false;
        Money oM = (Money) o;
        return compareTo(oM) == 0;       
    }
    
    @Override
    public int hashCode() {
        return Long.hashCode(getNearestPence(value));
    }
      

}
