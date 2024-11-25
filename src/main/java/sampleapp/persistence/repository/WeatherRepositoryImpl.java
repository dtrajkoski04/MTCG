package sampleapp.persistence.repository;

import sampleapp.persistence.DataAccessException;
import sampleapp.persistence.UnitOfWork;
import sampleapp.model.Weather;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

public class WeatherRepositoryImpl implements WeatherRepository {
    private UnitOfWork unitOfWork;

    public WeatherRepositoryImpl(UnitOfWork unitOfWork)
    {
        this.unitOfWork = unitOfWork;
    }

    @Override
    public Weather findById(int id) {
        try (PreparedStatement preparedStatement =
                     this.unitOfWork.prepareStatement("""
                    select * from public.weather
                    where id = ?
                """))
        {
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            Weather weather = null;
            while(resultSet.next())
            {
                weather = new Weather(
                        resultSet.getInt(1),
                        resultSet.getString(2),
                        resultSet.getString(3),
                        resultSet.getInt(4));
            }
            return weather;
        } catch (SQLException e) {
            throw new DataAccessException("Select nicht erfolgreich", e);
        }
    }

    @Override
    public Collection<Weather> findAllWeather() {
        try (PreparedStatement preparedStatement =
                     this.unitOfWork.prepareStatement("""
                    select * from weather
                """))
        {
            //preparedStatement.setString(1, "Europe");
            ResultSet resultSet = preparedStatement.executeQuery();
            Collection<Weather> weatherRows = new ArrayList<>();
            while(resultSet.next())
            {
                Weather weather = new Weather(
                        resultSet.getInt(1),
                        resultSet.getString(2),
                        resultSet.getString(3),
                        resultSet.getInt(4));
                weatherRows.add(weather);
            }

            return weatherRows;
        } catch (SQLException e) {
            throw new DataAccessException("Select nicht erfolgreich", e);
        }
    }

    @Override
    public void addWeather(Weather weather) {
        String insertQuery = """
        INSERT INTO weather (id, region, city, temperature)
        VALUES (?, ?, ?, ?)
        ON CONFLICT (id) DO UPDATE 
        SET region = EXCLUDED.region,
            city = EXCLUDED.city,
            temperature = EXCLUDED.temperature;
    """;

        try (UnitOfWork unitOfWork = new UnitOfWork();
             PreparedStatement preparedStatement = unitOfWork.prepareStatement(insertQuery)) {

            preparedStatement.setInt(1, weather.getId());
            preparedStatement.setString(2, weather.getRegion());
            preparedStatement.setString(3, weather.getCity());
            preparedStatement.setFloat(4, weather.getTemperature());

            preparedStatement.executeUpdate();
            unitOfWork.commitTransaction();

        } catch (SQLException e) {
            throw new DataAccessException("Failed to insert or update weather data", e);
        }
    }


}
