package com.wdiscute.starcatcher.util;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderOwner;
import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.Nullable;

public class TrustedHolder<T> extends Holder.Reference<T>
{
    protected TrustedHolder(Type type, HolderOwner<T> owner, @Nullable ResourceKey<T> key, @Nullable T value)
    {
        super(type, owner, key, value);
    }

    public static <T> Holder.Reference<T> createStandAlone(HolderOwner<T> owner, ResourceKey<T> key) {
        return new TrustedHolder<>(Type.STAND_ALONE, owner, key, null);
    }

    @Override
    public boolean canSerializeIn(HolderOwner<T> owner)
    {
        return false;
    }
}
