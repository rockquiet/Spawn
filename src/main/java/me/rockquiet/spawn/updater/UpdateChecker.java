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
                URL obj = new URL("https://api.github.com/repos/rockquiet/spawn/releases/latest");
                HttpURLConnection con = (HttpURLConnection) obj.openConnection();
                con.setRequestMethod("GET");
                con.setRequestProperty("User-Agent", "Mozilla/5.0");

                if (con.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    plugin.getLogger().warning("Unable to check for updates...");
                    return;
                }

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));

                String line;
                StringBuilder response = new StringBuilder();
                while ((line = bufferedReader.readLine()) != null) {
                    response.append(line);
                }
                bufferedReader.close();
                con.disconnect();

                JsonObject jsonResponse = new Gson().fromJson(response.toString(), JsonObject.class);

                Version latest = Version.parse(jsonResponse.get("tag_name").getAsString().replaceFirst("^v", ""));
                Version current = Version.parse(plugin.getDescription().getVersion());
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
