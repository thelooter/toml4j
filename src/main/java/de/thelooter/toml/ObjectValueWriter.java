package de.thelooter.toml;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

class ObjectValueWriter implements ValueWriter {
  static final ValueWriter OBJECT_VALUE_WRITER = new ObjectValueWriter();

  @Override
  public boolean canWrite(Object value) {
    return true;
  }

  @Override
  public void write(Object value, WriterContext context) {
    Map<String, Object> to = new LinkedHashMap<>();
    Set<Field> fields = getFields(value.getClass());
    for (Field field : fields) {
      to.put(field.getName(), getFieldValue(field, value));
    }

    MapValueWriter.MAP_VALUE_WRITER.write(to, context);
  }

  @Override
  public boolean isPrimitiveType() {
    return false;
  }

  private static Set<Field> getFields(Class<?> cls) {
    Set<Field> fields = new LinkedHashSet<>(Arrays.asList(cls.getDeclaredFields()));
    while (cls != Object.class) {
      fields.addAll(Arrays.asList(cls.getDeclaredFields()));
      cls = cls.getSuperclass();
    }
    removeConstantsAndSyntheticFields(fields);

    return fields;
  }

  private static void removeConstantsAndSyntheticFields(Set<Field> fields) {
    fields.removeIf(field -> {
      int modifiers = field.getModifiers();
      return (Modifier.isFinal(modifiers) && Modifier.isStatic(modifiers))
              || field.isSynthetic()
              || Modifier.isTransient(modifiers);
    });;
  }

  private static Object getFieldValue(Field field, Object o) {
    boolean isAccessible = field.isAccessible();
    field.setAccessible(true);
    Object value = null;
    try {
      value = field.get(o);
    } catch (IllegalAccessException ignored) {
    }
    field.setAccessible(isAccessible);

    return value;
  }

  private ObjectValueWriter() {}
}
