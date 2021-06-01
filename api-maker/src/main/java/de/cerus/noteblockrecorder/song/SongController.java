package de.cerus.noteblockrecorder.song;

import de.cerus.noteblockrecorder.config.impl.MessageConfig;
import de.cerus.noteblockrecorder.config.impl.PluginConfig;
import de.cerus.noteblockrecorder.fileformat.SongFileFormat;
import de.cerus.noteblockrecorder.inventory.InventoryItem;
import de.cerus.noteblockrecorder.inventory.PaginatedInventory;
import de.cerus.noteblockrecorder.inventory.SongManageInventoryHolder;
import de.cerus.noteblockrecorder.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.permissions.Permissible;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class SongController {

    private final Random random = new Random();

    private JavaPlugin plugin;
    private PluginConfig pluginConfig;
    private MessageConfig messageConfig;
    private File songFolder;
    private SongIndexes songIndexes;
    private ExecutorService executorService;

    private Map<UUID, BukkitRunnable> playingSongs = new HashMap<>();

    public SongController(JavaPlugin plugin, PluginConfig pluginConfig, MessageConfig messageConfig, PluginConfig config, SongIndexes songIndexes) {
        this.plugin = plugin;
        this.pluginConfig = pluginConfig;
        this.messageConfig = messageConfig;
        this.songFolder = new File(plugin.getDataFolder(), config.songsFolder);
        this.songIndexes = songIndexes;
        this.executorService = Executors.newSingleThreadExecutor();

        boolean mkdirs = songFolder.mkdirs();
        if (!mkdirs) {
            System.err.println("Warning: Failed to create directories");
        }
    }

    /**
     * This method saves the specified {@link Song} async.
     *
     * @param song     The song that should be saved
     * @param callback The callback that gets called after the song was saved
     * @author Cerus
     * @since 1.0.0
     */
    public void saveSongAsync(Song song, Consumer<Void> callback) {
        executorService.submit(() -> {
            saveSong(song);
            callback.accept(null);
        });
    }

    /**
     * This method saves the specified {@link Song}. This method will block the thread.
     *
     * @param song The song that should be saved
     * @author Cerus
     * @since 1.0.0
     */
    public void saveSong(Song song) {
        String fileName = song.getName().toLowerCase().replaceAll("\\s+", "-")
                .replaceAll("[^A-Za-z0-9]", "") + ".nbrsong";
        File file = new File(songFolder, fileName);

        if (file.exists()) {
            return;
        }

        try {
            boolean b = file.getParentFile().mkdirs();
            if (!b) {
                System.err.println("Warning: Failed to create directories");
            }

            b = file.createNewFile();
            if (!b) {
                System.err.println("Warning: Failed to create file");
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        try (FileOutputStream fos = new FileOutputStream(file); DataOutputStream out = new DataOutputStream(fos)) {
            new SongFileFormat(song).writeToStream(out);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        songIndexes.reindexIfNeeded(songFolder);
    }

    /**
     * This method plays specified {@link Song} to the specified {@link Player}.
     *
     * @param song   The song that should be played
     * @param player The listener
     * @author Cerus
     * @since 1.0.0
     */
    public void playSong(Player player, Song song) {
        if (song == null) {
            return;
        }

        BukkitRunnable bukkitRunnable = song.toRunnable(player);
        bukkitRunnable.runTaskTimer(plugin, 0, 1);
        playingSongs.put(player.getUniqueId(), bukkitRunnable);
    }

    /**
     * This method stops the song that the specified {@link Player} is currently listening to.
     *
     * @param player The song listener
     * @author Cerus
     * @since 1.0.0
     */
    public void stopSong(Player player) {
        if (!isSongPlaying(player)) {
            return;
        }

        playingSongs.remove(player.getUniqueId()).cancel();
    }

    /**
     * This method returns whether the specified {@link Player} is currently listening to a song or not.
     *
     * @param player The {@link Player} that should be checked
     * @return Whether the specified {@link Player} is currently playing a song or not
     * @author Cerus
     * @since 1.0.0
     */
    public boolean isSongPlaying(Player player) {
        if (playingSongs.containsKey(player.getUniqueId())) {
            BukkitRunnable bukkitRunnable = playingSongs.get(player.getUniqueId());
            if (bukkitRunnable.isCancelled()) {
                playingSongs.remove(player.getUniqueId());
            }
        }
        return playingSongs.containsKey(player.getUniqueId());
    }

    /**
     * This method returns whether a song with the specified name exists or not.
     *
     * @param name The song name
     * @return Whether a song with the name exists or not
     * @author Cerus
     * @since 1.0.0
     */
    public boolean doesSongExist(String name) {
        return songIndexes.getSongFiles().getOrDefault(name.toUpperCase(), null) != null;
    }

    /**
     * This method loads the song with the specified name async.
     *
     * @param name     The name of the song
     * @param callback The callback that will be called when the song was loaded
     * @author Cerus
     * @since 1.0.0
     */
    public void loadSongAsync(String name, Consumer<Song> callback) {
        executorService.submit(() -> callback.accept(loadSong(name)));
    }

    /**
     * This method loads the song with the specified name. This method will block the current thread.
     *
     * @param name The name of the song
     * @return The loaded {@link Song}
     * @author Cerus
     * @since 1.0.0
     */
    public Song loadSong(String name) {
        File songFile = songIndexes.getSongFiles().getOrDefault(name.toUpperCase(), null);
        if (songFile != null) {
            return loadSong(songFile);
        }

        File file = new File(songFolder, name.toLowerCase().replaceAll("\\s+", "-")
                .replaceAll("[^A-Za-z0-9]", "") + ".nbrsong");
        if (!file.exists()) {
            return null;
        }

        return loadSong(file);
    }

    /**
     * This method loads the song at the specified location async.
     *
     * @param file The song file
     * @param callback The callback that will be called when the song was loaded
     * @author Cerus
     * @since 1.0.0
     */
    public void loadSongAsync(File file, Consumer<Song> callback) {
        executorService.submit(() -> callback.accept(loadSong(file)));
    }

    /**
     * This method loads the song at the specified location. This method will block the current thread.
     *
     * @param file The song file
     * @return The loaded {@link Song}
     * @author Cerus
     * @since 1.0.0
     */
    public Song loadSong(File file) {
        try (FileInputStream fis = new FileInputStream(file); DataInputStream in = new DataInputStream(fis)) {
            SongFileFormat songFileFormat = new SongFileFormat(in);
            return songFileFormat.getSong();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * This method deletes the specified {@link Song} async.
     *
     * @param song The song
     * @param callback The callback that will be called when the song was deleted
     * @author Cerus
     * @since 1.0.0
     */
    public void deleteSongAsync(Song song, Consumer<Void> callback) {
        executorService.submit(() -> {
            deleteSong(song);
            callback.accept(null);
        });
    }

    /**
     * This method deletes the specified {@link Song}. This method will block the current thread.
     *
     * @param song The song to delete
     * @author Cerus
     * @since 1.0.0
     */
    public void deleteSong(Song song) {
        File file = songIndexes.getSongFiles().get(song.getName().toUpperCase());
        if (file == null) {
            return;
        }

        boolean delete = file.delete();
        if (!delete) {
            System.err.println("Warning: Failed to delete file");
        }

        songIndexes.reindexIfNeeded(songFolder);
    }

    /**
     * This method will return a new song inventory.
     *
     * @param permissible The permissible to whom the inventory should be shown
     * @author Cerus
     * @since 1.0.0
     * @return The new inventory
     */
    public PaginatedInventory newSongsInventory(Permissible permissible) {
        return new PaginatedInventory(plugin, true, songIndexes.getSongNames().values().stream()
                .map(s -> {
                    String name = Bukkit.getOfflinePlayer(songIndexes.getSongs().get(s.toUpperCase())).getName();

                    random.setSeed(s.hashCode());
                    int rnd = (int) (random.nextFloat() * 10);
                    Material material = getMaterial(rnd);

                    return new InventoryItem(new ItemBuilder(material)
                            .withName("§a" + s)
                            .withLore(permissible.hasPermission(pluginConfig.permissionManageSongs)
                                    ? new String[]{(name == null ? "§7Unknown author" : "§d" + name), " ", "§7§oRight-click to manage this song"}
                                    : new String[]{(name == null ? "§7Unknown author" : "§d" + name)})
                            .build(), event -> {
                        Player player = (Player) event.getWhoClicked();
                        player.sendMessage(messageConfig.messagePleaseWait);

                        loadSongAsync(s, song -> {
                            if (event.getClick() == ClickType.RIGHT && player.hasPermission(pluginConfig.permissionManageSongs)) {
                                SongManageInventoryHolder holder = new SongManageInventoryHolder(song);
                                Inventory inventory = Bukkit.createInventory(holder, 9);
                                inventory.setItem(4, new ItemBuilder(Material.BARRIER).withName("§cDelete song").build());
                                holder.setInventory(inventory);

                                plugin.getServer().getScheduler().runTask(plugin, () -> {
                                    player.openInventory(inventory);
                                });
                                return;
                            }

                            if (isSongPlaying(player)) {
                                stopSong(player);
                            }

                            plugin.getServer().getScheduler().runTask(plugin, () -> {
                                player.closeInventory();
                                playSong(player, song);
                            });
                        });
                    });
                }).collect(Collectors.toList()));
    }

    /**
     * This method returns the amount of songs the specified {@link Player} has
     *
     * @param player The player
     * @author Cerus
     * @since 1.0.0
     * @return The amount of songs the specified {@link Player} has
     */
    public long getPlayerSongs(Player player) {
        return songIndexes.getSongs().values().stream()
                .filter(uuid -> uuid.equals(player.getUniqueId()))
                .count();
    }

    private Material getMaterial(int i) {
        Material material;
        switch (i) {
            case 0:
                material = Material.MUSIC_DISC_BLOCKS;
                break;
            case 1:
                material = Material.MUSIC_DISC_CAT;
                break;
            case 2:
                material = Material.MUSIC_DISC_CHIRP;
                break;
            case 3:
                material = Material.MUSIC_DISC_FAR;
                break;
            case 4:
                material = Material.MUSIC_DISC_MALL;
                break;
            case 5:
                material = Material.MUSIC_DISC_MELLOHI;
                break;
            case 6:
                material = Material.MUSIC_DISC_STAL;
                break;
            case 7:
                material = Material.MUSIC_DISC_STRAD;
                break;
            case 8:
                material = Material.MUSIC_DISC_WAIT;
                break;
            case 9:
                material = Material.MUSIC_DISC_WARD;
                break;
            case 10:
                material = Material.MUSIC_DISC_13;
                break;
            default:
                material = Material.MUSIC_DISC_11;
                break;
        }
        return material;
    }

}
