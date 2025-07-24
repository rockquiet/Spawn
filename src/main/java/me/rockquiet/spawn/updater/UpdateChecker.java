package me.rockquiet.spawn.updater;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import me.rockquiet.spawn.Spawn;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class UpdateChecker {

    public UpdateChecker(Spawn plugin) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                URL url = new URL("https://api.github.com/repos/rockquiet/spawn/releases/latest");
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                con.setRequestProperty("User-Agent", "Mozilla/5.0");

                if (con.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    plugin.getLogger().warning("Unable to check for updates...");
                    return;
                }

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                JsonObject jsonResponse = new Gson().fromJson(bufferedReader, JsonObject.class);

                bufferedReader.close();
                con.disconnect();

                Version latest = Version.parse(jsonResponse.get("tag_name").getAsString());

                String pluginVersion = plugin.getDescription().getVersion();
                if (pluginVersion.contains("SNAPSHOT")) {
                    plugin.getLogger().info("You are running a development build, please report any bugs on the project's GitHub.");
                    plugin.getLogger().info("Latest release version: " + latest + ", you are using: " + pluginVersion);
                    return;
                }

                Version current = Version.parse(pluginVersion);
                int compare = latest.compareTo(current);

                if (compare > 0) {
                    plugin.getLogger().info("An update is available! Latest version: " + latest + ", you are using: " + current);
                } else if (compare < 0) {
                    plugin.getLogger().warning("You are running a newer version of the plugin than released. If you are using a development build, please report any bugs on the project's GitHub.");
                }
            } catch (IOException e) {
                plugin.getLogger().info("Unable to check for updates: " + e.getMessage());
            }
        });
    }
}
