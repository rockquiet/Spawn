package me.rockquiet.spawn;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

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

                String latest = jsonResponse.get("tag_name").getAsString().replaceFirst("^v", "");
                String current = plugin.getDescription().getVersion();

                // convert version strings to number; 1.4.1 = 141
                int latestAsNumber = Integer.parseInt(latest.replace(".", ""));
                int currentAsNumber = Integer.parseInt(current.replace(".", ""));

                if (latestAsNumber > currentAsNumber) {
                    plugin.getLogger().info("An update is available! Latest version: " + latest + ", you are using: " + current);
                } else {
                    plugin.getLogger().warning("You are running a newer version of the plugin than released. If you are using a development build, please report any bugs on the project's GitHub.");
                }
            } catch (IOException e) {
                plugin.getLogger().info("Unable to check for updates: " + e.getMessage());
            }
        });
    }
}
