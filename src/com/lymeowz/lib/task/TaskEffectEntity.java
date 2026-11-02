// 
// Decompiled by Procyon v0.5.36
// 

package com.lymeowz.lib.task;

import java.util.Iterator;
import com.lymeowz.lib.manager.game.EffectEntityManager;
import com.lymeowz.lib.manager.game.GameManager;
import com.lymeowz.lib.effect.EffectProperties;
import com.lymeowz.lib.effect.EffectPlugin;
import com.lymeowz.lib.effect.Effect;
import com.lymeowz.lib.effect.EffectEntity;
import com.lymeowz.lib.AgarthaLib;
import com.lymeowz.lib.handler.HandlerTask;

public class TaskEffectEntity extends HandlerTask implements Runnable
{
    public TaskEffectEntity(final AgarthaLib plugin) {
        super(plugin);
    }
    
    @Override
    public void run() {
        final GameManager gameManager = this.plugin.getGameManager();
        final EffectEntityManager effectEntityManager = gameManager.getEffectEntityManager();
        for (final EffectEntity effectEntity : effectEntityManager.getAllEffectEntity()) {
            final Iterator<Effect> allEffect = effectEntity.getAllEffect().iterator();
            while (allEffect.hasNext()) {
                final Effect effect = allEffect.next();
                final Iterator<EffectPlugin> allEffectPlugin = effect.getAllEffectPlugin().iterator();
                while (allEffectPlugin.hasNext()) {
                    final EffectPlugin effectPlugin = allEffectPlugin.next();
                    final Iterator<EffectProperties> allEffectProperties = effectPlugin.getAllEffectProperties().iterator();
                    while (allEffectProperties.hasNext()) {
                        final EffectProperties effectProperties = allEffectProperties.next();
                        if (effectProperties.isExpired()) {
                            allEffectProperties.remove();
                        }
                    }
                    if (effectPlugin.isEmpty()) {
                        allEffectPlugin.remove();
                    }
                }
                if (effect.isEmpty()) {
                    allEffect.remove();
                }
            }
        }
    }
}
