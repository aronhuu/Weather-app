package com.iot.mdp.weather_app;

public interface Communicator {
    public void answer(String lastPostalCode, String lastCityName, String lastSkyState);
}
