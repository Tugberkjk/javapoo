package fr.ubx.poo.ubgarden.game.go.decor;

import fr.ubx.poo.ubgarden.game.Position;
import fr.ubx.poo.ubgarden.game.go.personage.Gardener;

public class Carrots extends Decor {
    public Carrots(Position position) { super(position); }
    @Override
    public void pickUpBy(Gardener g) {
        this.remove();
    }
}
