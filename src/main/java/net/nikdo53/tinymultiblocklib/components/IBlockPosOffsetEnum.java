package net.nikdo53.tinymultiblocklib.components;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

import java.util.function.Function;

// TODO: Re-enable when TinyMultiblockLib 1.21.11 is available
// Stub interface for compilation
public interface IBlockPosOffsetEnum {

    Function<BlockPos, BlockPos> getOffsetFunction();

    static <T extends Enum<T> & IBlockPosOffsetEnum> T fromOffset(Class<T> clazz, BlockPos offset, Direction direction, T defaultValue) {
        // Stub implementation - just return default
        return defaultValue;
    }
}
