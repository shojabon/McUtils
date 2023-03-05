package com.shojabon.mcutils.Utils.SCommandRouterV2;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class SCommandData {

    public CommandSender sender;
    public Command command;
    public String label;
    public String[] args;

    public SCommandData(CommandSender sender, Command command, String label, String[] args) {
        this.sender = sender;
        this.command = command;
        this.label = label;
        this.args = args;
    }
}
