package sampleapp.service;

import httpserver.http.ContentType;
import httpserver.http.HttpStatus;
import httpserver.server.Request;
import httpserver.server.Response;
import sampleapp.model.Weather;
import sampleapp.persistence.UnitOfWork;
import sampleapp.persistence.repository.WeatherRepository;
import sampleapp.persistence.repository.WeatherRepositoryImpl;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

public class WeatherService extends AbstractService {

    private WeatherRepository weatherRepository;

    public WeatherService() {
        weatherRepository = new WeatherRepositoryImpl(new UnitOfWork());
    }


    // GET /weather(:id
    public Response getWeather(String id)
    {
        System.out.println("get weather for id: " + id);
        Weather weather = weatherRepository.findById(Integer.parseInt(id));
        String json = null;
        try {
            json = this.getObjectMapper().writeValueAsString(weather);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return new Response(HttpStatus.OK, ContentType.JSON, json);
    }
    // GET /weather
    public Response getWeather() {
        return new Response(HttpStatus.NOT_IMPLEMENTED);
    }

    // POST /weather
    public Response echo(Request request) {
        return new Response(HttpStatus.NOT_IMPLEMENTED);
    }

    // GET /weather
    public Response getWeatherPerRepository() {
        System.out.println("getWeatherPerRepository");

        // Fetch all weather data from the database
        Collection<Weather> weatherList = weatherRepository.findAllWeather();
        String json = null;

        try {
            // Convert the list of weather data to JSON
            json = this.getObjectMapper().writeValueAsString(weatherList);
        } catch (JsonProcessingException e) {
            // Handle any serialization errors
            throw new RuntimeException("Error serializing weather data to JSON", e);
        }

        // Return the JSON response with HTTP OK status
        return new Response(HttpStatus.OK, ContentType.JSON, json);
    }


    // POST /addWeather
    public Response addWeather(Request request) {
        try {
            Weather newWeather;

            // Check if the body is empty and parse query parameters instead
            if (request.getBody() == null || request.getBody().isEmpty()) {
                String params = request.getParams(); // Extract query parameters
                if (params != null) {
                    Map<String, String> queryParams = Arrays.stream(params.split("&"))
                            .map(s -> s.split("="))
                            .collect(Collectors.toMap(a -> a[0], a -> a[1]));
                    newWeather = new Weather(
                            Integer.parseInt(queryParams.get("id")),
                            queryParams.get("region"),
                            queryParams.get("city"),
                            Float.parseFloat(queryParams.get("temperature"))
                    );
                } else {
                    return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON, "{\"message\": \"Missing parameters\"}");
                }
            } else {
                // Deserialize JSON payload into Weather object
                newWeather = this.getObjectMapper().readValue(request.getBody(), Weather.class);
            }

            // Insert the Weather object into the database
            weatherRepository.addWeather(newWeather);

            // Return a success response
            return new Response(HttpStatus.CREATED, ContentType.JSON, "{\"message\": \"Weather data added successfully\"}");
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.JSON, "{\"message\": \"Failed to add weather data\"}");
        }
    }



}
