package fr.ubx.poo.ubgarden.game.go.decor;

import fr.ubx.poo.ubgarden.game.Position;
import fr.ubx.poo.ubgarden.game.go.personage.Gardener;
import fr.ubx.poo.ubgarden.game.go.personage.Hornet;
import javafx.scene.layout.Pane;
import fr.ubx.poo.ubgarden.game.view.SpriteHornet;
import fr.ubx.poo.ubgarden.game.Game;
import fr.ubx.poo.ubgarden.game.go.bonus.Bomb;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import fr.ubx.poo.ubgarden.game.Map;
import fr.ubx.poo.ubgarden.game.Level;

public class NestHornet extends Decor {
    private final Game game;
    private long lastSpawnTime = 0;
    private static final long SPAWN_INTERVAL = 10_000_000_000L; // 5 saniye

    public NestHornet(Game game, Position position) {
        super(position);
        this.game = game;
    }

    private Position findRandomDecorWithoutBonus() {
        Map grid = game.world().getGrid();
        int width = grid.width();
        int height = grid.height();
        List<Position> eligiblePositions = new ArrayList<>();
        int level = game.world().currentLevel();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Position pos = new Position(level, x, y);
                Decor decor = grid.get(pos);
                if (decor != null && decor.getBonus() == null && decor.walkableBy(game.getGardener())) {
                    eligiblePositions.add(pos);
                }
            }
        }

        if (eligiblePositions.isEmpty()) {
            return null;
        }

        return eligiblePositions.get(new Random().nextInt(eligiblePositions.size()));
    }


    private void spawnHornet(long now) {
        Position pos = this.getPosition();
        Hornet hornet = new Hornet(game, pos);
        game.addActiveHornet(hornet);
        for (int i = 0; i < 2; i++) {
            Position bombPos = findRandomDecorWithoutBonus();
            if (bombPos != null) {
                Decor decorAtPos = (Decor) game.world().getGrid().get(bombPos);
                decorAtPos.setBonus(new Bomb(bombPos, decorAtPos));
            }
        }
        lastSpawnTime = now;
    }

    @Override
    public void update(long now) {
        if (now - lastSpawnTime >= SPAWN_INTERVAL) {
            spawnHornet(now);
        }
    }
}
