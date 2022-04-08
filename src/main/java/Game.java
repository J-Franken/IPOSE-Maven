import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.app.scene.*;
import com.almasb.fxgl.audio.Music;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.level.Level;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.input.virtual.VirtualButton;
import com.almasb.fxgl.physics.CollisionHandler;
import com.almasb.fxgl.physics.PhysicsComponent;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Builder;
import javafx.util.Duration;
import javafx.scene.control.ButtonBase;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.Writer;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

import static com.almasb.fxgl.dsl.FXGL.*;


public class Game extends GameApplication {

    private static final int MAX_LEVEL = 4;
    private static final int STARTING_LEVEL = 0;
    private Entity player;
    private int ms = 0;
    private int sec = 0;
    private int min = 0;
    private String doehetNaam;
    private String naam1;
    private String naam2;
    private String naam3;
    private int moneyBags1;
    private int moneyBags2;
    private int moneyBags3;
    private int min1;
    private int sec1;
    private int ms1;
    private int min2;
    private int sec2;
    private int ms2;
    private int min3;
    private int sec3;
    private int ms3;


    @Override
    protected void initSettings(GameSettings gameSettings) {
        gameSettings.setWidth(15 * 70);
        gameSettings.setHeight(20 * 32);
        gameSettings.setTitle("A Way Out");
        gameSettings.setVersion("1.2");
        gameSettings.setSceneFactory(new UISceneFactory());
        gameSettings.setMainMenuEnabled(true);
    }

    @Override
    protected void initGame(){
        getGameWorld().addEntityFactory(new GameFactory());
        getAudioPlayer().stopAllMusic();
        loopBGM("dungeon.wav");

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
    protected void onPreInit() {
        getSettings().setGlobalMusicVolume(0.25);
        loopBGM("maintheme.wav");
    }

    @Override
    protected void initPhysics() {
        getPhysicsWorld().setGravity(0, 760);
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityTypes.PLAYER, EntityTypes.COIN) {
            @Override
            protected void onCollision(Entity player, Entity coin) {
                play("kaching.wav");
                inc("coin", +1);
                coin.removeFromWorld();
            }
        });

        onCollisionOneTimeOnly(EntityTypes.PLAYER, EntityTypes.CUTSCENE, (player, cutscene) -> {
            getDialog();
        });

        onCollision(EntityTypes.PLAYER, EntityTypes.OBSTACLE, (player, obstacle) -> {
            onPlayerDied();
            play("scream.wav");
        });

        onCollision(EntityTypes.PLAYER, EntityTypes.ENEMY, (player, enemy) -> {
            onPlayerDied();
            play("kraai.wav");
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
        if ((geti("coin")) < 0) {
            set("coin", 0);
        }
    }

    protected void initGameVars(Map<String, Object> vars){
        vars.put("level", STARTING_LEVEL);
        vars.put("coin",0);
    }

    public void onPlayerDied() {
            setLevel(geti("level"));
            inc("coin", -2);
    }

    public void getDialog(){
        getDialogService().showMessageBox("Let's get outta here!", () -> {
        });
    }

    private void nextLevel() {
        if (geti("level") == MAX_LEVEL) {
            askName();
        } else {
            inc("level", +1);
            setLevel(geti("level"));
        }
    }

    public void askName(){
        StringBuilder builder = new StringBuilder();
        FXGL.getDialogService().showInputBox("Vul hier jouw naam in: ", name -> {
            doehetNaam = name;
            try {
                createScoreboard();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        });
    }
    public void schrijfNaam1() throws IOException {
        String filePath = "src/main/Namen.txt";
        Scanner sc = new Scanner(new File(filePath));
        StringBuffer buffer = new StringBuffer();
        while (sc.hasNextLine()) {
            buffer.append(sc.nextLine()+System.lineSeparator());
        }
        String fileContents = buffer.toString();
        System.out.println("Contents of the file: "+fileContents);
        sc.close();
        String oldNaam = naam1;
        String newNaam = doehetNaam;
        fileContents = fileContents.replaceAll(oldNaam, newNaam);
        FileWriter writer = new FileWriter(filePath);
        System.out.println("");
        System.out.println("new data: "+fileContents);
        writer.append(fileContents);
        writer.flush();
    }
    public void schrijfNaam2() throws IOException {
        String filePath = "src/main/Namen.txt";
        Scanner sc = new Scanner(new File(filePath));
        StringBuffer buffer = new StringBuffer();
        while (sc.hasNextLine()) {
            buffer.append(sc.nextLine()+System.lineSeparator());
        }
        String fileContents = buffer.toString();
        System.out.println("Contents of the file: "+fileContents);
        sc.close();
        String oldNaam = naam2;
        String newNaam = doehetNaam;
        fileContents = fileContents.replaceAll(oldNaam, newNaam);
        FileWriter writer = new FileWriter(filePath);
        System.out.println("");
        System.out.println("new data: "+fileContents);
        writer.append(fileContents);
        writer.flush();
    }
    public void schrijfNaam3() throws IOException {
        String filePath = "src/main/Namen.txt";
        Scanner sc = new Scanner(new File(filePath));
        StringBuffer buffer = new StringBuffer();
        while (sc.hasNextLine()) {
            buffer.append(sc.nextLine()+System.lineSeparator());
        }
        String fileContents = buffer.toString();
        System.out.println("Contents of the file: "+fileContents);
        sc.close();
        String oldNaam = naam3;
        String newNaam = doehetNaam;
        fileContents = fileContents.replaceAll(oldNaam, newNaam);
        FileWriter writer = new FileWriter(filePath);
        System.out.println("");
        System.out.println("new data: "+fileContents);
        writer.append(fileContents);
        writer.flush();
    }
    public void schrijfMoneyBags1() throws IOException {
        String filePath = "src/main/Moneybags.txt";
        Scanner sc = new Scanner(new File(filePath));
        StringBuffer buffer = new StringBuffer();
        while (sc.hasNextLine()) {
            buffer.append(sc.nextLine()+System.lineSeparator());
        }
        String fileContents = buffer.toString();
        System.out.println("Contents of the file: "+fileContents);
        sc.close();
        int oldCoins = moneyBags1;
        int newCoins = FXGL.geti("coin");
        fileContents = fileContents.replaceAll(String.valueOf(oldCoins), String.valueOf(newCoins));
        FileWriter writer = new FileWriter(filePath);
        System.out.println("");
        System.out.println("new data: "+fileContents);
        writer.append(fileContents);
        writer.flush();
    }
    public void schrijfMoneyBags2() throws IOException {
        String filePath = "src/main/Moneybags.txt";
        Scanner sc = new Scanner(new File(filePath));
        StringBuffer buffer = new StringBuffer();
        while (sc.hasNextLine()) {
            buffer.append(sc.nextLine()+System.lineSeparator());
        }
        String fileContents = buffer.toString();
        System.out.println("Contents of the file: "+fileContents);
        sc.close();
        int oldCoins = moneyBags2;
        int newCoins = FXGL.geti("coin");
        fileContents = fileContents.replaceAll(String.valueOf(oldCoins), String.valueOf(newCoins));
        FileWriter writer = new FileWriter(filePath);
        System.out.println("");
        System.out.println("new data: "+fileContents);
        writer.append(fileContents);
        writer.flush();
    }
    public void schrijfMoneyBags3() throws IOException {
        String filePath = "src/main/Moneybags.txt";
        Scanner sc = new Scanner(new File(filePath));
        StringBuffer buffer = new StringBuffer();
        while (sc.hasNextLine()) {
            buffer.append(sc.nextLine()+System.lineSeparator());
        }
        String fileContents = buffer.toString();
        System.out.println("Contents of the file: "+fileContents);
        sc.close();
        int oldCoins = moneyBags3;
        int newCoins = FXGL.geti("coin");
        fileContents = fileContents.replaceAll(String.valueOf(oldCoins), String.valueOf(newCoins));
        FileWriter writer = new FileWriter(filePath);
        System.out.println("");
        System.out.println("new data: "+fileContents);
        writer.append(fileContents);
        writer.flush();
    }
    public void schrijfTijd1() throws IOException {
        String filePath = "src/main/Tijd.txt";
        Scanner sc = new Scanner(new File(filePath));
        StringBuffer buffer = new StringBuffer();
        while (sc.hasNextLine()) {
            buffer.append(sc.nextLine()+System.lineSeparator());
        }
        String fileContents = buffer.toString();
        System.out.println("Contents of the file: "+fileContents);
        sc.close();
        int oldMinTime = min1;
        int newMinTime = min;
        int oldSecTime = sec1;
        int newSecTime = sec;
        int oldMsTime = ms1;
        int newMsTime = ms;
        fileContents = fileContents.replaceAll(oldMinTime +":"+ oldSecTime+":"+ oldMsTime, newMinTime +":"+ newSecTime +":"+ newMsTime);
        FileWriter writer = new FileWriter(filePath);
        System.out.println("");
        System.out.println("new data: "+fileContents);
        writer.append(fileContents);
        writer.flush();
    }
    public void schrijfTijd2() throws IOException {
        String filePath = "src/main/Tijd.txt";
        Scanner sc = new Scanner(new File(filePath));
        StringBuffer buffer = new StringBuffer();
        while (sc.hasNextLine()) {
            buffer.append(sc.nextLine()+System.lineSeparator());
        }
        String fileContents = buffer.toString();
        System.out.println("Contents of the file: "+fileContents);
        sc.close();
        int oldMinTime = min2;
        int newMinTime = min;
        int oldSecTime = sec2;
        int newSecTime = sec;
        int oldMsTime = ms2;
        int newMsTime = ms;
        fileContents = fileContents.replaceAll(oldMinTime +":"+ oldSecTime+":"+ oldMsTime, newMinTime +":"+ newSecTime +":"+ newMsTime);
        FileWriter writer = new FileWriter(filePath);
        System.out.println("");
        System.out.println("new data: "+fileContents);
        writer.append(fileContents);
        writer.flush();
    }
    public void schrijfTijd3() throws IOException {
        String filePath = "src/main/Tijd.txt";
        Scanner sc = new Scanner(new File(filePath));
        StringBuffer buffer = new StringBuffer();
        while (sc.hasNextLine()) {
            buffer.append(sc.nextLine()+System.lineSeparator());
        }
        String fileContents = buffer.toString();
        System.out.println("Contents of the file: "+fileContents);
        sc.close();
        int oldMinTime = min3;
        int newMinTime = min;
        int oldSecTime = sec3;
        int newSecTime = sec;
        int oldMsTime = ms3;
        int newMsTime = ms;
        fileContents = fileContents.replaceAll(oldMinTime +":"+ oldSecTime+":"+ oldMsTime, newMinTime +":"+ newSecTime +":"+ newMsTime);
        FileWriter writer = new FileWriter(filePath);
        System.out.println("");
        System.out.println("new data: "+fileContents);
        writer.append(fileContents);
        writer.flush();
    }

    public void createScoreboard() throws FileNotFoundException {
        StringBuilder builder = new StringBuilder();

        int totalTime = min * 60 + sec * 60 + ms;

        if (totalTime < min1*60 + sec1*60 + ms1){
            try {
                schrijfNaam1();
                schrijfMoneyBags1();
                schrijfTijd1();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (totalTime < min2*60 + sec2*60 + ms2){
            try {
                schrijfNaam2();
                schrijfMoneyBags2();
                schrijfTijd2();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (totalTime < min3*60 + sec3*60 + ms3){
            try {
                schrijfNaam3();
                schrijfMoneyBags3();
                schrijfTijd3();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            File f = new File("src/main/Namen.txt");
            Scanner sc = new Scanner(f);

            while(sc.hasNextLine()){
                String line = sc.nextLine();
                String[] names = line.split(";");
                naam1 = names[names.length -3];
                naam2 = names[names.length -2];
                naam3 = names[names.length -1];

            }
            sc.close();

            File myObj = new File("src/main/Moneybags.txt");
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String line = myReader.nextLine();
                String[] bags = line.split(";");
                moneyBags1 = Integer.parseInt(bags[bags.length -3]);
                moneyBags2 = Integer.parseInt(bags[bags.length -2]);
                moneyBags3 = Integer.parseInt(bags[bags.length -1]);
            }
            myReader.close();
            File cmon = new File("src/main/Tijd.txt");
            Scanner ttt = new Scanner(cmon);

            while(ttt.hasNextLine()){
                String line = ttt.nextLine();
                String[] tijden = line.split(":");
                min1 = Integer.parseInt(tijden[tijden.length -9]);
                sec1 = Integer.parseInt(tijden[tijden.length -8]);
                ms1 = Integer.parseInt(tijden[tijden.length -7]);
                min2 = Integer.parseInt(tijden[tijden.length -6]);
                sec2 = Integer.parseInt(tijden[tijden.length -5]);
                ms2 = Integer.parseInt(tijden[tijden.length -4]);
                min3 = Integer.parseInt(tijden[tijden.length -3]);
                sec3 = Integer.parseInt(tijden[tijden.length -2]);
                ms3 = Integer.parseInt(tijden[tijden.length -1]);

            }
            sc.close();

        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        builder.append("You found a way out!!\n\n")
                .append("\nTotal Time: \t")
                .append(min + ":" + sec+ ":" + ms)
                .append("\nCollected cashbags: \t")
                .append(FXGL.geti("coin"))
                .append("\n\nScoreboard:")

                .append("\n1: " + naam1 + "\t Moneybags: " + moneyBags1 + "\tTime: " + min1+":"+sec1+":"+ms1)
                .append("\n2: " + naam2 + "\t\t Moneybags: " + moneyBags2 + "\tTime: " + min2+":"+sec2+":"+ms2)
                .append("\n3: " + naam3 + "\t\t Moneybags: " + moneyBags3 + "\tTime: " + min3+":"+sec3+":"+ms3)
                .append("\n\nVul hier het cijfer in dat je onze game geeft: ");
        FXGL.getDialogService().showInputBox(builder.toString(), name -> {
            FXGL.getGameController().gotoMainMenu();
            min = 0;
            sec = 0;
            ms = 0;
        });
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
