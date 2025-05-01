package fr.ubx.poo.ubgarden.game.go.bonus;

import fr.ubx.poo.ubgarden.game.Position;
import fr.ubx.poo.ubgarden.game.go.personage.Gardener;
import fr.ubx.poo.ubgarden.game.go.decor.Decor;


public class PoisonedApple extends Bonus {
    public PoisonedApple(Position position, Decor underneath) {
        super(position, underneath);
    }

    @Override
    public void pickUpBy(Gardener g) {
        this.remove();
        g.increaseFatigue();
        // g.setDisease(true);
    }
}
