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
    private String unique;
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
        this.unique = trainID + departure + destination;
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

    public String getUnique() {
        return unique;
    }

    public void setDepartTime(String departTime) {
        this.departTime = departTime;
    }

    public void setArriveTime(String arriveTime) {
        this.arriveTime = arriveTime;
    }

    public void setArriveDate(String arriveDate) {
        this.arriveDate = arriveDate;
    }

    public void setCatalog(String catalog) {
        this.catalog = catalog;
    }

    public void setDepartDate(String departDate) {
        this.departDate = departDate;
    }

    public void setDeparture(String departure) {
        this.departure = departure;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public void setTrainID(String trainID) {
        this.trainID = trainID;
    }

    public void setTrainName(String trainName) {
        this.trainName = trainName;
    }

    public void setUnique(String unique) {
        this.unique = unique;
    }
}



