package com.shojabon.mcutils.Utils;

import it.unimi.dsi.fastutil.Hash;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class SCommandScript {

    HashMap<String, ArrayList<String>> scripts = new HashMap<>();
    HashMap<String, String> placeholderData = new HashMap<>();

    HashMap<String, ArrayList<String>> placeholderArray = new HashMap<>();

    Plugin plugin;


    public void addScript(String scriptName, ArrayList<String> scriptCommands){
        scripts.put(scriptName, scriptCommands);
    }

    public void addPlaceholder(String placeholderName, String placeholderValue){
        placeholderData.put(placeholderName, placeholderValue);
    }

    public void addPlaceholderArray(String arrayName, ArrayList<String> arrayValues){
        placeholderArray.put(arrayName, arrayValues);
    }

    public SCommandScript(Plugin plugin){
        this.plugin = plugin;
    }

    public SCommandScript(Plugin plugin, FileConfiguration file){
        this.plugin = plugin;
        ConfigurationSection selection = file.getConfigurationSection("scripts");
        if(selection == null) return;
        for(String key : selection.getKeys(false)){
            addScript(key, (ArrayList<String>) selection.getStringList(key));
        }
    }

    public void executeScript(ArrayList<String> commands){
        for (String command : commands) {
            String[] parts = command.split("\\s+", 2);
            String commandName = parts[0];
            String commandArgs = parts.length > 1 ? parts[1] : "";
            String[] args = parts[1].split(" ");
            switch (commandName) {
                case "SLEEP":
                    long sleepTime = Long.parseLong(commandArgs);
                    try {
                        Thread.sleep(sleepTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    break;
                case "COMMAND":
                    executeCommand(commandArgs);
                    break;
                case "EXEC":
                    if(scripts.containsKey(args[0])){
                        executeScript(scripts.get(args[0]));
                    }
                    break;
                case "AEXEC":
                    new Thread(() -> {
                        if(scripts.containsKey(args[0])){
                            executeScript(scripts.get(args[0]));
                        }
                    }).start();
                    break;
                case "FOR":
                    String listName = args[0];
                    String cmd = commandArgs.split("\\s+", 2)[1];
                    if(!placeholderArray.containsKey(listName)) continue;
                    for(String arrayValue : placeholderArray.get(listName)){
                        String localCmd = cmd.replaceAll("\\{it}", arrayValue);
                        executeCommand(localCmd);
                    }
                    break;
                default:
                    executeCommand(commandArgs);
                    break;
            }
        }
    }

    private void executeCommand(String command){
        while (true){
            boolean touchedData = false;
            for(String placeholderKey : placeholderData.keySet()){
                String newCommand = command.replaceAll("\\{" + placeholderKey + "}", placeholderData.get(placeholderKey));
                if(!newCommand.equals(command)){
                    command = newCommand;
                    touchedData = true;
                }
            }
            if(!touchedData) break;
        }

        String finalCommand = command;
        Bukkit.getServer().getScheduler().runTask(
                plugin,
                () -> Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), finalCommand)
        );
    }
}
