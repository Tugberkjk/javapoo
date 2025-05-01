package fr.ubx.poo.ubgarden.game.go.bonus;

import fr.ubx.poo.ubgarden.game.Position;
import fr.ubx.poo.ubgarden.game.go.personage.Gardener;
import fr.ubx.poo.ubgarden.game.go.decor.Decor;

public class Apple extends Bonus {
    public Apple(Position position, Decor underneath) {
        super(position, underneath);
    }

    @Override
    public void pickUpBy(Gardener gardener) {
        gardener.setEnergy(gardener.getGame().configuration().gardenerEnergy());
        gardener.setFatigueLevel(1);
        this.remove();
    }
}
