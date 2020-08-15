package Data;

import javax.persistence.AttributeConverter;

@javax.persistence.Converter
public class Converter implements AttributeConverter<Integer, Integer> {

    @Override
    public Integer convertToDatabaseColumn(Integer attribute) {
        return 0;
    }

    @Override
    public Integer convertToEntityAttribute(Integer dbData) {
        return 0;
    }
}