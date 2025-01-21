package org.dominik.repository;

import org.dominik.dto.SensorData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WeatherRepository extends JpaRepository<SensorData,String> {
}