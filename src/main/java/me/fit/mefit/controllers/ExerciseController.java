package me.fit.mefit.controllers;

import me.fit.mefit.utils.ApiPaths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping(ApiPaths.EXERCISE_PATH)
@RestController
public class ExerciseController {
    Logger logger = LoggerFactory.getLogger(ExerciseController.class);

    /*
    @AutoWired
    private ExerciseRepository exerciseRepository;
    */

    /*
        Returns a list of currently available exercises arranged alphabetically by Target muscle group.
    */
    @GetMapping()
    public ResponseEntity<String> getExercises() {
        return ResponseEntity.ok("Not Implemented");
    }

    /*
        Returns a single exercise corresponding to the provided exercise_id.
    */

    @GetMapping("/{id}")
    public ResponseEntity<String> getExercise(@PathVariable long id) {
        return ResponseEntity.ok("Not Implemented");
    }

    /*
        Creates a new exercise. Accepts appropriate parameters in the ineligible body as
        application/json. Contributor only.

     */

    @PostMapping()
    public ResponseEntity<String> createExercise(/* @RequestBody Type type */) {
        return ResponseEntity.ok("Not Implemented");
    }

    /*
        Executes a partial update of the exercise corresponding to the provided exercise_id.
        Accepts appropriate parameters in the exercise body as application/json. Contributor
        only.
    */

    @PatchMapping("/{id}")
    public ResponseEntity<String> updateExercise(@PathVariable long id /*, @RequestBody Type type */) {
        return ResponseEntity.ok("Not Implemented");
    }

    /*
        Deletes an exercise. Contributor only
    */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteExercise(@PathVariable long id) {
        return ResponseEntity.ok("Not Implemented");
    }
}
