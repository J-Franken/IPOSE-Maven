import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.app.scene.*;
import com.almasb.fxgl.core.util.LazyValue;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.entity.level.Level;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.input.view.KeyView;
import com.almasb.fxgl.input.virtual.VirtualButton;
import com.almasb.fxgl.physics.CollisionHandler;
import com.almasb.fxgl.physics.PhysicsComponent;
import javafx.geometry.Point2D;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import com.almasb.fxgl.cutscene.Cutscene;

import java.awt.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import static com.almasb.fxgl.dsl.FXGL.*;


public class Game extends GameApplication {

    private static final int MAX_LEVEL = 1;
    private static final int STARTING_LEVEL = 0;
    private Entity player;
    private int ms = 0;
    private int sec = 0;
    private int min = 0;

    @Override
    protected void initSettings(GameSettings gameSettings) {
        gameSettings.setWidth(15 * 70);
        gameSettings.setHeight(20 * 32);
        gameSettings.setTitle("A Way Out");
        gameSettings.setVersion("1.2");
        gameSettings.setMainMenuEnabled(true);
    }

    @Override
    protected void initGame(){
        getGameWorld().addEntityFactory(new GameFactory());

        player = null;
        nextLevel();

        player = spawn("player", 25, 450);

        set("player", player);

        spawn("background");


        Viewport viewport = getGameScene().getViewport();
        viewport.setBounds(-1500, 0, 250 * 70, getAppHeight());
        viewport.bindToEntity(player, getAppWidth() / 2, getAppHeight() / 2);
        viewport.setLazy(true);
    }
    

    @Override
    protected void initInput(){
        getInput().addAction(new UserAction("left") {
            @Override
            protected void onAction() {
                player.getComponent(PlayerComponent.class).left();
            }

            @Override
            protected void onActionEnd() {
                player.getComponent(PhysicsComponent.class).setVelocityX(0);
            }
        }, KeyCode.A, VirtualButton.LEFT);

        getInput().addAction(new UserAction("right") {
            @Override
            protected void onAction() {
                player.getComponent(PlayerComponent.class).right();
            }

            @Override
            protected void onActionEnd() {
                player.getComponent(PhysicsComponent.class).setVelocityX(0);
            }
        }, KeyCode.D, VirtualButton.RIGHT);

        getInput().addAction(new UserAction("jump") {
            @Override
            protected void onActionBegin() {
                player.getComponent(PlayerComponent.class).jump();
            }
        }, KeyCode.W, VirtualButton.UP);
    }

    @Override
    protected void initPhysics() {
        getPhysicsWorld().setGravity(0, 760);
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityTypes.PLAYER, EntityTypes.COIN) {
            @Override
            protected void onCollision(Entity player, Entity coin) {
                inc("coin", +1);
                coin.removeFromWorld();
            }
        });

        onCollisionOneTimeOnly(EntityTypes.PLAYER, EntityTypes.CUTSCENE, (player, cutscene) -> {
            getDialog();
        });

        onCollision(EntityTypes.PLAYER, EntityTypes.OBSTACLE, (player, obstacle) -> {
            onPlayerDied();
        });

        onCollisionOneTimeOnly(EntityTypes.PLAYER, EntityTypes.EXIT_TRIGGER, (player, trigger) -> {
            getGameScene().getViewport().fade(this::nextLevel);
        });
    }

    @Override
    protected void initUI(){
        javafx.scene.control.Label coinValue = new Label("Stars:");
        javafx.scene.control.Label timer = new Label("Time:");
        coinValue.setStyle("-fx-text-fill: white");
        timer.setStyle("-fx-text-fill: white");
        coinValue.setTranslateX(20);
        coinValue.setTranslateY(20);
        timer.setTranslateX(60);
        timer.setTranslateY(20);

        getGameTimer().runAtInterval(() -> {
            ms++;
            if (ms == 60){
                ms = 0;
                sec++;
            }
            if (sec == 60){
                sec = 0;
                min++;
            }
            timer.setText("Time: " + min + ":" + sec+ ":" + ms);
        }, Duration.millis(1));

        coinValue.textProperty().bind(getWorldProperties().intProperty("coin").asString());

        getGameScene().addUINode(coinValue);
        getGameScene().addUINode(timer);
    }

    protected void onUpdate(double tpf) {
        if (player.getY() > getAppHeight() + 200) {
            onPlayerDied();
        }
    }

    protected void initGameVars(Map<String, Object> vars){
        vars.put("level", STARTING_LEVEL);
        vars.put("coin",0);
    }

    public void onPlayerDied() {
        setLevel(geti("level"));
    }

    public void getDialog(){
        getDialogService().showMessageBox("Let's get outta here!", () -> {
        });
    }


    private void nextLevel() {
        if (geti("level") == MAX_LEVEL) {
            createScoreboard();
        } else {
            inc("level", +1);
            setLevel(geti("level"));
        }
    }

    public void createScoreboard(){
        StringBuilder builder = new StringBuilder();
        builder.append("You found a way out!!\n\n")
                .append("Total Time: " + min + ":" + sec+ ":" + ms)
                .append(FXGL.geti("coin"))
                .append("\nNumber of Stars: \t")
                .append(FXGL.geti("coin"))
                .append("\n\nEnter your name to join the scoreboard:");
        FXGL.getDialogService().showInputBox(builder.toString(), name -> FXGL.getGameController().gotoMainMenu());
        return;
    }


    private void setLevel(int levelNum) {
        if (player != null) {
            player.getComponent(PhysicsComponent.class).overwritePosition(new Point2D(25, 450));
            player.setZIndex(Integer.MAX_VALUE);
        }
        Level level = setLevelFromMap("level" + levelNum  + ".tmx");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
