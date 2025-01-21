package org.dominik.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SensorData {

    private String id;
    private Date timestamp;
    private String name;
    private float temperature;
    private float humidity;
    private float windspeed;
}
