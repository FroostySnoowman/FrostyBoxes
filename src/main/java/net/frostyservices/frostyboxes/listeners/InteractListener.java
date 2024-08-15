package net.frostyservices.frostyboxes.listeners;

import net.frostyservices.frostyboxes.Main;
import net.frostyservices.frostyboxes.manager.SlotType;
import net.frostyservices.frostyboxes.util.MaterialUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;

public class InteractListener implements Listener {
    private final Main instance;

    public InteractListener(Main instance) {
        this.instance = instance;
        instance.getServer().getPluginManager().registerEvents(this, instance);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (e.getAction() != Action.RIGHT_CLICK_AIR) return;
        ItemStack is = e.getPlayer().getInventory().getItemInMainHand();
        if(is.getAmount() != 1) return;
        if (!MaterialUtil.isShulkerBox(is.getType())) return;
        BlockStateMeta bsm = (BlockStateMeta) is.getItemMeta();
        assert bsm != null;
        instance.getShulkerManager().openShulkerBoxInventory(e.getPlayer(), is, SlotType.HOTBAR, e.getPlayer().getInventory().getHeldItemSlot());
    }

    @EventHandler(ignoreCancelled = true)
    public void onClick(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        Inventory clickedInventory = e.getClickedInventory();
        if (e.getClick() != ClickType.RIGHT) return;
        if (clickedInventory == null) return;
        if (clickedInventory.getType() != InventoryType.PLAYER) {
            return;
        }

        ItemStack clicked = e.getCurrentItem();
        if(clicked != null && clicked.getAmount() != 1) return;
        boolean isShulker = clicked!=null && MaterialUtil.isShulkerBox(clicked.getType());
        if (player.getOpenInventory().getTopInventory().getType() != InventoryType.CRAFTING) {
            if(!isShulker) {
                return;
            }
        }
        if(!isShulker) return;
        e.setCancelled(true);
        instance.getShulkerManager().openShulkerBoxInventory(player, clicked, SlotType.INVENTORY, e.getRawSlot());

    }
}
