package com.shojabon.mcutils.Utils.SCommandRouterV2;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public abstract class SCommandRouter implements @Nullable CommandExecutor, @Nullable TabCompleter {
    public static ConcurrentHashMap<String, ArrayList<SCommandObject>> commands = new ConcurrentHashMap<>();

    Consumer<SCommandData> onNoCommandFoundEvent = null;
    Consumer<SCommandData> noPermissionEvent = null;

    public String pluginPrefix = null;

    private final String commandName;

    public SCommandRouter(JavaPlugin plugin, @NotNull String commandName){
        this.commandName = commandName;
        plugin.getCommand(commandName).setExecutor(this);
        plugin.getCommand(commandName).setTabCompleter(this);
        addCommand(new SCommandObject().argument(new SCommandArgument().allowedString("help"))
                .inlineExecutor(this::help));
    }

    public static void executeSCommand(CommandSender executor, String command){
        String[] splitCommands = command.split(" ");
        if(splitCommands.length == 0) return;
        String label = splitCommands[0];
        String[] args = Arrays.copyOfRange(splitCommands, 1, splitCommands.length);
        SCommandData commandData = new SCommandData(executor, Bukkit.getPluginCommand(label), label, args);
        for(SCommandObject commandObject: commands.get(label)){
            if(commandObject.matches(args, executor)) {

                //permission
                if(commandObject.permission != null){
                    boolean hasPermission = executor.hasPermission(commandObject.permission);
                    if(!hasPermission) continue;
                }

                commandObject.execute(commandData);
                return;
            }
        }
    }

    public void addCommand(SCommandObject command){
        if(!commands.containsKey(commandName)){
            commands.put(commandName, new ArrayList<>());
        }
        if(commands.get(commandName).contains(command)) return;
        commands.get(commandName).add(command);
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
        for(SCommandObject commandObject: commands.get(commandName)){
            if(commandObject.matches(args, sender)) {

                //permission
                if(commandObject.permission != null){
                    boolean hasPermission = sender.hasPermission(commandObject.permission);
                    if(!hasPermission){
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
        for(SCommandObject commandObject: commands.get(commandName)){
            if(commandObject.permission != null){
                boolean hasPermission = sender.hasPermission(commandObject.permission);
                if(!hasPermission) continue;
            }
            if(commandObject.validOption(args, sender)) {
                SCommandArgument argument = commandObject.arguments.get(args.length-1);
                if(argument.alias != null) result.add(argument.alias);
                result.addAll(argument.getAllowedStrings(sender));
            }
        }

        return result;
    }

    //help

    public void help(SCommandData data){
        data.sender.sendMessage("§e==========" + pluginPrefix + "§e===========");
        for(SCommandObject obj: commands.get(commandName)){
            if(obj.hasPermission(data.sender)) data.sender.sendMessage(obj.helpText(data.label, "§d", data.sender));
        }
        data.sender.sendMessage("§e===================================");
    }
}
