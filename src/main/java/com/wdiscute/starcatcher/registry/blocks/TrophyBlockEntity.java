package com.wdiscute.starcatcher.registry.blocks;

import com.mojang.logging.LogUtils;
import com.wdiscute.starcatcher.io.ModDataComponents;
import com.wdiscute.starcatcher.storage.TrophyProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.slf4j.Logger;

public class TrophyBlockEntity extends BlockEntity {
    private static final Logger LOGGER = LogUtils.getLogger();
    private TrophyProperties trophyProperties;

    public TrophyBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.TROPHY.get(), pPos, pBlockState);
    }

    @Override
    protected void applyImplicitComponents(net.minecraft.core.component.DataComponentGetter componentInput) {
        super.applyImplicitComponents(componentInput);
        this.trophyProperties = componentInput.getOrDefault(ModDataComponents.TROPHY.get(), TrophyProperties.builder().build());
        setChanged();
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);
        components.set(ModDataComponents.TROPHY, trophyProperties);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);

        if (this.trophyProperties == null) return;

        output.store("trophy_properties", TrophyProperties.CODEC, this.trophyProperties);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);

        this.trophyProperties = input.read("trophy_properties", TrophyProperties.CODEC)
                .orElse(TrophyProperties.builder().build());
    }
}
