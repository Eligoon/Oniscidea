package app.utils;

import app.exceptions.ApiException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Utils {



    public static String getPropertyValue(String propName, String resourceName) {
        // Prefer environment variables (GitHub Actions / deployment)
        String envValue = System.getenv(propName);
        if (envValue != null && !envValue.isBlank()) {
            return envValue.trim();
        }

        // Fall back to the properties file (local development)
        try (InputStream is = Utils.class.getClassLoader()
                .getResourceAsStream(resourceName)) {

            if (is == null) {
                throw new ApiException(
                        500,
                        String.format(
                                "Property %s not found: environment variable is unset and resource %s is missing.",
                                propName, resourceName
                        )
                );
            }

            Properties prop = new Properties();
            prop.load(is);

            String value = prop.getProperty(propName);
            if (value != null && !value.isBlank()) {
                return value.trim();
            }

            throw new ApiException(
                    500,
                    String.format("Property %s not found in %s or environment variables.", propName, resourceName)
            );

        } catch (IOException ex) {
            throw new ApiException(
                    500,
                    String.format("Could not read property %s.", propName)
            );
        }
    }


}
