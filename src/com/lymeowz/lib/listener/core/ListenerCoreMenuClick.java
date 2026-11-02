// 
// Decompiled by Procyon v0.5.36
// 

package com.lymeowz.lib.listener.core;

import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryAction;
import com.lymeowz.lib.core.builder.menu.MenuSlot;
import com.lymeowz.lib.core.builder.menu.MenuProperties;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.Inventory;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.Event;
import com.lymeowz.lib.utility.ServerEventUtil;
import com.lymeowz.lib.event.MenuClickEvent;
import com.lymeowz.lib.core.builder.menu.MenuGUI;
import com.lymeowz.lib.core.builder.menu.MenuSlotAction;
import org.bukkit.event.inventory.InventoryType;
import com.lymeowz.lib.core.builder.menu.Menu;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import com.lymeowz.lib.AgarthaLib;
import org.bukkit.event.Listener;
import com.lymeowz.lib.handler.HandlerListener;

public class ListenerCoreMenuClick extends HandlerListener implements Listener
{
    public ListenerCoreMenuClick(final AgarthaLib plugin) {
        super(plugin);
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void menuClickEvent(final InventoryClickEvent event) {
        if (!event.isCancelled()) {
            final HumanEntity human = event.getWhoClicked();
            if (human instanceof Player) {
                final Player player = (Player)human;
                final Inventory clickedInventory = event.getClickedInventory();
                if (clickedInventory != null) {
                    final Inventory inventory = event.getInventory();
                    final InventoryHolder holder = inventory.getHolder();
                    if (holder != null && holder instanceof Menu) {
                        final Menu menu = (Menu)holder;
                        final MenuProperties menuProperties = menu.getProperties();
                        final InventoryType inventoryType = clickedInventory.getType();
                        final ClickType clickType = event.getClick();
                        final boolean isEditable = menuProperties.isMenuEditable();
                        final boolean isPlayerInventory = inventoryType.equals((Object)InventoryType.PLAYER);
                        final int slot = event.getSlot();
                        final int hotbar = event.getHotbarButton();
                        final MenuSlotAction.ActionType actionType = MenuSlotAction.ActionType.getActionType(clickType, hotbar);
                        if (!isPlayerInventory) {
                            event.setCancelled(!isEditable);
                            if (menu instanceof MenuGUI) {
                                final MenuGUI menuGUI = (MenuGUI)menu;
                                final MenuSlot menuSlot = menuGUI.getMenuSlot(slot);
                                if (menuSlot != null) {
                                    final Boolean slotEditable = menuSlot.isEditable();
                                    if (slotEditable != null) {
                                        event.setCancelled(!slotEditable);
                                    }
                                }
                            }
                            if (actionType != null) {
                                final MenuClickEvent menuClickEvent = new MenuClickEvent(player, menu, actionType, slot);
                                ServerEventUtil.callEvent(menuClickEvent);
                            }
                        }
                        if (!event.isCancelled()) {
                            if (clickType.equals((Object)ClickType.SHIFT_LEFT) || clickType.equals((Object)ClickType.SHIFT_RIGHT)) {
                                event.setCancelled(true);
                                return;
                            }
                            final InventoryAction inventoryAction = event.getAction();
                            boolean cancelled = false;
                            switch (inventoryAction) {
                                case CLONE_STACK: {
                                    cancelled = true;
                                    break;
                                }
                                case COLLECT_TO_CURSOR: {
                                    cancelled = true;
                                    break;
                                }
                                case DROP_ALL_CURSOR: {
                                    cancelled = true;
                                    break;
                                }
                                case DROP_ALL_SLOT: {
                                    cancelled = true;
                                    break;
                                }
                                case DROP_ONE_CURSOR: {
                                    cancelled = true;
                                    break;
                                }
                                case DROP_ONE_SLOT: {
                                    cancelled = true;
                                    break;
                                }
                                case HOTBAR_MOVE_AND_READD: {
                                    cancelled = true;
                                    break;
                                }
                                case HOTBAR_SWAP: {
                                    cancelled = true;
                                    break;
                                }
                                default: {
                                    cancelled = false;
                                    break;
                                }
                            }
                            event.setCancelled(cancelled);
                        }
                    }
                }
            }
        }
    }
}
