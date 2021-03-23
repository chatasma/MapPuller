package me.tqnk.mp.util;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import lombok.Getter;
import me.tqnk.mp.models.GithubContent;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

@Getter
public class MapScavenger {
    private String mapDest;
    private String url;
    private List<String> mapNamez;
    private String authUser;
    private String authPassword;
    public MapScavenger(String mapDest, String url, List<String> mapNamez, String authToken) {
        this.mapDest = mapDest;
        this.url = "https://api.github.com/repos/" + url + "/contents";
        this.mapNamez = mapNamez;
        String[] authParts = authToken.split(":");
        this.authUser = authParts[0];
        this.authPassword = authParts[1];
        File parentDir = new File(mapDest);
        if(!parentDir.exists()) parentDir.mkdirs();
    }

    public void loadMaps() {
        if(mapNamez.isEmpty()) Bukkit.getLogger().info("No maps found in mapnames option");
        if(mapDest == null || url == null || mapNamez.isEmpty()) return;
        GithubContent[] mainGH = getGithubContentFromUrl(url, this.authUser, this.authPassword);
        if(mainGH != null) {
            Bukkit.getLogger().info("Attempting to download maps from " + url + " ...");
            long currentTime = System.currentTimeMillis();
            for(GithubContent content : mainGH) {
                loadMap(content, isInMapNamez(content.getImmediateName()));
            }
            long elapsed = System.currentTimeMillis() - currentTime;
            Bukkit.getLogger().info("Downloading finished! (" + elapsed + "ms!)");
        } else {
            Bukkit.getLogger().info("Could not find repository from " + url);
        }
    }
    private void loadMap(GithubContent ctnt, String dirName) {
        if(ctnt.getFileType().equalsIgnoreCase("dir")) {
            GithubContent[] subContent = getGithubContentFromUrl(ctnt.getRefUrl(), this.authUser, this.authPassword);
            if(subContent != null) {
                for(GithubContent subSingle : subContent) {
                    String theRealDirName = dirName;
                    if(dirName.isEmpty()) {
                        theRealDirName = isInMapNamez(subSingle.getImmediateName());
                        if(!theRealDirName.isEmpty()) {
                            File doesTheMapAlreadyExist = new File(mapDest + "/" + theRealDirName.substring(0, theRealDirName.length() - 1));
                            if(doesTheMapAlreadyExist.exists()) {
                                try {
                                    Bukkit.getLogger().info("Deleting " + subSingle.getImmediateName() + " to update it...");
                                    FileUtils.deleteDirectory(doesTheMapAlreadyExist);
                                } catch (IOException e) {
                                    Bukkit.getLogger().info("Error in deleting directory " + subSingle.getImmediateName());
                                }
                            }
                        }
                    }
                    loadMap(subSingle, theRealDirName);
                }
            }
        } else if(ctnt.getFileType().equalsIgnoreCase("file") && (dirName != null && !dirName.isEmpty())) {
            String realSuffix = ctnt.getFullPath().split(dirName, 2)[1];
            Bukkit.getLogger().info("Attempting to download FROM " + dirName + " FILE PATH: " + this.mapDest + "/" + dirName + realSuffix);
            try {
                FileUtils.copyURLToFile(new URL(ctnt.getDownloadLink()), new File(this.mapDest + "/" + dirName + realSuffix), 10000, 10000);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void loadFile(String path) throws IllegalArgumentException {
        String dir;
        String name;
        int lastSlash = path.lastIndexOf('/');
        if (lastSlash == -1) {
            dir = "";
            name = path;
        }
        else {
            lastSlash++;
            dir = path.substring(0, lastSlash);
            name = path.substring(lastSlash);
        }
        try {
            GithubContent target = null;
            GithubContent[] contents = getGithubContentFromUrl(url + "/" + dir, this.authUser, this.authPassword);
            for (GithubContent content : contents) {
                if (content.getImmediateName().equals(name)) {
                    target = content;
                    break;
                }
            }
            if (target == null) throw new IllegalArgumentException("File not found");
            if (target.getFileType().equals("file")) {
                long currentTime = System.currentTimeMillis();
                FileUtils.copyURLToFile(new URL(target.getDownloadLink()), new File(this.mapDest + "/" + dir + name), 10000, 10000);
                long elapsed = System.currentTimeMillis() - currentTime;
                Bukkit.getLogger().info("Single download finished! (" + elapsed + "ms!)");
                return;
            }
            loadMap(target, dir);
        } catch (Exception e) {
            throw new IllegalArgumentException("File not found.");
        }
    }

    private String isInMapNamez(String candidate) {
        return (mapNamez.contains(candidate) ? candidate + "/" : "");
    }

    private static GithubContent[] getGithubContentFromUrl(String url, String authUser, String authPassword) {
        try {
            return Unirest.get(url)
                    .header("accept", "application/json")
                    .header("Content-Type", "application/json")
                    .basicAuth(authUser, authPassword)
                    .asObject(GithubContent[].class).getBody();
        } catch (UnirestException e) {
            e.printStackTrace();
            return null;
        }
    }
}
