package me.fit.mefit.controllers;

import me.fit.mefit.models.Program;
import me.fit.mefit.payload.request.WorkoutId;
import me.fit.mefit.repositories.ProgramRepository;
import me.fit.mefit.repositories.WorkoutRepository;
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
@RequestMapping(ApiPaths.PROGRAM_PATH)
@RestController
public class ProgramController {
    Logger logger = LoggerFactory.getLogger(WorkoutController.class);

    @Autowired
    private ProgramRepository programRepository;

    @Autowired
    private WorkoutRepository workoutRepository;

    //Returns the details of all programs

    @PreAuthorize("hasRole('USER') or hasRole('CONTRIBUTOR') or hasRole('ADMIN')")
    @GetMapping()
    public ResponseEntity<List<Program>> getAllPrograms(){
        List<Program> programs = programRepository.findAll();
        HttpStatus status = HttpStatus.OK;
        return new ResponseEntity<>(programs, status);
    }
    //Returns the details of a program
    @PreAuthorize("hasRole('USER') or hasRole('CONTRIBUTOR') or hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<Program> getProgram(@PathVariable long id ){
        Program program = new Program();
        HttpStatus status;

        if (programRepository.existsById(id)){
            status = HttpStatus.OK;
            program = programRepository.findById(id).orElseThrow();
        } else {
            status = HttpStatus.NOT_FOUND;
        }

        return new ResponseEntity<>(program,status);
    }
    //Creates a new program. Accepts  appropriate  parameters  in
    // the  request  body  asapplication/json.Contributor only.
    @PreAuthorize("hasRole('CONTRIBUTOR') or hasRole('ADMIN')")
    @PostMapping()
    public ResponseEntity<URI> addProgram(@RequestBody Program program){
        HttpStatus status;
        programRepository.save(program);
        status = HttpStatus.CREATED;
        return new ResponseEntity<>(URI.create(ApiPaths.PROGRAM_PATH +"/"+ program.getId()), status);
    }
    //Executes a partial update of the program corresponding to the provided program id.
    // Accepts appropriate parameters in the request body asapplication/json.Contributoronly.
    @PreAuthorize("hasRole('CONTRIBUTOR') or hasRole('ADMIN')")
    @PatchMapping("/{id}")
    public ResponseEntity<Program> updateProgram(@PathVariable long id, @RequestBody Map<String, Object> fields) {
        if (id <= 0 || fields == null || fields.isEmpty() || !fields.containsKey("id")
                || !Long.valueOf(String.valueOf(fields.get("id"))).equals(id)){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Program returnProgram = programRepository.findById(id).orElseThrow();

        if (returnProgram == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        fields.remove("id");
        fields.remove("workouts");

        fields.forEach((k, v) -> {
            Field field = ReflectionUtils.findField(Program.class, k);
            assert field != null;
            field.setAccessible(true);
            ReflectionUtils.setField(field, returnProgram, v);
        });

        programRepository.save(returnProgram);
        return new ResponseEntity<>(returnProgram, HttpStatus.NO_CONTENT);
    }

    //Deletes a program
    //Contributors only
    @PreAuthorize("hasRole('CONTRIBUTOR') or hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Program> deleteProgram(@PathVariable long id){
        HttpStatus status;

        if ((!programRepository.existsById(id))){
            status = HttpStatus.BAD_REQUEST;
        } else {
            Program program = programRepository.getOne(id);
            program.setDeleted();
            programRepository.save(program);
            status = HttpStatus.NO_CONTENT;
        }
        return new ResponseEntity<>(status);
    }
    //Adds a workout to a program
    //Accepts  appropriate  parameters  in  the  request  body  asapplication/json
    //Contributor only
    @PreAuthorize("hasRole('CONTRIBUTOR') or hasRole('ADMIN')")
    @PostMapping("/{id}/workouts")
    public ResponseEntity<Program> addWorkout(@PathVariable long id, @RequestBody WorkoutId workoutId){
        if (!programRepository.existsById(id) || !workoutRepository.existsById(workoutId.getId())){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Program program = programRepository.getOne(id);
        program.getWorkouts().add(workoutRepository.getOne(workoutId.getId()));
        programRepository.save(program);

        HttpStatus status = HttpStatus.NO_CONTENT;

        return new ResponseEntity<>(status);
    }
    //removes a workout to a program
    //Accepts  appropriate  parameters  in  the  request  body  asapplication/json
    //Contributor only
    @PreAuthorize("hasRole('CONTRIBUTOR') or hasRole('ADMIN')")
    @DeleteMapping({"/{programId}/workouts"})
    public ResponseEntity<Program> removeWorkout(@PathVariable long programId, @RequestBody WorkoutId workoutId){
        HttpStatus status;

        if (!programRepository.existsById(programId)
                || !programRepository.getOne(programId).getWorkouts().contains(workoutRepository.getOne(workoutId.getId()))){
            status = HttpStatus.BAD_REQUEST;
        } else {
            Program program = programRepository.getOne(programId);
            program.getWorkouts().remove(workoutRepository.getOne(workoutId.getId()));
            programRepository.save(program);
            status = HttpStatus.NO_CONTENT;
        }
        return new ResponseEntity<>(status);
    }

}
