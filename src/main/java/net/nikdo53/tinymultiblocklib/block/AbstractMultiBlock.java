package net.nikdo53.tinymultiblocklib.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Ported multiblock implementation for 1.21.11
 * Implements actual multiblock placement logic
 */
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
        Level level = context.getLevel();
        BlockPos center = context.getClickedPos();
        BlockState baseState = defaultBlockState();

        // Get all positions for the multiblock
        List<BlockPos> positions = makeFullBlockShape(level, center, baseState, null, direction);

        // Check if all positions are replaceable
        for (BlockPos pos : positions) {
            if (!level.getBlockState(pos).canBeReplaced(context)) {
                return null; // Can't place - something is in the way
            }
        }

        // Place all blocks
        boolean isFirst = true;
        for (BlockPos pos : positions) {
            BlockPos offset = pos.subtract(center);
            BlockState stateForPos = getStateForEachBlock(baseState, pos, offset, level, direction);

            if (isFirst) {
                isFirst = false;
                // The first block (center) will be placed by the normal placement system
                baseState = stateForPos;
            } else {
                // Place the other blocks directly
                level.setBlock(pos, stateForPos, 3);
            }
        }

        return baseState;
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

    // Track positions being broken to prevent infinite recursion
    private static final Set<BlockPos> currentlyBreaking = new HashSet<>();

    /**
     * Called when a block is removed. Breaks all other parts of the multiblock.
     */
    @Override
    protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel level, BlockPos pos, boolean movedByPiston) {
        // Prevent infinite recursion when breaking other parts
        if (currentlyBreaking.contains(pos)) {
            return;
        }

        try {
            currentlyBreaking.add(pos);

            // Find the center position
            BlockPos center = IMultiBlock.getCenter(level, pos);
            Direction direction = getDirection(state);

            // Get all positions for this multiblock
            List<BlockPos> positions = makeFullBlockShape(level, center, state, null, direction);

            // Break all other parts of the multiblock
            for (BlockPos partPos : positions) {
                if (!partPos.equals(pos) && !currentlyBreaking.contains(partPos)) {
                    BlockState partState = level.getBlockState(partPos);
                    if (partState.getBlock() == this) {
                        currentlyBreaking.add(partPos);
                        level.setBlock(partPos, Blocks.AIR.defaultBlockState(), 3);
                    }
                }
            }
        } finally {
            currentlyBreaking.remove(pos);
        }

        super.affectNeighborsAfterRemoval(state, level, pos, movedByPiston);
    }

    @Override
    @Nullable
    public abstract BlockEntity newBlockEntity(BlockPos pos, BlockState state);
}
