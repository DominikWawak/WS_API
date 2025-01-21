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
    //TODO

}
