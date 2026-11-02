// 
// Decompiled by Procyon v0.5.36
// 

package com.lymeowz.lib.handler;

import java.util.Collection;
import com.lymeowz.lib.database.Database;
import com.lymeowz.lib.AgarthaLib;

public abstract class HandlerDatabase extends Handler
{
    protected HandlerDatabase(final AgarthaLib plugin) {
        super(plugin);
    }
    
    protected abstract Database getDatabase();
    
    public final void closeConnection() {
        final Database database = this.getDatabase();
        database.close();
    }
    
    public static Collection<HandlerDatabase> getAllHandlerDatabase() {
        return Handler.getHandlers(HandlerDatabase.class);
    }
}
