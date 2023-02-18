package screret.screenjs.kubejs;

import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.KubeJSRegistries;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.IContainerFactory;
import screret.screenjs.client.EntityContainerScreen;
import screret.screenjs.common.EntityContainerMenu;

public class EntityMenuType extends MenuType<EntityContainerMenu> {
    private final ResourceLocation id;

    public EntityMenuType(EntityMenuType.Builder builder) {
        super((IContainerFactory<EntityContainerMenu>)((pContainerId, inventory, extraData) -> {
            int entityId = extraData.readVarInt();
            Level world = inventory.player.getLevel();
            Entity entity = world.getEntity(entityId);
            return new EntityContainerMenu(builder, pContainerId, inventory, entity);
        }));
        this.id = builder.id;
    }

    public static class Builder extends MenuTypeBuilder<EntityContainerMenu> {
        public transient EntityType<?> openingEntity;


        public Builder(ResourceLocation i) {
            super(i);
        }

        @Override
        public ScreenConstructor getScreenConstructor() {
            return EntityContainerScreen::new;
        }

        public EntityMenuType.Builder setEntity(ResourceLocation entity) {
            this.openingEntity = KubeJSRegistries.entityTypes().get(entity);
            return this;
        }

        @Override
        public EntityMenuType createObject() {
            return new EntityMenuType(this);
        }
    }
}
