// 
// Decompiled by Procyon v0.5.36
// 

package com.lymeowz.lib.handler;

import java.util.Collection;
import com.lymeowz.lib.AgarthaLib;

public abstract class HandlerBridge extends Handler
{
    protected HandlerBridge(final AgarthaLib plugin) {
        super(plugin);
    }
    
    public static Collection<HandlerBridge> getAllHandlerBridge() {
        return Handler.getHandlers(HandlerBridge.class);
    }
}
