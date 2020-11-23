package com.wizardlybump17.wtags.runnable;

import com.google.gson.Gson;
import com.wizardlybump17.wlib.database.Database;
import com.wizardlybump17.wtags.api.manager.TaggablePlayerManager;
import lombok.AllArgsConstructor;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.ResultSet;

@AllArgsConstructor
public class UpdateDatabaseRunnable extends BukkitRunnable {

    private final TaggablePlayerManager taggablePlayerManager;
    private final Database database;

    @Override
    public void run() {
        Gson gson = new Gson();
        taggablePlayerManager.getRegisteredPlayers().forEach((name, player) -> {
            try (ResultSet resultSet = database.query(
                    "SELECT * FROM playerTags WHERE player = ?;", name)) {

                String serializedTags = gson.toJson(player.getTags());

                boolean next = resultSet.next();

                if(!next) database.update(
                        "INSERT INTO playerTags (player, tags, currentTag) VALUES (?, ?, ?);",
                        name, serializedTags, player.isUsingTag()
                                ? player.getCurrentTag().getName().toLowerCase()
                                : null);

                if(next) database.update(
                        "UPDATE playerTags SET tags = ? AND currentTag = ? WHERE player = ?;",
                        serializedTags,
                        player.isUsingTag()
                                ? player.getCurrentTag().getName().toLowerCase()
                                : null,
                        name);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
