// 
// Decompiled by Procyon v0.5.36
// 

package com.lymeowz.lib.handler;

import java.util.Collection;
import com.lymeowz.lib.AgarthaLib;

public class HandlerPacket extends Handler
{
    protected HandlerPacket(final AgarthaLib plugin) {
        super(plugin);
    }
    
    public static Collection<HandlerPacket> getAllHandlerPacket() {
        return Handler.getHandlers(HandlerPacket.class);
    }
}
