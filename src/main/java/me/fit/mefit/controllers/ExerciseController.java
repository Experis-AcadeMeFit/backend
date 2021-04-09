package me.fit.mefit.controllers;

import me.fit.mefit.models.Exercise;
import me.fit.mefit.models.Workout;
import me.fit.mefit.repositories.ExerciseRepository;
import me.fit.mefit.utils.ApiPaths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Field;
import java.net.URI;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping(ApiPaths.EXERCISE_PATH)
@RestController
public class ExerciseController {
    Logger logger = LoggerFactory.getLogger(ExerciseController.class);


    @Autowired
    private ExerciseRepository exerciseRepository;


    /*
        Returns a list of currently available exercises arranged alphabetically by Target muscle group.
    */
    @PreAuthorize("hasRole('USER') or hasRole('CONTRIBUTOR') or hasRole('ADMIN')")
    @GetMapping()
    public ResponseEntity<List<Exercise>> getAllExercises() {
        List<Exercise> exercises = exerciseRepository.findAllByOrderByTargetMuscleGroup();
        HttpStatus status = HttpStatus.OK;
        return new ResponseEntity<>(exercises, status);
    }

    /*
        Returns a single exercise corresponding to the provided exercise_id.
    */
    @PreAuthorize("hasRole('USER') or hasRole('CONTRIBUTOR') or hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<Exercise> getExercise(@PathVariable long id) {
        Exercise returnExercise = new Exercise();
        HttpStatus status;

        if(exerciseRepository.existsById(id)){
            status = HttpStatus.OK;
            returnExercise = exerciseRepository.findById(id).orElseThrow();
        } else {
            status = HttpStatus.NOT_FOUND;
        }
        return new ResponseEntity<>(returnExercise, status);
    }

    /*
        Creates a new exercise. Accepts appropriate parameters in the ineligible body as
        application/json. Contributor only.

     */
    @PreAuthorize("hasRole('CONTRIBUTOR') or hasRole('ADMIN')")
    @PostMapping()
    public ResponseEntity<?> createExercise(@RequestBody Exercise exercise) {
        Exercise returnExercise = exerciseRepository.save(exercise);
        return ResponseEntity.created(URI.create(ApiPaths.EXERCISE_PATH + "/" + returnExercise.getId()))
                .build();
    }

    /*
        Executes a partial update of the exercise corresponding to the provided exercise_id.
        Accepts appropriate parameters in the exercise body as application/json. Contributor
        only.
    */
    @PreAuthorize("hasRole('CONTRIBUTOR') or hasRole('ADMIN')")
    @PatchMapping("/{id}")
    public ResponseEntity<Exercise> updateExercise(@PathVariable long id, @RequestBody Map<String, Object> fields) {
        if (id <= 0 || fields == null || fields.isEmpty() || !fields.containsKey("id")
                || !Long.valueOf(String.valueOf(fields.get("id"))).equals(id)){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Exercise returnExercise = exerciseRepository.findById(id).orElseThrow();

        if (returnExercise == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        //not allowed to update id
        fields.remove("id");

        fields.forEach((k, v) -> {
            Field field = ReflectionUtils.findField(Exercise.class, k);
            assert field != null;
            field.setAccessible(true);
            ReflectionUtils.setField(field, returnExercise, v);
        });

        exerciseRepository.save(returnExercise);
        return new ResponseEntity<>(returnExercise, HttpStatus.OK);
    }

    /*
        Deletes an exercise. Contributor only
    */
    @PreAuthorize("hasRole('CONTRIBUTOR') or hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteExercise(@PathVariable long id) {
        HttpStatus status;

        if (!exerciseRepository.existsById(id)){
            status = HttpStatus.BAD_REQUEST;
        } else {
            Exercise exercise = exerciseRepository.getOne(id);
            exercise.setDeleted();
            exerciseRepository.save(exercise);
            status = HttpStatus.NO_CONTENT;
        }
        return new ResponseEntity<>(status);
    }
}
