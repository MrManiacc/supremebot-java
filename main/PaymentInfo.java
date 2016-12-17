package main;

/**
 * Created by faahmed on 9/23/16.
 */
public class PaymentInfo {

    public static final int NUMBER_OF_FIELDS = 13;

    String name;
    String email;
    String tel;
    String address;
    String zip;
    String city;
    String state;
    String country;
    String cardType;
    String number;
    String expMonth;
    String expYear;
    String cvv;

    public PaymentInfo (String[] fields){
        for (String field : fields) if (field.equals("")) throw new IllegalArgumentException(field);
        int i = 0;
        this.name = fields[i++];
        this.email = fields[i++];
        this.tel = fields[i++];
        this.address = fields[i++];
        this.zip = fields[i++];
        this.city = fields[i++];
        this.state = fields[i++];
        this.country = fields[i++];
        this.cardType = fields[i++];
        this.number = fields[i++];
        this.expMonth = fields[i++];
        this.expYear = fields[i++];
        this.cvv = fields[i++];
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getExpMonth() {
        return expMonth;
    }

    public void setExpMonth(String expMonth) {
        this.expMonth = expMonth;
    }

    public String getExpYear() {
        return expYear;
    }

    public void setExpYear(String expYear) {
        this.expYear = expYear;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    @Override
    public String toString(){
        return "********" + number.substring(getNumber().length() - 4);
    }
}
