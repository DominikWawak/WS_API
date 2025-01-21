package org.dominik.service;

import org.dominik.dto.SensorData;
import org.dominik.repository.WeatherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class WeatherService {

    private final String TEMPERATURE = "temperature";
    private final String HUMIDITY = "humidity";
    private final String WINDSPEED = "windspeed";

    @Autowired
    private WeatherRepository weatherRepository;



    @Deprecated
    private final List<SensorData> sensorData = new ArrayList<>();


    //Basic CRUD

    public void addSensorData(SensorData sensorData) {
        this.weatherRepository.save(sensorData);
    }

    public List<SensorData> getAllSensorData() {
        return weatherRepository.findAll();
    }

    public Optional<SensorData> getSensorDataById(String id) {
        for (SensorData data : weatherRepository.findAll()) {
            if (data.getId().equals(id)) {
                return Optional.of(data);
            }
        }
        return Optional.empty();
    }

    public boolean updateSensorData(String id, SensorData sensorData) {
        return getSensorDataById(id).map(existingData -> {
            this.weatherRepository.delete(existingData);
            this.weatherRepository.save(existingData);
            return true;
        }).orElse(false);
    }

    public boolean deleteSensorData(String id) {

        weatherRepository.deleteById(id);
        return !weatherRepository.existsById(id);
    }

    // Design Spec Query
    public Map<String, Double> querySensorData(List<String> sensorIds, List<String> metrics, String statistic, Date startDate, Date endDate) {
        List<SensorData> filteredData = weatherRepository.findAll();
        // Filter based on ID and Date
        if (!sensorIds.isEmpty()) {
            filteredData = filteredData.stream().filter(data -> sensorIds.contains(data.getId())).collect(Collectors.toList());
        }
        if (startDate != null && endDate != null) {
            filteredData = filteredData.stream().filter(data-> data.getTimestamp().after(startDate) && data.getTimestamp().before(endDate)).collect(Collectors.toList());
        }
        // Get the correct metrics
        Map<String, Double> results = new HashMap<>();
        for (String metric : metrics) {
            List<Double> metricValues = filteredData.stream().map(data -> extractMetricValue(data, metric)).filter(Objects::nonNull)
                    .toList();

            // Get the requested statistic
            if (!metricValues.isEmpty()) {
                switch (statistic.toLowerCase()) {
                    case "min":
                        results.put(metric, Collections.min(metricValues));
                        break;
                    case "max":
                        results.put(metric, Collections.max(metricValues));
                        break;
                    case "sum":
                        results.put(metric, metricValues.stream().mapToDouble(Double::doubleValue).sum());
                        break;
                    case "avg":
                        results.put(metric, metricValues.stream().mapToDouble(Double::doubleValue).average().orElse(0.0));
                        break;
                    default:
                        throw new IllegalArgumentException("Invalid statistic: " + statistic);
                }
            } else {
                results.put(metric, null);
            }
        }

        return results;
    }

    private Double extractMetricValue(SensorData data, String metric) {
        return switch (metric.toLowerCase()) {
            case TEMPERATURE -> (double) data.getTemperature();
            case HUMIDITY -> (double) data.getHumidity();
            case WINDSPEED -> (double) data.getWindspeed();
            default -> null;
        };
    }

}
