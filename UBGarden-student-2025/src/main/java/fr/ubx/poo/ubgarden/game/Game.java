package fr.ubx.poo.ubgarden.game;

import fr.ubx.poo.ubgarden.game.go.personage.Gardener;
import fr.ubx.poo.ubgarden.game.go.personage.Wasp;
import fr.ubx.poo.ubgarden.game.go.personage.Hornet;
import java.util.List;
import java.util.ArrayList;

public class Game {

    private final Configuration configuration;
    private final World world;
    private final Gardener gardener;
    private boolean switchLevelRequested = false;
    private int switchLevel;


    public Game(World world, Configuration configuration, Position gardenerPosition) {
        this.configuration = configuration;
        this.world = world;
        gardener = new Gardener(this, gardenerPosition);
    }

    public Configuration configuration() {
        return configuration;
    }

    public Gardener getGardener() {
        return this.gardener;
    }

    public World world() {
        return world;
    }

    public boolean isSwitchLevelRequested() {
        return switchLevelRequested;
    }

    public int getSwitchLevel() {
        return switchLevel;
    }

    public void requestSwitchLevel(int level) {
        this.switchLevel = level;
        switchLevelRequested = true;
    }
    private Position lastDoorEntered;

    public void setLastDoorEntered(Position pos) {
        this.lastDoorEntered = pos;
    }

    public Position getLastDoorEntered() {
        return this.lastDoorEntered;
    }

    public void clearSwitchLevel() {
        switchLevelRequested = false;
    }

}
