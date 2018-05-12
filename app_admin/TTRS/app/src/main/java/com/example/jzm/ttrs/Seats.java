package com.example.jzm.ttrs;

public class Seats {
    private String name;
    private String num;
    private String price;

    public Seats(String name, String num, String price){
        this.name = name;
        this.num = num;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public String getNum() {
        return num;
    }

    public String getPrice() {
        return price;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
