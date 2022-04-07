import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.MenuType;
import com.almasb.fxgl.app.scene.SceneFactory;

public class UISceneFactory extends SceneFactory {

    @Override
    public FXGLMenu newMainMenu(){
        return new StartMenu(MenuType.MAIN_MENU);
    }
}
