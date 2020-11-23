package com.wizardlybump17.wtags.api.player;

import com.wizardlybump17.wtags.api.tag.Tag;

import java.util.List;
import java.util.Map;

public interface TaggablePlayer {

    String getName();

    Map<String, Tag> getTags();

    void addTag(Tag tag);

    void removeTag(String tag);

    boolean hasTag(String tag);

    Tag getCurrentTag();

    void setCurrentTag(Tag tag);

    default boolean isUsingTag() {
        return getCurrentTag() != null;
    }
}
