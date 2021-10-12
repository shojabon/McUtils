package ToolMenu;

import com.shojabon.mcutils.Utils.SInventory.SInventory;
import com.shojabon.mcutils.Utils.SInventory.SInventoryItem;
import com.shojabon.mcutils.Utils.SItemStack;
import com.shojabon.mcutils.Utils.SStringBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class CategoricalSInventoryMenu extends SInventory{

    LinkedHashMap<String, ArrayList<SInventoryItem>> items = new LinkedHashMap<>();
    String currentCategory;
    int currentPage = 0;
    int categoryIndex = 0;

    public CategoricalSInventoryMenu(String title, String currentCategory, JavaPlugin plugin) {
        super(title, 6, plugin);
        this.currentCategory = currentCategory;
    }

    public void setItems(LinkedHashMap<String, ArrayList<SInventoryItem>>  items){
        this.items = items;
    }

    public void addItem(String category, SInventoryItem item){
        if(!items.containsKey(category)) items.put(category, new ArrayList<>());
        items.get(category).add(item);
    }

    public void renderControlBar(){
        int[] slots = new int[9];
        for(int i = 0; i < 9; i++){
            slots[i] = 5*9+i;
        }

        SInventoryItem background = new SInventoryItem(new SItemStack(Material.BLUE_STAINED_GLASS_PANE).setDisplayName(" ").build());
        background.clickable(false);
        setItem(slots, background);

        if(items.size() == 0) return;

        //buttons

        SInventoryItem left = new SInventoryItem(new SItemStack(Material.RED_STAINED_GLASS_PANE).setDisplayName(new SStringBuilder().green().bold().text("前へ").build()).build());
        SInventoryItem right = new SInventoryItem(new SItemStack(Material.RED_STAINED_GLASS_PANE).setDisplayName(new SStringBuilder().red().bold().text("次へ").build()).build());

        left.clickable(false);
        right.clickable(false);

        left.setEvent(e -> {
            currentPage--;
            renderInventory(currentCategory, currentPage);
        });

        right.setEvent(e -> {
            currentPage++;
            renderInventory(currentCategory, currentPage);
        });

        if(currentPage != 0) setItem(slots[0], left);        //has left
        if((currentPage+1)*5*9 <= items.get(currentCategory).size()-1) setItem(slots[8], right);    //has right

        if(items.size() == 1) return;
        //category selector

        setItem(slots[4], new SInventoryItem(new SItemStack(Material.PAPER).setAmount(categoryIndex+1).setDisplayName("§b§l" + items.keySet().toArray()[categoryIndex].toString()).build()).clickable(false));

        SInventoryItem categoryLeft = new SInventoryItem(new SItemStack(Material.RED_STAINED_GLASS_PANE).setDisplayName(new SStringBuilder().green().bold().text("前のカテゴリ").build()).build());
        SInventoryItem categoryRight = new SInventoryItem(new SItemStack(Material.RED_STAINED_GLASS_PANE).setDisplayName(new SStringBuilder().red().bold().text("次のカテゴリ").build()).build());

        categoryLeft.clickable(false);
        categoryRight.clickable(false);

        categoryLeft.setEvent(e -> {
            categoryIndex--;
            currentCategory = items.keySet().toArray()[categoryIndex].toString();
            currentPage = 0;
            renderInventory(currentCategory, currentPage);
        });

        categoryRight.setEvent(e -> {
            categoryIndex++;
            currentCategory = items.keySet().toArray()[categoryIndex].toString();
            currentPage = 0;
            renderInventory(currentCategory, currentPage);
        });
        if(categoryIndex != 0) setItem(slots[3], categoryLeft);        //has left
        if(categoryIndex+2 <= items.keySet().toArray().length) setItem(slots[5], categoryRight);    //has right
    }

    public void renderInventory(String category, int page){
        clear();
        renderControlBar();
        if(!items.containsKey(category))return;
        ArrayList<SInventoryItem> itemsInCategory = items.get(category);
        int startingIndex = page*5*9;
        int ending = itemsInCategory.size() - startingIndex;
        if(ending> 5*9) ending = 5*9;
        for(int i = 0; i < ending; i++){
            setItem(i, itemsInCategory.get(startingIndex+i));
        }
        renderInventory();
    }

    public void afterRenderMenu() {
        if(items.size() != 0){
            if(!items.containsKey(currentCategory)){
                currentCategory = items.keySet().toArray()[0].toString();
            }
            for(int i = 0; i < items.size(); i++){
                if(items.keySet().toArray()[i].toString().equalsIgnoreCase(currentCategory)){
                    categoryIndex = i;
                    break;
                }
            }
        }
        renderInventory(currentCategory, 0);
    }

    public void setCurrentCategory(String category){
        currentCategory = category;
    }
}
