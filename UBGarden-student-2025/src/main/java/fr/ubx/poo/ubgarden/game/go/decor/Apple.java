package fr.ubx.poo.ubgarden.game.go.decor;

import fr.ubx.poo.ubgarden.game.Position;
import fr.ubx.poo.ubgarden.game.go.personage.Gardener;

public class Apple extends Decor {
    public Apple(Position position) { super(position); }
    @Override
    public void pickUpBy(Gardener g) {
        this.remove();
        // g.restoreEnergy();
        // g.heal();
    }
}
