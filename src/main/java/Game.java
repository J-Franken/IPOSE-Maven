import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.app.scene.GameView;
import com.almasb.fxgl.app.scene.LoadingScene;
import com.almasb.fxgl.app.scene.SceneFactory;
import com.almasb.fxgl.app.scene.Viewport;
import com.almasb.fxgl.core.util.LazyValue;
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

import java.awt.*;
import java.util.Map;

import static com.almasb.fxgl.dsl.FXGL.*;


public class Game extends GameApplication {

    private static final int MAX_LEVEL = 4;
    private static final int STARTING_LEVEL = 0;
    private Entity player;

    @Override
    protected void initSettings(GameSettings gameSettings) {
        gameSettings.setWidth(15 * 70);
        gameSettings.setHeight(10 * 70);
        gameSettings.setTitle("Demo game");
        gameSettings.setVersion("1.2");
        gameSettings.setMainMenuEnabled(true);
    }

    @Override
    protected void initGame(){
        getGameWorld().addEntityFactory(new GameFactory());

        player = null;
        nextLevel();

        player = spawn("player", 200, 50);

        set("player", player);

        Viewport viewport = getGameScene().getViewport();
        viewport.setBounds(-1500, 0, 250 * 70, getAppHeight());
        viewport.bindToEntity(player, getAppWidth() / 2, getAppHeight() / 2);
        viewport.setLazy(true);
    }
    

    @Override
    protected void initInput(){
        getInput().addAction(new UserAction("Left") {
            @Override
            protected void onAction() {
                player.getComponent(PlayerComponent.class).left();
            }

            @Override
            protected void onActionEnd() {
                player.getComponent(PhysicsComponent.class).setVelocityX(0);
            }
        }, KeyCode.A, VirtualButton.LEFT);

        getInput().addAction(new UserAction("Right") {
            @Override
            protected void onAction() {
                player.getComponent(PlayerComponent.class).right();
            }

            @Override
            protected void onActionEnd() {
                player.getComponent(PhysicsComponent.class).setVelocityX(0);
            }
        }, KeyCode.D, VirtualButton.RIGHT);

        getInput().addAction(new UserAction("Jump") {
            @Override
            protected void onActionBegin() {
                player.getComponent(PlayerComponent.class).jump();
            }
        }, KeyCode.W, VirtualButton.UP);
    }

    @Override
    protected void initPhysics(){
        getPhysicsWorld().setGravity(0, 760);
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityTypes.PLAYER, EntityTypes.COIN) {
            @Override
            protected void onCollision(Entity player, Entity coin) {
                inc("coin", +1);
                coin.removeFromWorld();
            }
        });

        onCollisionOneTimeOnly(EntityTypes.PLAYER, EntityTypes.EXIT_TRIGGER, (player, trigger) -> {
            nextLevel();
        });
    }

    @Override
    protected void initUI(){
        getGameScene().setBackgroundColor(Color.LIGHTBLUE);
        javafx.scene.control.Label coinValue = new Label("Stars:");
        coinValue.setTranslateX(20);
        coinValue.setTranslateY(20);

        coinValue.textProperty().bind(getWorldProperties().intProperty("coin").asString());

        getGameScene().addUINode(coinValue);
    }

    protected void onUpdate(double tpf) {
        if (player.getY() > getAppHeight()) {
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


    private void nextLevel() {
        if (geti("level") == MAX_LEVEL) {
            showMessage("You found a way out!");
            return;
        }

        inc("level", +1);

        setLevel(geti("level"));
    }


    private void setLevel(int levelNum) {
        if (player != null) {
            player.getComponent(PhysicsComponent.class).overwritePosition(new Point2D(200, 50));
            player.setZIndex(Integer.MAX_VALUE);
        }
        Level level = setLevelFromMap("level" + levelNum  + ".tmx");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
