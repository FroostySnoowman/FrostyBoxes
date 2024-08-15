package net.frostyservices.frostyboxes.manager;

import net.frostyservices.frostyboxes.Main;
import net.frostyservices.frostyboxes.configuration.FBConfig;
import net.frostyservices.frostyboxes.util.FBPermission;
import net.frostyservices.frostyboxes.util.TimeUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class ShulkerManager {
    private final Main instance;
    private HashMap<Inventory, ShulkerOpenData> openShulkerInventories = new HashMap<>();
    private HashMap<UUID, Long> lastOpened = new HashMap<>();


    public ShulkerManager(Main instance) {
        this.instance = instance;
    }


    public void openShulkerBoxInventory(Player player, ItemStack shulkerStack, SlotType slotType, int rawSlot) {
        FBConfig bsbConfig = instance.getBSBConfig();

        // Cooldown check
        int cooldown = getPlayerCooldown(player.getUniqueId());
        if (cooldown > 0 && !player.hasPermission(FBPermission.BYPASS_COOLDOWN.toString())) {
            int[] formatted = TimeUtils.formatToMinutesAndSeconds(cooldown);
            bsbConfig.getCooldownMessage().send(player, "%seconds%", String.valueOf(formatted[1]));
            return;
        }


        // close the player's current inventory if they have one open
        if (player.getOpenInventory().getTopInventory().getType() != InventoryType.CRAFTING || openShulkerInventories.containsKey(player.getOpenInventory().getTopInventory())) {
            player.closeInventory();
        };


        // Check end

        lastOpened.put(player.getUniqueId(), System.currentTimeMillis());

        BlockStateMeta bsm = (BlockStateMeta) shulkerStack.getItemMeta();
        assert bsm != null;
        ShulkerBox shulker = (ShulkerBox) bsm.getBlockState();
        Inventory inventory = Bukkit.createInventory(null, InventoryType.SHULKER_BOX, formatShulkerPlaceholder(bsbConfig.getInventoryName(), shulkerStack));
        inventory.setContents(shulker.getInventory().getContents());

        player.playSound(player.getLocation(), Sound.BLOCK_SHULKER_BOX_OPEN, 1.0f, 1.0f);

        player.openInventory(inventory);
        ItemStack clone = shulkerStack.clone();
        openShulkerInventories.put(inventory, new ShulkerOpenData(clone, player.getLocation(), slotType, rawSlot));
    }

    public ItemStack closeShulkerBox(Player player, Inventory inventory, Optional<ItemStack> useStack) {
        player.getOpenInventory().getTopInventory();
        if (!openShulkerInventories.containsKey(inventory)) return null;
        ShulkerOpenData shulkerOpenData = openShulkerInventories.remove(inventory);

        ItemStack stackClone = shulkerOpenData.getItemStack();
        if (useStack.isPresent()) {
            stackClone = useStack.get();
        }

        if (player.getOpenInventory().getTopInventory().getType() == InventoryType.SHULKER_BOX) {
            player.closeInventory();
        }

        ItemStack targetItem = stackClone;
        boolean found = false;
        if (shulkerOpenData.getSlotType() == SlotType.HOTBAR) {
            ItemStack is = player.getInventory().getItemInMainHand();
            if (is.equals(stackClone)) {
                targetItem = is;
                found = true;
            }
        } else if (shulkerOpenData.getSlotType() == SlotType.INVENTORY) {
            ItemStack is = player.getInventory().getItem(shulkerOpenData.getRawSlot());
            if (is != null && is.equals(stackClone)) {
                targetItem = is;
                found = true;
            }
        }

        //Keep as fallback
        if (!found) {
            for (ItemStack is : player.getInventory().getContents()) {
                if (is != null && is.equals(stackClone)) {
                    found = true;
                    targetItem = is;
                    break;
                }
            }
        }
        if (!found) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "WARNING! Player " + player.getName() + " closed a shulkerbox and changes were not saved!");
        }

        BlockStateMeta cMeta = (BlockStateMeta) targetItem.getItemMeta();
        ShulkerBox shulker = (ShulkerBox) cMeta.getBlockState();
        shulker.getInventory().setContents(inventory.getContents());
        cMeta.setBlockState(shulker);
        targetItem.setItemMeta(cMeta);
        player.updateInventory();
        return targetItem;
    }

    public boolean isShulkerInventory(Inventory inv) {
        return openShulkerInventories.containsKey(inv);
    }

    public boolean doesPlayerHaveShulkerOpen(UUID uuid) {
        for (Inventory inv : openShulkerInventories.keySet()) {
            for (HumanEntity he : inv.getViewers()) {
                if (he.getUniqueId().equals(uuid)) {
                    return true;
                }
            }
        }
        return false;
    }

    public ItemStack getCorrespondingStack(Inventory inv) {
        ShulkerOpenData sod = openShulkerInventories.getOrDefault(inv, null);
        if (sod == null) return null;
        return sod.getItemStack();
    }

    public ShulkerOpenData getShulkerOpenData(Inventory inv) {
        return openShulkerInventories.getOrDefault(inv, null);
    }


    public void closeAllInventories(boolean disableCall) {
        HashMap<HumanEntity, Inventory> playersToCloseInventory = new HashMap<>();
        for (Inventory inventory : openShulkerInventories.keySet()) {
            for (HumanEntity he : inventory.getViewers()) {
                playersToCloseInventory.put(he, inventory);
            }
        }
        for (Map.Entry<HumanEntity, Inventory> entry : playersToCloseInventory.entrySet()) {
            Player player = (Player) entry.getKey();
            player.closeInventory();
            if (disableCall) {
                closeShulkerBox(player, entry.getValue(), Optional.empty());
            }
        }

    }

    public int getPlayerCooldown(UUID uuid) {
        if (!lastOpened.containsKey(uuid)) return 0;
        long timePassed = System.currentTimeMillis() - lastOpened.getOrDefault(uuid, 0L);
        return (int) Math.max(0, instance.getBSBConfig().getCooldown() - timePassed);
    }

    private String getShulkerPlaceholderReplacement(ItemStack shulker) {
        if (shulker == null) return "invalid";
        if (shulker.getItemMeta() == null || !shulker.getItemMeta().hasDisplayName())
            return InventoryType.SHULKER_BOX.getDefaultTitle();
        return shulker.getItemMeta().getDisplayName();
    }
    private String formatShulkerPlaceholder(String message, ItemStack shulker) {
        if (message.isEmpty()) return message;
        if (!message.contains("%shulker_name%")) return message;
        return message.replace("%shulker_name%",getShulkerPlaceholderReplacement(shulker));
    }

    private enum MessageSoundComb {
        OPEN,
        CLOSE
    }


}
