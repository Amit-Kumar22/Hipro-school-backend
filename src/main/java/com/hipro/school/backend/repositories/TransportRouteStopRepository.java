package com.hipro.school.backend.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hipro.school.backend.entities.TransportRouteStop;

@Repository
public interface TransportRouteStopRepository extends JpaRepository<TransportRouteStop, Long> {

	List<TransportRouteStop> findByRoute_IdOrderByStopOrderAsc(Long routeId);

	Optional<TransportRouteStop> findByIdAndRoute_Id(Long id, Long routeId);

	void deleteByRoute_Id(Long routeId);
}
