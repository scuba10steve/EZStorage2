package io.github.scuba10steve.s3.network;

import io.github.scuba10steve.s3.blockentity.ExtractPortBlockEntity;
import io.github.scuba10steve.s3.blockentity.SecurityBoxBlockEntity;
import io.github.scuba10steve.s3.blockentity.SecurityBoxBlockEntity.SecurePlayer;
import io.github.scuba10steve.s3.blockentity.StorageCoreBlockEntity;
import io.github.scuba10steve.s3.gui.server.StorageCoreCraftingMenu;
import io.github.scuba10steve.s3.platform.S3Platform;
import io.github.scuba10steve.s3.storage.StorageInventory;
import io.github.scuba10steve.s3.storage.StoredItemStack;
import io.github.scuba10steve.s3.util.SortMode;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.List;

/**
 * Server-side packet handlers. These use NeoForge's IPayloadContext,
 * so they live in the neoforge module.
 * <p>
 * Client-side handlers are in {@link ClientPacketHandlers} to avoid
 * loading {@code Minecraft} on dedicated servers.
 */
public final class PacketHandlers {
    private PacketHandlers() {}

    public static void handleStorageClick(StorageClickPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            if (player.level().getBlockEntity(packet.pos()) instanceof StorageCoreBlockEntity core) {
                StorageInventory inventory = core.getInventory();
                if (inventory != null) {
                    ItemStack heldStack = player.containerMenu.getCarried();

                    if (packet.slot() == -1) {
                        if (!heldStack.isEmpty()) {
                            ItemStack remainder = inventory.insertItem(heldStack);
                            player.containerMenu.setCarried(remainder);
                        }
                    } else {
                        List<StoredItemStack> items = inventory.getStoredItems();
                        if (packet.slot() >= 0 && packet.slot() < items.size()) {
                            StoredItemStack stored = items.get(packet.slot());

                            if (heldStack.isEmpty()) {
                                int maxStack = stored.getItemStack().getMaxStackSize();
                                int amount;

                                if (packet.shift()) {
                                    amount = maxStack;
                                    ItemStack extracted = inventory.extractItem(stored.getItemStack(), amount);
                                    if (!extracted.isEmpty()) {
                                        if (!player.getInventory().add(extracted)) {
                                            inventory.insertItem(extracted);
                                        }
                                    }
                                } else {
                                    if (packet.button() == 0) {
                                        amount = maxStack;
                                    } else {
                                        amount = maxStack / 2;
                                        if (amount == 0) amount = 1;
                                    }
                                    ItemStack extracted = inventory.extractItem(stored.getItemStack(), amount);
                                    player.containerMenu.setCarried(extracted);
                                }
                            } else {
                                ItemStack remainder = inventory.insertItem(heldStack);
                                player.containerMenu.setCarried(remainder);
                            }
                        }
                    }

                    core.setChanged();
                    if (player.level() instanceof ServerLevel serverLevel) {
                        S3Platform.getNetworkHelper().sendToPlayersTrackingChunk(
                            serverLevel, packet.pos(),
                            new StorageSyncPacket(packet.pos(), inventory.getStoredItems(),
                                inventory.getMaxItems(), core.hasSearchBox(), core.hasSortBox(),
                                core.getSortMode().ordinal())
                        );
                    }
                }
            }
        });
    }

    public static void handleSortMode(SortModePacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            if (player.level().getBlockEntity(packet.pos()) instanceof StorageCoreBlockEntity core) {
                SortMode currentMode = core.getSortMode();
                SortMode newMode = currentMode.rotateMode();
                core.setSortMode(newMode);
                core.setChanged();

                if (player.level() instanceof ServerLevel serverLevel) {
                    S3Platform.getNetworkHelper().sendToPlayersTrackingChunk(
                        serverLevel, packet.pos(),
                        new StorageSyncPacket(packet.pos(), core.getInventory().getStoredItems(),
                            core.getInventory().getMaxItems(), core.hasSearchBox(), core.hasSortBox(),
                            newMode.ordinal())
                    );
                }
            }
        });
    }

    public static void handleExtractPortConfig(ExtractPortConfigPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            if (player.level().getBlockEntity(packet.pos()) instanceof ExtractPortBlockEntity extractPort) {
                if (packet.cycleMode()) {
                    extractPort.cycleListMode();
                } else {
                    extractPort.setRoundRobin(packet.roundRobin());
                }
            }
        });
    }

    public static void handleSecurityPlayer(SecurityPlayerPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            if (player.level().getBlockEntity(packet.pos()) instanceof SecurityBoxBlockEntity securityBox) {
                if (!securityBox.isPlayerAllowed(player)) return;

                if (packet.add()) {
                    securityBox.addAllowedPlayer(new SecurePlayer(packet.playerId(), packet.playerName()));
                } else {
                    securityBox.removeAllowedPlayer(packet.playerId());
                }
            }
        });
    }

    public static void handleRecipeTransfer(RecipeTransferPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            if (!(player.containerMenu instanceof StorageCoreCraftingMenu menu)) {
                return;
            }

            menu.handleRecipeTransfer(packet.items());

            if (player.level() instanceof ServerLevel serverLevel) {
                if (serverLevel.getBlockEntity(menu.getPos()) instanceof StorageCoreBlockEntity core) {
                    StorageInventory inventory = core.getInventory();
                    if (inventory != null) {
                        core.setChanged();
                        S3Platform.getNetworkHelper().sendToPlayersTrackingChunk(
                            serverLevel, menu.getPos(),
                            new StorageSyncPacket(menu.getPos(), inventory.getStoredItems(),
                                inventory.getMaxItems(), core.hasSearchBox(), core.hasSortBox(),
                                core.getSortMode().ordinal())
                        );
                    }
                }
            }
        });
    }
}
