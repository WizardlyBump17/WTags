package com.wizardlybump17.wtags;

import com.google.gson.Gson;
import com.wizardlybump17.wlib.config.WConfig;
import com.wizardlybump17.wlib.database.Database;
import com.wizardlybump17.wlib.database.MySQLDatabase;
import com.wizardlybump17.wlib.database.SQLiteDatabase;
import com.wizardlybump17.wlib.item.ItemBuilder;
import com.wizardlybump17.wtags.api.manager.TagManager;
import com.wizardlybump17.wtags.api.manager.TaggablePlayerManager;
import com.wizardlybump17.wtags.api.player.TaggablePlayer;
import com.wizardlybump17.wtags.api.tag.Tag;
import com.wizardlybump17.wtags.command.TagCommand;
import com.wizardlybump17.wtags.implementation.manager.DefaultTagManager;
import com.wizardlybump17.wtags.implementation.manager.DefaultTaggablePlayerManager;
import com.wizardlybump17.wtags.implementation.player.DefaultTaggablePlayer;
import com.wizardlybump17.wtags.runnable.UpdateDatabaseRunnable;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
public class WTags extends JavaPlugin {

    private final TagManager tagManager = new DefaultTagManager();
    private final TaggablePlayerManager taggablePlayerManager = new DefaultTaggablePlayerManager();

    private Database playersDatabase;

    private WConfig databaseConfig;

    @Override
    public void onEnable() {
        reloadTags();

        new TagCommand(this);

        databaseConfig = new WConfig(this, "database.yml", true);
//        initDatabase();

        new UpdateDatabaseRunnable(taggablePlayerManager, playersDatabase)
                .runTaskTimerAsynchronously(this, 30 * 60 * 20, 40 * 60 * 20);
    }

    @Override
    public void onDisable() {
        if(!playersDatabase.isClosed()) playersDatabase.closeConnection();
    }

    private void initDatabase() {
        switch (databaseConfig.getString("type", "sqlite").toLowerCase()) {
            case "sqlite": {
                playersDatabase = new SQLiteDatabase(this, "players.db");
                break;
            }

            case "mysql": {
                playersDatabase = new MySQLDatabase(
                        this,
                        databaseConfig.getString("host", "localhost"),
                        databaseConfig.getInt("port", 3306),
                        databaseConfig.getString("database", "wtags"),
                        databaseConfig.getString("user", "root"),
                        databaseConfig.getString("password", ""));
                break;
            }
        }

        playersDatabase.openConnection();

        if(playersDatabase.isClosed()) {
            Bukkit.getPluginManager().disablePlugin(this);
            getLogger().severe("Cannot possible open connection with database!");
            return;
        }

        playersDatabase.update("CREATE TABLE IF NOT EXISTS playerTags " +
                "(player VARCHAR(16) PRIMARY_KEY NOT NULL, " +
                "tags TEXT, " +
                "currentTag VARCHAR(256);");

        Bukkit.getScheduler().runTaskAsynchronously(this, this::initPlayers);
    }

    @SuppressWarnings("unchecked")
    private void initPlayers() {
        Gson gson = new Gson();
        try (ResultSet resultSet = playersDatabase.query("SELECT * FROM playersTags;")) {
            while(resultSet.next()) {
                String playerName = resultSet.getString("player");
                Map<String, Object> serializedTags = (Map<String, Object>) gson.fromJson(resultSet.getString("tags"), Map.class);
                String currentTagName = resultSet.getString("currentTag");

                TaggablePlayer taggablePlayer = new DefaultTaggablePlayer(playerName);
                taggablePlayer.setCurrentTag(tagManager.getTag(currentTagName));

                serializedTags.forEach((name, serializedTag) -> {
                    Map<String, Object> serializedTagMap = (Map<String, Object>) serializedTag;
                    taggablePlayer.addTag(Tag.deserialize(serializedTagMap));
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void reloadTags() {
        tagManager.getRegisteredTags().clear();

        File tagsFolder = new File(getDataFolder(), "tags");
        if(!getDataFolder().exists()) getDataFolder().mkdir();
        if (!tagsFolder.exists()) tagsFolder.mkdir();

        File[] tagFiles = tagsFolder.listFiles(file -> file.getName().endsWith(".yml"));
        if (tagFiles == null) return;

        for (File tagFile : tagFiles) {
            WConfig config = new WConfig(this, "tags/" + tagFile.getName(), false);

            String name = config.getString("name");
            String display = config.getString("display").replace('&', '§');
            double cashPrice = config.getDouble("prices.cash");
            double coinsPrice = config.getDouble("prices.coins");

            List<String> lore = new ArrayList<>();
            lore.add("");
            lore.add("§7Display: " + display);
            if (cashPrice > 0) lore.add("§7Price in cash: §f" + cashPrice);
            if (coinsPrice > 0) lore.add("§7Price in coins: §f" + coinsPrice);
            if (cashPrice <= 0 && coinsPrice <= 0) lore.add("§fFree!");

            tagManager.registerTag(
                    Tag.builder()
                            .name(name)
                            .display(display)
                            .cashPrice(cashPrice)
                            .coinsPrice(coinsPrice)
                            .inventoryItem(
                                    ItemBuilder.fromConfig(config, "inventory-item")
                                            .displayName("§f" + name)
                                            .lore(lore)
                                            .build())
                            .build());
        }
    }
}
