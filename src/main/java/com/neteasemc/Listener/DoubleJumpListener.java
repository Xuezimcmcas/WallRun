package com.neteasemc.Listener;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.util.Vector;
import org.bukkit.metadata.FixedMetadataValue;

public class DoubleJumpListener implements Listener {

    private JavaPlugin plugin;

    public DoubleJumpListener(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (player.isOnGround() && !player.getAllowFlight()) {
            player.setAllowFlight(true);
            player.removeMetadata("doublejumped", plugin);
        }
    }

    @EventHandler
    public void onPlayerToggleFlight(PlayerToggleFlightEvent event) {
        Player player = event.getPlayer();
        if (player.getAllowFlight() && !player.hasMetadata("wallrunning")) {
            // 检查是否处于飞行模式
            if (player.hasMetadata("flyingMode")) {
                return; // 如果处于飞行模式，不执行二段跳
            }

            event.setCancelled(true);
            player.setFlying(false);
            player.setAllowFlight(false);
            if (!player.hasMetadata("doublejumped")) {
                player.setMetadata("doublejumped", new FixedMetadataValue(plugin, true));
                Vector jump = player.getVelocity();
                jump.setY(0.5); // 设置二段跳的高度
                player.setVelocity(jump);
            }
        }
    }

}
