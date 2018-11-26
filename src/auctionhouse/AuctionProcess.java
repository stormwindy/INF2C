package auctionhouse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

/**
 * Created by egeelgun on 11/22/18.
 */
public class AuctionProcess {
    CatalogueEntry entry;
    Money currentBid;
    String highestBidder;
    String auctioneer;
    BuyerInfo buyerInfo;
    SellerInfo sellerInf;
    Parameters parameters;
    String sellerName;
    ArrayList<String> interested;
    HashMap<Integer, Money> reservePrices = new HashMap<Integer, Money>();
    MockMessagingService msgServiceAuction;
    private static Logger logger = Logger.getLogger("auctionhouse");

    public AuctionProcess(CatalogueEntry entry, ArrayList<String> interested,
                          Parameters parameters, BuyerInfo buyInf, SellerInfo selInf, 
                          HashMap<Integer, Money> reservePrices) {
            this.entry = entry;
            this.interested = interested;
            this.parameters = parameters;
            this.reservePrices =reservePrices;
            buyerInfo = buyInf;
            sellerInf = selInf;
            sellerName = sellerInf.getName(entry.lotNumber);
            msgServiceAuction = new MockMessagingService();
    }

    public Status openAuction() {
        logger.finer("Entering");
        if (entry.status != LotStatus.UNSOLD) {
            return Status.error("This auction is either already in auction or sold.");
        }

        for (int i = 0; i < interested.size(); i++) {
        	logger.finer(buyerInfo.buyerList.get(interested.get(i)).address);
        	msgServiceAuction.auctionOpened(buyerInfo.buyerList.get(interested.get(i)).address, entry.lotNumber);
        }
        msgServiceAuction.auctionOpened(sellerInf.getAddress(sellerName), entry.lotNumber);
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
            currentBid = bid;
            highestBidder = buyerName;
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
        sendMsg("bid");
        return Status.OK();
    }

    public Status.Kind closeBid() {
        logger.finer("Entering");
        if(currentBid.lessEqual(reservePrices.get(entry.lotNumber)
        						.subtract(new Money("0.01")))) {
            entry.status = LotStatus.UNSOLD;
            sendMsg("noSale");
            return Status.Kind.NO_SALE;
        }
        MockBankingService bankingService = new MockBankingService();
        String buyerAddress = buyerInfo.getBankAccount(highestBidder);
        String buyerAuthCode = buyerInfo.getAuthCode(highestBidder);
        String sellerAddress = sellerInf.getAddress(sellerName);
        Money premiumCost = currentBid.addPercent(parameters.buyerPremium);
        logger.finer("transfer");
        Status transfer = bankingService.transfer(buyerAuthCode, buyerAddress, sellerAddress, premiumCost);
        if (transfer != Status.OK()) {
            entry.status = LotStatus.SOLD_PENDING_PAYMENT;
            sendMsg("noSale");
            return Status.Kind.SALE_PENDING_PAYMENT;
        }

        logger.finer("Lot SOLD");
        entry.status = LotStatus.SOLD;
        sendMsg("sale");
        return Status.Kind.SALE;
    }

    private void sendMsg(String type) {
        logger.finer("Entering");
        String[] addresses = new String[interested.size()];
        logger.finer("For loop");
        for (int i = 0; i < interested.size(); i++) {
            String name = interested.get(i);
            String address = buyerInfo.buyerList.get(name).address;
            addresses[i] = address;
        }
        logger.finer("End loop");

        switch (type) {

            case "open": for (String address : addresses) {
                logger.finer("Type" + type + "\t expected: open");
                msgServiceAuction.auctionOpened(address, entry.lotNumber);
                
            }
            case "bid":for (String address : addresses) {
                logger.finer("Type" + type + "\t expected: bid");
                msgServiceAuction.bidAccepted(address, entry.lotNumber, currentBid);
                msgServiceAuction.bidAccepted(sellerInf.getAddress(sellerName), entry.lotNumber, currentBid);
            }
            case "sale": for (String address : addresses) {
                logger.finer("Type" + type + "\t expected: sale");
                msgServiceAuction.lotSold(address, entry.lotNumber);
                msgServiceAuction.lotSold(sellerInf.getAddress(sellerName), entry.lotNumber);
            }
            case "noSale": for (String address : addresses) {
                logger.finer("Type" + type + "\t expected: noSale");
                msgServiceAuction.lotUnsold(address, entry.lotNumber);
                msgServiceAuction.lotUnsold(sellerInf.getAddress(sellerName), entry.lotNumber);
            }
        }

    }
}
