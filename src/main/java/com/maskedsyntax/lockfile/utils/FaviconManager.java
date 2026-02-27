package com.maskedsyntax.lockfile.utils;

import javafx.scene.image.Image;
import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class FaviconManager {
    private static final String CACHE_DIR = StorageUtils.getIconCacheDir();
    private static final Map<String, Image> memoryCache = new HashMap<>();
    private static final String GOOGLE_FAVICON_SERVICE = "https://www.google.com/s2/favicons?domain=%s&sz=64";

    static {
        new File(CACHE_DIR).mkdirs();
    }

    public static CompletableFuture<Image> getFavicon(String url) {
        if (url == null || url.trim().isEmpty()) {
            return CompletableFuture.completedFuture(null);
        }

        String domain = extractDomain(url);
        if (domain == null) {
            return CompletableFuture.completedFuture(null);
        }

        if (memoryCache.containsKey(domain)) {
            return CompletableFuture.completedFuture(memoryCache.get(domain));
        }

        return CompletableFuture.supplyAsync(() -> {
            try {
                Path cachePath = Paths.get(CACHE_DIR, domain + ".png");
                if (Files.exists(cachePath)) {
                    Image image = new Image(cachePath.toUri().toString());
                    memoryCache.put(domain, image);
                    return image;
                }

                String serviceUrl = String.format(GOOGLE_FAVICON_SERVICE, domain);
                try (InputStream in = new URI(serviceUrl).toURL().openStream()) {
                    Files.copy(in, cachePath);
                    Image image = new Image(cachePath.toUri().toString());
                    memoryCache.put(domain, image);
                    return image;
                }
            } catch (Exception e) {
                return null;
            }
        });
    }

    private static String extractDomain(String url) {
        try {
            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                url = "https://" + url;
            }
            URI uri = new URI(url);
            String domain = uri.getHost();
            return domain != null && domain.startsWith("www.") ? domain.substring(4) : domain;
        } catch (Exception e) {
            return null;
        }
    }
}
