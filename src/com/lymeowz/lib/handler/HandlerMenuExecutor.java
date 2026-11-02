// 
// Decompiled by Procyon v0.5.36
// 

package com.lymeowz.lib.handler;

import java.util.Collection;
import com.lymeowz.lib.AgarthaLib;

public abstract class HandlerMenuExecutor extends Handler
{
    protected HandlerMenuExecutor(final AgarthaLib plugin) {
        super(plugin);
    }
    
    public static Collection<HandlerMenuExecutor> getAllHandlerMenuExecutor() {
        return Handler.getHandlers(HandlerMenuExecutor.class);
    }
}
