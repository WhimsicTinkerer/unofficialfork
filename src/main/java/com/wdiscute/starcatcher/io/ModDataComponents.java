package com.wdiscute.starcatcher.io;

import com.mojang.serialization.Codec;
import com.wdiscute.starcatcher.Starcatcher;
import com.wdiscute.starcatcher.secretnotes.SecretNote;
import com.wdiscute.starcatcher.storage.FishProperties;
import com.wdiscute.starcatcher.storage.TrophyProperties;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import org.jspecify.annotations.Nullable;
import java.util.List;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public class ModDataComponents
{
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENT_TYPES =
            DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, Starcatcher.MOD_ID);

    //bucketed fish
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<SingleStackContainer>> BUCKETED_FISH = register(
            "bucketed_fish",
            builder -> builder.persistent(SingleStackContainer.CODEC));

    //rod menu
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<SingleStackContainer>> BOBBER = register(
            "bobber",
            builder -> builder.persistent(SingleStackContainer.CODEC));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<SingleStackContainer>> BAIT = register(
            "bait",
            builder -> builder.persistent(SingleStackContainer.CODEC));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<SingleStackContainer>> HOOK = register(
            "hook",
            builder -> builder.persistent(SingleStackContainer.CODEC));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<TrophyProperties>> TROPHY = register(
            "trophy",
            builder -> builder.persistent(TrophyProperties.CODEC));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<FishProperties>> FISH_PROPERTIES = register(
            "fish_properties",
            builder -> builder.persistent(FishProperties.CODEC));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<SecretNote.Note>> SECRET_NOTE = register(
            "secret_note",
            builder -> builder.persistent(SecretNote.Note.CODEC));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<SizeAndWeightInstance>> SIZE_AND_WEIGHT = register(
            "size_and_weight",
            builder -> builder.persistent(SizeAndWeightInstance.CODEC));


    //modifiers
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<Identifier>>> MINIGAME_MODIFIERS = register(
            "minigame_modifiers",
            builder -> builder.persistent(Identifier.CODEC.listOf()));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<Identifier>>> CATCH_MODIFIERS = register(
            "catch_modifiers",
            builder -> builder.persistent(Identifier.CODEC.listOf()));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Identifier>> TACKLE_SKIN = register(
            "tackle_skin",
            builder -> builder.persistent(Identifier.CODEC));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> NETHERITE_UPGRADE = register(
            "netherite_upgraded",
            builder -> builder.persistent(Codec.BOOL));

    public static <T> void set(ItemStack stack, Supplier<DataComponentType<T>> component, T data){
        stack.set(component, data);
    }

    @Nullable
    public static <T> T get(ItemStack stack, Supplier<DataComponentType<T>> component){
        return stack.get(component);
    }

    public static <T> boolean has(ItemStack stack, Supplier<DataComponentType<T>> component){
        return stack.has(component);
    }

    public static  <T> void remove(ItemStack stack, Supplier<DataComponentType<T>> component){
        stack.remove(component);
    }


    public static <T> T getOrDefault(ItemStack stack, Supplier<DataComponentType<T>> component, T defaultValue) {
        return stack.getOrDefault(component, defaultValue);
    }



    private static <T> DeferredHolder<DataComponentType<?>, DataComponentType<T>> register(String name,
                                                                                           UnaryOperator<DataComponentType.Builder<T>> builderOperator)
    {
        return DATA_COMPONENT_TYPES.register(name, () -> builderOperator.apply(DataComponentType.builder()).build());
    }

    public static void register(IEventBus eventBus)
    {
        DATA_COMPONENT_TYPES.register(eventBus);
    }

}
