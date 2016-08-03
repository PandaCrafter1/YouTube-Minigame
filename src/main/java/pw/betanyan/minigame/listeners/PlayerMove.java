package pw.betanyan.minigame.listeners;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import pw.betanyan.minigame.Minigame;
import pw.betanyan.minigame.game.Arena;
import pw.betanyan.minigame.game.GameState;

public class PlayerMove implements Listener {

    @EventHandler
    public void onMove(PlayerMoveEvent event) {

        Location to = event.getTo();
        Location from = event.getFrom();

        Arena arena = Minigame.getInstance().getArenaManager().getArenaByPlayer(event.getPlayer().getName());

        if (arena != null && arena.getState() == GameState.CORN) {
            if (to.getX() != from.getX() || to.getZ() != from.getZ()) {

                event.getPlayer().teleport(from);

            }
        }

    }

}
