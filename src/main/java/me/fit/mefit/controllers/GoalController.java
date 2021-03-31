package me.fit.mefit.controllers;

import me.fit.mefit.utils.ApiPaths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping(ApiPaths.GOAL_PATH)
@RestController
public class GoalController {
    Logger logger = LoggerFactory.getLogger(GoalController.class);

    /*
    @AutoWired
    private GoalRepository goalRepository;
    */

    /*
        Returns detail about current state of the users current goal.
        Must be able to see the current progress. I.E. Completed workouts assigned to a goal.
    */

    @GetMapping("/{id}")
    public ResponseEntity<String> getGoal(@PathVariable long id) {
        return ResponseEntity.ok("Not Implemented");
    }

    /*
        Creates a new goal. Accepts appropriate parameters in the request body as application/json.
    */

    @PostMapping()
    public ResponseEntity<String> createGoal(/* @RequestBody Type type */) {
        return ResponseEntity.ok("Not Implemented");
    }

    /*
        Executes a partial update of the corresponding goal_id. Accepts appropriate parameters
        in the request body as application/json.
    */

    @PatchMapping("/{id}")
    public ResponseEntity<String> updateGoal(@PathVariable long id /*, @RequestBody Type type */) {
        return ResponseEntity.ok("Not Implemented");
    }

    /*
        This should delete a goal of the corresponding goal_id
    */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteGoal(@PathVariable long id) {
        return ResponseEntity.ok("Not Implemented");
    }
}
