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
import fr.ubx.poo.ubgarden.game.go.bonus.Bomb;

import fr.ubx.poo.ubgarden.game.*;
import fr.ubx.poo.ubgarden.game.go.*;

public class Hornet extends GameObject implements Movable, WalkVisitor {
    private Direction direction = Direction.random();
    private long lastMoveTime = 0;
    private final long moveInterval;

    private int health = 2;

    public void hurt() {
        health--;
    }

    public boolean isDead() {
        return health <= 0;
    }

    public Hornet(Game game, Position position) {
        super(game, position);
        this.moveInterval = game.configuration().hornetMoveFrequency()* 1_000_000_000L;
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
    public boolean canWalkOn(Decor decor) {
        return !(decor instanceof Tree || decor instanceof DoorNextClosed);
    }

    @Override
    public boolean canMove(Direction direction) {
        Position next = direction.nextPosition(getPosition());
        if (!game.world().getGrid().inside(next)) {
            return false;
        }

        Decor decor = game.world().getGrid().get(next);
        if (decor != null && !decor.walkableBy(this)) {
            return false;
        }

        return true;
    }


    public void moveRandom() {
        Direction dir = Direction.random();
        if (canMove(dir)) {
            setPosition(dir.nextPosition(getPosition()));
        }
    }

    @Override
    public void update(long now) {
        if (now - lastMoveTime >= moveInterval) {
            moveRandom();
            lastMoveTime = now;
        }
        Decor decor = game.world().getGrid().get(this.getPosition());
        if (decor != null && decor.getBonus() instanceof Bomb) {
            System.out.println("Hornet collided with bomb and took damage!");
            this.hurt();
            decor.getBonus().remove();
            if (this.isDead()) {
                System.out.println("Hornet collided with bomb for second time and died");
                this.remove();
             }
        }
    }
}