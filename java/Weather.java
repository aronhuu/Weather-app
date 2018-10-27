package com.example.ao.tabapplication;

public class Weather {
    private int imageResource;
    private String maxTemp, minTemp;
    private String location;

    public Weather(int img, String max, String min, String loc){
        imageResource = img;
        maxTemp = max;
        minTemp = min;
        location = loc;
    }

    public int getImageResource() {
        return imageResource;
    }

    public String getMaxTemp() {
        return maxTemp;
    }

    public String getMinTemp() {
        return minTemp;
    }

    public String getLocation() { return location;}
//    public String toString() {
//        return name + ":" + population;
//    }

}
