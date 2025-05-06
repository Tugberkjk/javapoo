package fr.ubx.poo.ubgarden.game.go.bonus;

import fr.ubx.poo.ubgarden.game.Position;
import fr.ubx.poo.ubgarden.game.go.personage.Gardener;
import fr.ubx.poo.ubgarden.game.go.decor.Decor;
import fr.ubx.poo.ubgarden.game.go.decor.ground.*;

public class Apple extends Bonus {
    public Apple(Position position, Decor underneath) {
        super(position, underneath);
    }

    @Override
    public void pickUpBy(Gardener gardener) {
        gardener.pickUp(new EnergyBoost(this.getPosition(), new Grass(this.getPosition())));
        gardener.heal();
        this.remove();
    }
}