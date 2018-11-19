 /* 
                           o                    
                       _---|         _ _ _ _ _ 
                    o   ---|     o   ]-I-I-I-[ 
   _ _ _ _ _ _  _---|      | _---|    \ ` ' / 
   ]-I-I-I-I-[   ---|      |  ---|    |.   | 
    \ `   '_/       |     / \    |    | /^\| 
     [*]  __|       ^    / ^ \   ^    | |*|| 
     |__   ,|      / \  /    `\ / \   | ===| 
  ___| ___ ,|__   /    /=_=_=_=\   \  |,  _|
  I_I__I_I__I_I  (====(_________)___|_|____|____
  \-\--|-|--/-/  |     I  [ ]__I I_I__|____I_I_| 
   |[]      '|   | []  |`__  . [  \-\--|-|--/-/  
   |.   | |' |___|_____I___|___I___|---------| 
  / \| []   .|_|-|_|-|-|_|-|_|-|_|-| []   [] | 
 <===>  |   .|-=-=-=-=-=-=-=-=-=-=-|   |    / \  
 ] []|`   [] ||.|.|.|.|.|.|.|.|.|.||-      <===> 
 ] []| ` |   |/////////\\\\\\\\\\.||__.  | |[] [ 
 <===>     ' ||||| |   |   | ||||.||  []   <===>
  \T/  | |-- ||||| | O | O | ||||.|| . |'   \T/ 
   |      . _||||| |   |   | ||||.|| |     | |
../|' v . | .|||||/____|____\|||| /|. . | . ./
.|//\............/...........\........../../\\\
*/


package auctionhouse;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class BuyerInfo {
   static Map<String, Buyer> buyerList;
   public BuyerInfo() {
       buyerList = new HashMap<>();
   }

    public Status registerNewBuyer(String name, String address, String bankAccount, String authCode) {
        if(!buyerList.containsKey(name)) {
            buyerList.put(name, new Buyer(name, address, bankAccount, authCode));
            return Status.OK();
        }
        return Status.error("User already exists.");
    }

    public Bool buyerExists(String name) {
        if (buyerList.containsKey(name)) {
            return true;
        }
        return false;
    }

    public int getBankAccount(String name) {
        if(buyerList.containsKey(name)) {
            Buyer a = buyerList.get(name);
            return a.bankAccount;
        } else {
            return -1; //No bank details
        }
    }
}

class Buyer {
    String name;
    String address;
    String bankAccount;
    String authCode;
    public Buyer(String name, String address, String bankAccount, String authCode) {
        this.address = address;
        this.authCode = authCode;
        this.bankAccount = bankAccount;
        this.name = name;
    }
}