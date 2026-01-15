package net.nikdo53.tinymultiblocklib.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.List;

// TODO: Re-enable when TinyMultiblockLib 1.21.11 is available
// Stub class for compilation - tournament stand will have limited functionality
public abstract class AbstractMultiBlock extends Block implements EntityBlock {

    public AbstractMultiBlock(Properties properties) {
        super(properties);
    }

    public abstract List<BlockPos> makeFullBlockShape(Level level, BlockPos center, BlockState blockState, @Nullable BlockEntity blockEntity, @Nullable Direction direction);

    public abstract RenderShape getMultiblockRenderShape(BlockState state, boolean isCenter);

    @Nullable
    public abstract EnumProperty<Direction> getDirectionProperty();

    public abstract BlockState getStateForEachBlock(BlockState state, BlockPos pos, BlockPos centerOffset, Level level, @Nullable Direction direction);

    @Nullable
    public BlockState getStateForPlacementHelper(BlockPlaceContext context, Direction direction) {
        return defaultBlockState();
    }

    public Direction getDirection(BlockState state) {
        EnumProperty<Direction> prop = getDirectionProperty();
        if (prop != null && state.hasProperty(prop)) {
            return state.getValue(prop);
        }
        return Direction.NORTH;
    }

    public VoxelShape voxelShapeHelper(BlockState state, BlockGetter level, BlockPos pos, VoxelShape shape) {
        return shape != null ? shape : Block.box(0, 0, 0, 16, 16, 16);
    }

    @Override
    @Nullable
    public abstract BlockEntity newBlockEntity(BlockPos pos, BlockState state);
}
