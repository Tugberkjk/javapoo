package fr.ubx.poo.ubgarden.game.go.bonus;

import fr.ubx.poo.ubgarden.game.Position;
import fr.ubx.poo.ubgarden.game.go.personage.Gardener;
import fr.ubx.poo.ubgarden.game.go.decor.Decor;

public class Bomb extends Bonus {
    public Bomb(Position position, Decor underneath) {
        super(position, underneath);
    }

    @Override
    public void pickUpBy(Gardener g) {
        this.remove();
        g.addBomb();
    }
}
