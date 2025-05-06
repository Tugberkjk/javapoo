package fr.ubx.poo.ubgarden.game.go.decor;

import fr.ubx.poo.ubgarden.game.Position;
import fr.ubx.poo.ubgarden.game.go.WalkVisitor;
import fr.ubx.poo.ubgarden.game.go.personage.Gardener;

public class DoorPrevOpened extends Decor {
    public DoorPrevOpened(Position position) {
        super(position);
    }


    public boolean walkableBy(WalkVisitor walker) {
        return true;
    }
}