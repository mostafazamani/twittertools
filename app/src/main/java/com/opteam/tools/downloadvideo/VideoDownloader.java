package com.opteam.tools.downloadvideo;

public interface VideoDownloader {

    String createDirectory();

    String getVideoId(String link);

    void DownloadVideo();
}
