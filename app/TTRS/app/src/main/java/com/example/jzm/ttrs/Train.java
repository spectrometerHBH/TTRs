package com.example.jzm.ttrs;

public class Train {
    private String trainID;
    private String trainName;
    private String catalog;
    private String departure;
    private String destination;
    private String departTime;
    private String arriveTime;
    private String departDate;
    private String arriveDate;
    public Train(){

    }
    public Train(String trainID, String trainName, String catalog, String departure, String destination, String departTime, String arriveTime, String departDate, String arriveDate){
        this.trainID = trainID;
        this.trainName = trainName;
        this.catalog = catalog;
        this.departure = departure;
        this.destination = destination;
        this.departTime = departTime;
        this.arriveTime = arriveTime;
        this.departDate = departDate;
        this.arriveDate = arriveDate;
    }
    public String getTrainID(){
        return trainID;
    }
    public String getTrainName(){
        return trainName;
    }
    public String getCatalog(){
        return catalog;
    }
    public String getDeparture(){
        return departure;
    }
    public String getDestination(){
        return destination;
    }
    public String getDepartTime(){
        return departTime;
    }
    public String getArriveTime(){
        return arriveTime;
    }
    public String getArriveDate() {
        return arriveDate;
    }
    public String getDepartDate() {
        return departDate;
    }
}



