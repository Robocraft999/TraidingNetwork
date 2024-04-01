package com.robocraft999.amazingtrading.resourcepoints.conversion;


import com.google.common.reflect.TypeToken;
import com.google.gson.*;
import com.robocraft999.amazingtrading.resourcepoints.nss.NormalizedSimpleStack;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.robocraft999.amazingtrading.resourcepoints.mapper.arithmetic.FullBigFractionArithmetic.FREE_ARITHMETIC_VALUE;

public class FixedValuesDeserializer implements JsonDeserializer<FixedValues> {

    @Override
    public FixedValues deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        FixedValues fixed = new FixedValues();
        JsonObject o = json.getAsJsonObject();
        for (Map.Entry<String, JsonElement> entry : o.entrySet()) {
            String key = entry.getKey();
            switch (key) {
                case "before" -> fixed.setValueBefore = parseSetValueMap(entry.getValue().getAsJsonObject(), context);
                case "after" -> fixed.setValueAfter = parseSetValueMap(entry.getValue().getAsJsonObject(), context);
                case "conversion" -> fixed.conversion = context.deserialize(entry.getValue().getAsJsonArray(), new TypeToken<List<CustomConversion>>() {}.getType());
                default -> throw new JsonParseException(String.format("Can not parse \"%s\":%s in fixedValues", key, entry.getValue()));
            }
        }
        return fixed;
    }

    private Map<NormalizedSimpleStack, Long> parseSetValueMap(JsonObject o, JsonDeserializationContext context) {
        Map<NormalizedSimpleStack, Long> out = new HashMap<>();
        for (Map.Entry<String, JsonElement> entry : o.entrySet()) {
            JsonPrimitive primitive = entry.getValue().getAsJsonPrimitive();
            if (primitive.isNumber()) {
                long value = primitive.getAsLong();
                if (value < 1) {
                    throw new JsonParseException("RP value must be at least one.");
                }
                out.put(context.deserialize(new JsonPrimitive(entry.getKey()), NormalizedSimpleStack.class), value);
                continue;
            } else if (primitive.isString()) {
                if (primitive.getAsString().toLowerCase(Locale.ROOT).equals("free")) {
                    out.put(context.deserialize(new JsonPrimitive(entry.getKey()), NormalizedSimpleStack.class), FREE_ARITHMETIC_VALUE);
                    continue;
                }
            }
            throw new JsonParseException("Could not parse " + o + " into 'free' or integer.");
        }
        return out;
    }
}