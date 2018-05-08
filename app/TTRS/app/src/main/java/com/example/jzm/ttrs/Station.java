package com.example.jzm.ttrs;

public class Station {
    private String station;
    private String arriveTime;
    private String departTime;
    private double fare;

    public Station(){

    }
    public Station(String station, String arriveTime, String departTime, double fare){
        this.station = station;
        this.arriveTime = arriveTime;
        this.departTime = departTime;
        this.fare = fare;
    }

    public void setStation(String station) {
        this.station = station;
    }

    public void setArriveTime(String arriveTime) {
        this.arriveTime = arriveTime;
    }

    public void setDepartTime(String departTime) {
        this.departTime = departTime;
    }

    public void setFare(double fare) {
        this.fare = fare;
    }

    public String getStation() {
        return station;
    }

    public String getArriveTime() {
        return arriveTime;
    }

    public String getDepartTime() {
        return departTime;
    }

    public double getFare() {
        return fare;
    }
}
