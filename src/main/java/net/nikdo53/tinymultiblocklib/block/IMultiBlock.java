package net.nikdo53.tinymultiblocklib.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.nikdo53.tinymultiblocklib.components.IBlockPosOffsetEnum;

/**
 * Interface for multiblock structures
 */
public interface IMultiBlock {

    /**
     * Get the center position of the multiblock from any part position
     * @param level The world/level
     * @param pos Any position within the multiblock
     * @return The center position where the main block entity resides
     */
    static BlockPos getCenter(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        Block block = state.getBlock();

        if (block instanceof AbstractMultiBlock multiBlock) {
            EnumProperty<Direction> directionProp = multiBlock.getDirectionProperty();

            // Find the part property by looking for an EnumProperty with IBlockPosOffsetEnum values
            for (var property : state.getProperties()) {
                if (property instanceof EnumProperty<?> enumProp) {
                    Object value = state.getValue(enumProp);
                    if (value instanceof IBlockPosOffsetEnum offsetEnum) {
                        // Get the facing direction
                        Direction facing = directionProp != null && state.hasProperty(directionProp)
                                ? state.getValue(directionProp)
                                : Direction.NORTH;

                        // Calculate offset back to center based on part name
                        String partName = ((Enum<?>) value).name().toLowerCase();
                        boolean isTop = partName.contains("top");
                        boolean isRight = partName.contains("right");

                        BlockPos center = pos;

                        // Move down if this is a top part
                        if (isTop) {
                            center = center.below();
                        }

                        // Move opposite of counter-clockwise if this is a right part
                        if (isRight) {
                            center = center.relative(facing.getClockWise());
                        }

                        return center;
                    }
                }
            }
        }

        return pos;
    }
}
