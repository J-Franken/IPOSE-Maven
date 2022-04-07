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
import com.almasb.fxgl.entity.Entity;
import java.awt.*;
import java.util.Map;
import com.almasb.fxgl.time.LocalTimer;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.entity.component.Required;

import static com.almasb.fxgl.dsl.FXGL.*;


public class liftComponent extends Component {

    private LocalTimer timer;
    private Duration duration;
    private double distance;
    private boolean goingUp;
    private double speed;

    public liftComponent(Duration duration, double distance , boolean goingUp) {
        this.distance= distance;
        this.duration = duration;
        this.goingUp = goingUp;
    }

    public void onAdded() {
        timer = newLocalTimer();
        timer.capture();
        speed= distance/ duration.toSeconds();

    }

    public void onUpdate( double tpf ) {
        if (timer.elapsed(duration)) {
            goingUp = ! goingUp;
            timer.capture();
    }
        entity.translateY(goingUp ? - speed * tpf : speed * tpf);
    }
}
