// 
// Decompiled by Procyon v0.5.36
// 

package com.lymeowz.lib.handler;

import java.util.Collection;
import com.lymeowz.lib.AgarthaLib;

public abstract class HandlerCommand extends Handler
{
    protected HandlerCommand(final AgarthaLib plugin) {
        super(plugin);
    }
    
    public static Collection<HandlerCommand> getAllHandlerCommand() {
        return Handler.getHandlers(HandlerCommand.class);
    }
}
