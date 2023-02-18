package screret.screenjs.packets;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import screret.screenjs.ScreenJS;

import java.util.function.Supplier;

public class C2SRequestCapabilities {

    public C2SRequestCapabilities() {

    }

    public C2SRequestCapabilities(FriendlyByteBuf buf) {

    }

    public void encode(FriendlyByteBuf buf) {

    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            var menu = ctx.getSender().containerMenu;
            menu.broadcastChanges();
        });
        ctx.setPacketHandled(true);
    }
}
