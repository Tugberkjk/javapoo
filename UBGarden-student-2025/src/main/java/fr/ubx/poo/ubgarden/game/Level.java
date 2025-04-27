package fr.ubx.poo.ubgarden.game;

import fr.ubx.poo.ubgarden.game.go.bonus.EnergyBoost;
import fr.ubx.poo.ubgarden.game.go.decor.*;
import fr.ubx.poo.ubgarden.game.go.decor.ground.Grass;
import fr.ubx.poo.ubgarden.game.launcher.MapEntity;
import fr.ubx.poo.ubgarden.game.launcher.MapLevel;

import java.util.Collection;
import java.util.HashMap;

import static fr.ubx.poo.ubgarden.game.launcher.MapEntity.PoisonedApple;

public class Level implements Map {
    private Position hedgehogPosition;
    private final int level;
    private final int width;

    private final int height;

    private final java.util.Map<Position, Decor> decors = new HashMap<>();

    public Level(Game game, int level, MapLevel entities) {
        this.level = level;
        this.width = entities.width();
        this.height = entities.height();


        for (int i = 0; i < width; i++)
            for (int j = 0; j < height; j++) {
                Position position = new Position(level, i, j);
                MapEntity mapEntity = entities.get(i, j);
                switch (mapEntity) {
                    case Hedgehog:
                        this.hedgehogPosition = position;
                        decors.put(position, new Grass(position));
                        break;
                    case Grass:
                        decors.put(position, new Grass(position));
                        break;
                    case Land:
                        decors.put(position, new Land(position));
                        break;
                    case Flowers:
                        decors.put(position, new Flowers(position));
                        break;
                    case Tree:
                        decors.put(position, new Tree(position));
                        break;
                    case NestWasp:
                        decors.put(position, new NestWasp(position));
                        break;

                    case NestHornet:
                        decors.put(position, new NestHornet(position));
                        break;

                    case DoorNextClosed:
                        decors.put(position, new DoorNextClosed(position));
                        break;

                    case DoorNextOpened:
                        decors.put(position, new DoorNextOpened(position));
                        break;
                    case Carrots: {
                        Decor ground = new Land(position);
                        ground.setBonus(new Carrots(position, ground));
                        decors.put(position, ground);
                        break;
                    }
                    case Apple: {
                        Decor grass = new Grass(position);
                        grass.setBonus(new EnergyBoost(position, grass));
                        decors.put(position, grass);
                        break;
                    }
                    case PoisonedApple: {
                        Decor grass = new Grass(position);
                        grass.setBonus(new PoisonedApple(position).getBonus());
                        decors.put(position, grass);
                        break;
                    }
                    case Bomb:
                        decors.put(position, new Bomb(position));
                        break;
                    case Gardener:
                        decors.put(position, new Grass(position));
                        break;
                    default:
                        throw new RuntimeException("EntityCode " + mapEntity.name() + " not processed");
                }
            }
    }

    @Override
    public int width() {
        return this.width;
    }

    @Override
    public int height() {
        return this.height;
    }

    public Decor get(Position position) {
        return decors.get(position);
    }

    public Collection<Decor> values() {
        return decors.values();
    }

    public Position getHedgehogPosition() {
        return hedgehogPosition;
    }

    @Override
    public boolean inside(Position position) {
        int x = position.x();
        int y = position.y();
        return x >= 0 && x < width && y >= 0 && y < height;
    }
    public void set(Position position, Decor decor) {
        if (inside(position)) {
            decors.put(position, decor);
        }
    }

}
