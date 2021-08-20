package fun.fifu.nekoplayer;

import cn.hutool.core.io.FileUtil;
import cn.hutool.json.JSONObject;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class NekoPlayer extends JavaPlugin implements CommandExecutor {
    static NekoPlayer nekoPlayer;

    enum DrawMod {
        Block, Entity
    }

    public static int[] skewing = {0, 47, 0};
    Set<BukkitTask> tempTask = new HashSet<>();

    @Override
    public void onLoad() {
        nekoPlayer = this;
    }

    @Override
    public void onEnable() {
        Bukkit.getPluginCommand("neko_play").setExecutor(this);
        saveResource("config.json", false);
        try {
            for (int i = 1; i <= 1745; i++) {
                FileUtil.writeFromStream(MyResourceUtil.getStream("bad-apple/" + i + ".txt"), new File("plugins/NekoPlayer/bad-apple/" + i + ".txt"), true);
            }
            for (int i = 1; i <= 2193; i++) {
                FileUtil.writeFromStream(MyResourceUtil.getStream("bad-apple-mini/pic_" + i + ".jpg.txt"), new File("plugins/NekoPlayer/bad-apple-mini/pic_" + i + ".jpg.txt"), true);
            }
        } catch (Exception ignored) {
        }

        getLogger().info("NekoPlayer is on Enable!!!");
    }

    @Override
    public void onDisable() {
        tempTask.forEach(BukkitTask::cancel);
        getLogger().info("Player is closed");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        World world;
        if (sender instanceof Player) {
            world = ((Player) sender).getWorld();
        } else {
            world = Bukkit.getWorld("world");
        }

        String[] testFrame = FileUtil.readString(Configer.getFrameName("bad-apple", 568), StandardCharsets.UTF_8).split("\n");
        JSONObject jsonObject = Configer.readConfig();

        getLogger().info("debug_args=" + Arrays.toString(args));

        if (args.length > 0) {
            if ("entity_play".equals(args[0])) {
                Character blackBit = Configer.getBlackBit(args[1]);
                Character whiteBit = Configer.getWhiteBit(args[1]);
                play(args[1], world, DrawMod.Entity, blackBit, whiteBit);
            } else if ("play".equals(args[0])) {
                Character blackBit = Configer.getBlackBit(args[1]);
                Character whiteBit = Configer.getWhiteBit(args[1]);
                play(args[1], world, DrawMod.Block, blackBit, whiteBit);
            } else if ("playXY".equals(args[0])) {
                Character blackBit = Configer.getBlackBit(args[1]);
                Character whiteBit = Configer.getWhiteBit(args[1]);
                playXY(args[1], world, blackBit, whiteBit);
            } else if ("clear".equals(args[0])) {
                onDisable();
                draw(testFrame, world, Material.AIR, Material.AIR, '1', ' ');
            } else if ("draw".equals(args[0])) {
                Character blackBit = Configer.getBlackBit(args[1]);
                Character whiteBit = Configer.getWhiteBit(args[1]);
                draw(FileUtil.readString(Configer.getFrameName(args[1], Integer.parseInt(args[2])), StandardCharsets.UTF_8).split("\n"), world, Material.BLACK_CONCRETE, Material.WHITE_CONCRETE, blackBit, whiteBit);
            } else if ("drawXY".equals(args[0])) {
                Character blackBit = Configer.getBlackBit(args[1]);
                Character whiteBit = Configer.getWhiteBit(args[1]);
                drawXY(FileUtil.readString(Configer.getFrameName(args[1], Integer.parseInt(args[2])), StandardCharsets.UTF_8).split("\n"), world, Material.BLACK_CONCRETE, Material.WHITE_CONCRETE, blackBit, whiteBit);
            } else if ("draw_sheep".equals(args[0])) {
                Character blackBit = Configer.getBlackBit("bad-apple");
                Character whiteBit = Configer.getBlackBit("bad-apple");
                entity_draw(testFrame, world, blackBit, whiteBit);
            } else if ("skewing".equals(args[0]) && args.length == 4) {
                skewing[0] = Integer.parseInt(args[1]);
                skewing[1] = Integer.parseInt(args[2]);
                skewing[2] = Integer.parseInt(args[3]);
                sender.sendMessage("ok,skewing is set to " + Arrays.toString(skewing));
            }
        } else {
            sender.sendMessage("帮助");
            sender.sendMessage("/neko_play play <animation> 播放animation");
            sender.sendMessage("/neko_play draw <animation> <frame> 绘制单帧");
            sender.sendMessage("/neko_play playXY <animation> 在XY平面播放animation");
            sender.sendMessage("/neko_play drawXY <animation> <frame> 在XY平面绘制单帧");
            sender.sendMessage("/neko_play draw_sheep 绘制实体画布 (预览版本");
            sender.sendMessage("/neko_play entity_play <animation> 用实体的方式播放animation (预览版本");
            sender.sendMessage("/neko_play clear 清空画布");
            sender.sendMessage("/neko_play skewing <x> <y> <z> 设置原点偏移，默认为{0,47,0}，当前为" + Arrays.toString(skewing));
            sender.sendMessage("现在可播放的动画有：");
            jsonObject.forEach((k, v) -> {
                sender.sendMessage(k);
            });
        }
        getLogger().info("ok");
        return true;
    }


    private void play(String animation, World world, DrawMod drawMod, Character BlackBit, Character WhiteBit) {
        if (animation == null || animation.equals(""))
            return;

        List<String> frames = new ArrayList<>();
        getLogger().info("loading...");

        JSONObject jsonObject = Configer.readConfig().getJSONObject(animation);
        Integer frame = jsonObject.getInt("frames");
        for (int i = 1; i <= frame; i++) {
            frames.add(FileUtil.readString(Configer.getFrameName(animation, i), StandardCharsets.UTF_8));
        }
        getLogger().info("load over");

        BukkitTask temp = new BukkitRunnable() {
            int p = 0;

            @Override
            public void run() {
                if (p >= frame) {
                    this.cancel();
                    draw(frames.get(0).split("\n"), world, Material.AIR, Material.AIR, BlackBit, WhiteBit);
                    return;
                }
                if (drawMod == DrawMod.Block) {
                    draw(frames.get(p).split("\n"), world, Material.BLACK_CONCRETE, Material.WHITE_CONCRETE, BlackBit, WhiteBit);
                } else if (drawMod == DrawMod.Entity) {
                    entity_draw(frames.get(p).split("\n"), world, BlackBit, WhiteBit);
                }

                p++;
            }
        }.runTaskTimer(this, 0L, 2L);

        getLogger().info("Task push over");
    }

    private void playXY(String animation, World world, Character BlackBit, Character WhiteBit) {
        if (animation == null || animation.equals(""))
            return;

        List<String> frames = new ArrayList<>();
        getLogger().info("loading...");

        JSONObject jsonObject = Configer.readConfig().getJSONObject(animation);
        Integer frame = jsonObject.getInt("frames");
        for (int i = 1; i <= frame; i++) {
            frames.add(FileUtil.readString(Configer.getFrameName(animation, i), StandardCharsets.UTF_8));
        }
        getLogger().info("load over");

        BukkitTask temp = new BukkitRunnable() {
            int p = 0;

            @Override
            public void run() {
                if (p >= frame) {
                    this.cancel();
                    drawXY(frames.get(0).split("\n"), world, Material.AIR, Material.AIR, BlackBit, WhiteBit);
                    return;
                }
                drawXY(frames.get(p).split("\n"), world, Material.BLACK_CONCRETE, Material.WHITE_CONCRETE, BlackBit, WhiteBit);
                p++;
            }
        }.runTaskTimer(this, 0L, 2L);

        getLogger().info("Task push over");
    }

    private void entity_draw(String[] frames, World world, Character BlackBit, Character WhiteBit) {
        for (int i = 0; i < frames.length; i++) {
            char[] y = frames[i].toCharArray();
            for (int j = 0; j < y.length; j++) {
                if (BlackBit.equals(y[j])) {
                    Entity hitEntity = world.rayTraceEntities(new Location(world, i + skewing[0] + 0.0, skewing[1] + 0.0, frames.length - j + skewing[2] + 0.0), new Vector(1, 1, 1), 1).getHitEntity();
                    if (hitEntity instanceof Sheep)
                        ((Sheep) hitEntity).setColor(DyeColor.BLACK);
                } else if (WhiteBit.equals(y[j])) {
                    Entity hit = world.rayTraceEntities(new Location(world, i + skewing[0] + 0.0, skewing[1] + 0.0, frames.length - j + skewing[2] + 0.0), new Vector(1, 1, 1), 1).getHitEntity();
                    if (hit instanceof Sheep)
                        ((Sheep) hit).setColor(DyeColor.WHITE);
                }
            }
        }
    }

    public static void draw(String[] frames, World world, Material Black, Material White, Character BlackBit, Character WhiteBit) {
        for (int i = 0; i < frames.length; i++) {
            char[] y = frames[i].toCharArray();
            for (int j = 0; j < y.length; j++) {
                if (BlackBit.equals(y[j]))
                    world.getBlockAt(i + skewing[0], skewing[1], frames.length - j + skewing[2]).setType(Black);
                else if (WhiteBit.equals(y[j]))
                    world.getBlockAt(i + skewing[0], skewing[1], frames.length - j + skewing[2]).setType(White);
            }
        }
    }

    public static void drawXY(String[] frames, World world, Material Black, Material White, Character BlackBit, Character WhiteBit) {
        for (int i = 0; i < frames.length; i++) {
            char[] y = frames[i].toCharArray();
            for (int j = 0; j < y.length; j++) {
                if (BlackBit.equals(y[j]))
                    world.getBlockAt(j + skewing[0], frames.length - i + skewing[1], skewing[2]).setType(Black);
                else if (WhiteBit.equals(y[j]))
                    world.getBlockAt(j + skewing[0], frames.length - i + skewing[1], skewing[2]).setType(White);
            }
        }
    }
}