
package app.config;

import app.daos.BodyPartDAO;
import app.entities.BodyPart;

import java.util.Map;

public class BodyPartSeeder {

    private BodyPartSeeder() {
    }

    public static void seed(BodyPartDAO bodyPartDAO) {
        Map<String, String> predefined = Map.ofEntries(
                Map.entry("Head", "#E53935"),
                Map.entry("Shoulders", "#6A1B9A"),
                Map.entry("Biceps", "#AB47BC"),
                Map.entry("Triceps", "#CE93D8"),
                Map.entry("Chest", "#8D3200"),
                Map.entry("Back", "#F9A825"),
                Map.entry("Abs", "#FF6D00"),
                Map.entry("Obliques", "#FFCC80"),
                Map.entry("Forearms", "#0D47A1"),
                Map.entry("Glutes", "#1B5E20"),
                Map.entry("Quadriceps", "#43A047"),
                Map.entry("Hamstrings", "#A5D6A7"),
                Map.entry("Calves", "#F9E300"),
                Map.entry("Full Body", "#90A4AE")
        );

        for (Map.Entry<String, String> entry : predefined.entrySet()) {
            boolean exists = bodyPartDAO.getAll().stream()
                    .anyMatch(bodyPart ->
                            bodyPart.getName().equalsIgnoreCase(entry.getKey())
                    );

            if (!exists) {
                bodyPartDAO.create(
                        new BodyPart(entry.getKey(), entry.getValue())
                );
            }
        }
    }
}
