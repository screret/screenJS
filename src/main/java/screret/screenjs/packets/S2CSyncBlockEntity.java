package screret.screenjs.packets;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class S2CSyncBlockEntity {
    private final BlockPos pos;
    private final CompoundTag tag;

    public S2CSyncBlockEntity(BlockPos pos, CompoundTag tag) {
        this.pos = pos;
        this.tag = tag;
    }

    public S2CSyncBlockEntity(FriendlyByteBuf byteBuf) {
        this.pos = byteBuf.readBlockPos();
        this.tag = byteBuf.readAnySizeNbt();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeBlockPos(this.pos);
        buf.writeNbt(tag);
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            var be = Minecraft.getInstance().level.getBlockEntity(pos);
            if(be != null) {
                be.handleUpdateTag(this.tag);
            }
        });
        ctx.setPacketHandled(true);
    }
}
