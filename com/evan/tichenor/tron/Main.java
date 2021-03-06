package com.evan.tichenor.tron;

import com.evan.tichenor.tron.generic.Value;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

import java.util.ArrayList;

import static com.evan.tichenor.tron.TronGame.*;

/**
 * @author Evan Tichenor (evan.tichenor@gmail.com)
 * @version 1.0, 10/16/2017
 */
public class Main extends Application {

    private TronGame game;

    @Override
    public void start(Stage stage) throws Exception {
        game = new TronGame(WIDTH, HEIGHT);

        Group root = new Group();
        Scene scene = new Scene(root);

        root.getChildren().add(game);

        stage.setTitle("TRON Light Bikes");
        stage.setScene(scene);

        ArrayList<KeyCode> keysPressed = new ArrayList<>();

        scene.setOnKeyPressed(event -> {
            if(!keysPressed.contains(event.getCode()))
                keysPressed.add(event.getCode());
        });

        scene.setOnKeyReleased(event ->
            keysPressed.remove(event.getCode())
        );

        game.setKeysPressed(keysPressed);

        Value<Long> lastNanoTime = new Value<>(System.nanoTime()),
                    timer = new Value<>(0L);

        final double ns = 1e+9 / 15; // ns / frame, 15 fps

        Value<Double> delta = new Value<>(0d);

        new AnimationTimer() {
            public void handle(long currentNanoTime) {
                if (game.isPaused()) {
                    lastNanoTime.value = currentNanoTime;
                    keysPressed.removeIf(x -> true);
                    return;
                }

                delta.value += (currentNanoTime - lastNanoTime.value) / ns;
                lastNanoTime.value = currentNanoTime;

                while(delta.value >= 1) {
                    game.update();
                    delta.value--;
                }

                // its been a second!
                if (System.currentTimeMillis() - timer.value > 1000) {
                    timer.value += 1000;
                }
            }
        }.start();

        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
