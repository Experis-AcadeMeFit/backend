package me.fit.mefit.repositories;

import me.fit.mefit.models.WorkoutSet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkoutSetRepository extends JpaRepository<WorkoutSet, Long> {
}
