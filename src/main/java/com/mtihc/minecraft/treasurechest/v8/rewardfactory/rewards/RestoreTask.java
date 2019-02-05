package com.mtihc.minecraft.treasurechest.v8.rewardfactory.rewards;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.world.snapshot.InvalidSnapshotException;
import com.sk89q.worldedit.world.snapshot.Snapshot;
import com.sk89q.worldedit.world.snapshot.SnapshotRestore;
import com.sk89q.worldedit.world.storage.ChunkStore;
import com.sk89q.worldedit.world.storage.MissingWorldException;

import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

abstract class RestoreTask implements Runnable {
    private static JavaPlugin plugin;
    private long delay;
    private int subregionSize;
    private int taskId;
    private ChunkStore chunkStore;
    private String snapshotName;
    private String worldName;
    private Vector3 min;
    private Vector3 max;
    private RegionIterator iterator;

    public static World getLocalWorld(WorldEdit worldEdit, String worldName) {
        for (World world: plugin.getServer().getWorlds()) {
            if (world.getName().equalsIgnoreCase(worldName)) {
                return world;
            }
        }
        return null;
    }

    public static Vector3 getVector(Vector vec) {
        return Vector3.at(vec.getBlockX(), vec.getBlockY(), vec.getBlockZ());
    }

    //private RestoreRepository repo;

    RestoreTask(JavaPlugin plugin, String snapshotName, String worldName, Vector3 min, Vector3 max, long subregionTicks, int subregionSize) {
        this.plugin = plugin;
        this.snapshotName = snapshotName;
        this.worldName = worldName;
        this.min = min;
        this.max = max;
        this.delay = Math.max(delay, 2);
        this.subregionSize = Math.max(subregionSize, 10);
        this.taskId = -1;
    }

    public abstract String getId();

    public boolean isRunning() {
        return taskId != -1;
    }

    public void start() {
        if (!isRunning()) {
            onStart();

            iterator = new RegionIterator(min, max, subregionSize);

            // toggle isRunning
            taskId = 0;


            Snapshot snapshot;
            if (snapshotName != null) {
                try {
                    snapshot = WorldEdit.getInstance().getConfiguration().snapshotRepo.getSnapshot(snapshotName);
                } catch (InvalidSnapshotException e) {
                    snapshot = null;
                }
            } else {
                try {
                    snapshot = WorldEdit.getInstance().getConfiguration().snapshotRepo.getDefaultSnapshot(worldName);
                } catch (MissingWorldException e) {
                    snapshot = null;
                }
            }

            if (snapshot != null) {
                try {
                    this.chunkStore = snapshot.getChunkStore();
                } catch (Exception e) {
                    this.chunkStore = null;
                }
            } else {
                this.chunkStore = null;
            }

            if (this.chunkStore != null) {
                BukkitTask task = plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, this, delay, delay);
                taskId = task.getTaskId();
            } else {
                run();
            }

        }
    }

    public void cancel() {
        if (stop()) {
            onCancel();
        }
    }

    private boolean stop() {
        if (isRunning()) {
            plugin.getServer().getScheduler().cancelTask(taskId);
            taskId = -1;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public synchronized void run() {
        if (chunkStore != null) {

            CuboidRegion region = iterator.next();
            if (region == null) {
                chunkStore = null;
                return;
            } else {
                region.setWorld(BukkitAdapter.adapt(getLocalWorld(WorldEdit.getInstance(), worldName)));
            }
            restoreRegionInstantly(chunkStore, region);
        } else {
            if (isRunning()) {
                stop();
            }
            onFinish();
        }
    }

    protected abstract void onStart();

    protected abstract void onCancel();

    protected abstract void onFinish();

    /**
     * Restore a region
     *
     * @param chunkStore The <code>ChunkStore</code>, usually retrieved from a
     *                   <code>Snapshot</code>
     * @param region     The region to restore
     */
    public static void restoreRegionInstantly(ChunkStore chunkStore, Region region) {
        try {
            EditSession session = WorldEdit.getInstance().getEditSessionFactory().getEditSession(region.getWorld(), -1);
            SnapshotRestore restore = new SnapshotRestore(chunkStore, session, region);
            restore.restore();
        } catch (NullPointerException e) {

        } catch (MaxChangedBlocksException e) {

        }
    }

}