package cz.tw.proxymanager.chromedriver;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ProxyAuthExtension {

    public static File createExtension(String proxyHost, int proxyPort, String proxyUsername, String proxyPassword) throws IOException {
        // Create a temporary directory for the extension
        Path extensionDir = Files.createTempDirectory("proxy_auth_extension");

        // Create the manifest.json file
        File manifestFile = new File(extensionDir.toFile(), "manifest.json");
        try (FileWriter manifestWriter = new FileWriter(manifestFile)) {
            manifestWriter.write("{\n" +
                    "  \"version\": \"1.0.0\",\n" +
                    "  \"manifest_version\": 2,\n" +
                    "  \"name\": \"Chrome Proxy\",\n" +
                    "  \"permissions\": [\n" +
                    "    \"proxy\",\n" +
                    "    \"tabs\",\n" +
                    "    \"unlimitedStorage\",\n" +
                    "    \"storage\",\n" +
                    "    \"<all_urls>\",\n" +
                    "    \"webRequest\",\n" +
                    "    \"webRequestBlocking\"\n" +
                    "  ],\n" +
                    "  \"background\": {\n" +
                    "    \"scripts\": [\"background.js\"]\n" +
                    "  },\n" +
                    "  \"minimum_chrome_version\": \"22.0.0\"\n" +
                    "}");
        }

        // Create the background.js file
        File backgroundFile = new File(extensionDir.toFile(), "background.js");
        try (FileWriter backgroundWriter = new FileWriter(backgroundFile)) {
            backgroundWriter.write("var config = {\n" +
                    "  mode: \"fixed_servers\",\n" +
                    "  rules: {\n" +
                    "    singleProxy: {\n" +
                    "      scheme: \"http\",\n" +
                    "      host: \"" + proxyHost + "\",\n" +
                    "      port: parseInt(" + proxyPort + ")\n" +
                    "    },\n" +
                    "    bypassList: [\"localhost\"]\n" +
                    "  }\n" +
                    "};\n" +
                    "\n" +
                    "chrome.proxy.settings.set({value: config, scope: \"regular\"}, function() {});\n" +
                    "\n" +
                    "function callbackFn(details) {\n" +
                    "  return {\n" +
                    "    authCredentials: {\n" +
                    "      username: \"" + proxyUsername + "\",\n" +
                    "      password: \"" + proxyPassword + "\"\n" +
                    "    }\n" +
                    "  };\n" +
                    "}\n" +
                    "\n" +
                    "chrome.webRequest.onAuthRequired.addListener(\n" +
                    "  callbackFn,\n" +
                    "  {urls: [\"<all_urls>\"]},\n" +
                    "  ['blocking']\n" +
                    ");");
        }

        // Create the ZIP file from the extension directory
        File zipFile = new File(System.getProperty("java.io.tmpdir"), "proxy_auth_extension_" + proxyHost + "_" + proxyPort + ".zip");
        zipDirectory(extensionDir, zipFile.toPath());

        return zipFile;
    }

    private static void zipDirectory(Path sourceDirPath, Path zipPath) throws IOException {
        try (ZipOutputStream zs = new ZipOutputStream(Files.newOutputStream(zipPath))) {
            Files.walk(sourceDirPath)
                    .filter(path -> !Files.isDirectory(path))
                    .forEach(path -> {
                        ZipEntry zipEntry = new ZipEntry(sourceDirPath.relativize(path).toString());
                        try {
                            zs.putNextEntry(zipEntry);
                            Files.copy(path, zs);
                            zs.closeEntry();
                        } catch (IOException e) {
                            System.err.println(e);
                        }
                    });
        }
    }
}
