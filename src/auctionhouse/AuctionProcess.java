package auctionhouse;

import java.util.ArrayList;

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
    ArrayList<String> interested;
    static MockMessagingService msgServiceAuction = new MockMessagingService();

    public AuctionProcess(CatalogueEntry entry, ArrayList<String> interested,
                          Parameters parameters, BuyerInfo buyInf, SellerInfo selInf) {
            this.entry = entry;
            this.interested = interested;
            this.parameters = parameters;
            buyerInfo = buyInf;
            sellerInf = selInf;
    }

    public Status openAuction() {
        if (entry.status != LotStatus.UNSOLD) {
            return Status.error("This auction is either already in auction or sold.");
        }
        entry.status = LotStatus.IN_AUCTION;
        sendMsg("open");
        return Status.OK();
    }

    public Status makeBid(Money bid, String buyerName) {
        if (!AuctionHouseImp.interestedNodes.get(entry.lotNumber).contains(buyerName)) {
            return Status.error("This user is not interested in the lot.");
        }

        if (currentBid == null) {
            currentBid = bid;
            highestBidder = buyerName;
            return Status.OK();
        }

        Money newBidComperator = currentBid.add(parameters.increment);
        if (!newBidComperator.lessEqual(bid)) {
            return Status.error("New bid is less than the minimum increment.");
        }

        currentBid = bid;
        highestBidder = buyerName;
        sendMsg("bid");
        return Status.OK();
    }

    public Status.Kind closeBid() {
        if(currentBid.lessEqual(entry.reserve.subtract(new Money("0.01")))) {
            entry.status = LotStatus.UNSOLD;
            sendMsg("noSale");
            return Status.Kind.NO_SALE;
        }
        MockBankingService bankingService = new MockBankingService();
        String buyerAddress = buyerInfo.getBankAccount(highestBidder);
        String buyerAuthCode = buyerInfo.getAuthCode(highestBidder);
        String sellerAddress = sellerInf.getAddress(entry.sellerName);
        Money premiumCost = currentBid.addPercent(parameters.buyerPremium);

        Status transfer = bankingService.transfer(buyerAuthCode, buyerAddress, sellerAddress, premiumCost);
        if (transfer != Status.OK()) {
            entry.status = LotStatus.SOLD_PENDING_PAYMENT;
            sendMsg("noSale");
            return Status.Kind.SALE_PENDING_PAYMENT;
        }
        entry.status = LotStatus.SOLD;
        sendMsg("sale");
        return Status.Kind.SALE;
    }

    private void sendMsg(String type) {
        String[] addresses = new String[interested.size()];
        for (int i = 0; i < interested.size(); i++) {
            String name = interested.get(i);
            String address = BuyerInfo.buyerList.get(name).address;
            addresses[i] = address;
        }
        switch (type) {
            case "open": for (String address : addresses) {
                msgServiceAuction.auctionOpened(address, entry.lotNumber);
                msgServiceAuction.auctionOpened(sellerInf.getAddress(entry.sellerName), entry.lotNumber);
            }
            case "bid":for (String address : addresses) {
                msgServiceAuction.bidAccepted(address, entry.lotNumber, currentBid);
                msgServiceAuction.bidAccepted(sellerInf.getAddress(entry.sellerName), entry.lotNumber, currentBid);
            }
            case "sale": for (String address : addresses) {
                msgServiceAuction.lotSold(address, entry.lotNumber);
                msgServiceAuction.lotSold(sellerInf.getAddress(entry.sellerName), entry.lotNumber);
            }
            case "noSale": for (String address : addresses) {
                msgServiceAuction.lotUnsold(address, entry.lotNumber);
                msgServiceAuction.lotUnsold(sellerInf.getAddress(entry.sellerName), entry.lotNumber);
            }
        }

    }
}
