package app.config;

import app.entities.*;
import org.hibernate.cfg.Configuration;

final class EntityRegistry {

    private EntityRegistry() {}

    static void registerEntities(Configuration configuration) {

        configuration.addAnnotatedClass(User.class);
        configuration.addAnnotatedClass(Profile.class);
        configuration.addAnnotatedClass(TrainingCalendar.class);
        configuration.addAnnotatedClass(TrainingProgram.class);
        configuration.addAnnotatedClass(TrainingProgramExercise.class);
        configuration.addAnnotatedClass(TrainingSession.class);
        configuration.addAnnotatedClass(Exercise.class);
        configuration.addAnnotatedClass(BodyPart.class);
        configuration.addAnnotatedClass(ExerciseLog.class);
        configuration.addAnnotatedClass(SetLog.class);
        configuration.addAnnotatedClass(Note.class);
    }
}