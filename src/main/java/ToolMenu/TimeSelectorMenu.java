package ToolMenu;

import com.shojabon.mcutils.Utils.BannerDictionary;
import com.shojabon.mcutils.Utils.SInventory.SInventory;
import com.shojabon.mcutils.Utils.SInventory.SInventoryItem;
import com.shojabon.mcutils.Utils.SItemStack;
import com.shojabon.mcutils.Utils.SStringBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Calendar;
import java.util.Date;
import java.util.function.Consumer;

public class TimeSelectorMenu extends SInventory {

    Consumer<Long> onConfirm;

    BannerDictionary banner = new BannerDictionary();

    ItemStack information;

    Calendar calendar = Calendar.getInstance();

    public TimeSelectorMenu(long current, String title, JavaPlugin plugin) {
        super(title, 6, plugin);
        this.calendar.setTimeInMillis(current*1000L);
    }

    public void setOnConfirm(Consumer<Long> event){
        this.onConfirm = event;
    }


    int[] yearDisplay = new int[]{2,3,4,5};
    int[] monthDisplay = new int[]{19, 20};
    int[] dateDisplay = new int[]{24, 25};
    int[] hourDisplay = new int[]{37, 38};
    int[] minuteDisplay = new int[]{42, 43};

    public void renderTimeDisplay(){
        setItem(new int[]{2,3,4,5,19,20,24,25,37,38,42,43}, new SItemStack(Material.AIR).build());
        //year
        int year = calendar.get(Calendar.YEAR);
        String yearString = padString(String.valueOf(year), 4);
        for(int i =0; i < yearString.length(); i++){
            setItem(yearDisplay[i], new SInventoryItem(new SItemStack(banner.getItem(Integer.parseInt(String.valueOf(yearString.charAt(i))))).setDisplayName(" ").build()).clickable(false));
        }

        //month
        String monthString = padString(String.valueOf(calendar.get(Calendar.MONTH)+1), 2);
        for(int i =0; i < monthString.length(); i++){
            setItem(monthDisplay[i], new SInventoryItem(new SItemStack(banner.getItem(Integer.parseInt(String.valueOf(monthString.charAt(i))))).setDisplayName(" ").build()).clickable(false));
        }

        //date
        String dateString = padString(String.valueOf(calendar.get(Calendar.DATE)), 2);
        for(int i =0; i < dateString.length(); i++){
            setItem(dateDisplay[i], new SInventoryItem(new SItemStack(banner.getItem(Integer.parseInt(String.valueOf(dateString.charAt(i))))).setDisplayName(" ").build()).clickable(false));
        }

        //hour
        String hourString = padString(String.valueOf(calendar.get(Calendar.HOUR_OF_DAY)), 2);
        for(int i =0; i < hourString.length(); i++){
            setItem(hourDisplay[i], new SInventoryItem(new SItemStack(banner.getItem(Integer.parseInt(String.valueOf(hourString.charAt(i))))).setDisplayName(" ").build()).clickable(false));
        }

        //minute
        String minuteString = padString(String.valueOf(calendar.get(Calendar.MINUTE)), 2);
        for(int i =0; i < minuteString.length(); i++){
            setItem(minuteDisplay[i], new SInventoryItem(new SItemStack(banner.getItem(Integer.parseInt(String.valueOf(minuteString.charAt(i))))).setDisplayName(" ").build()).clickable(false));
        }
        renderInventory();
        
    }
    
    public String padString(String base, int requiredLength){
        StringBuilder padString = new StringBuilder();
        if(base.length() >= requiredLength) return base;
        for(int i = 0; i < requiredLength - base.length(); i++){
            padString.append("0");
        }
        padString.append(base);
        return padString.toString();
    }

    public void renderButtons(){
        setItem(6, getButtonItem(true, 1, Calendar.YEAR));
        setItem(21, getButtonItem(true, 1, Calendar.MONTH));
        setItem(26, getButtonItem(true, 1, Calendar.DATE));
        setItem(39, getButtonItem(true, 1, Calendar.HOUR_OF_DAY));
        setItem(44, getButtonItem(true, 1, Calendar.MINUTE));

        setItem(1, getButtonItem(false, 1, Calendar.YEAR));
        setItem(18, getButtonItem(false, 1, Calendar.MONTH));
        setItem(23, getButtonItem(false, 1, Calendar.DATE));
        setItem(36, getButtonItem(false, 1, Calendar.HOUR_OF_DAY));
        setItem(41, getButtonItem(false, 1, Calendar.MINUTE));

        SInventoryItem currentTime = new SInventoryItem(new SItemStack(Material.COMPASS).setDisplayName("§a§l現在の時刻に設定する").build());
        currentTime.clickable(false);
        currentTime.setEvent(e -> {
            calendar.setTimeInMillis(System.currentTimeMillis());
            renderTimeDisplay();
        });

        setItem(8, currentTime);
    }

    public SInventoryItem getButtonItem(boolean adding, int value, int type){
        SInventoryItem item;
        if(adding){
            item = new SInventoryItem(new SItemStack(banner.getSymbol("plus")).setDisplayName("§a§l+").build());
        }else{
            item = new SInventoryItem(new SItemStack(banner.getSymbol("minus")).setDisplayName("§c§l-").build());
        }
        item.clickable(false);
        item.setEvent(e -> {
            if(adding){
                calendar.add(type, value);
                if(calendar.getTimeInMillis() > 253402268399L*1000){ //9999/12/31
                    calendar.setTimeInMillis(253402268399L*1000L);
                }
            }else{
                calendar.add(type, -value);
                if(calendar.getTimeInMillis() < 0){
                    calendar.setTimeInMillis(0);
                }
            }
            renderTimeDisplay();
        });
        return item;
    }

    public void renderMenu(){
        SInventoryItem background = new SInventoryItem(new SItemStack(Material.BLUE_STAINED_GLASS_PANE).setDisplayName(" ").build());
        background.clickable(false);
        fillItem(background);

        renderButtons();
        renderTimeDisplay();

        SInventoryItem confirm = new SInventoryItem(new SItemStack(Material.LIME_STAINED_GLASS_PANE).setDisplayName(new SStringBuilder().green().bold().text("確認").build()).build());
        confirm.clickable(false);
        confirm.setAsyncEvent(e-> {
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            onConfirm.accept(calendar.getTimeInMillis()/1000L);
        });
        setItem(new int[]{48}, confirm);

        SInventoryItem cancel = new SInventoryItem(new SItemStack(Material.RED_STAINED_GLASS_PANE).setDisplayName(new SStringBuilder().red().bold().text("設定を解除").build()).build());
        cancel.clickable(false);
        cancel.setAsyncEvent(e-> {
            onConfirm.accept(-1L);
        });
        setItem(new int[]{50}, cancel);


    }
}
