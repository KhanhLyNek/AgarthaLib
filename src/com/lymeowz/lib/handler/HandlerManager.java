// 
// Decompiled by Procyon v0.5.36
// 

package com.lymeowz.lib.handler;

import java.util.Collection;
import com.lymeowz.lib.AgarthaLib;

public abstract class HandlerManager extends Handler
{
    protected HandlerManager(final AgarthaLib plugin) {
        super(plugin);
    }
    
    public static Collection<HandlerManager> getAllHandlerManager() {
        return Handler.getHandlers(HandlerManager.class);
    }
}
