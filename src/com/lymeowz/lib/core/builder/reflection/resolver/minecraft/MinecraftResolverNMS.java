// 
// Decompiled by Procyon v0.5.36
// 

package com.lymeowz.lib.core.builder.reflection.resolver.minecraft;

import com.lymeowz.lib.utility.ServerUtil;
import com.lymeowz.lib.core.builder.reflection.resolver.main.ResolverClass;

public class MinecraftResolverNMS extends ResolverClass
{
    @Override
    public Class resolve(final String... names) throws ClassNotFoundException {
        final String version = ServerUtil.getTextVersionNMS();
        for (int i = 0; i < names.length; ++i) {
            if (!names[i].startsWith("net.minecraft.server")) {
                names[i] = "net.minecraft.server." + version + "." + names[i];
            }
        }
        return super.resolve(names);
    }
}
