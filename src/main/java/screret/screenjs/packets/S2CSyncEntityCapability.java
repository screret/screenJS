package screret.screenjs.packets;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class S2CSyncEntityCapability {
    private final int entityid;
    private final CompoundTag tag;

    public S2CSyncEntityCapability(int entityid, CompoundTag tag) {
        this.entityid = entityid;
        this.tag = tag;
    }

    public S2CSyncEntityCapability(FriendlyByteBuf byteBuf) {
        this.entityid = byteBuf.readVarInt();
        this.tag = byteBuf.readAnySizeNbt();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeVarInt(this.entityid);
        buf.writeNbt(tag);
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            var entity =  Minecraft.getInstance().level.getEntity(entityid);
            if(entity != null) {
                entity.load(this.tag);
            }
        });
        ctx.setPacketHandled(true);
    }
}
