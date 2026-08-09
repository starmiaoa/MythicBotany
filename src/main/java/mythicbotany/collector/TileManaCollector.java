package mythicbotany.collector;

import mythicbotany.base.BlockEntityMana;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.NeoForge;
import org.moddingx.libx.base.tile.TickingBlock;
import vazkii.botania.api.internal.ManaBurst;
import vazkii.botania.api.mana.ManaBlockType;
import vazkii.botania.api.mana.ManaCollector;
import vazkii.botania.api.mana.ManaNetworkAction;
import vazkii.botania.api.neoforge.mana.ManaNetworkEvent;
import vazkii.botania.common.handler.ManaNetworkHandler;

public class TileManaCollector extends BlockEntityMana implements ManaCollector, TickingBlock {

    public TileManaCollector(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state, 10000, true, true);
    }

    @Override
    protected boolean canReceive() {
        return false; // This is just for sparks. We don't want mana from sparks.
    }

    @Override
    public void onClientDisplayTick() {
        //
    }

    @Override
    public float getManaYieldMultiplier(ManaBurst iManaBurst) {
        return 1;
    }

    @Override
    public int getMaxMana() {
        return this.maxMana;
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        if (this.level != null && this.level.isClientSide()) {
            NeoForge.EVENT_BUS.post(new ManaNetworkEvent(this, ManaBlockType.COLLECTOR, ManaNetworkAction.REMOVE));
        }
    }

    @Override
    public void onChunkUnloaded() {
        super.onChunkUnloaded();
        if (this.level != null && this.level.isClientSide()) {
            NeoForge.EVENT_BUS.post(new ManaNetworkEvent(this, ManaBlockType.COLLECTOR, ManaNetworkAction.REMOVE));
        }
    }

    @Override
    public void tick() {
        if (this.level != null && this.level.isClientSide()) {
            boolean inNetwork = ManaNetworkHandler.instance.isCollectorIn(this.level, this);
            if (!inNetwork && !this.isRemoved()) {
                NeoForge.EVENT_BUS.post(new ManaNetworkEvent(this, ManaBlockType.COLLECTOR, ManaNetworkAction.ADD));
            }
        }
    }
}
