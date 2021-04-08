package me.fit.mefit.repositories;

import me.fit.mefit.models.GoalWorkout;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GoalWorkoutRepository extends JpaRepository<GoalWorkout, Long> {
}
