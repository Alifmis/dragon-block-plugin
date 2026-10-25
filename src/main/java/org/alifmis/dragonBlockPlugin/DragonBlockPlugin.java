package org.alifmis.dragonBlockPlugin;

import com.mojang.brigadier.Command;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.plugin.java.JavaPlugin;
import org.alifmis.dragonBlockPlugin.listener.DragonBlockListener;

// Prevents the Ender Dragon from destroying blocks unless whitelisted in config.yml; works on Folia and Paper.
public final class DragonBlockPlugin extends JavaPlugin {

    private DragonBlockListener listener;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        this.listener = new DragonBlockListener(this);
        getServer().getPluginManager().registerEvents(this.listener, this);

        // Commands (Brigadier, Folia-safe)
        registerCommands();

        getLogger().info("DragonBlockPlugin enabled — dragon block destruction is restricted.");
    }

    @Override
    public void onDisable() {
        getLogger().info("DragonBlockPlugin disabled.");
    }

    private void registerCommands() {
        var manager = this.getLifecycleManager();
        manager.registerEventHandler(
                io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents.COMMANDS,
                commands -> {
                    var registrar = commands.registrar();
                    registrar.register(
                            io.papermc.paper.command.brigadier.Commands.literal("dragonblock")
                                    .requires(ctx -> ctx.getSender().hasPermission("dragonblock.admin"))
                                    .then(io.papermc.paper.command.brigadier.Commands.literal("reload")
                                            .executes(ctx -> {
                                                reloadPluginConfig();
                                                ctx.getSource().getSender().sendMessage(
                                                        Component.text("DragonBlock config reloaded.")
                                                                .color(NamedTextColor.GREEN)
                                                );
                                                return Command.SINGLE_SUCCESS;
                                            })
                                    )
                                    .executes(ctx -> {
                                        ctx.getSource().getSender().sendMessage(
                                                Component.text("Usage: /dragonblock reload")
                                                        .color(NamedTextColor.YELLOW)
                                        );
                                        return Command.SINGLE_SUCCESS;
                                    })
                                    .build()
                    );
                }
        );
    }

    // Reloads the config and pushes the new whitelist to the listener.
    public void reloadPluginConfig() {
        if (this.listener != null) {
            this.listener.reloadConfig();
        }
    }
}
