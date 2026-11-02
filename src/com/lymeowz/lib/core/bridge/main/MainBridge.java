// 
// Decompiled by Procyon v0.5.36
// 

package com.lymeowz.lib.core.bridge.main;

import com.lymeowz.lib.core.bridge.unity.BridgePlayer;
import com.lymeowz.lib.core.bridge.unity.BridgeTagsNBT;
import com.lymeowz.lib.core.bridge.unity.BridgeSound;
import com.lymeowz.lib.core.bridge.unity.BridgeServer;
import com.lymeowz.lib.core.bridge.unity.BridgeParticle;
import com.lymeowz.lib.core.bridge.unity.BridgeMessage;
import com.lymeowz.lib.core.bridge.unity.BridgeMaterial;
import com.lymeowz.lib.core.bridge.unity.BridgeLivingEntity;
import com.lymeowz.lib.core.bridge.unity.BridgeInventory;
import com.lymeowz.lib.core.bridge.unity.BridgeEquipment;
import com.lymeowz.lib.core.bridge.unity.BridgeBlock;
import com.lymeowz.lib.core.bridge.unity.Bridge;
import com.lymeowz.lib.core.bridge.unity.BridgeArrow;

@Deprecated
public class MainBridge
{
    public static final BridgeArrow getBridgeArrow() {
        return Bridge.getBridgeArrow();
    }
    
    public static final BridgeBlock getBridgeBlock() {
        return Bridge.getBridgeBlock();
    }
    
    public static final BridgeEquipment getBridgeEquipment() {
        return Bridge.getBridgeEquipment();
    }
    
    public static final BridgeInventory getBridgeInventory() {
        return Bridge.getBridgeInventory();
    }
    
    public static final BridgeLivingEntity getBridgeLivingEntity() {
        return Bridge.getBridgeLivingEntity();
    }
    
    public static final BridgeMaterial getBridgeMaterial() {
        return Bridge.getBridgeMaterial();
    }
    
    public static final BridgeMessage getBridgeMessage() {
        return Bridge.getBridgeMessage();
    }
    
    public static final BridgeParticle getBridgeParticle() {
        return Bridge.getBridgeParticle();
    }
    
    public static final BridgeServer getBridgeServer() {
        return Bridge.getBridgeServer();
    }
    
    public static final BridgeSound getBridgeSound() {
        return Bridge.getBridgeSound();
    }
    
    public static final BridgeTagsNBT getBridgeTagsNBT() {
        return Bridge.getBridgeTagsNBT();
    }
    
    public static final BridgePlayer getBridgePlayer() {
        return Bridge.getBridgePlayer();
    }
}
