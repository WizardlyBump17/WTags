package com.wizardlybump17.wtags.implementation.manager;

import com.wizardlybump17.wtags.api.manager.TaggablePlayerManager;
import com.wizardlybump17.wtags.api.player.TaggablePlayer;

import java.util.HashMap;
import java.util.Map;

public class DefaultTaggablePlayerManager implements TaggablePlayerManager {

    private final Map<String, TaggablePlayer> players = new HashMap<>();

    @Override
    public Map<String, TaggablePlayer> getRegisteredPlayers() {
        return players;
    }

    @Override
    public void registerPlayer(TaggablePlayer player) {
        players.put(player.getName().toLowerCase(), player);
    }

    @Override
    public void unregisterPlayer(String player) {
        players.remove(player.toLowerCase());
    }

    @Override
    public boolean isPlayerRegistered(String player) {
        return players.containsKey(player.toLowerCase());
    }

    @Override
    public TaggablePlayer getPlayer(String player) {
        return players.get(player.toLowerCase());
    }
}
