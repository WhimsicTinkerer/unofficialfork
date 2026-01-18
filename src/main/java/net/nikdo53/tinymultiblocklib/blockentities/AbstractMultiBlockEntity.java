package net.nikdo53.tinymultiblocklib.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.nikdo53.tinymultiblocklib.block.IMultiBlock;

/**
 * Base block entity class for multiblock structures
 */
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

    /**
     * Check if this block entity is at the center position of the multiblock
     * @return true if this is the center block entity
     */
    public boolean isCenter() {
        if (level == null) return true;
        BlockPos center = IMultiBlock.getCenter(level, worldPosition);
        return center.equals(worldPosition);
    }
}
