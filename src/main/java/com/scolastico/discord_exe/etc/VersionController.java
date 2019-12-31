package com.scolastico.discord_exe.etc;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class VersionController {

    private static String version = "";
    private static String commit = "";
    private static String versionInfo = "";

    public static String getVersion() {
        if (version == "") {
            try {
                if (versionInfo == "") {
                    versionInfo = new VersionController().versionInformation();
                }
                JSONObject object = new JSONObject(versionInfo);
                version = object.getString("git.build.version");
            } catch (Exception e) {
                e.printStackTrace();
                return "error";
            }
        }
        return version;
    }

    public static String getCommit() {
        if (commit == "") {
            try {
                if (versionInfo == "") {
                    versionInfo = new VersionController().versionInformation();
                }
                JSONObject object = new JSONObject(versionInfo);
                commit = object.getString("git.commit.id.abbrev");
            } catch (Exception e) {
                return "error";
            }
        }
        return commit;
    }

    public static String getVersionsCode() {
        return "$v=" + getVersion() + "$c=" + getCommit();
    }

    public String versionInformation() {
        return readGitProperties();
    }

    private String readGitProperties() {
        ClassLoader classLoader = getClass().getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream("git.properties");
        try {
            return readFromInputStream(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("[ERROR] This build is corrupt!");
            System.exit(1);
            return "Version information could not be retrieved";
        }
    }

    private String readFromInputStream(InputStream inputStream) throws IOException {
        StringBuilder resultStringBuilder = new StringBuilder();
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while ((line = br.readLine()) != null) {
            resultStringBuilder.append(line).append("\n");
        }
        return resultStringBuilder.toString();
    }

}
