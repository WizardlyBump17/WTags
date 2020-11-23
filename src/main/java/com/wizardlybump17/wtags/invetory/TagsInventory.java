package com.wizardlybump17.wtags.invetory;

import com.google.gson.Gson;
import com.wizardlybump17.wlib.inventory.item.ItemButton;
import com.wizardlybump17.wlib.inventory.item.ItemClickAction;
import com.wizardlybump17.wlib.inventory.paginated.PaginatedInventory;
import com.wizardlybump17.wlib.inventory.paginated.PaginatedInventoryBuilder;
import com.wizardlybump17.wlib.item.ItemBuilder;
import com.wizardlybump17.wtags.api.manager.TagManager;
import com.wizardlybump17.wtags.api.player.TaggablePlayer;
import com.wizardlybump17.wtags.api.tag.Tag;
import org.bukkit.DyeColor;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class TagsInventory {

    public static PaginatedInventory generate(TaggablePlayer player, TagManager tagManager) {
        PaginatedInventoryBuilder builder = new PaginatedInventoryBuilder(
                "Available tags",
                tagManager.getRegisteredTags().isEmpty()
                        ? "#########" +
                        "#0000000#" +
                        "#0001000#" +
                        "#0000000#" +
                        "#########"
                        : "####@####" +
                        "#0000000#" +
                        "#0xxxxx0#" +
                        "#0xxxxx0#" +
                        "#0000000#" +
                        "<#######>");

        List<ItemButton> content = new ArrayList<>();
        for (Tag tag : tagManager.getRegisteredTags().values()) {
            boolean modify = false;
            boolean glow = false;

            ItemClickAction action = event -> {
                System.out.println(tag);
                System.out.println(new Gson().toJson(tag.serialize(), Map.class));
                if (player.hasTag(tag.getName())) {
                    if (player.isUsingTag() && player.getCurrentTag().equals(tag)) return;

                    player.setCurrentTag(tag);

                    event.getWhoClicked().closeInventory();

                    event.getWhoClicked().sendMessage("§aYou've set your current tag to " +
                            tag.getName() + " (" + tag.getDisplay() + "§a)");
                    return;
                }


                if (event.isLeftClick()) {
                    //money
                }

                if (event.isRightClick()) {
                    //cash
                }

                player.addTag(tag);
                player.setCurrentTag(tag);

                event.getWhoClicked().closeInventory();

                event.getWhoClicked().sendMessage("§aYou've purchased and set your current tag to " +
                        tag.getName() + " (" + tag.getDisplay() + "§a)");
            };

            List<String> lore = new ArrayList<>(Arrays.asList(
                    "",
                    "§7Display: " + tag.getDisplay()));

            if (player.hasTag(tag.getName())) {
                lore.add("§aYou already have this tag!");
                modify = true;
            }
            if (player.isUsingTag() && player.getCurrentTag().equals(tag)) {
                lore.add("§aYou're using this tag!");
                modify = true;
                glow = true;
            }

            if (modify) {
                ItemBuilder itemBuilder =
                        ItemBuilder.fromItemStack(tag.getInventoryItem())
                                .lore(lore);
                if (glow) itemBuilder.glow(true);
                content.add(new ItemButton(itemBuilder.build(), action));
                continue;
            }

            content.add(new ItemButton(tag.getInventoryItem(), action));
        }

        builder
                .addShapeReplacement('0', null)
                .addShapeReplacement('1', new ItemButton(
                        new ItemBuilder(Material.WEB)
                                .displayName("§cWithout tags")
                                .lore(
                                        "§cIt seems that this server",
                                        "§cnot have tags yet.").build()))
                .addShapeReplacement('@', new ItemButton(
                        player.isUsingTag()
                                ? ItemBuilder.fromItemStack(player.getCurrentTag().getInventoryItem())
                                .lore(
                                        "",
                                        "§7Display: " + player.getCurrentTag().getDisplay(),
                                        "§aYou're using this tag!")
                                .glow(true)
                                .build()
                                : new ItemBuilder(Material.WEB)
                                .displayName("§cWithout tag!")
                                .lore("§cYou arent using a tag!")
                                .build(),
                        event -> {
                            if (!player.isUsingTag()) return;

                            player.setCurrentTag(null);

                            event.getWhoClicked().closeInventory();

                            event.getWhoClicked().sendMessage("§aYou removed your current tag!");
                        }))
                .previousPageItemStack(
                        new ItemBuilder(Material.ARROW)
                                .displayName("§aPrevious page")
                                .build())
                .nextPageItemStack(new ItemBuilder(Material.ARROW)
                        .displayName("§aPrevious page")
                        .build())
                .border(new ItemButton(
                        new ItemBuilder(Material.STAINED_GLASS_PANE, 1, DyeColor.BLACK.getData())
                                .displayName(" ")
                                .build()))
                .items(content);
        return builder.build();
    }
}
