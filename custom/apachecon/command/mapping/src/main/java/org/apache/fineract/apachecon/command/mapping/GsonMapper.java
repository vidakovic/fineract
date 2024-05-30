package org.apache.fineract.apachecon.command.mapping;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.fineract.infrastructure.core.serialization.GoogleGsonSerializerHelper;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public final class GsonMapper {

    private final Gson gson = GoogleGsonSerializerHelper.createSimpleGson();

    public JsonElement map(String source) {
        return gson.toJsonTree(source);
    }

    public String map(JsonElement source) {
        return gson.toJson(source);
    }
}
