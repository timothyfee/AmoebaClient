package amoebaclient;

import DataTransferUnits.KeyValuePair;
import DataTransferUnits.NetworkMessage;
import Messages.*;
import NetworkComponents.AmoebaNetworkClientController;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;

public class AmoebaClient extends Application {

    public long lag;
    private final Label lagLabel = new Label();
    private long lastReceivedTime;

    private final ArrayList<Drawable> drawableCache = new ArrayList<>();

    private final AnchorPane renderPane = new AnchorPane();
    private final TextArea chatBox = new TextArea();
    private final TextField chatInput = new TextField();

    private final TextArea highScores = new TextArea();
    private final TextArea currentScores = new TextArea();
    private final Group scores = new Group(highScores, currentScores);

    private final AmoebaNetworkClientController networkController = new AmoebaNetworkClientController();

    private double x, y;
    private boolean sendCoordinates;

    private final MediaPlayer music = new MediaPlayer(new Media("http://23.28.17.54/network/Timothy/Music/misc/Sonic%20Unleased/27-Holoska%20~%20Cool%20Edge%20Day.mp3"));
    private final BackgroundImage backgroundImage = new BackgroundImage(new Image("http://23.28.17.54/network/Timothy/gameBackground.png"),
            BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER,
            BackgroundSize.DEFAULT);

    @Override
    public void start(Stage primaryStage) {

        VBox loginPane = new VBox();
        loginPane.setAlignment(Pos.CENTER);
        primaryStage.setTitle("Amoeba");
        primaryStage.setScene(new Scene(loginPane, 800, 600));
        primaryStage.show();

        TextField username = new TextField();
        username.setPromptText("Username");
        username.setMaxSize(200, 50);

        Timeline renderTimer = new Timeline();
        Timeline inputTimer = new Timeline();

        ColorPicker colorPicker = new ColorPicker();
        colorPicker.setEditable(true);
        colorPicker.setValue(Color.BLACK);

        lagLabel.setTranslateX(25);
        lagLabel.setTranslateY(10);
        lagLabel.setTextFill(Color.RED);

        TextField address = new TextField("localhost");
        address.setPromptText("Server IP Address");
        address.setMaxSize(200, 50);

        TextField port = new TextField("8080");
        port.setPromptText("Port Number");
        port.setMaxSize(200, 50);

        Button btn = new Button("Play");
        btn.setDefaultButton(true);

        loginPane.getChildren().addAll(username, colorPicker, address, port, btn);

        primaryStage.setResizable(false);
        music.setCycleCount(MediaPlayer.INDEFINITE);
        renderPane.setBackground(new Background(backgroundImage));
        renderPane.setPrefSize(800, 600);

        chatBox.setPrefSize(400, 100);
        chatBox.setTranslateX(0);
        chatBox.setTranslateY(468);
        chatBox.setWrapText(true);
        chatBox.setEditable(false);
        chatBox.setStyle("-fx-opacity: 0.5");

        chatInput.setPrefSize(400, 10);
        chatInput.setTranslateX(0);
        chatInput.setTranslateY(570);
        chatInput.setStyle("-fx-opacity: 0.8");

        highScores.setPrefSize(400, 210);
        highScores.setEditable(false);
        currentScores.setPrefSize(400, 250);
        currentScores.setEditable(false);
        currentScores.setLayoutY(210);
        scores.setTranslateX(-390);
        scores.setTranslateY(0);
        scores.setStyle("-fx-opacity: 0.8");

        scores.setOnMouseEntered((e) -> {
            scores.setTranslateX(0);
        });

        scores.setOnMouseExited((e) -> {
            if (e.getX() > 350) {
                scores.setTranslateX(-390);
            }
        });

        btn.setOnAction((e) -> {
            if (!networkController.isConnected()) {
                networkController.connect(address.getText(), Integer.parseInt(port.getText()));
                if (username.getText().isEmpty()) {
                    username.setText("Anonymous");
                }
                networkController.sendMessage(new LoginMessage(username.getText(), ""));

                ArrayList<KeyValuePair> properties = new ArrayList<>();
                properties.add(new KeyValuePair("color", colorPicker.getValue().toString()));
                networkController.sendMessage(new SetBlobPropertiesMessage(properties));

                primaryStage.setScene(new Scene(renderPane));
                renderTimer.play();
                inputTimer.play();
                music.play();
            }
        });

        inputTimer.setCycleCount(Timeline.INDEFINITE);
        inputTimer.getKeyFrames().add(new KeyFrame(Duration.seconds(0.05), (e) -> {
            if (sendCoordinates) {
                networkController.sendMessage(new MoveTowardCoordinatesMessage(x, y));
            }
        }));

        renderTimer.setCycleCount(Timeline.INDEFINITE);
        renderTimer.getKeyFrames().add(new KeyFrame(Duration.seconds(0.01), (e) -> {
            render();
        }));

        renderPane.setOnMouseReleased((e) -> {
            this.sendCoordinates = false;
        });

        renderPane.setOnMouseDragged((e) -> {
            this.sendCoordinates = true;
            this.x = e.getX();
            this.y = e.getY();
        });

        renderPane.setOnMouseClicked((e) -> {
            if (e.getButton() == MouseButton.SECONDARY) {
                networkController.sendMessage(new BoostMessage());
            } else if (e.getClickCount() > 1 || e.getButton() == MouseButton.MIDDLE) {
                networkController.sendMessage(new SplitMessage());
            }
        });

        renderPane.setOnKeyPressed((e) -> {
            if (e.getCode().equals(KeyCode.ENTER) && !chatInput.getText().isEmpty()) {
                networkController.sendMessage(new ChatMessage("", chatInput.getText()));
                chatInput.clear();
            }
        });

        primaryStage.setOnCloseRequest((e) -> {
            networkController.sendMessage(new LogoutMessage());
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                Logger.getLogger(AmoebaClient.class.getName()).log(Level.WARNING, null, ex);
            } finally {
                System.exit(0);
            }
        });
    }

    private Circle drawCircle(double x, double y, double radius, Color color) {
        Circle circle = new Circle(x, y, radius, color);
        RadialGradient gradient1 = new RadialGradient(
                0,
                0.1,
                x,
                y,
                radius,
                false,
                CycleMethod.NO_CYCLE,
                new Stop(0, color),
                new Stop(1, Color.BLACK));
        circle.setFill(gradient1);
        circle.setStyle("-fx-opacity: 0.8;");
        return circle;
    }

    private Text drawText(double x, double y, String text, Color color) {
        Text t = new Text(x, y, text);
        t.setFill(color);
        t.setFont(Font.font("Tahoma", 20));
        t.setTextAlignment(TextAlignment.CENTER);
        return t;
    }

    private Drawable getCachedDrawable(long id) {
        for (Drawable drawable : drawableCache) {
            if (drawable.id == id) {
                return drawable;
            }
        }
        return null;
    }

    private void render() {
        ArrayList<NetworkMessage> messages = networkController.getMessages();

        for (NetworkMessage message : messages) {
            String messageType = message.getClass().getName().replace("NetworkMessages.", "").replace("Messages.", "");
            switch (messageType) {
                case "PelletPositionMessage": {
                    PelletPositionMessage m = (PelletPositionMessage) message;
                    Drawable drawable = getCachedDrawable(m.id);
                    if (drawable != null) {
                        drawable.node.relocate(m.x, m.y);
                        drawable.cacheMisses = 0;
                    } else {
                        Random r = new Random();
                        Node node = drawCircle(m.x, m.y, 5, new Color(r.nextFloat(), r.nextFloat(), r.nextFloat(), 1));
                        drawableCache.add(new Drawable(m.id, node));
                    }
                }
                break;
                case "BlobStateMessage": {
                    BlobStateMessage m = (BlobStateMessage) message;
                    Color color = Color.web(m.color);
                    Node node;
                    if (color.getBrightness() != 0) {
                        node = drawCircle(m.x, m.y, m.size / 2, color);
                    } else {
                        node = drawCircle(m.x, m.y, m.size / 2, color.brighter().brighter());
                    }
                    Node nameText = drawText((m.x - m.username.length() * 5), (m.y - m.size / 2), m.username, color);
                    Drawable drawable = getCachedDrawable(m.id);
                    if (drawable != null) {
                        node.setLayoutX((drawable.node.getTranslateX() - m.x) / 10000);
                        node.setLayoutY((drawable.node.getTranslateY() - m.y) / 10000);
                        drawable.node = node;
                        drawable.cacheMisses = 0;
                        Drawable nameTag = getCachedDrawable(m.id + 9000);
                        nameTag.node = nameText;
                        nameTag.cacheMisses = 0;
                    } else {
                        drawableCache.add(new Drawable(m.id, node));
                        drawableCache.add(new Drawable(m.id + 9000, nameText));
                    }
                }
                break;
                case "PingMessage": {
                    renderPane.getChildren().clear();
                    ArrayList<Drawable> drawables = new ArrayList<>(drawableCache);
                    for (Drawable drawable : drawables) {
                        if (drawable.cacheMisses > 1) {
                            drawableCache.remove(drawable);
                        }
                        drawable.cacheMisses++;
                        renderPane.getChildren().add(drawable.node);
                    }
                    renderPane.getChildren().addAll(chatBox, chatInput, lagLabel, scores);

                    lag = System.currentTimeMillis() - lastReceivedTime;
                    lastReceivedTime = System.currentTimeMillis();
                    lagLabel.setText("Net Lag: " + Long.toString(lag));
                }
                break;
                case "HighScoreMessage": {
                    HighScoreMessage m = (HighScoreMessage) message;
                    highScores.setText("HIGH SCORES:" + m.text);
                }
                break;
                case "CurrentScoreMessage": {
                    CurrentScoreMessage m = (CurrentScoreMessage) message;
                    currentScores.setText("Current Scores:" + m.text);
                }
                break;
                case "ChatMessage": {
                    ChatMessage m = (ChatMessage) message;
                    chatBox.appendText(m.username + "> " + m.text + "\n");
                }
                break;
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
