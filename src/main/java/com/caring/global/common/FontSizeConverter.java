package com.caring.global.common;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class FontSizeConverter implements AttributeConverter<FontSize, Integer> {
    @Override
    public Integer convertToDatabaseColumn(FontSize attribute) {
        if(attribute == null) return null;
        return switch (attribute) {
            case SMALL -> 1;
            case MEDIUM -> 2;
            case LARGE -> 3;
        };
    }

    @Override
    public FontSize convertToEntityAttribute(Integer dbData) {
        if(dbData == null) return null;
        return switch (dbData) {
            case 1 -> FontSize.SMALL;
            case 2 -> FontSize.MEDIUM;
            case 3 -> FontSize.LARGE;
            default -> throw new IllegalArgumentException("알 수 없는 font_size 값: " + dbData);
        };
    }
}
