package com.shojabon.mcutils.Utils.SCommandRouter;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public abstract class SCommandRouter implements @Nullable CommandExecutor, @Nullable TabCompleter {
    ArrayList<SCommandObject> commands = new ArrayList<>();

    Consumer<SCommandData> onNoCommandFoundEvent = null;

    Consumer<SCommandData> noPermissionEvent = null;

    public String pluginPrefix = null;

    public SCommandRouter(){
        addCommand(new SCommandObject().addArgument(new SCommandArgument().addAllowedString("help"))
                .setExecutor(this::help));
    }

    public void addCommand(SCommandObject args){
        if(commands.contains(args)) return;
        commands.add(args);
    }

    public void setOnNoCommandFoundEvent(Consumer<SCommandData> event){
        onNoCommandFoundEvent = event;
    }

    public void setNoPermissionEvent(Consumer<SCommandData> event){
        noPermissionEvent = event;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        SCommandData commandData = new SCommandData(sender, command, label, args);
        for(SCommandObject commandObject: commands){
            if(commandObject.matches(args)) {

                //permission
                for(String permission: commandObject.requiredPermissions){
                    if(!sender.hasPermission(permission)){
                        if(noPermissionEvent != null) noPermissionEvent.accept(commandData);
                        return false;
                    }
                }


                commandObject.execute(commandData);
                return true;
            }
        }
        if(onNoCommandFoundEvent != null) onNoCommandFoundEvent.accept(commandData);
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> result = new ArrayList<>();
        for(SCommandObject commandObject: commands){
            boolean hasPermission = true;
            for(String permission: commandObject.requiredPermissions){
                if(!sender.hasPermission(permission)){
                    hasPermission = false;
                }
            }
            if(!hasPermission) continue;
            if(commandObject.validOption(args)) {
                SCommandArgument argument = commandObject.arguments.get(args.length-1);

                //player name type
                if(argument.hasType(SCommandArgumentType.ONLINE_PLAYER)){
                    for(Player p : Bukkit.getServer().getOnlinePlayers()){
                        if(p == null) continue;
                        if(!p.isOnline()) continue;
                        result.add(p.getName());
                    }
                }

                //world name type
                if(argument.hasType(SCommandArgumentType.WORLD)){
                    for(World w : Bukkit.getServer().getWorlds()){
                        result.add(w.getName());
                    }
                }
                result.addAll(argument.alias);
            }
        }

        return result;
    }

    //help

    public void help(SCommandData data){
        data.sender.sendMessage("§e==========" + pluginPrefix + "§e===========");
        for(SCommandObject obj: commands){
            if(obj.hasPermission((Player) data.sender)) data.sender.sendMessage(obj.helpText(data.label, "§d"));
        }
        data.sender.sendMessage("§e===================================");
        data.sender.sendMessage("§lコマンドクリックで情報表示");
    }
}
