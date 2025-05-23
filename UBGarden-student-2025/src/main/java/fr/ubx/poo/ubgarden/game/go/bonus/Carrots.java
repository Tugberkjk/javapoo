package fr.ubx.poo.ubgarden.game.go.bonus;

import fr.ubx.poo.ubgarden.game.Position;
import fr.ubx.poo.ubgarden.game.go.bonus.Bonus;
import fr.ubx.poo.ubgarden.game.go.personage.Gardener;
import fr.ubx.poo.ubgarden.game.go.decor.Decor;

public class Carrots extends Bonus {
    public Carrots(Position position, Decor underneath) {
        super(position, underneath);
    }

    @Override
    public void pickUpBy(Gardener gardener) {
        this.remove();
    }
}