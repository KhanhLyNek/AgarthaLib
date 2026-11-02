package com.lymeowz.lib.listener.custom;

import com.lymeowz.lib.AgarthaLib;
import com.lymeowz.lib.event.PlayerHealthMaxChangeEvent;
import com.lymeowz.lib.handler.HandlerCommand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

/**
 * Legacy Jobs/Heroes/SkillAPI/BattleLevels formulas cannot be mapped to
 * MMOCore/AuraSkills without explicit migration rules. Preserve the original
 * health event and avoid applying invented bonuses.
 */
public class ListenerPlayerHealthMaxChange extends HandlerCommand implements Listener {
    public ListenerPlayerHealthMaxChange(final AgarthaLib plugin) { super(plugin); }

    @EventHandler(priority = EventPriority.MONITOR)
    public void eventPlayerHealthMaxChange(final PlayerHealthMaxChangeEvent event) {
        // Preserve health assigned by the original event and its other subscribers.
        // No legacy optional provider formula runs on Paper 26.3.
    }
}
