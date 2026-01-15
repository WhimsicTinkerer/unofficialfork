package com.wdiscute.starcatcher.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wdiscute.starcatcher.Starcatcher;
import com.wdiscute.starcatcher.io.ModDataComponents;
import com.wdiscute.starcatcher.registry.ModRecipes;
import com.wdiscute.starcatcher.registry.custom.catchmodifiers.AbstractCatchModifier;
import com.wdiscute.starcatcher.registry.custom.minigamemodifiers.AbstractMinigameModifier;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.ShapedCraftingRecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public class ModifierShapedRecipe implements CraftingRecipe
{
    public final ShapedRecipePattern pattern;
    final ItemStack result;
    final List<Identifier> minigameModifiers;
    final List<Identifier> catchModifiers;
    final Identifier bobberSkin;
    final String group;
    final CraftingBookCategory category;
    final boolean showNotification;
    private @Nullable PlacementInfo placementInfo;

    public ModifierShapedRecipe(String group, CraftingBookCategory category, ShapedRecipePattern pattern, ItemStack result,
                                boolean showNotification,
                                List<Identifier> minigameModifiers,
                                List<Identifier> catchModifiers,
                                Identifier bobberSkin
    )
    {
        this.group = group;
        this.category = category;
        this.pattern = pattern;
        this.result = result;
        this.showNotification = showNotification;
        this.minigameModifiers = minigameModifiers;
        this.catchModifiers = catchModifiers;
        this.bobberSkin = bobberSkin;
    }

    @Override
    public RecipeSerializer<? extends CraftingRecipe> getSerializer()
    {
        return ModRecipes.MODIFIER_SHAPED_RECIPE.get();
    }

    @Override
    public String group()
    {
        return this.group;
    }

    @Override
    public CraftingBookCategory category()
    {
        return this.category;
    }

    @Override
    public PlacementInfo placementInfo()
    {
        if (this.placementInfo == null)
        {
            this.placementInfo = PlacementInfo.createFromOptionals(this.pattern.ingredients());
        }
        return this.placementInfo;
    }

    @Override
    public boolean showNotification()
    {
        return this.showNotification;
    }

    public boolean matches(CraftingInput input, Level level)
    {
        return this.pattern.matches(input);
    }

    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries)
    {
        var itemstack = this.result.copy();

        List<Identifier> catchModifiers = new ArrayList<>();
        List<Identifier> minigameModifiers = new ArrayList<>();

        for (Identifier rl : this.minigameModifiers)
        {
            ResourceKey<Supplier<AbstractCatchModifier>> catchRK = ResourceKey.create(Starcatcher.CATCH_MODIFIERS, rl);
            ResourceKey<Supplier<AbstractMinigameModifier>> minigameRK = ResourceKey.create(Starcatcher.MINIGAME_MODIFIERS, rl);

            if (registries.lookupOrThrow(Starcatcher.CATCH_MODIFIERS).get(catchRK).isPresent())
                catchModifiers.add(rl);

            if (registries.lookupOrThrow(Starcatcher.MINIGAME_MODIFIERS).get(minigameRK).isPresent())
                minigameModifiers.add(rl);
        }

        if (!catchModifiers.isEmpty())
            ModDataComponents.set(itemstack, ModDataComponents.CATCH_MODIFIERS, catchModifiers);
        if (!minigameModifiers.isEmpty())
            ModDataComponents.set(itemstack, ModDataComponents.MINIGAME_MODIFIERS, minigameModifiers);

        return itemstack;
    }

    public int getWidth()
    {
        return this.pattern.width();
    }

    public int getHeight()
    {
        return this.pattern.height();
    }

    @Override
    public List<RecipeDisplay> display()
    {
        return List.of(
            new ShapedCraftingRecipeDisplay(
                this.pattern.width(),
                this.pattern.height(),
                this.pattern.ingredients().stream().map(opt -> opt.map(Ingredient::display).orElse(SlotDisplay.Empty.INSTANCE)).toList(),
                new SlotDisplay.ItemStackSlotDisplay(this.result),
                new SlotDisplay.ItemSlotDisplay(Items.CRAFTING_TABLE)
            )
        );
    }

    public static class Serializer implements RecipeSerializer<ModifierShapedRecipe>
    {
        public static final MapCodec<ModifierShapedRecipe> CODEC = RecordCodecBuilder.mapCodec(
                group -> group.group(
                                Codec.STRING.optionalFieldOf("group", "").forGetter(shapedRecipe -> shapedRecipe.group),
                                CraftingBookCategory.CODEC.fieldOf("category").orElse(CraftingBookCategory.MISC).forGetter(shapedRecipe -> shapedRecipe.category),
                                ShapedRecipePattern.MAP_CODEC.forGetter(shapedRecipe -> shapedRecipe.pattern),
                                ItemStack.STRICT_CODEC.fieldOf("result").forGetter(shapedRecipe -> shapedRecipe.result),
                                Codec.BOOL.optionalFieldOf("show_notification", Boolean.TRUE).forGetter(shapedRecipe -> shapedRecipe.showNotification),
                                Identifier.CODEC.listOf().optionalFieldOf("minigame_modifiers", List.of()).forGetter(shapedRecipe -> shapedRecipe.minigameModifiers),
                                Identifier.CODEC.listOf().optionalFieldOf("catch_modifiers", List.of()).forGetter(shapedRecipe -> shapedRecipe.catchModifiers),
                                Identifier.CODEC.optionalFieldOf("bobber_skin", Starcatcher.rl("missingno")).forGetter(shapedRecipe -> shapedRecipe.bobberSkin)
                        )
                        .apply(group, ModifierShapedRecipe::new)
        );
        public static final StreamCodec<RegistryFriendlyByteBuf, ModifierShapedRecipe> STREAM_CODEC = StreamCodec.of(
                ModifierShapedRecipe.Serializer::toNetwork, ModifierShapedRecipe.Serializer::fromNetwork
        );

        @Override
        public MapCodec<ModifierShapedRecipe> codec()
        {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, ModifierShapedRecipe> streamCodec()
        {
            return STREAM_CODEC;
        }

        private static ModifierShapedRecipe fromNetwork(RegistryFriendlyByteBuf buffer)
        {
            String s = buffer.readUtf();
            CraftingBookCategory craftingbookcategory = buffer.readEnum(CraftingBookCategory.class);
            ShapedRecipePattern shapedrecipepattern = ShapedRecipePattern.STREAM_CODEC.decode(buffer);
            ItemStack itemstack = ItemStack.STREAM_CODEC.decode(buffer);
            boolean flag = buffer.readBoolean();
            List<Identifier> minigameModifiers = Identifier.STREAM_CODEC.apply(ByteBufCodecs.list()).decode(buffer);
            List<Identifier> catchModifiers = Identifier.STREAM_CODEC.apply(ByteBufCodecs.list()).decode(buffer);
            Identifier bobberSkin = Identifier.STREAM_CODEC.decode(buffer);
            return new ModifierShapedRecipe(s, craftingbookcategory, shapedrecipepattern, itemstack, flag, minigameModifiers, catchModifiers, bobberSkin);
        }

        private static void toNetwork(RegistryFriendlyByteBuf buffer, ModifierShapedRecipe recipe)
        {
            buffer.writeUtf(recipe.group);
            buffer.writeEnum(recipe.category);
            ShapedRecipePattern.STREAM_CODEC.encode(buffer, recipe.pattern);
            ItemStack.STREAM_CODEC.encode(buffer, recipe.result);
            buffer.writeBoolean(recipe.showNotification);
            Identifier.STREAM_CODEC.apply(ByteBufCodecs.list()).encode(buffer, recipe.minigameModifiers);
            Identifier.STREAM_CODEC.apply(ByteBufCodecs.list()).encode(buffer, recipe.catchModifiers);
            Identifier.STREAM_CODEC.encode(buffer, recipe.bobberSkin);
        }
    }

}
