import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.*;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.entity.components.IrremovableComponent;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.physics.box2d.dynamics.BodyType;
import com.almasb.fxgl.physics.box2d.dynamics.FixtureDef;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;


public class GameFactory implements EntityFactory {

    @Spawns("platform")
    public Entity newPlatform(SpawnData data){
        PhysicsComponent physics = new PhysicsComponent();
        return FXGL.entityBuilder()
                .type(EntityTypes.PLATFORM)
                .bbox(new HitBox(BoundingShape.box(data.<Integer>get("width"), data.<Integer>get("height"))))
                .with(new CollidableComponent(true))
                .with(physics)
                .build();
    }

    @Spawns("coin")
    public Entity newCoin(SpawnData data){
        return FXGL.entityBuilder()
                .type(EntityTypes.COIN)
                .viewWithBBox("star.png")
                .with(new CollidableComponent(true))
                .build();
    }

    @Spawns("player")
    public Entity newPlayer(SpawnData data){
        PhysicsComponent physics = new PhysicsComponent();
        physics.setBodyType(BodyType.DYNAMIC);
        physics.addGroundSensor(new HitBox("GROUND_SENSOR", new Point2D(16 , 38), BoundingShape.box(6, 8)));
        physics.setFixtureDef(new FixtureDef().friction(0.0f));

        return FXGL.entityBuilder(data)
                .type(EntityTypes.PLAYER)
                .bbox(new HitBox(new Point2D(5, 5), BoundingShape.circle(12)))
                .bbox(new HitBox(new Point2D(10, 25), BoundingShape.box(6, 8)))
                .with(physics)
                .with(new CollidableComponent(true))
                .with(new IrremovableComponent())
                .with(new PlayerComponent())
                .build();
    }

    @Spawns("exit_trigger")
    public Entity newExitTrigger(SpawnData data) {
        return FXGL.entityBuilder(data)
                .type(EntityTypes.EXIT_TRIGGER)
                .bbox(new HitBox(BoundingShape.box(data.<Integer>get("width"), data.<Integer>get("height"))))
                .with(new CollidableComponent(true))
                .build();
    }
}
