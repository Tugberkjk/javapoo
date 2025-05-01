package fr.ubx.poo.ubgarden.game.go.decor;

import fr.ubx.poo.ubgarden.game.Position;
import fr.ubx.poo.ubgarden.game.go.personage.Wasp;
import javafx.scene.layout.Pane;
import fr.ubx.poo.ubgarden.game.view.SpriteWasp;
import fr.ubx.poo.ubgarden.game.Game;



public class NestWasp extends Decor {
    private final Game game;
    private long lastSpawnTime = 0;
    private static final long SPAWN_INTERVAL = 5_000_000_000L; // 5 saniye

    public NestWasp(Game game, Position position) {
        super(position);
        this.game = game;
    }



    private void spawnWasp(long now) {
        Position pos = this.getPosition();
        Wasp wasp = new Wasp(game, pos);
        game.addActiveWasp(wasp);
        lastSpawnTime = now;
    }

    @Override
    public void update(long now) {
        if (now - lastSpawnTime >= SPAWN_INTERVAL) {
            spawnWasp(now);
        }
    }
}