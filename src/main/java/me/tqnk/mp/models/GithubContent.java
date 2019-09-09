package me.tqnk.mp.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor @Getter
public class GithubContent {
    private String fileType;
    private String downloadLink;
    private String immediateName;
    private String fullPath;
    private String refUrl;
}
