package me.tqnk.mp.models;

import com.google.gson.*;

import java.lang.reflect.Type;

public class GithubContentDeserializer implements JsonDeserializer<GithubContent> {

    @Override
    public GithubContent deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject json = jsonElement.getAsJsonObject();
        String fileType = json.get("type").getAsString();
        String downloadLink;
        downloadLink = (fileType.equalsIgnoreCase("dir")) ? "none" : json.get("download_url").getAsString();
        String immediateName = json.get("name").getAsString();
        String fullPath = json.get("path").getAsString();
        String refUrl = json.get("url").getAsString();
        return new GithubContent(fileType, downloadLink, immediateName, fullPath, refUrl);
    }
}
