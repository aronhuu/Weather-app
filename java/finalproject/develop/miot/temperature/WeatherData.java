package finalproject.develop.miot.temperature;

import java.util.ArrayList;


public class WeatherData {

    private ArrayList<Weather> mList = new ArrayList<Weather>();

    public WeatherData(String [] places, String[] maxTemp, String[] minTemp) {
        // Build data array list
        for (int i = 0; i < places.length; ++i) {
            Weather weather = new Weather( 1, maxTemp[i], minTemp[i], places[i] );
            mList.add(weather);
        }
    }

    public ArrayList<Weather> getWeatherList() {
        return mList;
    }
}
