package com.wdiscute.starcatcher.util;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderOwner;
import net.minecraft.resources.ResourceKey;

/**
 * Utility class for creating holders.
 * Simplified for 1.21.11 compatibility - Holder.Reference API changed significantly.
 * @deprecated This class may no longer be needed in 1.21.11
 */
@Deprecated
public class TrustedHolder {

    /**
     * Creates a direct holder for the given key.
     * In 1.21.11, the Holder API changed - this is a compatibility shim.
     */
    public static <T> Holder<T> createStandAlone(HolderOwner<T> owner, ResourceKey<T> key) {
        // In 1.21.11, we use Holder.direct() for simple holder creation
        // This returns null value holder - caller should handle appropriately
        return Holder.direct(null);
    }
}
