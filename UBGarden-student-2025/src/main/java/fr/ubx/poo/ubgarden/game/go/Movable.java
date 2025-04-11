/*
 * Copyright (c) 2020. Laurent Réveillère
 */

package fr.ubx.poo.ubgarden.game.go;

import fr.ubx.poo.ubgarden.game.Direction;
import fr.ubx.poo.ubgarden.game.Position;
import fr.ubx.poo.ubgarden.game.Game;
import fr.ubx.poo.ubgarden.game.go.decor.Decor;
import fr.ubx.poo.ubgarden.game.go.personage.Gardener;


public interface Movable {
    Game getGame();
    Position getPosition();
    void setPosition(Position position);
    int getEnergy();
    void setEnergy(int energy);
    /**
     * Checks whether the entity can move in the given direction.
     *
     * @param direction the direction in which the entity intends to move
     * @return true if the move is possible, false otherwise
     */
    default boolean canMove(Direction direction) {
        Position next = direction.nextPosition(getPosition());

        if (!getGame().world().getGrid().inside(next)) {
            return false;
        }

        Decor decor = getGame().world().getGrid().get(next);

        return decor == null || decor.walkableBy((Gardener) this);

    }

    /**
     * Moves the entity in the given direction and returns its new position.
     *
     * @param direction the direction in which to move
     * @return the new position after the move
     */
    default Position move(Direction direction) {
        Position next = direction.nextPosition(getPosition());

        if (canMove(direction)) {
            setPosition(next);
            setEnergy(getEnergy() - 1);

            Decor decor = getGame().world().getGrid().get(next);
            if (decor != null) {
                decor.pickUpBy((Gardener) this);

            }
        }

        return getPosition();
    }
}
