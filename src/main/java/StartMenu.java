import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.MenuType;
import com.almasb.fxgl.dsl.FXGL;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import org.jetbrains.annotations.NotNull;


public class StartMenu extends FXGLMenu {
    public StartMenu(@NotNull MenuType type) {
        super(type);

        //FX

        Button button = new Button("", new ImageView("assets/textures/start.png"));
        button.setStyle("-fx-background-color: #000; -fx-background-radius: 10px;");

        Button button1 = new Button("", new ImageView("assets/textures/exit.png"));
        button1.setStyle("-fx-background-color: #000; -fx-background-radius: 10px;");

        BorderPane pane = new BorderPane();
        HBox hBox = new HBox(15);
        VBox vBox = new VBox(150);
        vBox.setMinHeight(500);
        VBox vBox1 = new VBox(10);

        hBox.setAlignment(Pos.CENTER);
        vBox.setAlignment(Pos.CENTER);

        hBox.getChildren().addAll(button, button1);
        vBox.getChildren().addAll(vBox1, hBox);

        pane.setMinHeight(FXGL.getAppHeight());
        pane.setMinWidth(FXGL.getAppWidth());


        pane.setCenter(vBox);

        button.setOnAction(e -> {
            fireNewGame();
        });

        button1.setOnAction(e -> {
            fireExit();
        });

        BackgroundImage mainBackground = new BackgroundImage(new Image("assets/textures/AWayOutBackground.png", FXGL.getAppWidth(), FXGL.getAppHeight(), true, false),
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.DEFAULT,
                BackgroundSize.DEFAULT);

        getContentRoot().setBackground(new Background(mainBackground));

        super.getContentRoot().getChildren().add(pane);

    }
}
