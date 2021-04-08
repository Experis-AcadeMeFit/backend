package me.fit.mefit.controllers;

import me.fit.mefit.keysecurity.services.AuthAdapter;
import me.fit.mefit.models.Goal;
import me.fit.mefit.models.*;
import me.fit.mefit.payload.request.GoalWorkoutPatch;
import me.fit.mefit.payload.request.GoalWorkoutRequest;
import me.fit.mefit.repositories.GoalRepository;
import me.fit.mefit.repositories.GoalWorkoutRepository;
import me.fit.mefit.repositories.WorkoutRepository;
import me.fit.mefit.utils.ApiPaths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;


@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping(ApiPaths.GOAL_PATH)
@RestController
public class GoalController {
    Logger logger = LoggerFactory.getLogger(GoalController.class);


    @Autowired
    private GoalRepository goalRepository;

    @Autowired
    private GoalWorkoutRepository goalWorkoutRepository;

    @Autowired
    private WorkoutRepository workoutRepository;

    @Autowired
    AuthAdapter authAdapter;

    @PreAuthorize("hasRole('USER') or hasRole('CONTRIBUTOR') or hasRole('ADMIN')")
    @GetMapping()
    public ResponseEntity<List<Goal>> getGoals() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = authAdapter.getUser(authentication.getPrincipal());

        List<Goal> goals = user.getProfile().getGoals();

        return ResponseEntity.ok(goals);
    }

    /*
        Returns detail about current state of the users current goal.
        Must be able to see the current progress. I.E. Completed workouts assigned to a goal.
    */
    @PreAuthorize("hasRole('USER') or hasRole('CONTRIBUTOR') or hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<Goal> getGoal(@PathVariable long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = authAdapter.getUser(authentication.getPrincipal());
        if (!goalRepository.existsById(id)){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Goal goal = goalRepository.findById(id).orElseThrow();

        if(!(goal.getProfile().getId() == user.getProfile().getId())){
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        return ResponseEntity.ok(goal);
    }

    /*
        Creates a new goal. Accepts appropriate parameters in the request body as application/json.
    */
    @PreAuthorize("hasRole('USER') or hasRole('CONTRIBUTOR') or hasRole('ADMIN')")
    @PostMapping()
    public ResponseEntity<?> createGoal( @RequestBody Goal goal ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = authAdapter.getUser(authentication.getPrincipal());
        Goal returnGoal = new Goal();

        if (user.getProfile() == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        returnGoal.setStartDate(goal.getStartDate());
        returnGoal.setEndDate(goal.getEndDate());
        returnGoal.setProfile(user.getProfile());

        goalRepository.save(returnGoal);

        return ResponseEntity
                .created(URI.create(ApiPaths.GOAL_PATH + "/" + returnGoal.getId()))
                .build();
    }

    /*
        Executes a partial update of the corresponding goal_id. Accepts appropriate parameters
        in the request body as application/json.
    */
    @PreAuthorize("hasRole('USER') or hasRole('CONTRIBUTOR') or hasRole('ADMIN')")
    @PatchMapping("/{id}")
    public ResponseEntity<Goal> updateGoal(@PathVariable long id, @RequestBody Goal goal) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = authAdapter.getUser(authentication.getPrincipal());

        Goal returnGoal = goalRepository.findById(id).orElseThrow();

        if (returnGoal == null || !goalRepository.existsById(id)){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        if (!(returnGoal.getProfile().getId() == user.getProfile().getId())){
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        if (id <= 0 || goal == null || !Long.valueOf(String.valueOf(goal.getId())).equals(id)){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if (!(goal.getEndDate() == null)){
            returnGoal.setEndDate(goal.getEndDate());
        }

        if (!(goal.getStartDate() == null)){
            returnGoal.setStartDate(goal.getStartDate());
        }

        goalRepository.save(returnGoal);
        return new ResponseEntity<>(returnGoal, HttpStatus.OK);
    }

    /*
        This should delete a goal of the corresponding goal_id
    */
    @PreAuthorize("hasRole('USER') or hasRole('CONTRIBUTOR') or hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteGoal(@PathVariable long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = authAdapter.getUser(authentication.getPrincipal());
        HttpStatus status;
        Goal goal = goalRepository.getOne(id);

        if (!goalRepository.existsById(id) || !(goal.getProfile().getId() == user.getProfile().getId())){
            status = HttpStatus.FORBIDDEN;
        } else {
            goal.setDeleted();
            goalRepository.save(goal);
            status = HttpStatus.NO_CONTENT;
        }
        return new ResponseEntity<>(status);
    }

    @PreAuthorize("hasRole('USER') or hasRole('CONTRIBUTOR') or hasRole('ADMIN')")
    @PostMapping("/{id}/goalworkouts")
    public ResponseEntity<String> addWorkout(@PathVariable Long id,@RequestBody List<GoalWorkoutRequest> goalWorkoutRequests){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = authAdapter.getUser(authentication.getPrincipal());

        if (!goalRepository.existsById(id)){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Goal goal = goalRepository.getOne(id);

        if (!(goal.getProfile().getId() == user.getProfile().getId())){
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        for (GoalWorkoutRequest goalWorkoutRequest: goalWorkoutRequests){
            if(!workoutRepository.existsById(goalWorkoutRequest.getWorkoutId())){
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            } else {
                GoalWorkout goalWorkout = new GoalWorkout();
                if(goalWorkoutRequest.getEndDate() == null){
                    goalWorkout.setEndDate(goal.getEndDate());
                } else {
                    goalWorkout.setEndDate(goalWorkoutRequest.getEndDate());
                }
                goalWorkout.setGoal(goal);
                goalWorkout.setWorkout(workoutRepository.getOne(goalWorkoutRequest.getWorkoutId()));
                goal.getGoalWorkouts().add(goalWorkout);
            }
        }
        goalRepository.save(goal);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PreAuthorize("hasRole('USER') or hasRole('CONTRIBUTOR') or hasRole('ADMIN')")
    @PatchMapping("/{goalId}/goalworkouts/{goalWorkoutId}")
    public ResponseEntity<String> addWorkout(@PathVariable Long goalId, @PathVariable Long goalWorkoutId,
                                             @RequestBody GoalWorkoutPatch goalWorkoutPatch){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = authAdapter.getUser(authentication.getPrincipal());

        if (!goalRepository.existsById(goalId) || !goalWorkoutRepository.existsById(goalWorkoutId)){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Goal goal = goalRepository.getOne(goalId);
        GoalWorkout goalWorkout = goalWorkoutRepository.getOne(goalWorkoutId);

        if (!(goal.getProfile().getId() == user.getProfile().getId()) ||
                !(goalWorkout.getGoal().getProfile().getId() == user.getProfile().getId())){
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        if(!(goalWorkoutPatch.getEndDate() == null)){
            goalWorkout.setEndDate(goalWorkoutPatch.getEndDate());
        }
        if(!(goalWorkoutPatch.isCompleted() == null)){
            goalWorkout.setCompleted(goalWorkoutPatch.isCompleted());
        }
        goalWorkoutRepository.save(goalWorkout);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PreAuthorize("hasRole('USER') or hasRole('CONTRIBUTOR') or hasRole('ADMIN')")
    @DeleteMapping("/{goalId}/goalworkouts/{goalWorkoutId}")
    public ResponseEntity<String> addWorkout(@PathVariable Long goalId, @PathVariable Long goalWorkoutId){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = authAdapter.getUser(authentication.getPrincipal());

        if (!goalRepository.existsById(goalId) || !goalWorkoutRepository.existsById(goalWorkoutId)){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Goal goal = goalRepository.getOne(goalId);
        GoalWorkout goalWorkout = goalWorkoutRepository.getOne(goalWorkoutId);

        if (!(goal.getProfile().getId() == user.getProfile().getId()) ||
                !(goalWorkout.getGoal().getProfile().getId() == user.getProfile().getId())){
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        goalWorkout.setDeleted();

        goalWorkoutRepository.save(goalWorkout);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
