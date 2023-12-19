package com.neteasemc;

import com.neteasemc.Listener.DoubleJumpListener;
import com.neteasemc.Listener.WallRunListener;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;

public class WallRunPlugin extends JavaPlugin implements CommandExecutor {
    @Override
    public void onEnable() {
        // 注册事件监听器
        getServer().getPluginManager().registerEvents(new WallRunListener(this), this);
        getServer().getPluginManager().registerEvents(new DoubleJumpListener(this), this);
        this.getCommand("flymode").setExecutor(this);
        getLogger().info("WallRunPlugin has been enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("WallRunPlugin has been disabled.");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("flymode")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("这个指令必须由玩家执行.");
                return true;
            }
            Player player = (Player) sender;
            if (args.length > 0) {
                if (args[0].equalsIgnoreCase("on")) {
                    player.setAllowFlight(true);
                    player.setFlying(true);
                    player.setMetadata("flyingMode", new FixedMetadataValue(this, true)); // 添加飞行模式元数据
                    player.removeMetadata("doublejumped", this);
                    player.sendMessage(ChatColor.GREEN + "飞行模式已开启");
                } else if (args[0].equalsIgnoreCase("off")) {
                    player.setAllowFlight(false);
                    player.setFlying(false);
                    player.removeMetadata("flyingMode", this); // 移除飞行模式元数据
                    player.setMetadata("doublejumped", new FixedMetadataValue(this, true));
                    player.sendMessage(ChatColor.GREEN + "飞行模式已关闭");
                }
                return true;
            }
        }
        return false;
    }

}
