    /*
     * Copyright (c) 2020. Laurent Réveillère
     */

    package fr.ubx.poo.ubgarden.game.engine;

    import fr.ubx.poo.ubgarden.game.go.decor.*;
    import fr.ubx.poo.ubgarden.game.Direction;
    import fr.ubx.poo.ubgarden.game.Position;
    import fr.ubx.poo.ubgarden.game.Level;
    import fr.ubx.poo.ubgarden.game.go.bonus.Carrots;
    import fr.ubx.poo.ubgarden.game.go.decor.DoorNextClosed;
    import fr.ubx.poo.ubgarden.game.go.decor.DoorNextOpened;
    import fr.ubx.poo.ubgarden.game.Game;
    import fr.ubx.poo.ubgarden.game.go.personage.Gardener;
    import fr.ubx.poo.ubgarden.game.view.ImageResource;
    import fr.ubx.poo.ubgarden.game.view.Sprite;
    import fr.ubx.poo.ubgarden.game.view.SpriteFactory;
    import fr.ubx.poo.ubgarden.game.view.SpriteGardener;
    import javafx.animation.AnimationTimer;
    import javafx.application.Platform;
    import javafx.scene.Group;
    import javafx.scene.Scene;
    import javafx.scene.layout.Pane;
    import javafx.scene.layout.StackPane;
    import javafx.scene.paint.Color;
    import javafx.scene.text.Font;
    import javafx.scene.text.Text;
    import javafx.scene.text.TextAlignment;
    import fr.ubx.poo.ubgarden.game.go.personage.Wasp;
    import fr.ubx.poo.ubgarden.game.go.personage.Hornet;

    import java.util.*;


    public final class GameEngine {

        private static AnimationTimer gameLoop;
        private final Game game;
        private final Gardener gardener;
        private final List<Sprite> sprites = new LinkedList<>();
        private final Set<Sprite> cleanUpSprites = new HashSet<>();

        private final Scene scene;

        private StatusBar statusBar;

        private final Pane rootPane = new Pane();
        private final Group root = new Group();
        private final Pane layer = new Pane();
        private Input input;
        private long lastWaspHitTime = 0; // === NEW: Çarpışma cooldown'u için zaman damgası
        private static final long WASP_HIT_COOLDOWN = 1000; // 1000 ms = 1 saniye

        private long lastHornetHitTime = 0;
        private static final long HORNET_HIT_COOLDOWN = 1000;


        public GameEngine(Game game, Scene scene) {
            this.game = game;
            this.scene = scene;
            this.gardener = game.getGardener();
            initialize();
            buildAndSetGameLoop();
        }

        public Pane getRoot() {
            return rootPane;
        }

        private void initialize() {
            int height = game.world().getGrid().height();
            int width = game.world().getGrid().width();
            int sceneWidth = width * ImageResource.size;
            int sceneHeight = height * ImageResource.size;
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/application.css")).toExternalForm());
            input = new Input(scene);

            root.getChildren().clear();
            root.getChildren().add(layer);
            statusBar = new StatusBar(root, sceneWidth, sceneHeight);

            rootPane.getChildren().clear();
            rootPane.setPrefSize(sceneWidth, sceneHeight + StatusBar.height);
            rootPane.getChildren().add(root);

            // Create sprites
            int currentLevel = game.world().currentLevel();

            for (var decor : game.world().getGrid().values()) {
                sprites.add(SpriteFactory.create(layer, decor));
                decor.setModified(true);
                var bonus = decor.getBonus();
                if (bonus != null) {
                    sprites.add(SpriteFactory.create(layer, bonus));
                    bonus.setModified(true);
                }
            }

            sprites.add(new SpriteGardener(layer, gardener));
            resizeScene(sceneWidth, sceneHeight);
            for (var wasp : game.getActiveWasps()) {
                sprites.add(SpriteFactory.create(layer, wasp));
                wasp.setModified(true);
            }
        }

        void buildAndSetGameLoop() {
            gameLoop = new AnimationTimer() {
                public void handle(long now) {
                    checkLevel();

                    // Check keyboard actions
                    processInput();

                    // Do actions
                    update(now);
                    checkCollision();

                    // Graphic update
                    cleanupSprites();
                    render();
                    statusBar.update(game);
                }
            };
        }


        private void checkLevel() {
            if (!game.isSwitchLevelRequested())
                return;

            int nextLevel = game.getSwitchLevel();

            if (game.world().getGrid(nextLevel) == null) {
                System.out.println("Level " + nextLevel + " not loaded!");
                return;
            }

            sprites.clear();
            layer.getChildren().clear();

            game.world().setCurrentLevel(nextLevel);


            Position doorPos = null;
            for (var entry : game.world().getGrid().values()) {
                if (entry instanceof DoorNextOpened|| entry instanceof DoorPrevOpened) {
                    doorPos = entry.getPosition();
                    break;
                }
            }

            if (doorPos == null) {
                System.out.println("No DoorNextOpened found in level " + nextLevel);
                doorPos = new Position(nextLevel, 0, 0);
            }
            gardener.setPosition(doorPos);
            game.clearSwitchLevel();
            initialize();
        }

        // Check a collision between the gardener and a wasp or an hornet
        private void checkCollision() {
            long now = System.currentTimeMillis();
            for (Sprite sprite : sprites) {
                if (sprite.getGameObject() instanceof Wasp) {
                    Wasp wasp = (Wasp) sprite.getGameObject();
                    if (wasp.getPosition().equals(gardener.getPosition()) && (now - lastWaspHitTime) >= WASP_HIT_COOLDOWN) {
                        if (gardener.getBombCount() > 0) {
                            gardener.setBombCount(gardener.getBombCount() - 1);
                            wasp.remove();
                        } else {
                            gardener.hurt(20);
                            wasp.remove();
                            if (gardener.getEnergy() < 0) {
                                gardener.setEnergy(0);
                            }
                        }
                        lastWaspHitTime = now;
                    }
                }
                if (sprite.getGameObject() instanceof Hornet) {
                    Hornet hornet = (Hornet) sprite.getGameObject();
                    if (hornet.getPosition().equals(gardener.getPosition()) && (now - lastHornetHitTime) >= HORNET_HIT_COOLDOWN) {
                        hornet.hurt();
                        if(hornet.isDead()){
                            hornet.remove();
                        }
                        if (gardener.getBombCount() >= 2) {
                            gardener.setBombCount(gardener.getBombCount() - 2);
                            hornet.remove();
                        } else {
                            gardener.hurt(30);
                            if (gardener.getEnergy() < 0) {
                                gardener.setEnergy(0);
                            }
                        }
                        lastHornetHitTime = now;
                    }
                }
            }
        }



        private void processInput() {
            if (input.isExit()) {
                gameLoop.stop();
                Platform.exit();
                System.exit(0);
            } else if (input.isMoveDown()) {
                gardener.requestMove(Direction.DOWN);
            } else if (input.isMoveLeft()) {
                gardener.requestMove(Direction.LEFT);
            } else if (input.isMoveRight()) {
                gardener.requestMove(Direction.RIGHT);
            } else if (input.isMoveUp()) {
                gardener.requestMove(Direction.UP);
            }
            input.clear();
        }

        private void showMessage(String msg, Color color) {
            Text message = new Text(msg);
            message.setTextAlignment(TextAlignment.CENTER);
            message.setFont(new Font(60));
            message.setFill(color);

            StackPane pane = new StackPane(message);
            pane.setPrefSize(rootPane.getWidth(), rootPane.getHeight());
            rootPane.getChildren().clear();
            rootPane.getChildren().add(pane);

            new AnimationTimer() {
                public void handle(long now) {
                    processInput();
                }
            }.start();
        }

        private void update(long now) {
            game.world().getGrid().values().forEach(decor -> decor.update(now));

            gardener.update(now);

            if (gardener.getPosition().equals(game.world().getHedgehogPosition())) {
                gameLoop.stop();
                showMessage("Won!", Color.GREEN);
            }

            if (gardener.getEnergy() < 0) {
                gameLoop.stop();
                showMessage("Perdu!", Color.RED);
            }
            boolean carrotsRemaining = game.world().getGrid().values().stream()
                    .anyMatch(decor -> decor != null && decor.getBonus() instanceof Carrots);

            if (!carrotsRemaining) {
                for (var decor : game.world().getGrid().values()) {
                    if (decor instanceof DoorNextClosed) {
                        Position pos = decor.getPosition();
                        decor.remove();
                        DoorNextOpened openedDoor = new DoorNextOpened(pos);
                        ((Level) game.world().getGrid()).set(pos, openedDoor);
                        sprites.add(SpriteFactory.create(layer, openedDoor));
                        openedDoor.setModified(true);
                    }
                }
            }

            for (Wasp wasp : game.getActiveWasps()) {
                wasp.update(now);
                if (sprites.stream().noneMatch(s -> s.getGameObject() == wasp)) {
                    sprites.add(SpriteFactory.create(layer, wasp));
                }
            }
            for (Hornet hornet : game.getActiveHornets()) {
                hornet.update(now);
                if (sprites.stream().noneMatch(s -> s.getGameObject() == hornet)) {
                    sprites.add(SpriteFactory.create(layer, hornet));
                }
            }
            for (var decor : game.world().getGrid().values()) {
                var bonus = decor.getBonus();
                if (bonus != null && sprites.stream().noneMatch(s -> s.getGameObject() == bonus)) {
                    sprites.add(SpriteFactory.create(layer, bonus));
                    bonus.setModified(true);
                }
            }

        }


        public void cleanupSprites() {
            sprites.forEach(sprite -> {
                if (sprite.getGameObject().isDeleted()) {
                    cleanUpSprites.add(sprite);
                }
            });
            cleanUpSprites.forEach(Sprite::remove);
            sprites.removeAll(cleanUpSprites);
            cleanUpSprites.clear();
        }

        private void render() {
            sprites.forEach(Sprite::render);
        }

        public void start() {
            gameLoop.start();
        }

        private void resizeScene(int width, int height) {
            rootPane.setPrefSize(width, height + StatusBar.height);
            layer.setPrefSize(width, height);
            Platform.runLater(() -> scene.getWindow().sizeToScene());
        }
    }