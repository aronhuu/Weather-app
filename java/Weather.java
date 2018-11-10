package com.iot.mdp.weather_app;

public class Weather {
    private String maxTemp, minTemp;//Temperature of the city
    private String location;        //The city name
    private boolean check=false;    //If the item is checked

    public Weather(boolean c, String max, String min, String loc){
        check = c;
        maxTemp = max;
        minTemp = min;
        location = loc;
    }

    public String getMaxTemp() {
        return maxTemp;
    }

    public String getMinTemp() {
        return minTemp;
    }

    public String getLocation() { return location;}
	
    public boolean setCheck(){
        check=!check;
        return check;
    }
    public boolean getCheck(){
        return check;
    }


}
