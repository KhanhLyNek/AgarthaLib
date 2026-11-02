// 
// Decompiled by Procyon v0.5.36
// 

package com.lymeowz.lib.handler;

import java.util.Collection;
import com.lymeowz.lib.AgarthaLib;

public abstract class HandlerListener extends Handler
{
    protected HandlerListener(final AgarthaLib plugin) {
        super(plugin);
    }
    
    public static Collection<HandlerListener> getAllHandlerListener() {
        return Handler.getHandlers(HandlerListener.class);
    }
}
