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
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import auctionhouse.Status.Kind;

/**
 * @author pbj
 *
 */
public class AuctionHouseImp implements AuctionHouse {
    private BuyerInfo buyerInf = new BuyerInfo();
    private SellerInfo sellerInf = new SellerInfo();
    private HashMap<Integer, CatalogueEntry> lotList = new HashMap<Integer, CatalogueEntry>();
    private HashMap<Integer, AuctionProcess> currentAuctions = new HashMap<Integer, AuctionProcess>();
    private static Logger logger = Logger.getLogger("auctionhouse");
    private static final String LS = System.lineSeparator();
    private HashMap<Integer, Money> reservePrices = new HashMap<Integer, Money>();
    private Parameters parameters;
    
    private String startBanner(String messageName) {
        return  LS 
          + "-------------------------------------------------------------" + LS
          + "MESSAGE IN: " + messageName + LS
          + "-------------------------------------------------------------";
    }
   
    public AuctionHouseImp(Parameters parameters) {
        this.parameters = parameters;
    }

    public Status registerBuyer(
            String name,
            String address,
            String bankAccount,
            String bankAuthCode) {
        logger.finer("Entering");
        if(name == null) {
            return Status.error("Name invalid");
        }
        if(address == null) {
            return Status.error("Address Invalid.");
        }
        if(bankAccount == null) {
            return Status.error("Account or authentication code invalid.");
        }
        logger.fine(startBanner("registerBuyer " + name));
        return buyerInf.registerNewBuyer(name, address, bankAccount, bankAuthCode);
    }

    public Status registerSeller(
            String name,
            String address,
            String bankAccount) {
        logger.finer("Entering");
            if(name == null) {
                return Status.error("Name invalid");
            }
            if(address == null) {
                return Status.error("Address Invalid.");
            }
            if(bankAccount == null) {
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
        logger.finer("Entering");
        if(!sellerInf.sellerInfo.containsKey(sellerName)) {
            return Status.error("User does not exist");
        }
        logger.finer("For loop");
/*        for (int i = 0; i < lotList.size(); i++) {
        	logger.finer("Loop" + i);
            if  (lotList.containsKey(i) && lotList.get(i).equals(new CatalogueEntry(number, description, LotStatus.UNSOLD
                    ))) {
                return Status.error("This lot already exists");
            }
            if (lotList.containsKey(i)) {
                return Status.error("This lot number already exists. Please choose a different number");
            }
        }*/
        
        if(lotList.containsKey(number)) {
        	return Status.error("This lot already exists.");
        }
        logger.finer("End loop");
        logger.fine(startBanner("addLot " + sellerName + " " + number));
        lotList.put(number, (new CatalogueEntry(number, description , LotStatus.UNSOLD)));
        logger.finer("Adding reserve price");
        reservePrices.put(number, reservePrice);
        sellerInf.addLottoSeller(number, sellerName);
        return Status.OK();    
    }

    public List<CatalogueEntry> viewCatalogue() {
        logger.fine(startBanner("viewCatalog"));
        List<CatalogueEntry> catalogue = new ArrayList<CatalogueEntry>(lotList.values());
        logger.fine("Catalogue: " + catalogue.toString());
        return catalogue;
    }

    public Status noteInterest(
            String buyerName,
            int lotNumber) {
        logger.finer("Entering");
        if (!buyerInf.buyerExists(buyerName)) {
            Status.error("Buyer not registered. Please register");
        }
        
        if(!lotList.containsKey(lotNumber)) {
            Status.error("This lot does not exist.");
        }
        
        if(!buyerInf.interestedNodes.containsKey(lotNumber)) {
        	ArrayList<String> namesI= new ArrayList<String>();
        	namesI.add(buyerName);
        	buyerInf.interestedNodes.put(lotNumber, namesI);
        } else {
        	buyerInf.interestedNodes.get(lotNumber).add(buyerName);
        }
        logger.fine(startBanner("noteInterest " + buyerName + " " + lotNumber));
        return Status.OK();   
    }

    public Status openAuction(
            String auctioneerName,
            String auctioneerAddress,
            int lotNumber) {
        logger.finer("Entering");
        if (!lotList.containsKey(lotNumber)) {
            return Status.error("This lot does not exist.");
        }
       logger.finer("getting lot");
        CatalogueEntry currentLot = lotList.get(lotNumber);
        logger.finer("Got lot");
        AuctionProcess process = new AuctionProcess(currentLot, buyerInf.interestedNodes.get(lotNumber), parameters,
        		buyerInf, sellerInf, reservePrices, auctioneerName, auctioneerAddress);
        logger.finer("Created auction process.");
        currentAuctions.put(lotNumber, process);
        logger.fine(startBanner("openAuction " + auctioneerName + " " + lotNumber));
        
        return process.openAuction();
    }

    public Status makeBid(
            String buyerName,
            int lotNumber,
            Money bid) {
        logger.finer("Entering");
        if(!currentAuctions.containsKey(lotNumber)) {
            return Status.error("This lot either does not exist or under auction process.");
        }

        logger.fine(startBanner("makeBid " + buyerName + " " + lotNumber + " " + bid));

        return currentAuctions.get(lotNumber).makeBid(bid, buyerName);
    }

    public Status closeAuction(
            String auctioneerName,
            int lotNumber) {
        logger.finer("Entering");

        if(!currentAuctions.containsKey(lotNumber)) {
            return Status.error("This lot either does not exist or already closed");
        }
        return currentAuctions.get(lotNumber).closeBid();
    }

}