package me.petrolingus.zanudainfinityrails;

import com.google.gson.Gson;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.util.TriState;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Rail;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInputEvent;
import org.bukkit.event.vehicle.VehicleCreateEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.PluginLogger;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.logging.Logger;

public class ZanudaInfinityRailsPlugin extends JavaPlugin implements Listener {

    Logger logger = PluginLogger.getLogger("ZanudaInfinityRailsPlugin");

    private static final NamespacedKey LAST_VELOCITY_KEY = new NamespacedKey("namespace", "last_velocity");
    public static final Gson GSON = new Gson();

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
    }

    @SuppressWarnings("unused")
    @EventHandler
    public void onVehicleCreate(VehicleCreateEvent event) {

        // Check that created vehicle is actual minecart
        Entity vehicle = event.getVehicle();
        Minecart minecart = vehicle instanceof Minecart ? (Minecart) vehicle : null;
        if (minecart == null) return;

        World world = minecart.getWorld();
        BlockData blockData = world.getBlockAt(minecart.getLocation()).getBlockData();
        Rail rail = blockData instanceof Rail ? (Rail) blockData : null;

        if (rail != null && rail.getShape() == Rail.Shape.NORTH_SOUTH) {
            minecart.setRotation(-90, 0);
        }
    }

    @SuppressWarnings({"unused"})
    @EventHandler
    public void onVehicleMoveEvent(VehicleMoveEvent event) {

        if (!(event.getVehicle() instanceof Minecart minecart)) return;

        List<Entity> passengers = minecart.getPassengers();
        if (!passengers.isEmpty()) {
            if (!(passengers.getFirst() instanceof Player)) {
                return;
            }
        }

        // Get block under minecart
        BlockData blockData = minecart.getWorld().getBlockAt(minecart.getLocation()).getBlockData();
        Rail rail = blockData instanceof Rail ? (Rail) blockData : null;

        if (rail == null) return;

        Rail.Shape shape = rail.getShape();
        boolean isAscending = shape.equals(Rail.Shape.ASCENDING_EAST) || shape.equals(Rail.Shape.ASCENDING_WEST) ||
                shape.equals(Rail.Shape.ASCENDING_NORTH) || shape.equals(Rail.Shape.ASCENDING_SOUTH);

        if (isAscending) {
            String velocityJson = minecart.getPersistentDataContainer().get(LAST_VELOCITY_KEY, PersistentDataType.STRING);
            Vector velocity = GSON.fromJson(velocityJson, Vector.class);
            if (velocity != null) {
                minecart.setVelocity(velocity);
            }
        } else {
            Vector velocity = minecart.getVelocity();
            minecart.getPersistentDataContainer().set(LAST_VELOCITY_KEY, PersistentDataType.STRING, GSON.toJson(velocity));
        }
    }

    @SuppressWarnings({"unused"})
    @EventHandler
    public void onVehicleExitEvent(VehicleExitEvent event) {
        if (event.getVehicle() instanceof Minecart minecart) {
            minecart.setFrictionState(TriState.TRUE);
        }
    }

    @SuppressWarnings({"UnstableApiUsage", "unused"})
    @EventHandler
    public void onPlayerInputEvent(PlayerInputEvent event) {

        Entity vehicle = event.getPlayer().getVehicle();
        Minecart minecart = vehicle instanceof Minecart ? (Minecart) vehicle : null;
        if (minecart == null || !event.getInput().isForward()) return;

        // If minecart out of rails
        BlockData blockData = minecart.getWorld().getBlockAt(minecart.getLocation()).getBlockData();
        Rail rail = blockData instanceof Rail ? (Rail) blockData : null;
        if (rail == null) return;

        // Player direction
        Player player = event.getPlayer();
        float x = (float) -Math.sin(Math.toRadians(player.getYaw()));
        float z = (float) Math.cos(Math.toRadians(player.getYaw()));
        Vector playerDirection = new Vector(x, 0, z);

        // Minecart direction
        Vector minecartDirection = minecart.getFacing().getDirection();

        // Calculate angle between players' and minecart direction
        float angle = (float) Math.toDegrees(playerDirection.angle(minecartDirection));

        // Disable friction
        minecart.setFrictionState(TriState.FALSE);

        // 8 blocks per 20 ticks = 0.4 blocks per tick
        Vector velocity = minecartDirection.multiply((angle < 90) ? 0.4 : -0.4);
        minecart.setVelocity(velocity);
    }

}