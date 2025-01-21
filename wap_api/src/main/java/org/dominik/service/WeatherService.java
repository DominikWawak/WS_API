package org.dominik.service;

import org.dominik.dto.SensorData;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class WeatherService {


    private final List<SensorData> sensorData = new ArrayList<>();


    //Basic CRUD

    public void addSensorData(SensorData sensorData) {
        this.sensorData.add(sensorData);
    }

    public List<SensorData> getAllSensorData() {
        return sensorData;
    }

    public Optional<SensorData> getSensorDataById(String id) {
        for (SensorData data : sensorData) {
            if (data.getId().equals(id)) {
                return Optional.of(data);
            }
        }
        return Optional.empty();
    }

    public boolean updateSensorData(String id, SensorData sensorData) {
        return getSensorDataById(id).map(existingCourse -> {
            this.sensorData.remove(existingCourse);
            this.sensorData.add(sensorData);
            return true;
        }).orElse(false);
    }

    public boolean deleteSensorData(String id) {

        return this.sensorData.removeIf(data -> data.getId().equals(id));
    }

    // Design Spec Query
    public Map<String, Double> querySensorData(List<String> sensorIds, List<String> metrics, String statistic, Date startDate, Date endDate) {
        // Filter based on ID and Date
        List<SensorData> filteredData = sensorData.stream().filter(data -> (sensorIds == null || sensorIds.isEmpty() || sensorIds.contains(data.getId()))).filter(data -> (startDate == null || data.getTimestamp().after(startDate)) && (endDate == null || data.getTimestamp().before(endDate))).toList();
        // Get the correct metrics
        Map<String, Double> results = new HashMap<>();
        for (String metric : metrics) {
            List<Double> metricValues = filteredData.stream().map(data -> extractMetricValue(data, metric)).filter(Objects::nonNull) // Remove null values
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
            case "temperature" -> (double) data.getTemperature();
            case "humidity" -> (double) data.getHumidity();
            case "windspeed" -> (double) data.getWindspeed();
            default -> null;
        };
    }

}
