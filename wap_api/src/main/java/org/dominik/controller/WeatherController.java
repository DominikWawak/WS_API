package org.dominik.controller;


import org.dominik.dto.SensorData;
import org.dominik.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/weatherStation")
public class WeatherController {

    @Autowired
    private WeatherService weatherService;


    @PostMapping(produces = "application/json", consumes = "application/json")
    public ResponseEntity<SensorData> addSensorData(@RequestBody SensorData sensorData) {
        weatherService.addSensorData(sensorData);
        return new ResponseEntity<>(sensorData, HttpStatus.CREATED);
    }

    @GetMapping(produces = "application/json")
    public ResponseEntity<List<SensorData>> getAllSensorData() {
        List<SensorData> sensorData = weatherService.getAllSensorData();
        return new ResponseEntity<>(sensorData, HttpStatus.OK);
    }

    @GetMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<SensorData> getSensorDataById(@PathVariable String id) {
        Optional<SensorData> sensorData = weatherService.getSensorDataById(id);
        return sensorData.map(value -> new ResponseEntity<>(value, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping(value = "/{id}", produces = "application/json", consumes = "application/json")
    public ResponseEntity<SensorData> updateSensorData(@PathVariable String id, @RequestBody SensorData newSensorData) {
        boolean updated = weatherService.updateSensorData(id, newSensorData);
        if (updated) {
            return new ResponseEntity<>(newSensorData, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<Void> deleteSensorData(@PathVariable String id) {
        boolean deleted = weatherService.deleteSensorData(id);
        if (deleted) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/query")
    public ResponseEntity<Map<String, Double>> querySensorData(
            @RequestParam(required = false) List<String> sensorIds,
            @RequestParam() List<String> metrics,
            @RequestParam() String statistic,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate
    ) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date start = (startDate != null) ? dateFormat.parse(startDate) : null;
        Date end = (endDate != null) ? dateFormat.parse(endDate) : null;

        Map<String, Double> response = weatherService.querySensorData(sensorIds, metrics, statistic, start, end);

        return new ResponseEntity<>(response,HttpStatus.OK);
    }



}
