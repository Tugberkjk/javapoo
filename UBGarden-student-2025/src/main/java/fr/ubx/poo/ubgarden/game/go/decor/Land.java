package fr.ubx.poo.ubgarden.game.go.decor;

import fr.ubx.poo.ubgarden.game.Position;

import fr.ubx.poo.ubgarden.game.go.personage.Gardener;

public class Land extends Decor {
    public Land(Position position) { super(position); }
    @Override
    public boolean walkableBy(Gardener g) { return true; }
    public int movementCost(int diseaseLevel) { return 2 * diseaseLevel; }
}