package org.motechproject.couch.mrs.util;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import org.joda.time.DateTime;
import org.motechproject.commons.api.json.MotechJsonReader;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public final class CouchJsonUtils {

    private CouchJsonUtils() { }

    private static final MotechJsonReader READER = new MotechJsonReader();
    private static Map<Type, Object> providedAdapters = new HashMap<Type, Object>();

    static {
        providedAdapters.put(DateTime.class, new CouchDateTimeAdapter());
    }

    public static Object readJson(String json, Type type) {
        return READER.readFromString(json, type, providedAdapters);
    }

    public static Object readJsonWithAdapters(String json, Type type, Map<Type, Object> adapters) {
        adapters.putAll(providedAdapters);
        return READER.readFromString(json, type, adapters);
    }

    private static class CouchDateTimeAdapter implements JsonSerializer<DateTime>, JsonDeserializer<DateTime> {
        public DateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
            DateTime date = null;

            date = DateTime.parse(json.getAsString());

            return date;
        }

        @Override
        public JsonElement serialize(DateTime src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.toString());
        }
    }
}
