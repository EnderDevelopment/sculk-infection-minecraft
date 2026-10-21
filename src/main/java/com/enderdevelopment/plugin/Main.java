package com.enderdevelopment.plugin;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.PortalCreateEvent;
import org.bukkit.block.Block;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.HashSet;
import java.util.Set;

public class Main extends JavaPlugin implements Listener {

    private FileConfiguration config;
    private Set<Material> immuneBlocks;
    private Set<Material> infectedBlocks;
    private Set<Material> overworldBlocks;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        config = getConfig();
        immuneBlocks = new HashSet<>();
        infectedBlocks = new HashSet<>();
        overworldBlocks = new HashSet<>();

        // Load immune blocks from config
        for (String blockName : config.getStringList("immune-blocks")) {
            Material material = Material.matchMaterial(blockName);
            if (material != null) {
                immuneBlocks.add(material);
            }
        }

        // Load infected blocks from config
        for (String blockName : config.getStringList("infected-blocks")) {
            Material material = Material.matchMaterial(blockName);
            if (material != null) {
                infectedBlocks.add(material);
            }
        }

        // Load overworld blocks from config
        for (String blockName : config.getStringList("overworld-blocks")) {
            Material material = Material.matchMaterial(blockName);
            if (material != null) {
                overworldBlocks.add(material);
            }
        }

        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be run by a player.");
            return false;
        }

        Player player = (Player) sender;

        if (command.getName().equalsIgnoreCase("infect")) {
            infectBlock(player);
            return true;
        } else if (command.getName().equalsIgnoreCase("revert")) {
            revertBlock(player);
            return true;
        } else if (command.getName().equalsIgnoreCase("overworldificator")) {
            overworldificator(player);
            return true;
        }

        return false;
    }

    private void infectBlock(Player player) {
        Block targetBlock = player.getTargetBlock(null, 5);
        if (targetBlock.getType() == Material.NETHERRACK) {
            if (!immuneBlocks.contains(targetBlock.getType())) {
                targetBlock.setType(Material.SCULK);
                player.sendMessage("Block infected with Sculk-like properties.");
            } else {
                player.sendMessage("This block is immune to infection.");
            }
        } else {
            player.sendMessage("You can only infect Netherrack blocks.");
        }
    }

    private void revertBlock(Player player) {
        Block targetBlock = player.getTargetBlock(null, 5);
        if (targetBlock.getType() == Material.SCULK) {
            targetBlock.setType(Material.NETHERRACK);
            player.sendMessage("Block reverted to Netherrack.");
        } else {
            player.sendMessage("You can only revert Sculk blocks.");
        }
    }

    private void overworldificator(Player player) {
        World world = player.getWorld();
        for (Block block : world.getBlocks()) {
            if (infectedBlocks.contains(block.getType())) {
                block.setType(overworldBlocks.iterator().next());
            }
        }
        player.sendMessage("All infected blocks have been converted to overworld variants.");
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Block placedBlock = event.getBlockPlaced();
        if (placedBlock.getType() == Material.NETHERRACK) {
            if (!immuneBlocks.contains(placedBlock.getType())) {
                placedBlock.setType(Material.SCULK);
                event.getPlayer().sendMessage("Block infected with Sculk-like properties.");
            } else {
                event.getPlayer().sendMessage("This block is immune to infection.");
            }
        }
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        // Logic to spread infection in the chunk
    }

    @EventHandler
    public void onPortalCreate(PortalCreateEvent event) {
        // Logic to infect blocks around the portal
    }
}