package com.uber.repository;

import com.uber.entity.RideRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RideRatingRepo extends JpaRepository<RideRating, Long> {
}
