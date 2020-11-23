package com.wizardlybump17.wtags.api.tag;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;


@Builder
@Getter
@Setter
public class Tag {

    private final String name;
    private String display;
    private double coinsPrice, cashPrice;
    private ItemStack inventoryItem;

    @Override
    public String toString() {
        return serialize().toString();
    }

    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("display", display);
        map.put("coinsPrice", coinsPrice);
        map.put("cashPrice", cashPrice);
        map.put("inventoryItem", inventoryItem.serialize());
        return map;
    }

    @SuppressWarnings("unchecked")
    public static Tag deserialize(Map<String, Object> args) {
        return builder()
                .name((String) args.get("name"))
                .display((String) args.get("display"))
                .coinsPrice((double) args.get("coinsPrice"))
                .cashPrice((double) args.get("cashPrice"))
                .inventoryItem(ItemStack.deserialize((Map<String, Object>) args.get("inventoryItem")))
                .build();
    }
}
