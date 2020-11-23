package com.wizardlybump17.wtags.implementation.player;

import com.wizardlybump17.wtags.api.player.TaggablePlayer;
import com.wizardlybump17.wtags.api.tag.Tag;

import java.util.HashMap;
import java.util.Map;

public class DefaultTaggablePlayer implements TaggablePlayer {

    private final String name;
    private final Map<String, Tag> tags = new HashMap<>();
    private Tag currentTag;

    public DefaultTaggablePlayer(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Map<String, Tag> getTags() {
        return tags;
    }

    @Override
    public void addTag(Tag tag) {
        tags.put(tag.getName().toLowerCase(), tag);
    }

    @Override
    public void removeTag(String tag) {
        tags.remove(tag.toLowerCase());
    }

    @Override
    public boolean hasTag(String tag) {
        return tags.containsKey(tag.toLowerCase());
    }

    @Override
    public Tag getCurrentTag() {
        return currentTag;
    }

    @Override
    public void setCurrentTag(Tag tag) {
        currentTag = tag;
    }
}
