package org.alifmis.dragonBlockPlugin.listener;

import org.bukkit.Material;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Explosive;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.alifmis.dragonBlockPlugin.DragonBlockPlugin;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DragonBlockListener implements Listener {

    private final DragonBlockPlugin plugin;
    private volatile Set<String> whitelistedBlocks;  // guarded by reloadConfig()

    public DragonBlockListener(DragonBlockPlugin plugin) {
        this.plugin = plugin;
        this.whitelistedBlocks = loadBlockNames();
    }

    public void reloadConfig() {
        plugin.reloadConfig();
        this.whitelistedBlocks = loadBlockNames();
    }

    private Set<String> loadBlockNames() {
        List<String> raw = plugin.getConfig().getStringList("whitelisted-blocks");
        Set<String> set = new HashSet<>(raw.size());
        for (String s : raw) {
            if (s != null && !s.isBlank()) {
                set.add(s.trim().toUpperCase());
            }
        }
        return Set.copyOf(set); // immutable snapshot
    }

    private boolean isWhitelisted(Material material) {
        return whitelistedBlocks.contains(material.name());
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntityChangeBlock(EntityChangeBlockEvent event) {
        if (!(event.getEntity() instanceof EnderDragon)) {
            return;
        }

        Material blockType = event.getBlock().getType();

        // Allow the change only if the block is whitelisted
        if (!isWhitelisted(blockType)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntityExplode(EntityExplodeEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof EnderDragon) && !isFromDragonFireball(entity)) {
            return;
        }

        // Filter the block list — keep only whitelisted blocks
        event.blockList().removeIf(state -> !isWhitelisted(state.getType()));
    }

    private boolean isFromDragonFireball(Entity entity) {
        if (!(entity instanceof Explosive explosive)) {
            return false;
        }
        // Many explosive projectiles have a shooter (fireballs, etc.)
        if (explosive instanceof org.bukkit.entity.Projectile projectile) {
            return projectile.getShooter() instanceof EnderDragon;
        }
        return false;
    }
}
