package main;

/**
 * Created by faahmed on 9/13/16.
 */

public class Order  {
    Category    category      ;
    String      keyword       ;
    String      color         ;
    String      size          ;
    PaymentInfo paymentInfo   ;
    String      additionalInfo = "Order is in process";
    Status      status         = Status.PENDING;


    public enum Category {
        JACKETS, SHIRTS, TOPS_SWEATERS, SWEATSHIRTS, PANTS, T_SHIRTS, HATS, BAGS, ACCESSORIES,
        SHOES, SKATE
    }

    public enum Status {
        PENDING, FAILURE, SUCCESS
    }

    public Order (Category category, String keyword, String color, String size,
                  PaymentInfo paymentInfo) {
        if (paymentInfo == null) throw new IllegalArgumentException("You must select payment info");
        if (category == null) throw new IllegalArgumentException("You must select a category");
        if (keyword.equals("")) throw new IllegalArgumentException("You must define a keyword");
        this.category    = category    ;
        this.keyword     = keyword     ;
        this.color       = color       ;
        this.size        = size        ;
        this.paymentInfo = paymentInfo ;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setAdditionalInfo(String info) {
        this.additionalInfo = info;
    }

    @Override
    public String toString() {
        return keyword + "/" + size + "  " + status + "  " + paymentInfo.toString() + "  " +
                additionalInfo;
    }
}
