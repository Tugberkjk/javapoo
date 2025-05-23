/*
 * Copyright (c) 2020. Laurent Réveillère
 */

package fr.ubx.poo.ubgarden.game.go.personage;

import fr.ubx.poo.ubgarden.game.Direction;
import fr.ubx.poo.ubgarden.game.Game;
import fr.ubx.poo.ubgarden.game.Position;
import fr.ubx.poo.ubgarden.game.go.GameObject;
import fr.ubx.poo.ubgarden.game.go.Movable;
import fr.ubx.poo.ubgarden.game.go.PickupVisitor;
import fr.ubx.poo.ubgarden.game.go.WalkVisitor;
import fr.ubx.poo.ubgarden.game.go.decor.Decor;
import fr.ubx.poo.ubgarden.game.go.decor.Land;
import fr.ubx.poo.ubgarden.game.go.bonus.EnergyBoost;
import fr.ubx.poo.ubgarden.game.go.bonus.PoisonedApple;
import fr.ubx.poo.ubgarden.game.go.bonus.Bomb;
import fr.ubx.poo.ubgarden.game.go.decor.DoorNextOpened;
import java.util.List;
import java.util.ArrayList;
import fr.ubx.poo.ubgarden.game.go.decor.DoorPrevOpened;

public class Gardener extends GameObject implements Movable, PickupVisitor, WalkVisitor {

    private int energy;
    private Direction direction;
    private boolean moveRequested = false;
    private long lastMoveTime = 0;
    private long lastEnergyGainTime = 0;
    private int fatigueLevel = 1;
    private int bombCount = 0;
    private boolean diseased = false;
    private long diseaseEndTimeNs = 0;  // nanoseconds cinsinden, game loop now ile eşleşsin
    private List<Long> diseaseEndTimes = new ArrayList<>();



    public int getFatigueLevel() {
        return fatigueLevel;
    }
    public void setFatigueLevel(int level) {
        this.fatigueLevel = level;
    }

    public int getBombCount() {
        return bombCount;
    }
    public void addBomb() {
        bombCount++;
    }

    public void increaseFatigue() {
        fatigueLevel++;
    }

    @Override

    public Position getPosition() {
        return super.getPosition();
    }

    @Override
    public void setPosition(Position position) {
        super.setPosition(position);
    }
    public Gardener(Game game, Position position) {

        super(game, position);
        this.direction = Direction.DOWN;
        this.energy = game.configuration().gardenerEnergy();
    }

    public void pickUp(EnergyBoost energyBoost) {
        int boost = game.configuration().energyBoost();
        int maxEnergy = game.configuration().gardenerEnergy();
        int newEnergy = energy + boost;

        if (newEnergy > maxEnergy) {
            newEnergy = maxEnergy;
        }

        setEnergy(newEnergy);
        fatigueLevel = 1;

        energyBoost.remove();
    }
    public void heal() {
        diseaseEndTimes.clear();
        diseased = false;
        fatigueLevel = 1;
    }

    public void pickUp(PoisonedApple poisonedApple) {
        long endTime = System.nanoTime() + game.configuration().diseaseDuration() * 1_000_000L;
        diseaseEndTimes.add(endTime);
        diseased = true;
        increaseFatigue();
        poisonedApple.remove();
    }


    public boolean isDiseased() { return diseased; }

    public void setDiseased(boolean diseased) { this.diseased = diseased; }

    public void pickUp(Bomb bomb) {
        addBomb();
        bomb.remove();
    }


    @Override
    public void setEnergy(int energy) {
        this.energy = energy;
    }

    public int getEnergy() {
        return this.energy;
    }


    public void requestMove(Direction direction) {
        if (direction != this.direction) {
            this.direction = direction;
            setModified(true);
        }
        moveRequested = true;
    }

    @Override
    public final boolean canMove(Direction direction) {
        Position nextPos = direction.nextPosition(getPosition());
        if (!game.world().getGrid().inside(nextPos)) {
            return false;
        }
        Decor decor = game.world().getGrid().get(nextPos);

        if (decor == null) {
            return true;
        }

        return decor.walkableBy(this);
    }


    public Position move(Direction direction) {
        Position nextPos = direction.nextPosition(getPosition());
        if (!canMove(direction)) {
            return getPosition();
        }
        setPosition(nextPos);

        Decor decor = game.world().getGrid().get(nextPos);
        if (decor != null) {
            decor.pickUpBy(this);
            if (decor.getBonus() != null) {
                decor.getBonus().pickUpBy(this);
            }
        }
        if (decor instanceof Land) {
            setEnergy(getEnergy() - 2 * getFatigueLevel());
        } else {
            setEnergy(getEnergy() - 1 * getFatigueLevel());
        }
        if (decor instanceof DoorNextOpened) {
            game.setLastDoorEntered(getPosition());
            game.requestSwitchLevel(getPosition().level() + 1);
        }
        if (decor instanceof DoorPrevOpened) {
            game.setLastDoorEntered(getPosition());
            game.requestSwitchLevel(getPosition().level()-1);
        }
        return getPosition();
    }
    public void setBombCount(int count) {
        this.bombCount = count;
    }

    public void update(long now) {
        if (moveRequested) {
            if (canMove(direction)) {
                move(direction);
                lastMoveTime = now;
            }
            moveRequested = false;
        } else {
            long recoverDelay = game.configuration().energyRecoverDuration();
            long elapsedMs = (now - lastEnergyGainTime) / 1_000_000;
            if (elapsedMs >= recoverDelay && getEnergy() < game.configuration().gardenerEnergy()) {
                setEnergy(getEnergy() + 1);
                lastEnergyGainTime = now;
            }
        }
        if (!diseaseEndTimes.isEmpty()) {
            diseaseEndTimes.removeIf(endTime -> now > endTime);

            if (diseaseEndTimes.isEmpty()) {
                diseased = false;
                fatigueLevel = 1;
            } else {
                diseased = true;
                fatigueLevel = 1 + diseaseEndTimes.size();
            }
        }
    }

    public void hurt(int damage) {
        setEnergy(getEnergy() - damage);
    }

    public void hurt() {
        hurt(1);
    }

    public Direction getDirection() {
        return direction;
    }


}
