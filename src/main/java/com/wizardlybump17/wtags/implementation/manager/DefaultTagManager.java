package com.wizardlybump17.wtags.implementation.manager;

import com.wizardlybump17.wtags.api.manager.TagManager;
import com.wizardlybump17.wtags.api.tag.Tag;

import java.util.HashMap;
import java.util.Map;

public class DefaultTagManager implements TagManager {

    private final Map<String, Tag> tags = new HashMap<>();

    @Override
    public Map<String, Tag> getRegisteredTags() {
        return tags;
    }

    @Override
    public void registerTag(Tag tag) {
        tags.put(tag.getName().toLowerCase(), tag);
    }

    @Override
    public void unregisterTag(String tag) {
        tags.remove(tag.toLowerCase());
    }

    @Override
    public boolean isTagRegistered(String tag) {
        return tags.containsKey(tag.toLowerCase());
    }

    @Override
    public Tag getTag(String tag) {
        return tags.get(tag.toLowerCase());
    }
}
