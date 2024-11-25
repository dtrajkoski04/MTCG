package sampleapp.persistence.repository;

import sampleapp.model.Weather;

import java.util.Collection;

public interface WeatherRepository {

    Weather findById(int id);
    Collection<Weather> findAllWeather();
    void addWeather(Weather weather);

}
