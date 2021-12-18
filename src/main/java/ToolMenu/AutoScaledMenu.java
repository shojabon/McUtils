package ToolMenu;

import com.shojabon.mcutils.Utils.SInventory.SInventory;
import com.shojabon.mcutils.Utils.SInventory.SInventoryItem;
import com.shojabon.mcutils.Utils.SItemStack;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;

public class AutoScaledMenu extends SInventory {

    ArrayList<SInventoryItem> items = new ArrayList<>();

    public int[][] itemPlacement = {
            {22},
            {21, 23},
            {13, 30, 32},
            {12, 14, 30, 32},
            {13, 21, 22, 23, 31},
            {11, 13, 15, 29, 31, 33},
            {11, 13, 15, 29, 31, 33, 22},
            {11, 13, 15, 29, 31, 33, 21, 23},
            {12, 13, 14, 20,22,24,30,31,32},
            {11,13,15,19,21,23,25,29,31,33},
            {10,12,14,16,20,22,24,28,30,32,34},
            {10,12,14,16,19,21,23,25,28,30,32,34},
            {10,12,14,16,19,21,23,25,28,30,32,34,22},
            {10,12,14,16,19,21,23,25,28,30,32,34,20,24},
            {10,12,14,16,19,21,23,25,28,30,32,34,20,22,24},
            {10,12,14,16,19,21,23,25,28,30,32,34,20,24,13,31},

    };

    public AutoScaledMenu(String title, JavaPlugin plugin) {
        super(title, 5, plugin);
        fillItem(new SInventoryItem(new SItemStack(Material.BLUE_STAINED_GLASS_PANE).setDisplayName(" ").build()).clickable(false));
    }

    public AutoScaledMenu(String title, ArrayList<SInventoryItem> items, JavaPlugin plugin) {
        super(title, 5, plugin);
        this.items = items;
    }

    public void addItem(SInventoryItem items){
        this.items.add(items);
    }

    @Override
    public void renderMenu() {
        if(items.size() <= itemPlacement.length){
            for(int i = 0; i < items.size(); i++){
                setItem(itemPlacement[items.size()-1][i], items.get(i));
            }
        }else{
            for(int i = 0; i < items.size(); i++){
                setItem(i, items.get(i));
            }
        }
    }
}
