package ToolMenu;

import com.shojabon.mcutils.Utils.SInventory.SInventory;
import com.shojabon.mcutils.Utils.SInventory.SInventoryItem;
import com.shojabon.mcutils.Utils.SItemStack;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.function.Consumer;

public class SingleItemStackSelectorMenu extends SInventory {

    ItemStack currentItem;

    Consumer<ItemStack> onConfirm = null;

    boolean selectTypeItem = false;
    boolean materialSelector = false;
    boolean allowNullItem = false;

    public void selectTypeItem(boolean r){
        this.selectTypeItem = r;
    }

    public void selectMaterial(boolean r){
        this.materialSelector = r;
    }
    public void allowNullItem(boolean r){
        this.allowNullItem = r;
        SItemStack blankItemSButton = new SItemStack(Material.RED_STAINED_GLASS_PANE).setDisplayName("§a§l設定を解除する");
        SInventoryItem blankItem = new SInventoryItem(blankItemSButton.build()).clickable(false);
        blankItem.setEvent(e -> {
            if(onConfirm == null) return;
            onConfirm.accept(null);
        });

        if(allowNullItem) setItem(35, blankItem);
        renderInventory();
    }

    public void setOnConfirm(Consumer<ItemStack> consumer){
        this.onConfirm = consumer;
    }


    public SingleItemStackSelectorMenu(String title, ItemStack defaultItem, JavaPlugin plugin) {
        super(title, 4, plugin);
        fillItem(new SInventoryItem(new SItemStack(Material.BLUE_STAINED_GLASS_PANE).setDisplayName(" ").build()).clickable(false));
        if (defaultItem == null) {
            currentItem = new ItemStack(Material.DIAMOND);
        }else{
            currentItem = defaultItem;
        }
        setItem(13, new SInventoryItem(currentItem).clickable(false));

        setOnClickEvent(e -> {
            if(e.getClickedInventory() == null)return;
            if(e.getClickedInventory().getType() != InventoryType.PLAYER) return;
            if(e.getCurrentItem() == null)return;
            e.setCancelled(true);

            //type item selector
            if(selectTypeItem){
                currentItem = new SItemStack(e.getCurrentItem()).getTypeItem();
            }else{
                currentItem = e.getCurrentItem();
            }

            //material selector
            if(materialSelector){
                currentItem = new SItemStack(currentItem.getType()).build();
            }

            setItem(13, new SInventoryItem(currentItem).clickable(false));
            renderInventory();
        });

        SItemStack confirmButton = new SItemStack(Material.LIME_STAINED_GLASS_PANE).setDisplayName("§a§l決定");
        SInventoryItem confirm = new SInventoryItem(confirmButton.build()).clickable(false);
        confirm.setEvent(e -> {
            if(onConfirm == null) return;
            onConfirm.accept(currentItem);
        });

        setItem(new int[]{30, 31, 32}, confirm);

    }


}
