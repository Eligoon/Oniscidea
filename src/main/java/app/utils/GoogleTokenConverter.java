
package app.utils;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class GoogleTokenConverter
        implements AttributeConverter<String, String> {

    @Override
    public String convertToDatabaseColumn(String attribute) {
        return GoogleTokenEncryption.encrypt(attribute);
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        return GoogleTokenEncryption.decrypt(dbData);
    }
}
