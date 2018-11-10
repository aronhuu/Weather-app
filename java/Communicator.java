package iot.mdp.weather_app;

//Interface for the communication between tabs
public interface Communicator {
    public  void answer(String lastPostalCode, String lastCityName, String lastSkyState);
}
