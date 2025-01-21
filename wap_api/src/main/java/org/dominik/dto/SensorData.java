package org.dominik.dto;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class SensorData {

    @Id
    private String id;
    private Date timestamp;
    private String name;
    private float temperature;
    private float humidity;
    private float windspeed;
}
