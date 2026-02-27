package io.github.scuba10steve.s3.blockentity;

import io.github.scuba10steve.s3.gui.server.SecurityBoxMenu;
import io.github.scuba10steve.s3.platform.S3Platform;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Block entity for the Security Box.
 * Manages a whitelist of players who can interact with the storage multiblock.
 */
public class SecurityBoxBlockEntity extends MultiblockBlockEntity implements MenuProvider {
    private static final Logger LOGGER = LoggerFactory.getLogger(SecurityBoxBlockEntity.class);

    private final List<SecurePlayer> allowedPlayers = new ArrayList<>();

    // Op override notification queue
    private ServerPlayer pendingOpNotification = null;

    public SecurityBoxBlockEntity(BlockPos pos, BlockState state) {
        super(S3Platform.getSecurityBoxBEType(), pos, state);
    }

    @Override
    public void tick() {
        super.tick();

        if (level != null && !level.isClientSide && pendingOpNotification != null) {
            sendOpNotification(pendingOpNotification);
            pendingOpNotification = null;
        }
    }

    /**
     * Player identification for security purposes.
     */
    public record SecurePlayer(UUID id, String name) {
        public SecurePlayer(Player player) {
            this(player.getUUID(), player.getName().getString());
        }
    }

    public List<SecurePlayer> getAllowedPlayers() {
        return allowedPlayers;
    }

    public int getAllowedPlayerCount() {
        return allowedPlayers.size();
    }

    /**
     * Checks if a player is allowed to interact with the secured multiblock.
     * Returns true if: the whitelist is empty (open system), the player is on the whitelist,
     * or the player has op override privileges.
     */
    public boolean isPlayerAllowed(Player player) {
        if (allowedPlayers.isEmpty()) {
            return true;
        }

        UUID playerId = player.getUUID();
        for (SecurePlayer sp : allowedPlayers) {
            if (sp.id.equals(playerId)) {
                return true;
            }
        }

        // Op override: creative mode + op level 2 + config enabled
        if (S3Platform.getConfig().isOpOverrideEnabled()
                && !player.level().isClientSide
                && player.isCreative()
                && player instanceof ServerPlayer serverPlayer
                && serverPlayer.hasPermissions(2)) {
            pendingOpNotification = serverPlayer;
            return true;
        }

        return false;
    }

    /**
     * Checks if the player is the owner (first entry in the whitelist).
     */
    public boolean isOwner(Player player) {
        if (allowedPlayers.isEmpty()) return false;
        return allowedPlayers.getFirst().id.equals(player.getUUID());
    }

    public void addAllowedPlayer(Player player) {
        addAllowedPlayer(new SecurePlayer(player));
    }

    public void addAllowedPlayer(SecurePlayer player) {
        // Don't add duplicates
        for (SecurePlayer sp : allowedPlayers) {
            if (sp.id.equals(player.id)) return;
        }
        allowedPlayers.add(player);
        setChanged();
        syncToClients();
    }

    public void removeAllowedPlayer(UUID playerId) {
        allowedPlayers.removeIf(sp -> sp.id.equals(playerId));
        setChanged();
        syncToClients();
    }

    public void setAllowedPlayers(List<SecurePlayer> players) {
        allowedPlayers.clear();
        allowedPlayers.addAll(players);
    }

    private void sendOpNotification(ServerPlayer op) {
        if (allowedPlayers.isEmpty()) return;

        String ownerName = allowedPlayers.getFirst().name;
        // Notify the operator
        op.sendSystemMessage(Component.literal(
            "You have overridden the lockout to the storage system owned by " + ownerName + "."));

        // Notify all online allowed players
        if (level != null && level.getServer() != null) {
            String opName = op.getName().getString();
            for (SecurePlayer sp : allowedPlayers) {
                ServerPlayer target = level.getServer().getPlayerList().getPlayer(sp.id);
                if (target != null) {
                    target.sendSystemMessage(Component.literal(
                        "Operator " + opName + " has overridden your storage system lockout."));
                }
            }
        }

        LOGGER.info("Operator {} has overridden the lockout to the storage system owned by {}",
            op.getName().getString(), ownerName);
    }

    // NBT persistence
    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);

        tag.putInt("AllowedPlayerCount", allowedPlayers.size());
        for (int i = 0; i < allowedPlayers.size(); i++) {
            SecurePlayer sp = allowedPlayers.get(i);
            tag.putUUID("AllowedPlayer" + i + "_id", sp.id);
            tag.putString("AllowedPlayer" + i + "_name", sp.name);
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);

        allowedPlayers.clear();
        int count = tag.getInt("AllowedPlayerCount");
        for (int i = 0; i < count; i++) {
            if (tag.hasUUID("AllowedPlayer" + i + "_id")) {
                UUID id = tag.getUUID("AllowedPlayer" + i + "_id");
                String name = tag.getString("AllowedPlayer" + i + "_name");
                allowedPlayers.add(new SecurePlayer(id, name));
            }
        }
    }

    // Client sync
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag, registries);
        return tag;
    }

    public void syncToClients() {
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
        }
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.s3.security_box");
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new SecurityBoxMenu(containerId, playerInventory, this);
    }
}
