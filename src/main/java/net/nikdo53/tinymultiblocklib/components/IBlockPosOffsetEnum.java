package net.nikdo53.tinymultiblocklib.components;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

import java.util.function.Function;

/**
 * Interface for multiblock part enums that define position offsets
 */
public interface IBlockPosOffsetEnum {

    Function<BlockPos, BlockPos> getOffsetFunction();

    /**
     * Find the enum value that corresponds to the given offset from center
     * @param clazz The enum class
     * @param offset The offset from center (pos.subtract(center))
     * @param direction The facing direction of the multiblock
     * @param defaultValue Default value if no match is found
     * @return The matching enum value
     */
    static <T extends Enum<T> & IBlockPosOffsetEnum> T fromOffset(Class<T> clazz, BlockPos offset, Direction direction, T defaultValue) {
        // Calculate expected offsets based on direction
        BlockPos sidewaysOffset = BlockPos.ZERO.relative(direction.getCounterClockWise());

        for (T value : clazz.getEnumConstants()) {
            String name = ((Enum<?>) value).name().toLowerCase();

            // Match based on the part name and expected offset
            boolean isTop = name.contains("top");
            boolean isRight = name.contains("right");

            int expectedY = isTop ? 1 : 0;
            int expectedX = isRight ? sidewaysOffset.getX() : 0;
            int expectedZ = isRight ? sidewaysOffset.getZ() : 0;

            if (offset.getX() == expectedX && offset.getY() == expectedY && offset.getZ() == expectedZ) {
                return value;
            }
        }

        return defaultValue;
    }
}
