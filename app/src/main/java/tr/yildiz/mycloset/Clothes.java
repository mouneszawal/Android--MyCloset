package tr.yildiz.mycloset;




public class Clothes {

    private String clothingType;
    private String clothingColor;
    private String dateOfPurchase;
    private String price;
    private String uri;
    private String filename;
    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }



    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }


    public Clothes(String clothingType, String clothingColor, String dateOfPurchase, String price) {
        this.clothingType = clothingType;
        this.clothingColor = clothingColor;
        this.dateOfPurchase = dateOfPurchase;
        this.price = price;
    }
    
    


    public String getClothingType() {
        return clothingType;
    }

    public void setClothingType(String clothingType) {
        this.clothingType = clothingType;
    }

    public String getClothingColor() {
        return clothingColor;
    }

    public void setClothingColor(String clothingColor) {
        this.clothingColor = clothingColor;
    }

    public String getDateOfPurchase() {
        return dateOfPurchase;
    }

    public void setDateOfPurchase(String dateOfPurchase) {
        this.dateOfPurchase = dateOfPurchase;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

}
