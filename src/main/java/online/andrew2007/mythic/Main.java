package online.andrew2007.mythic;

import com.google.common.collect.ImmutableSet;
import online.andrew2007.mythic.config.runtimeParams.TransmittableRuntimeParams;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;


public class Main {
    public static void main(String[] args) {
        System.out.println("Don't run this jar directly, it's a Minecraft mod.");
        Field[] allFields = TransmittableRuntimeParams.class.getDeclaredFields();
        ImmutableSet.Builder<Field> builder = new ImmutableSet.Builder<>();
        for (Field field : allFields) {
            if (field.getType().isPrimitive() && !Modifier.isStatic(field.getModifiers())) {
                builder.add(field);
            }
        }
        builder.build().forEach(System.out::println);
    }
}
