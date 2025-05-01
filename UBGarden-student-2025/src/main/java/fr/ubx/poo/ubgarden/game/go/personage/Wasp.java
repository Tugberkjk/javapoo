package fr.ubx.poo.ubgarden.game.go.personage;

import fr.ubx.poo.ubgarden.game.Direction;
import fr.ubx.poo.ubgarden.game.Game;
import fr.ubx.poo.ubgarden.game.Position;
import fr.ubx.poo.ubgarden.game.go.GameObject;
import fr.ubx.poo.ubgarden.game.go.Movable;
import fr.ubx.poo.ubgarden.game.go.PickupVisitor;
import fr.ubx.poo.ubgarden.game.go.WalkVisitor;
import fr.ubx.poo.ubgarden.game.go.decor.Decor;
import fr.ubx.poo.ubgarden.game.go.decor.*;

import fr.ubx.poo.ubgarden.game.*;
import fr.ubx.poo.ubgarden.game.go.*;

public class Wasp extends GameObject implements Movable, WalkVisitor {
    private Direction direction = Direction.random();
    private long lastMoveTime = 0;
    private static final long MOVE_INTERVAL = 500_000_000L; // 0.5 saniye

    public Wasp(Game game, Position position) {
        super(game, position);
    }

    public Direction getDirection() {
        return direction;
    }

    @Override
    public int getEnergy() {
        return 0;
    }

    @Override
    public void setEnergy(int energy) {

    }

    @Override
    public boolean canMove(Direction direction) {
        Position next = direction.nextPosition(getPosition());
        return game.world().getGrid().inside(next);
    }

    public void moveRandom() {
        Direction dir = Direction.random();
        if (canMove(dir)) {
            setPosition(dir.nextPosition(getPosition()));
        }
    }

    @Override
    public void update(long now) {
        if (now - lastMoveTime >= MOVE_INTERVAL) {
            moveRandom();
            lastMoveTime = now;
        }
    }
}