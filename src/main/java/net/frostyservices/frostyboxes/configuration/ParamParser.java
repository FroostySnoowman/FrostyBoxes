package net.frostyservices.frostyboxes.configuration;

import net.frostyservices.frostyboxes.configuration.annotations.ColorString;
import net.frostyservices.frostyboxes.configuration.types.ConfigMessage;
import net.frostyservices.frostyboxes.util.GradientUtil;
import org.bukkit.Bukkit;
import java.lang.reflect.Field;
import java.util.List;

public class ParamParser {
    private static final List<Class<?>> yamlClasses = List.of(Boolean.class, Integer.class, Float.class, Double.class, Short.class, Long.class,
            boolean.class, int.class, float.class, short.class, double.class, long.class);

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Object deserialize(Object input, Field targetField) {
        Class targetFieldType = targetField.getType();
        if(targetFieldType.isEnum()) {
            return Enum.valueOf(targetFieldType, (String) input);
        } else if(targetFieldType.equals(String.class)) {
            ColorString colorStringAnnotation = targetField.getAnnotation(ColorString.class);
            if(colorStringAnnotation!=null) {
                return GradientUtil.colorify((String) input);
            }
            return input;
        } else if(targetFieldType.equals(ConfigMessage.class)) {
            return new ConfigMessage((String) input);
        } else if(yamlClasses.contains(targetFieldType)) {
            return input;
        }

        Bukkit.getConsoleSender().sendMessage("Warning: Could not deserialize field of type " + targetFieldType.getName() + " correctly!");
        return input;
    }

    public static Object serialize(Object input) {
        if(input.getClass().isEnum()) {
            return input.toString();
        } else if(input instanceof ConfigMessage) {
            return ((ConfigMessage) input).getRaw();
        } else if(yamlClasses.contains(input.getClass())) {
            return input;
        }
        return input;
    }
}
