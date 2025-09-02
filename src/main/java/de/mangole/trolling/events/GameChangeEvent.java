package de.mangole.trolling.events;

import de.mangole.trolling.GameManager;
import de.mangole.trolling.GameMode;
import de.mangole.trolling.GameStatus;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class GameChangeEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();
    private final GameMode oldMode;
    private final GameMode newMode;
    private final GameStatus oldStatus;
    private final GameStatus newStatus;

    public GameChangeEvent(GameMode oldMode, GameMode newMode, GameStatus oldStatus, GameStatus newStatus) {
        this.oldMode = oldMode;
        this.newMode = newMode;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
    }

    public GameMode getOldMode() {
        return oldMode;
    }

    public GameMode getNewMode() {
        return newMode;
    }

    public GameStatus getOldStatus() {
        return oldStatus;
    }

    public GameStatus getNewStatus() {
        return newStatus;
    }

    @Override
    public  @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

}
