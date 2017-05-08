package app.logicify.com.imageprocessing;

/**
 * Created by Vadim on 11.04.2017.
 */

public class Product {
    static int count = 0;
    private int id;
    private String name;
    private double price;
    private String category;

    public Product(String name, double price, String category) {
        this.id = count;
        this.name = name;
        this.price = price;
        this.category = category;

        count++;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }


}
