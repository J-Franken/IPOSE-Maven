import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import com.almasb.fxgl.time.LocalTimer;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.util.Duration;
import javafx.geometry.HorizontalDirection;

import static com.almasb.fxgl.dsl.FXGL.image;
import static com.almasb.fxgl.dsl.FXGL.newLocalTimer;


public class EnemySpiderComponent extends Component {

        private AnimatedTexture texture;
        private AnimationChannel  animWalk;
        private int patrolEndX;
        private boolean goingRight = true;

        private LocalTimer timer;
        private Duration duration;
        private double distance;
        private PhysicsComponent physics;
        private String direction;

        private double speed;
        public EnemySpiderComponent(int patrolEndX) {
            this.patrolEndX =patrolEndX;

            duration = Duration.seconds(2);
            int w = 1392 / 4;
            int h = 390 /4 ;



            Image image = image("spider.png");


            animWalk = new AnimationChannel(image, 3, 128/3, 42, Duration.seconds(0.5), 0, 2);

            texture = new AnimatedTexture(animWalk);
            texture.loop();
        }

        @Override
        public void onAdded() {
            distance= patrolEndX - entity.getX();

            timer = newLocalTimer();
            timer.capture();
            speed= distance / duration.toSeconds();
            entity.getTransformComponent().setScaleOrigin(new Point2D(232 /4 / 2, 390 /4 /2));
            entity.getViewComponent().addChild(texture);

        }

        public void onUpdate( double tpf ) {
            if (timer.elapsed(duration)) {
                goingRight = ! goingRight;
                timer.capture();
            }
            entity.translateY(goingRight ? - speed * tpf : speed * tpf);
            entity.setScaleX(goingRight ? 1 : -1);
        }



        public void left() {
            direction = "left";
            getEntity().setScaleX(-1);
            physics.setVelocityX(-170);
        }

        public void right() {
            direction = "right";
            getEntity().setScaleX(1);
            physics.setVelocityX(170);
        }

        public void stop() {
            physics.setVelocityX(0);
        }

    }



