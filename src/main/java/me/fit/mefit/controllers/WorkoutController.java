package me.fit.mefit.controllers;

import me.fit.mefit.utils.ApiPaths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping(ApiPaths.WORKOUT_PATH)
@RestController
public class WorkoutController {
    Logger logger = LoggerFactory.getLogger(WorkoutController.class);

    /*
    @AutoWired
    private WorkoutRepository workoutRepository;
    */

    /*
    Returns details of a workout
    */

    @GetMapping("/{id}")
    public ResponseEntity<String> getWorkout(@PathVariable long id) {
        return ResponseEntity.ok("Not Implemented");
    }

    /*
    Creates a new workout. Accepts appropriate parameters in the request body as
    application/json. Contributor only.
    */

    @PostMapping()
    public ResponseEntity<String> createWorkout(/* @RequestBody Type type */) {
        return ResponseEntity.ok("Not Implemented");
    }

    /*
    Executes a partial update of the workout corresponding to the provided workout_id.
    Accepts appropriate parameters in the request body as application/json. Contributor
    only.
    If an unauthorized person attempts to update a workout then the server should respond with 403 Forbidden.
     */

    @PatchMapping("/{id}")
    public ResponseEntity<String> updateWorkout(@PathVariable long id /*, @RequestBody Type type */) {
        return ResponseEntity.ok("Not Implemented");
    }

    /*
    Deletes a workout. Accepts appropriate parameters in the request body as application/json.
    Contributor only.
    Deleting a workout may only be done by a contributor and can only delete workouts
    that they have contributed.
    If an unauthorized person attempts to delete a workout then the server should respond
    with 403 Forbidden.

    */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteWorkout(@PathVariable long id) {
        return ResponseEntity.ok("Not Implemented");
    }

}
