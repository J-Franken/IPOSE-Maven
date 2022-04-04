import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;

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
                .view("madotsuki.png")
                .scale(3,3)
                .buildAndAttach();

        FXGL.getGameScene().setBackgroundColor(Color.BLACK);
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

    public static void main(String[] args) {
        launch(args);
    }
}
