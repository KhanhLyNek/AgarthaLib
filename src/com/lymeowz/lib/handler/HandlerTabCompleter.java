// 
// Decompiled by Procyon v0.5.36
// 

package com.lymeowz.lib.handler;

import java.util.Collection;
import com.lymeowz.lib.AgarthaLib;

public abstract class HandlerTabCompleter extends Handler
{
    protected HandlerTabCompleter(final AgarthaLib plugin) {
        super(plugin);
    }
    
    public static Collection<HandlerTabCompleter> getAllHandlerTabCompleter() {
        return Handler.getHandlers(HandlerTabCompleter.class);
    }
}
