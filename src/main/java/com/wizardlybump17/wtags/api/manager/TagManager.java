package com.wizardlybump17.wtags.api.manager;

import com.wizardlybump17.wtags.api.tag.Tag;

import java.util.Map;

public interface TagManager {

    Map<String, Tag> getRegisteredTags();

    void registerTag(Tag tag);

    void unregisterTag(String tag);

    boolean isTagRegistered(String tag);

    Tag getTag(String tag);
}
