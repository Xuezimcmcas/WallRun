package com.neteasemc.Listener;

import com.neteasemc.WallRunPlugin;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.bukkit.block.BlockFace;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

public class WallRunListener implements Listener {

    private WallRunPlugin plugin;
    private Set<Material> validWallMaterials;

    public WallRunListener(WallRunPlugin plugin) {
        this.plugin = plugin;
        validWallMaterials = new HashSet<>(Arrays.asList(Material.STONE, Material.WOOD, Material.COBBLESTONE));
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (isNearLargeWall(player) && !player.hasMetadata("wallrunning")) {
            performWallRun(player);
        } else if (player.hasMetadata("wallrunning")) {
            handleWallJump(player, event);
        }
    }

    private boolean isNearLargeWall(Player player) {
        List<BlockFace> facesToCheck = Arrays.asList(BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST);
        for (BlockFace face : facesToCheck) {
            if (isLargeWall(player.getLocation().getBlock().getRelative(face))) {
                return true;
            }
        }
        return false;
    }

    private boolean isLargeWall(Block startBlock) {
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                if (!validWallMaterials.contains(startBlock.getRelative(x, y, 0).getType())) {
                    return false;
                }
            }
        }
        return true;
    }

    private void performWallRun(Player player) {
        player.setMetadata("wallrunning", new FixedMetadataValue(plugin, true));

        new BukkitRunnable() {
            @Override
            public void run() {
                if (isNearLargeWall(player)) {
                    Vector direction = player.getLocation().getDirection();
                    double speedMultiplier = 0.4; // 减速系数
                    direction.setX(direction.getX() * speedMultiplier);
                    direction.setZ(direction.getZ() * speedMultiplier);
                    direction.setY(0); // 轻微垂直提升

                    player.setVelocity(direction);
                } else {
                    resetPlayerState(player);
                    this.cancel();
                }
            }
        }.runTaskTimer(plugin, 0L, 1L);

        new BukkitRunnable() {
            @Override
            public void run() {
                resetPlayerState(player);
            }
        }.runTaskLater(plugin, 100L);
    }

    private void resetPlayerState(Player player) {
        if (player.hasMetadata("wallrunning")) {
            player.removeMetadata("wallrunning", plugin);
            player.setVelocity(new Vector(0, -0.1, 0));
        }
    }

    private void handleWallJump(Player player, PlayerMoveEvent event) {
        if (player.isSneaking()) {
            Vector jumpVelocity = new Vector(0, 1, 0); // 跳跃向量
            player.setVelocity(jumpVelocity);
            resetPlayerState(player);
        }
    }
}
