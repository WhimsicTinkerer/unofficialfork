package net.nikdo53.tinymultiblocklib.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

// TODO: Re-enable when TinyMultiblockLib 1.21.11 is available
// Stub class for compilation
public abstract class AbstractMultiBlockEntity extends BlockEntity {

    public AbstractMultiBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    public void sync() {
        if (level != null && !level.isClientSide()) {
            setChanged();
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    // Stub method - in real TinyMultiblockLib this checks if this block entity is the center of a multiblock
    public boolean isCenter() {
        return true;
    }
}
