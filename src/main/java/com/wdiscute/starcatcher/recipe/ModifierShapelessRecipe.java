package com.wdiscute.starcatcher.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wdiscute.starcatcher.registry.ModRecipes;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.ShapelessCraftingRecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;

import java.util.List;

public class ModifierShapelessRecipe implements CraftingRecipe
{
    final String group;
    final CraftingBookCategory category;
    final ItemStack result;
    final List<Ingredient> ingredients;
    private final boolean isSimple;
    private final List<Identifier> modifiers;
    private @Nullable PlacementInfo placementInfo;

    public ModifierShapelessRecipe(String group, CraftingBookCategory category, ItemStack result, List<Ingredient> ingredients, List<Identifier> modifiers)
    {
        this.group = group;
        this.category = category;
        this.result = result;
        this.ingredients = ingredients;
        this.isSimple = ingredients.stream().allMatch(Ingredient::isSimple);
        this.modifiers = modifiers;
    }

    @Override
    public RecipeSerializer<ModifierShapelessRecipe> getSerializer()
    {
        return ModRecipes.MODIFIER_SHAPELESS_RECIPE.get();
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
            this.placementInfo = PlacementInfo.create(this.ingredients);
        }
        return this.placementInfo;
    }

    @Override
    public boolean matches(CraftingInput input, Level level)
    {
        if (input.ingredientCount() != this.ingredients.size())
        {
            return false;
        }
        else if (!isSimple)
        {
            var nonEmptyItems = new java.util.ArrayList<ItemStack>(input.ingredientCount());
            for (var item : input.items())
                if (!item.isEmpty())
                    nonEmptyItems.add(item);
            return net.neoforged.neoforge.common.util.RecipeMatcher.findMatches(nonEmptyItems, this.ingredients) != null;
        }
        else
        {
            return input.size() == 1 && this.ingredients.size() == 1
                    ? this.ingredients.getFirst().test(input.getItem(0))
                    : input.stackedContents().canCraft(this, null);
        }
    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries)
    {
        return this.result.copy();
    }

    @Override
    public List<RecipeDisplay> display()
    {
        return List.of(
            new ShapelessCraftingRecipeDisplay(
                this.ingredients.stream().map(Ingredient::display).toList(),
                new SlotDisplay.ItemStackSlotDisplay(this.result),
                new SlotDisplay.ItemSlotDisplay(Items.CRAFTING_TABLE)
            )
        );
    }

    public static class Serializer implements RecipeSerializer<ModifierShapelessRecipe>
    {
        private static final MapCodec<ModifierShapelessRecipe> CODEC = RecordCodecBuilder.mapCodec(
                instance -> instance.group(
                                Codec.STRING.optionalFieldOf("group", "").forGetter(recipe -> recipe.group),
                                CraftingBookCategory.CODEC.fieldOf("category").orElse(CraftingBookCategory.MISC).forGetter(recipe -> recipe.category),
                                ItemStack.STRICT_CODEC.fieldOf("result").forGetter(recipe -> recipe.result),
                                Codec.lazyInitialized(() -> Ingredient.CODEC.listOf(1, 9)).fieldOf("ingredients").forGetter(recipe -> recipe.ingredients),
                                Identifier.CODEC.listOf().fieldOf("modifiers").forGetter(recipe -> recipe.modifiers)
                        )
                        .apply(instance, ModifierShapelessRecipe::new)
        );
        public static final StreamCodec<RegistryFriendlyByteBuf, ModifierShapelessRecipe> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.STRING_UTF8,
                recipe -> recipe.group,
                CraftingBookCategory.STREAM_CODEC,
                recipe -> recipe.category,
                ItemStack.STREAM_CODEC,
                recipe -> recipe.result,
                Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list()),
                recipe -> recipe.ingredients,
                Identifier.STREAM_CODEC.apply(ByteBufCodecs.list()),
                recipe -> recipe.modifiers,
                ModifierShapelessRecipe::new
        );

        @Override
        public MapCodec<ModifierShapelessRecipe> codec()
        {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, ModifierShapelessRecipe> streamCodec()
        {
            return STREAM_CODEC;
        }
    }


}
