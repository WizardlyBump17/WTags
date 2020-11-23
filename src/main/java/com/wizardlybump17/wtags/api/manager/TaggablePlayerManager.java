package com.wizardlybump17.wtags.api.manager;

import com.wizardlybump17.wtags.api.player.TaggablePlayer;

import java.util.Map;

public interface TaggablePlayerManager {

    Map<String, TaggablePlayer> getRegisteredPlayers();

    void registerPlayer(TaggablePlayer player);

    void unregisterPlayer(String player);

    boolean isPlayerRegistered(String player);

    TaggablePlayer getPlayer(String player);
}
