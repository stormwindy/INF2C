/**
 * 
__________________|      |____________________________________________
     ,--.    ,--.          ,--.   ,--.
    |oo  | _  \  `.       | oo | |  oo|
o  o|~~  |(_) /   ;       | ~~ | |  ~~|o  o  o  o  o  o  o  o  o  o  o
    |/\/\|   '._,'        |/\/\| |/\/\|
__________________        ____________________________________________
                  |      |
 */
package auctionhouse;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author pbj
 *
 */
public class AuctionHouseImp implements AuctionHouse {
    private BuyerInfo buyerInf = new BuyerInfo();
    private SellerInfo sellerInf = new SellerInfo();
    private List<CatalogueEntry> lotList = new ArrayList();
    private static Logger logger = Logger.getLogger("auctionhouse");
    private static final String LS = System.lineSeparator();
    
    private String startBanner(String messageName) {
        return  LS 
          + "-------------------------------------------------------------" + LS
          + "MESSAGE IN: " + messageName + LS
          + "-------------------------------------------------------------";
    }
   
    public AuctionHouseImp(Parameters parameters) {

    }

    public Status registerBuyer(
            String name,
            String address,
            String bankAccount,
            String bankAuthCode) {
        logger.fine(startBanner("registerBuyer " + name));
        if(!name.matches("[a-z A-Z]")) {
            return Status.error("Name invalid");
        }
        if(!address.matches("[a-z A-Z]")) {
            return Status.error("Address Invalid.");
        }
        if(bankAccount.matches("[^a-z A-Z0-9]") || bankAuthCode.matches("[^a-z A-Z0-9]")) {
            return Status.error("Account or authentication code invalid.");
        }
        return buyerInf.registerNewBuyer(name, address, bankAccount, bankAuthCode);
    }

    public Status registerSeller(
            String name,
            String address,
            String bankAccount) {
            if(!name.matches("[a-z A-Z]")) {
                return Status.error("Name invalid");
            }
            if(!address.matches("[a-z A-Z]")) {
                return Status.error("Address Invalid.");
            }
            if(bankAccount.matches("[^a-z A-Z0-9]") || bankAuthCode.matches("[^a-z A-Z0-9]")) {
                return Status.error("Account or authentication code invalid.");
            }
        logger.fine(startBanner("registerSeller " + name));
        return sellerInf.registerNewSeller(name, bankAccount, address);
    }

    public Status addLot(
            String sellerName,
            int number,
            String description,
            Money reservePrice) {
        logger.fine(startBanner("addLot " + sellerName + " " + number));
        if(!sellerInf.sellerInfo.containsKey(sellerName)) {
            return Status.error("User does not exist");
        }
        boolean exists = false;
        for (int i = 0; i < lotList.size(); i++) {
            if  (lotList.get(i).equals(new CatalogueEntry(number, description, LotStatus.UNSOLD))) {
                exists = true;
                return Status.error("This lot already exists");
            }
        }

        lotList.add(new CatalogueEntry(number, description , LotStatus.UNSOLD));
        return Status.OK();    
    }

    public List<CatalogueEntry> viewCatalogue() {
        logger.fine(startBanner("viewCatalog"));
        
        List<CatalogueEntry> catalogue = new ArrayList<CatalogueEntry>();
        catalogue = lotList;
        logger.fine("Catalogue: " + catalogue.toString());
        return catalogue;
    }

    //Requires further implementation.
    public Status noteInterest(
            String buyerName,
            int lotNumber) {
        logger.fine(startBanner("noteInterest " + buyerName + " " + lotNumber));
        if(!buyerInf.buyerList.containsKey(buyerName)) {
            return Status.error("User does not exist. Please register.");
        }

        if(!lotList.containsKey){

        }
        return Status.OK();   
    }

    public Status openAuction(
            String auctioneerName,
            String auctioneerAddress,
            int lotNumber) {
        logger.fine(startBanner("openAuction " + auctioneerName + " " + lotNumber));
        
        return Status.OK();
    }

    public Status makeBid(
            String buyerName,
            int lotNumber,
            Money bid) {
        logger.fine(startBanner("makeBid " + buyerName + " " + lotNumber + " " + bid));

        return Status.OK();    
    }

    public Status closeAuction(
            String auctioneerName,
            int lotNumber) {
        logger.fine(startBanner("closeAuction " + auctioneerName + " " + lotNumber));
 
        return Status.OK();  
    }
}
