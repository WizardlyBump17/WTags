package com.wizardlybump17.wtags.command;

import com.wizardlybump17.wlib.command.WCommand;
import com.wizardlybump17.wtags.WTags;
import com.wizardlybump17.wtags.api.manager.TagManager;
import com.wizardlybump17.wtags.api.manager.TaggablePlayerManager;
import com.wizardlybump17.wtags.api.player.TaggablePlayer;
import com.wizardlybump17.wtags.api.tag.Tag;
import com.wizardlybump17.wtags.implementation.player.DefaultTaggablePlayer;
import com.wizardlybump17.wtags.invetory.TagsInventory;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class TagCommand extends WCommand {

    public TagCommand(JavaPlugin plugin) {
        super(plugin, "tag");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        WTags wTags = (WTags) plugin;
        TagManager tagManager = wTags.getTagManager();
        TaggablePlayerManager taggablePlayerManager = wTags.getTaggablePlayerManager();

        if (args.length == 0) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(new String[]{
                        "§eUsage:",
                        "§7/tag set <player> <tag>",
                        "§7/tag add <player> <tag>",
                        "§7/tag remove <player> [tag]",
                        "§7/tag reload"
                });
                return true;
            }

            Player player = (Player) sender;
            if (player.hasPermission("wtags.admin")) {
                player.sendMessage(new String[]{
                        "§eAvailable commands:",
                        "§7/tag set <player> <tag>",
                        "§7/tag add <player> <tag>",
                        "§7/tag remove <player> [tag]",
                        "§7/tag reload"
                });
            }

            if (!taggablePlayerManager.isPlayerRegistered(player.getName()))
                taggablePlayerManager.registerPlayer(new DefaultTaggablePlayer(player.getName()));

            TagsInventory.generate(taggablePlayerManager.getPlayer(player.getName()), tagManager)
                    .openPage(player, 0);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "set": {
                if (!sender.hasPermission("wtags.admin.set")) {
                    sender.sendMessage("§cYou need the wtags.admin.set permission to use this!");
                    return true;
                }

                if (args.length < 3) {
                    sender.sendMessage("§eUsage: §7/tag set <player> <tag>");
                    return true;
                }

                TaggablePlayer target = taggablePlayerManager.getPlayer(args[1]);
                Tag tag = tagManager.getTag(StringUtils.join(args, ' ', 2, args.length));

                if (target == null || tag == null) {
                    sender.sendMessage("§cInvalid player or tag.");
                    return true;
                }

                target.setCurrentTag(tag);

                sender.sendMessage("§aYou've set current tag of " + target.getName() + " to " +
                        tag.getName() + " (" + tag.getDisplay() + "§a)");
                return true;
            }

            case "add": {
                if (!sender.hasPermission("wtags.admin.add")) {
                    sender.sendMessage("§cYou need the wtags.admin.add permission to use this!");
                    return true;
                }

                if (args.length < 3) {
                    sender.sendMessage("§eUsage: §7/tag add <player> <tag>");
                    return true;
                }

                TaggablePlayer target = taggablePlayerManager.getPlayer(args[1]);
                Tag tag = tagManager.getTag(StringUtils.join(args, ' ', 2, args.length));

                if (target == null || tag == null) {
                    sender.sendMessage("§cInvalid player or tag.");
                    return true;
                }

                target.addTag(tag);

                sender.sendMessage("§aYou added the " + tag.getName() +
                        " (" + tag.getDisplay() + "§a) tag for " + target.getName());
                return true;
            }

            case "remove": {
                if (!sender.hasPermission("wtags.admin.remove")) {
                    sender.sendMessage("§cYou need the wtags.admin.remove permission to use this!");
                    return true;
                }

                if (args.length < 2) {
                    sender.sendMessage("§eUsage: §7/tag remove <player> [tag]");
                    return true;
                }

                TaggablePlayer target = taggablePlayerManager.getPlayer(args[1]);

                if (args.length >= 3) {
                    Tag tag = tagManager.getTag(StringUtils.join(args, ' ', 2, args.length));

                    if (target == null || tag == null) {
                        sender.sendMessage("§cInvalid player or tag.");
                        return true;
                    }

                    target.removeTag(tag.getName());

                    sender.sendMessage("§aYou removed the " + tag.getName() +
                            " (" + tag.getDisplay() + "§a) tag for " + target.getName());
                    return true;
                }

                if (target == null) {
                    sender.sendMessage("§cInvalid player.");
                    return true;
                }

                target.setCurrentTag(null);

                sender.sendMessage("§aYou removed the current tag of " + target.getName());
                return true;
            }

            case "reload": {
                if (!sender.hasPermission("wtags.admin.reload")) {
                    sender.sendMessage("§cYou need the wtags.admin.remove permission to use this!");
                    return true;
                }

                wTags.reloadTags();
                return true;
            }

            default: {
                if(sender.hasPermission("wtags.admin")) {
                    sender.sendMessage(new String[]{
                            "§eAvailable commands:",
                            "§7/tag set <player> <tag>",
                            "§7/tag add <player> <tag>",
                            "§7/tag remove <player> [tag]",
                            "§7/tag reload"
                    });
                }

                if (!(sender instanceof Player)) return true;

                Player player = (Player) sender;
                TagsInventory.generate(taggablePlayerManager.getPlayer(player.getName()), tagManager)
                        .openPage(player, 0);
                return true;
            }
        }
    }
}
