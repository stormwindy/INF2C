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
    private List<CatalogueEntry> lotList = new ArrayList<>();
    private HashMap<Integer, AuctionProcess> currentAuctions = new HashMap();
    private static Logger logger = Logger.getLogger("auctionhouse");
    private static final String LS = System.lineSeparator();
    static List<ArrayList<String>> interestedNodes = new ArrayList<>();
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
            if(bankAccount.matches("[^a-z A-Z0-9]")) {
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
            if  (lotList.get(i).equals(new CatalogueEntry(number, description, LotStatus.UNSOLD,
                    reservePrice,sellerName))) {
                exists = true;
                return Status.error("This lot already exists");
            }
            if (lotList.contains(i)) {
                return Status.error("This lot number already exists. Please choose a different number");
            }
        }

        lotList.add(number, new CatalogueEntry(number, description , LotStatus.UNSOLD, reservePrice, sellerName));
        return Status.OK();    
    }

    public List<CatalogueEntry> viewCatalogue() {
        logger.fine(startBanner("viewCatalog"));
        List<CatalogueEntry> catalogue = new ArrayList<CatalogueEntry>();
        catalogue = lotList;
        logger.fine("Catalogue: " + catalogue.toString());
        return catalogue;
    }

    public Status noteInterest(
            String buyerName,
            int lotNumber) {
        if (!buyerInf.buyerExists(buyerName)) {
            Status.error("Buyer not registered. Please register");
        }

        if(!lotList.contains(lotNumber)) {
            Status.error("This lot does not exist.");
        }

        interestedNodes.get(lotNumber).add(buyerName);
        logger.fine(startBanner("noteInterest " + buyerName + " " + lotNumber));
        return Status.OK();   
    }

    public Status openAuction(
            String auctioneerName,
            String auctioneerAddress,
            int lotNumber) {

        if (!lotList.contains(lotNumber)) {
            Status.error("This lot does not exist.");
        }
        CatalogueEntry currentLot = lotList.get(lotNumber);
        AuctionProcess process = new AuctionProcess(currentLot, interestedNodes.get(lotNumber), parameters,
                                                    buyerInf, sellerInf);
        currentAuctions.put(lotNumber, process);
        logger.fine(startBanner("openAuction " + auctioneerName + " " + lotNumber));
        
        return process.openAuction();
    }

    public Status makeBid(
            String buyerName,
            int lotNumber,
            Money bid) {

        if(!currentAuctions.containsKey(lotNumber)) {
            Status.error("This lot either does not exist or under auction process.");
        }

        logger.fine(startBanner("makeBid " + buyerName + " " + lotNumber + " " + bid));

        return currentAuctions.get(lotNumber).makeBid(bid, buyerName);
    }

    public Status closeAuction(
            String auctioneerName,
            int lotNumber) {
        logger.fine(startBanner("closeAuction " + auctioneerName + " " + lotNumber));

        return Status.OK();
    }

}
