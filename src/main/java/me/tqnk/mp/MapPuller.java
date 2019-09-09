package me.tqnk.mp;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mashape.unirest.http.ObjectMapper;
import com.mashape.unirest.http.Unirest;
import lombok.Getter;
import lombok.Setter;
import me.tqnk.mp.command.CommandHandler;
import me.tqnk.mp.command.ConfigReloadCommand;
import me.tqnk.mp.command.LoadCommand;
import me.tqnk.mp.models.GithubContent;
import me.tqnk.mp.models.GithubContentDeserializer;
import me.tqnk.mp.util.MapScavenger;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapPuller extends JavaPlugin {
    private static MapPuller instance;
    @Getter @Setter private MapScavenger scavenger = null;
    private CommandHandler cmdHandler;
    public static MapPuller get() {
        return instance;
    }
    private FileConfiguration fileConfig;
    @Getter private Gson gson;

    @Override
    public void onEnable() {
        instance = this;
        gson = new GsonBuilder().registerTypeAdapter(GithubContent.class, new GithubContentDeserializer()).create();
        Unirest.setObjectMapper(new ObjectMapper() {

            public <T> T readValue(String s, Class<T> aClass) {
                try {
                    return gson.fromJson(s, aClass);
                } catch(Exception e){
                    throw new RuntimeException(e);
                }
            }

            public String writeValue(Object o) {
                try {
                    return gson.toJson(o);
                } catch(Exception e){
                    throw new RuntimeException(e);
                }
            }
        });
        cmdHandler = new CommandHandler(this);
        cmdHandler.add(new LoadCommand());
        cmdHandler.add(new ConfigReloadCommand());
        this.fileConfig = getConfig();
        saveDefaultConfig();
        ConfigurationSection mapGrabData = fileConfig.getConfigurationSection("source");
        if(mapGrabData != null) {
            this.scavenger = createScavengerFromConfig(mapGrabData);
        }
    }

    public static MapScavenger createScavengerFromConfig(ConfigurationSection mapGrabData) {
        if(mapGrabData == null) return null;
        List<String> mapsRequested = new ArrayList<>();
        mapsRequested.addAll(mapGrabData.getStringList("mapnames"));
        String mapDest = "Maps/dl";
        String getFrom = "WarzoneMC/Maps";
        String authToken = "";
        if(mapGrabData.getString("target") != null) mapDest = mapGrabData.getString("target");
        if(mapGrabData.getString("repo") != null) getFrom = mapGrabData.getString("repo");
        if(mapGrabData.get("auth_token") != null) authToken = mapGrabData.getString("auth_token");
        if(authToken != null && !authToken.isEmpty()) {
            MapScavenger scavenger = new MapScavenger(mapDest, getFrom, mapsRequested, authToken);
            return scavenger;
        } else {
            Bukkit.getLogger().warning("No GitHub personal access auth_token found.");
        }
        return null;
    }

    @Override
    public void onDisable() {
        try {
            Unirest.shutdown();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
