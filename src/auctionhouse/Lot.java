package auctionhouse;

class Lot{
    String sellerName;
    int ID;
    String description;
    Money reservePrice;

    public Lot (String sellerName, int ID, String description, Money reservePrice) {
        this.sellerName = sellerName;
        this.ID = ID;
        this.description = description;
        this.reservePrice = reservePrice;
    }
}