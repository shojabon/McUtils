package ToolMenu;

import com.shojabon.mcutils.Utils.SInventory.SInventory;
import com.shojabon.mcutils.Utils.SInventory.SInventoryItem;
import com.shojabon.mcutils.Utils.SItemStack;
import com.shojabon.mcutils.Utils.SStringBuilder;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.function.Consumer;

public class BooleanInputMenu extends SInventory {

    Consumer<Boolean> onConfirm;
    Consumer<InventoryClickEvent> onCancel;
    Consumer<InventoryCloseEvent> onClose;

    ItemStack information;

    boolean current;

    public BooleanInputMenu(boolean current, String title, JavaPlugin plugin) {
        super(title, 6, plugin);
        this.current = current;
    }


    public void setInformation(ItemStack item){
        information = item;
    }

    public void setOnConfirm(Consumer<Boolean> event){
        this.onConfirm = event;
    }

    public void setOnCancel(Consumer<InventoryClickEvent> event){
        this.onCancel = event;
    }

    public void setOnClose(Consumer<InventoryCloseEvent> event){
        this.onClose = event;
    }


    public void renderButtons(){

        SInventoryItem t = new SInventoryItem(new SItemStack(Material.LIME_STAINED_GLASS_PANE).setDisplayName(new SStringBuilder().green().bold().text("ture").build()).setGlowingEffect(current).build());
        t.clickable(false);
        t.setEvent(e -> {
            current = true;
            renderButtons();
        });
        setItem(new int[]{10,11,19,20}, t);

        SInventoryItem f = new SInventoryItem(new SItemStack(Material.RED_STAINED_GLASS_PANE).setDisplayName(new SStringBuilder().darkRed().bold().text("false").build()).setGlowingEffect(!current).build());
        f.clickable(false);
        f.setEvent(e -> {
            current = false;
            renderButtons();
        });
        setItem(new int[]{15,16,24,25}, f);
        renderInventory();
    }

    public void renderMenu(){
        SInventoryItem background = new SInventoryItem(new SItemStack(Material.BLUE_STAINED_GLASS_PANE).setDisplayName(" ").build());
        background.clickable(false);
        fillItem(background);

        renderButtons();

        if(information != null){
            SInventoryItem invItem = new SInventoryItem(information);
            invItem.clickable(false);
            setItem(13, invItem);
        }

        SInventoryItem confirm = new SInventoryItem(new SItemStack(Material.LIME_STAINED_GLASS_PANE).setDisplayName(new SStringBuilder().green().bold().text("確認").build()).build());
        confirm.clickable(false);
        confirm.setAsyncEvent(e-> onConfirm.accept(current));
        setItem(40, confirm);

        setAsyncOnCloseEvent(e -> {
            if(onClose!=null)onClose.accept(e);
        });


    }
}
