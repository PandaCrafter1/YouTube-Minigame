package pw.betanyan.minigame.game;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import pw.betanyan.minigame.Minigame;

public class Timer extends BukkitRunnable {

    private Arena arena;

    private int lobbyTimeLeft;
    private int cornTimeLeft;
    private int gameTimeLeft;
    private int endTimeLeft;

    private boolean paused;

    public Timer(Arena arena) {

        this.arena = arena;

        this.lobbyTimeLeft = 60;
        this.cornTimeLeft = 15;
        this.gameTimeLeft = 300;
        this.endTimeLeft = 10;

        this.paused = true;

        this.runTaskTimer(Minigame.getPlugin(Minigame.class), 0, 20);

    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    @Override
    public void run() {

        if (!paused) {

            switch (arena.getState()) {

                case LOBBY:
                    if (lobbyTimeLeft != 0) {
                        lobbyTimeLeft--;
                    } else {

                        arena.setState(GameState.CORN);

                        for (int i=0; i<arena.getIngame().size(); i++) {
                            Bukkit.getPlayer(arena.getIngame().get(i))
                                    .teleport(arena.getSpawns().get(i));
                        }

                        arena.updateSign();
                        //TODO: Send players to spawn, start game, etc
                    }
                    arena.updateScoreboards();
                    break;
                case CORN:
                    if (cornTimeLeft != 0) {
                        cornTimeLeft--;
                    } else {
                        arena.setState(GameState.INGAME);
                        arena.updateSign();
                    }
                    arena.updateScoreboards();
                    break;
                case INGAME:
                    if (gameTimeLeft != 0) {
                        gameTimeLeft--;
                    } else {
                        arena.setState(GameState.END);
                        arena.updateSign();
                        //TODO: Send players to spawn, start game, etc
                    }
                    arena.updateScoreboards();
                    break;
                case END:
                    if (endTimeLeft != 0) {
                        endTimeLeft--;
                    } else {
                        arena.updateSign();
                        //TODO: End/Restart Game
                    }
                    arena.updateScoreboards();
                    break;
                default:
                    break;

            }

        }

    }

    public int getMinutes() {

        switch (arena.getState()) {

            case LOBBY:
                return lobbyTimeLeft / 60;
            case INGAME:
                return gameTimeLeft / 60;
            case END:
                return endTimeLeft / 60;
            default:
                return 0;

        }

    }

    public int getSeconds() {

        switch (arena.getState()) {

            case LOBBY:
                return lobbyTimeLeft % 60;
            case INGAME:
                return gameTimeLeft % 60;
            case END:
                return endTimeLeft % 60;
            default:
                return 0;

        }

    }

    public String formatTime() {

        int iminutes = getMinutes(), iseconds = getSeconds();
        String minutes = String.valueOf(getMinutes()), seconds = String.valueOf(getSeconds());

        if (iminutes < 10) {
            minutes = "0".concat(minutes);
        }

        if (iseconds < 10) {
            seconds = "0".concat(seconds);
        }

        return minutes + ":" + seconds;

    }

}
