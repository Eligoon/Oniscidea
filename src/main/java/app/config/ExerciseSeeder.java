package app.config;

import app.daos.BodyPartDAO;
import app.daos.ExerciseDAO;
import app.entities.BodyPart;
import app.entities.Exercise;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class ExerciseSeeder {

    private ExerciseSeeder() {
    }

    private record ExerciseDefinition(
            String name,
            String description,
            List<String> bodyParts
    ) {
    }

    public static void seed(
            ExerciseDAO exerciseDAO,
            BodyPartDAO bodyPartDAO
    ) {
        Map<String, BodyPart> bodyParts = new HashMap<>();

        for (BodyPart bodyPart : bodyPartDAO.getAll()) {
            bodyParts.put(
                    bodyPart.getName().toLowerCase(Locale.ROOT),
                    bodyPart
            );
        }

        Set<String> existingNames = new HashSet<>();

        for (Exercise exercise : exerciseDAO.getAll()) {
            existingNames.add(
                    exercise.getName().toLowerCase(Locale.ROOT)
            );
        }

        List<ExerciseDefinition> predefined = List.of(
                new ExerciseDefinition(
                        "Bench Press",
                        "Press a barbell upward from the chest while lying on a bench, then lower it under control.",
                        List.of("Chest", "Triceps", "Shoulders")
                ),
                new ExerciseDefinition(
                        "Incline Dumbbell Press",
                        "Press dumbbells upward from an inclined bench, then lower them slowly toward the upper chest.",
                        List.of("Chest", "Shoulders", "Triceps")
                ),
                new ExerciseDefinition(
                        "Push-Up",
                        "Lower the body toward the floor with a straight torso, then push back up.",
                        List.of("Chest", "Triceps", "Shoulders", "Abs")
                ),
                new ExerciseDefinition(
                        "Cable Fly",
                        "Bring cable handles together in front of the chest with slightly bent elbows.",
                        List.of("Chest", "Shoulders")
                ),

                new ExerciseDefinition(
                        "Lat Pulldown",
                        "Pull a cable bar down toward the upper chest, then return it slowly overhead.",
                        List.of("Back", "Biceps", "Forearms")
                ),
                new ExerciseDefinition(
                        "Seated Cable Row",
                        "Pull the cable handle toward the torso while keeping the back stable, then extend the arms.",
                        List.of("Back", "Biceps", "Forearms")
                ),
                new ExerciseDefinition(
                        "Pull-Up",
                        "Pull the body upward until the chin reaches bar height, then lower with control.",
                        List.of("Back", "Biceps", "Forearms")
                ),
                new ExerciseDefinition(
                        "Dumbbell Row",
                        "Pull a dumbbell toward the hip while supporting the body and keeping the back stable.",
                        List.of("Back", "Biceps", "Forearms")
                ),

                new ExerciseDefinition(
                        "Military Press",
                        "Press a barbell overhead from shoulder height while standing upright, then lower it under control.",
                        List.of("Shoulders", "Triceps", "Abs")
                ),
                new ExerciseDefinition(
                        "Dumbbell Shoulder Press",
                        "Press dumbbells overhead from shoulder level, then lower them slowly.",
                        List.of("Shoulders", "Triceps")
                ),
                new ExerciseDefinition(
                        "Lateral Raise",
                        "Raise dumbbells out to the sides until approximately shoulder height, then lower them.",
                        List.of("Shoulders")
                ),
                new ExerciseDefinition(
                        "Reverse Fly",
                        "With the torso hinged forward, move the arms outward to work the rear shoulders and upper back.",
                        List.of("Shoulders", "Back")
                ),

                new ExerciseDefinition(
                        "Biceps Curl",
                        "Bend the elbows to raise dumbbells toward the shoulders without swinging the torso.",
                        List.of("Biceps", "Forearms")
                ),
                new ExerciseDefinition(
                        "Hammer Curl",
                        "Raise dumbbells with the palms facing inward while keeping the elbows close to the body.",
                        List.of("Biceps", "Forearms")
                ),
                new ExerciseDefinition(
                        "Triceps Pushdown",
                        "Push a cable handle downward until the elbows extend, keeping the upper arms close to the torso.",
                        List.of("Triceps")
                ),
                new ExerciseDefinition(
                        "Overhead Triceps Extension",
                        "Extend the elbows to raise a weight overhead while keeping the upper arms relatively still.",
                        List.of("Triceps", "Shoulders")
                ),
                new ExerciseDefinition(
                        "Wrist Curl",
                        "Move the wrists to curl a light weight upward while the forearms remain supported.",
                        List.of("Forearms")
                ),
                new ExerciseDefinition(
                        "Reverse Wrist Curl",
                        "Raise the backs of the hands by extending the wrists while the forearms remain supported.",
                        List.of("Forearms")
                ),

                new ExerciseDefinition(
                        "Barbell Squat",
                        "Bend the hips and knees to lower the body, then stand up while keeping the torso controlled.",
                        List.of("Quadriceps", "Glutes", "Hamstrings", "Abs")
                ),
                new ExerciseDefinition(
                        "Goblet Squat",
                        "Hold a weight in front of the chest, squat down with control, then stand upright.",
                        List.of("Quadriceps", "Glutes", "Abs")
                ),
                new ExerciseDefinition(
                        "Leg Press",
                        "Push the platform away by extending the knees and hips, then lower it under control.",
                        List.of("Quadriceps", "Glutes", "Hamstrings")
                ),
                new ExerciseDefinition(
                        "Romanian Deadlift",
                        "Push the hips backward while lowering a weight along the legs, then extend the hips to stand.",
                        List.of("Hamstrings", "Glutes", "Back", "Forearms")
                ),
                new ExerciseDefinition(
                        "Leg Extension",
                        "Straighten the knees against resistance while seated, then lower the weight slowly.",
                        List.of("Quadriceps")
                ),
                new ExerciseDefinition(
                        "Seated Leg Curl",
                        "Bend the knees against resistance while seated, then return to the starting position.",
                        List.of("Hamstrings")
                ),
                new ExerciseDefinition(
                        "Lying Leg Curl",
                        "Bend the knees to bring the heels toward the body against resistance, then lower slowly.",
                        List.of("Hamstrings")
                ),
                new ExerciseDefinition(
                        "Standing Calf Raise",
                        "Rise onto the balls of the feet, pause briefly, then lower the heels under control.",
                        List.of("Calves")
                ),
                new ExerciseDefinition(
                        "Hip Thrust",
                        "Drive through the feet to raise the hips while keeping the upper back supported, then lower slowly.",
                        List.of("Glutes", "Hamstrings")
                ),
                new ExerciseDefinition(
                        "Glute Bridge",
                        "Lie on the back with bent knees and lift the hips by contracting the glutes.",
                        List.of("Glutes", "Hamstrings", "Abs")
                ),

                new ExerciseDefinition(
                        "Plank",
                        "Hold a straight body position supported by the forearms and toes while bracing the torso.",
                        List.of("Abs", "Obliques", "Shoulders")
                ),
                new ExerciseDefinition(
                        "Crunch",
                        "Lift the shoulders slightly off the floor by contracting the abdominal muscles, then lower slowly.",
                        List.of("Abs")
                ),
                new ExerciseDefinition(
                        "Hanging Knee Raise",
                        "While hanging from a bar, raise the knees toward the torso, then lower them with control.",
                        List.of("Abs", "Obliques", "Forearms")
                ),
                new ExerciseDefinition(
                        "Russian Twist",
                        "Sit with the torso slightly reclined and rotate the upper body from side to side.",
                        List.of("Obliques", "Abs")
                ),
                new ExerciseDefinition(
                        "Dead Bug",
                        "Lie on the back and alternately extend opposite arms and legs while keeping the lower back stable.",
                        List.of("Abs", "Obliques")
                ),
                new ExerciseDefinition(
                        "Bicycle Crunch",
                        "Alternate bringing each elbow toward the opposite knee while rotating the torso.",
                        List.of("Abs", "Obliques")
                ),
                new ExerciseDefinition(
                        "Side Plank",
                        "Hold the body in a straight line while supported on one forearm and the side of one foot.",
                        List.of("Obliques", "Abs", "Shoulders")
                ),

                new ExerciseDefinition(
                        "Conventional Deadlift",
                        "Lift a barbell from the floor by extending the hips and knees while keeping the weight close to the body.",
                        List.of("Glutes", "Hamstrings", "Quadriceps", "Back", "Forearms")
                ),
                new ExerciseDefinition(
                        "Kettlebell Swing",
                        "Drive the hips forward to swing a kettlebell to chest height, then guide it back between the legs.",
                        List.of("Glutes", "Hamstrings", "Abs", "Shoulders")
                ),
                new ExerciseDefinition(
                        "Burpee",
                        "Move from standing into a floor-supported position, return to standing, and finish with a jump if appropriate.",
                        List.of("Full Body", "Chest", "Quadriceps", "Abs", "Shoulders")
                ),
                new ExerciseDefinition(
                        "Farmer's Carry",
                        "Walk a controlled distance while carrying weights at the sides and keeping the torso upright.",
                        List.of("Forearms", "Shoulders", "Abs", "Full Body")
                ),
                new ExerciseDefinition(
                        "Mountain Climber",
                        "From a high plank position, alternate driving the knees toward the chest while keeping the torso stable.",
                        List.of("Full Body", "Abs", "Shoulders", "Quadriceps")
                )
        );

        for (ExerciseDefinition definition : predefined) {
            String normalizedName =
                    definition.name().toLowerCase(Locale.ROOT);

            if (existingNames.contains(normalizedName)) {
                continue;
            }

            Exercise exercise = new Exercise(
                    definition.name(),
                    definition.description()
            );

            for (String bodyPartName : definition.bodyParts()) {
                BodyPart bodyPart = bodyParts.get(
                        bodyPartName.toLowerCase(Locale.ROOT)
                );

                if (bodyPart == null) {
                    throw new IllegalStateException(
                            "Missing body part: " + bodyPartName
                    );
                }

                exercise.addBodyPart(bodyPart);
            }

            // Predefined exercises are shared by all users.
            exercise.setOwner(null);

            exerciseDAO.create(exercise);
            existingNames.add(normalizedName);
        }
    }
}
