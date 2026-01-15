package net.nikdo53.tinymultiblocklib.block;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

// TODO: Re-enable when TinyMultiblockLib 1.21.11 is available
// Stub interface for compilation
public interface IPreviewableMultiblock {

    default BlockState getDefaultStateForPreviews(Direction direction) {
        return null;
    }
}
