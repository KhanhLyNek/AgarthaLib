// 
// Decompiled by Procyon v0.5.36
// 

package com.lymeowz.lib.handler;

import java.util.Collection;
import com.lymeowz.lib.AgarthaLib;

public abstract class HandlerConfig extends Handler
{
    protected HandlerConfig(final AgarthaLib plugin) {
        super(plugin);
    }
    
    public abstract void setup();
    
    public static Collection<HandlerConfig> getAllHandlerConfig() {
        return Handler.getHandlers(HandlerConfig.class);
    }
}
