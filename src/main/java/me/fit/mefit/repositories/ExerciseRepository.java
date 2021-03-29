package me.fit.mefit.repositories;

import me.fit.mefit.models.Exercise;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExerciseRepository extends JpaRepository<Exercise, Long>  {
    //returns a list of exercises ordered alphabetically by muscle group
    List<Exercise> findAllByOrderByTargetMuscleGroup();
}
