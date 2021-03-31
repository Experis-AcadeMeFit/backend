package me.fit.mefit.controllers;

import me.fit.mefit.models.WorkoutSet;
import me.fit.mefit.payload.request.SetRequest;
import me.fit.mefit.payload.request.WorkoutRequest;
import me.fit.mefit.repositories.ExerciseRepository;
import me.fit.mefit.repositories.WorkoutSetRepository;
import me.fit.mefit.repositories.WorkoutRepository;
import me.fit.mefit.models.Workout;
import me.fit.mefit.utils.ApiPaths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Field;
import java.net.URI;
import java.util.*;

@RequestMapping(ApiPaths.WORKOUT_PATH)
@RestController
public class WorkoutController {
    Logger logger = LoggerFactory.getLogger(WorkoutController.class);

    @Autowired
    private WorkoutRepository workoutRepository;

    @Autowired
    private ExerciseRepository exerciseRepository;

    @Autowired
    private WorkoutSetRepository workoutSetRepository;

    /*
    Returns all workouts
    */
    @GetMapping()
    public ResponseEntity<List<Workout>> getAllWorkouts(){
        List<Workout> workouts = workoutRepository.findAll();
        HttpStatus status = HttpStatus.OK;
        return new ResponseEntity<>(workouts, status);
    }

    /*
    Returns details of a workout
    */

    @GetMapping("/{id}")
    public ResponseEntity<Workout> getWorkout(@PathVariable long id) {
        Workout returnWorkout = new Workout();
        HttpStatus status;

        if(workoutRepository.existsById(id)){
            status = HttpStatus.OK;
            returnWorkout = workoutRepository.findById(id).orElseThrow();
        } else {
            status = HttpStatus.NOT_FOUND;
        }
        return new ResponseEntity<>(returnWorkout, status);
    }

    /*
    Creates a new workout. Accepts appropriate parameters in the request body as
    application/json. Contributor only.
    */

    @PostMapping()
    public ResponseEntity<URI> createWorkout(@RequestBody WorkoutRequest workoutRequest) {
        Workout returnWorkout = new Workout();
        returnWorkout.setName(workoutRequest.getName());
        returnWorkout.setType(workoutRequest.getType());
        returnWorkout = workoutRepository.save(returnWorkout);

        for (SetRequest set : workoutRequest.getSets()) {
            WorkoutSet workoutSet = new WorkoutSet();
            workoutSet.setWorkout(returnWorkout);
            workoutSet.setExercise(exerciseRepository.getOne(set.getExerciseId()));
            workoutSet.setExerciseRepetitions(set.getExerciseRepetitions());
            workoutSet.setSets(set.getSets());
            returnWorkout.getExerciseSets().add(workoutSet);
        }
        workoutRepository.save(returnWorkout);
        HttpStatus status = HttpStatus.CREATED;
        return new ResponseEntity<>(URI.create(ApiPaths.WORKOUT_PATH +"/"+ returnWorkout.getId()), status);
    }

    /*
    Executes a partial update of the workout corresponding to the provided workout_id.
    Accepts appropriate parameters in the request body as application/json. Contributor
    only.
    If an unauthorized person attempts to update a workout then the server should respond with 403 Forbidden.
     */

    @PatchMapping("/{id}")
    public ResponseEntity<Workout> updateWorkout(@PathVariable long id, @RequestBody Map<String, Object> fields) {
        if (id <= 0 || fields == null || fields.isEmpty() || !fields.containsKey("id")
                || !Long.valueOf(String.valueOf(fields.get("id"))).equals(id)){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Workout returnWorkout = workoutRepository.findById(id).orElseThrow();

        if (returnWorkout == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        fields.remove("id");
        fields.remove("exerciseSets");

        fields.forEach((k, v) -> {
          Field field = ReflectionUtils.findField(Workout.class, k);
            assert field != null;
            field.setAccessible(true);
            ReflectionUtils.setField(field, returnWorkout, v);
        });

        workoutRepository.save(returnWorkout);
        return new ResponseEntity<>(returnWorkout, HttpStatus.OK);
    }

    /*
    Soft Deletes a workout. Accepts appropriate parameters in the request body as application/json.
    Contributor only.
    Deleting a workout may only be done by a contributor and can only delete workouts
    that they have contributed.
    If an unauthorized person attempts to delete a workout then the server should respond
    with 403 Forbidden.

    */
    @DeleteMapping("/{id}")
    public ResponseEntity<Workout> deleteWorkout(@PathVariable long id) {
        HttpStatus status;

        if (!workoutRepository.existsById(id)){
            status = HttpStatus.BAD_REQUEST;
        } else {
            Workout workout = workoutRepository.getOne(id);
            workout.setDeleted();
            workoutRepository.save(workout);
            status = HttpStatus.NO_CONTENT;
        }
        return new ResponseEntity<>(status);
    }

    /*
    SETS
     */

    @PostMapping("/{id}/sets")
    public ResponseEntity<Workout> addSet(@PathVariable long id, @RequestBody SetRequest setRequest){
        if (!workoutRepository.existsById(id)){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Workout workout = workoutRepository.findById(id).orElseThrow();

        WorkoutSet workoutSet = new WorkoutSet();
        workoutSet.setWorkout(workout);
        workoutSet.setExercise(exerciseRepository.getOne(setRequest.getExerciseId()));
        workoutSet.setExerciseRepetitions(setRequest.getExerciseRepetitions());
        workoutSet.setSets(setRequest.getSets());
        workout.getExerciseSets().add(workoutSet);

        workoutRepository.save(workout);
        HttpStatus status = HttpStatus.CREATED;
        return new ResponseEntity<>(workout, status);
    }

    @DeleteMapping("/{workoutId}/sets/{setId}")
    public ResponseEntity<WorkoutSet> deleteSet(@PathVariable long setId, @PathVariable Long workoutId) {
        HttpStatus status;

        if (!workoutSetRepository.existsById(setId)
                || workoutSetRepository.getOne(setId).getWorkout().getId() != workoutId){
            status = HttpStatus.BAD_REQUEST;
        } else {
            WorkoutSet workoutSet = workoutSetRepository.getOne(setId);
            workoutSet.setDeleted();
            workoutSetRepository.save(workoutSet);
            status = HttpStatus.NO_CONTENT;
        }
        return new ResponseEntity<>(status);
    }

}

