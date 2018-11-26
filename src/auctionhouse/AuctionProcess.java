package auctionhouse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

import auctionhouse.Status.Kind;

/**
 * Created by egeelgun on 11/22/18.
 */
public class AuctionProcess {
    CatalogueEntry entry;
    Money currentBid;
    String highestBidder;
    String auctioneer;
    String auctioneerAddress;
    BuyerInfo buyerInfo;
    SellerInfo sellerInf;
    Parameters parameters;
    String sellerName;
    ArrayList<String> interested;
    HashMap<Integer, Money> reservePrices = new HashMap<Integer, Money>();
    MessagingService msg;
    BankingService bankingService;
    private static Logger logger = Logger.getLogger("auctionhouse");

    public AuctionProcess(CatalogueEntry entry, ArrayList<String> interested,
                          Parameters parameters, BuyerInfo buyInf, SellerInfo selInf, 
                          HashMap<Integer, Money> reservePrices, String auctioneer, String auctioneerAddress) {
    	logger.finer("Constructing process.");
            this.entry = entry;
            this.interested = interested;
            this.parameters = parameters;
            this.reservePrices =reservePrices;
            this.auctioneer = auctioneer;
            this.auctioneerAddress = auctioneerAddress;
            buyerInfo = buyInf;
            sellerInf = selInf;
            sellerName = sellerInf.getName(entry.lotNumber);
            msg = parameters.messagingService;
            bankingService = parameters.bankingService;
    }

    public Status openAuction() {
        logger.finer("Entering");
        
        for (int i = 0; i < interested.size(); i++) {
            logger.finer("Type open" +  "\t expected: open");
            msg.auctionOpened(buyerInfo.buyerList.get(interested.get(i)).address, entry.lotNumber);
        }
        msg.auctionOpened(sellerInf.getAddress(sellerName), entry.lotNumber);
        logger.finer("Open Auction complete");
        entry.status = LotStatus.IN_AUCTION;
        return Status.OK();
    }

   public Status makeBid(Money bid, String buyerName) {
        logger.finer("Entering");
        if (!buyerInfo.interestedNodes.get(entry.lotNumber).contains(buyerName)) {
            return Status.error("This user is not interested in the lot.");
        }

        if (currentBid == null) {
        	logger.finer("First bid.");
            currentBid = bid;
            highestBidder = buyerName;
            logger.finer("Type open" +  "\t expected: open");
            logger.finer(buyerName);
            for (int i = 0; i < interested.size(); i++) {
                if (buyerInfo.buyerList.get(interested.get(i)).address.equals('@' + buyerName)) continue;
            	logger.finer("Type open" +  "\t expected: open");
                msg.bidAccepted(buyerInfo.buyerList.get(interested.get(i)).address, entry.lotNumber, bid);
            } 
            msg.bidAccepted(auctioneerAddress, entry.lotNumber, bid);
            msg.bidAccepted(sellerInf.getAddress(sellerName), entry.lotNumber, bid);
            return Status.OK();
        }
        logger.finer("Modifying money");
        Money newBidComperator = currentBid.add(parameters.increment);
        if (!newBidComperator.lessEqual(bid)) {
            return Status.error("New bid is less than the minimum increment.");
        }
        logger.finer("Bid Complete");
        currentBid = bid;
        highestBidder = buyerName;
        for (int i = 0; i < interested.size(); i++) {
            if (buyerInfo.buyerList.get(interested.get(i)).address.equals('@' + buyerName)) continue;
        	logger.finer("Type open" +  "\t expected: open");
            msg.bidAccepted(buyerInfo.buyerList.get(interested.get(i)).address, entry.lotNumber, bid);
        } 
        msg.bidAccepted(auctioneerAddress, entry.lotNumber, bid);
        msg.bidAccepted(sellerInf.getAddress(sellerName), entry.lotNumber, bid);
        return Status.OK();
    }

    public Status closeBid() {
        logger.finer("Entering");
        if(currentBid.lessEqual(reservePrices.get(entry.lotNumber)
        						.subtract(new Money("0.01")))) {
        	logger.finer("Bid not enough.");
            entry.status = LotStatus.UNSOLD;
            for (int i = 0; i < interested.size(); i++) {
                logger.finer("Type open" +  "\t expected: open");
                msg.lotUnsold(buyerInfo.buyerList.get(interested.get(i)).address, entry.lotNumber);
            }
            msg.lotUnsold(sellerInf.getAddress(sellerName), entry.lotNumber);
            return new Status(Kind.NO_SALE);
        }
        BankingService bankingService = parameters.bankingService;
        String buyerAddress = buyerInfo.getBankAccount(highestBidder);
        String buyerAuthCode = buyerInfo.getAuthCode(highestBidder);
        String sellerAddress = sellerInf.getBankAccount(sellerName);
        Money premiumCost = currentBid.addPercent(parameters.buyerPremium);
        logger.finer("transfer");
        Status transfer = bankingService.transfer(parameters.houseBankAccount, parameters.houseBankAuthCode, 
        		sellerAddress, currentBid.subtract(new Money(String.valueOf(parameters.commission))));
        Status transfer1 = bankingService.transfer(buyerAddress, buyerAuthCode, parameters.houseBankAccount, 
        		premiumCost);

        logger.finer("Lot SOLD");
        entry.status = LotStatus.SOLD;
        for (int i = 0; i < interested.size(); i++) {
            logger.finer("Type open" +  "\t expected: open");
            msg.lotSold(buyerInfo.buyerList.get(interested.get(i)).address, entry.lotNumber);
        }
        msg.lotSold(sellerInf.getAddress(sellerName), entry.lotNumber);
        return new Status(Kind.SALE);
    }

}
