import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.physics.CollisionHandler;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class Game extends GameApplication {

    private Entity player;

    @Override
    protected void initSettings(GameSettings gameSettings) {
        gameSettings.setWidth(800);
        gameSettings.setHeight(800);
        gameSettings.setTitle("Demo game");
        gameSettings.setVersion("1.0");
    }

    @Override
    protected void initGame(){
        player = FXGL.entityBuilder()
                .at(400, 400)
                .viewWithBBox("madotsuki.png")
                .with(new CollidableComponent(true))
                .type(EntityTypes.PLAYER)
                .scale(3,3)
                .buildAndAttach();

        FXGL.getGameTimer().runAtInterval(() -> {
            int randomPosX = ThreadLocalRandom.current().nextInt(80,FXGL.getGameScene().getAppWidth() -80);
            int randomPosY = ThreadLocalRandom.current().nextInt(80,FXGL.getGameScene().getAppWidth() -80);
            FXGL.entityBuilder()
                    .at(randomPosX, randomPosY)
                    .viewWithBBox(new Circle(5, Color.YELLOW))
                    .with(new CollidableComponent(true))
                    .type(EntityTypes.STAR)
                    .buildAndAttach();
        }, Duration.millis(2000));

        FXGL.entityBuilder()
                .at(200, 200)
                .viewWithBBox(new Circle(5, Color.YELLOW))
                .with(new CollidableComponent(true))
                .type(EntityTypes.STAR)
                .buildAndAttach();
    }

    @Override
    protected void initInput(){
        FXGL.onKey(KeyCode.D, () -> {
            player.translateX(5);
        });
        FXGL.onKey(KeyCode.A, () -> {
            player.translateX(-5);
        });
        FXGL.onKey(KeyCode.W, () -> {
            player.translateY(-5);
        });
        FXGL.onKey(KeyCode.S, () -> {
            player.translateY(5);
        });
    }

    @Override
    protected void initPhysics(){
        FXGL.getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityTypes.PLAYER, EntityTypes.STAR) {
            @Override
            protected void onCollision(Entity player, Entity star) {
                FXGL.inc("kills", +1);
               star.removeFromWorld();
            }
        });
    }

    @Override
    protected void initUI(){
        Label myText = new Label("Hiya");
        myText.setStyle("-fx-text-fill: white");
        myText.setTranslateX(30);
        myText.setTranslateY(30);
        myText.textProperty().bind(FXGL.getWorldProperties().intProperty("kills").asString());


        FXGL.getGameScene().addUINode(myText);
        FXGL.getGameScene().setBackgroundColor(Color.BLACK);
    }

    @Override
    protected void initGameVars(Map<String, Object> vars){
        vars.put("kills", 0);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
