package me.fit.mefit.utils;

public class ApiPaths {
    public static final int VERSION = 1;
    public static final String BASE_PATH = "/api/v" + VERSION;
    public static final String LOGIN_PATH = BASE_PATH + "/login";
    public static final String EXERCISE_PATH = BASE_PATH + "/exercises";
    public static final String WORKOUT_PATH = BASE_PATH + "/workouts";
    public static final String PROGRAM_PATH = BASE_PATH + "/programs";
    public static final String GOAL_PATH = BASE_PATH + "/goals";
    public static final String PROFILE_PATH = BASE_PATH + "/profiles";
    public static final String USER_PATH = BASE_PATH + "/users";
    public static final String PUBLIC_PATH = LOGIN_PATH + "/**";
    public static final String PROTECTED_PATH = BASE_PATH + "/**";
}
