package com.wdiscute.starcatcher.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wdiscute.starcatcher.Starcatcher;
import com.wdiscute.starcatcher.io.ModDataComponents;
import com.wdiscute.starcatcher.registry.ModRecipes;
import com.wdiscute.starcatcher.registry.custom.tackleskin.ITackleSkin;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.item.crafting.display.SmithingRecipeDisplay;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public record FishingRodSmithingRecipe(
        Ingredient template,
        Ingredient rod
)
        implements SmithingRecipe
{
    @Override
    public boolean matches(SmithingRecipeInput input, Level level)
    {
        //netherite upgrade
        if (input.template().is(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE)
                && !ModDataComponents.has(input.base(), ModDataComponents.NETHERITE_UPGRADE)
                && input.addition().is(Items.NETHERITE_INGOT)
        ) return true;

        //bobber skins - only allow if ingredient slot is empty
        if (ModDataComponents.has(input.template(), ModDataComponents.TACKLE_SKIN) && input.addition().isEmpty())
        {
            Identifier rl = ModDataComponents.get(input.template(), ModDataComponents.TACKLE_SKIN);

            Optional<Supplier<ITackleSkin>> optional = level.registryAccess().lookupOrThrow(Starcatcher.TACKLE_SKIN).getOptional(rl);

            return optional.isPresent();
        }

        return false;
    }

    public ItemStack assemble(SmithingRecipeInput input, HolderLookup.Provider registries)
    {
        ItemStack newRod = input.base().copy();

        //assemble netherite upgraded rod
        if (input.template().is(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE) && input.addition().is(Items.NETHERITE_INGOT))
        {
            ModDataComponents.set(newRod, ModDataComponents.NETHERITE_UPGRADE, true);
            return newRod;
        }

        //assemble bobber skin
        if (ModDataComponents.has(input.template(), ModDataComponents.TACKLE_SKIN) && input.addition().isEmpty())
        {
            ModDataComponents.set(newRod, ModDataComponents.TACKLE_SKIN, ModDataComponents.get(input.template(), ModDataComponents.TACKLE_SKIN));
            return newRod;
        }

        throw new RuntimeException("starcatcher - that template is not supported >:( talk to @wdiscute on discord");
    }

    @Override
    public Optional<Ingredient> templateIngredient()
    {
        return Optional.of(this.template);
    }

    @Override
    public Ingredient baseIngredient()
    {
        return this.rod;
    }

    @Override
    public Optional<Ingredient> additionIngredient()
    {
        return Optional.empty();
    }

    @Override
    public RecipeSerializer<FishingRodSmithingRecipe> getSerializer()
    {
        return ModRecipes.FISHING_ROD_SMITHING.get();
    }

    @Override
    public PlacementInfo placementInfo()
    {
        return PlacementInfo.createFromOptionals(List.of(Optional.of(this.template), Optional.of(this.rod), Optional.empty()));
    }

    @Override
    public List<RecipeDisplay> display()
    {
        return List.of(
            new SmithingRecipeDisplay(
                this.template.display(),
                this.rod.display(),
                SlotDisplay.Empty.INSTANCE,
                this.rod.display(),
                new SlotDisplay.ItemSlotDisplay(Items.SMITHING_TABLE)
            )
        );
    }

    public static class Serializer implements RecipeSerializer<FishingRodSmithingRecipe>
    {
        private static final MapCodec<FishingRodSmithingRecipe> CODEC = RecordCodecBuilder.mapCodec(
                instance -> instance.group(
                        Ingredient.CODEC.fieldOf("template").forGetter(FishingRodSmithingRecipe::template),
                        Ingredient.CODEC.fieldOf("rod").forGetter(FishingRodSmithingRecipe::rod)
                ).apply(instance, FishingRodSmithingRecipe::new)
        );

        public static final StreamCodec<RegistryFriendlyByteBuf, FishingRodSmithingRecipe> STREAM_CODEC = StreamCodec.of(
                FishingRodSmithingRecipe.Serializer::toNetwork, FishingRodSmithingRecipe.Serializer::fromNetwork
        );

        @Override
        public MapCodec<FishingRodSmithingRecipe> codec()
        {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, FishingRodSmithingRecipe> streamCodec()
        {
            return STREAM_CODEC;
        }

        private static FishingRodSmithingRecipe fromNetwork(RegistryFriendlyByteBuf buffer)
        {
            Ingredient template = Ingredient.CONTENTS_STREAM_CODEC.decode(buffer);
            Ingredient rod = Ingredient.CONTENTS_STREAM_CODEC.decode(buffer);
            return new FishingRodSmithingRecipe(template, rod);
        }

        private static void toNetwork(RegistryFriendlyByteBuf buffer, FishingRodSmithingRecipe recipe)
        {
            Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.template);
            Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.rod);
        }
    }
}
