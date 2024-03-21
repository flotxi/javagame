package my.javagame;

import javafx.animation.Animation;
import javafx.animation.AnimationTimer;
import javafx.animation.Transition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.util.Duration;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static my.javagame.Main.resourceBundle;


public class BoardController {
    @FXML
    public Label time;
    @FXML
    public Label score ;
    @FXML
    public Label addedScore ;
    @FXML
    public HBox scoreBox ;
    @FXML
    private VBox vbox;
    private Board board;
    private AnimationTimer timer;
    private final Label[][] labels = new Label[Board.GAME_SIZE][Board.GAME_SIZE];

    @FXML
    void initialize(){
        createLabels();


        startGame();

        final Animation scoreAnimation = new Transition() {
            {
                setCycleDuration(Duration.millis(600L));
            }
            @Override
            protected void interpolate(double progress) {
                addedScore.opacityProperty().set(1.0 - progress);
                // Impuls animation of score hbox
                final double total = scoreBox.getWidth() / 14.0;
                final double current = (1.0 - progress) * total * -1;
                final var fill = scoreBox.getBackground().getFills().getFirst().getFill();
                final var radii = scoreBox.getBackground().getFills().getFirst().getRadii();
                scoreBox.setBackground(new Background(new BackgroundFill(fill, radii, new Insets(current, current, current, current))));
            }
        };

        score.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> ov, String oldText, String newText) {

                int addedScoreValue = Integer.valueOf(newText.replaceAll("[^0-9]", "")) - Integer.valueOf(oldText.replaceAll("[^0-9]", ""));
                addedScore.setText(" +" + Integer.toString(addedScoreValue));

                scoreAnimation.playFromStart();
            }
        });
    }

    private void startGame() {
        initializeBoard();
        initializeScore();
        updateLabels();
        startGameTimer();
    }

    private void initializeScore() {
        setScore(0);
    }

    private void setScore(int newScore){
        score.setText(resourceBundle.getString("score") + newScore);
    }

    private void startGameTimer() {
        var gameStart = LocalDateTime.now();
        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                LocalDateTime tempDateTime = gameStart;

                long hours = tempDateTime.until(LocalDateTime.now(), ChronoUnit.HOURS);
                tempDateTime = tempDateTime.plusHours(hours);

                long minutes = tempDateTime.until(LocalDateTime.now(), ChronoUnit.MINUTES);
                tempDateTime = tempDateTime.plusMinutes(minutes);

                long seconds = tempDateTime.until(LocalDateTime.now(), ChronoUnit.SECONDS);

                time.setText(resourceBundle.getString("duration")  +
                        String.format("%02d", hours) + ":" +
                        String.format("%02d", minutes) + ":" +
                        String.format("%02d", seconds));
            }
        };
        timer.start();
    }

    private void createLabels(){
        var gridPane = new GridPane();
        double squareLength = 700.0 / Board.GAME_SIZE;
        for(int size = 0; size < Board.GAME_SIZE; size++){
            var column = new ColumnConstraints();
            column.setMinWidth(squareLength);
            column.setPrefWidth(squareLength);
            gridPane.getColumnConstraints().add(column);

            var row = new RowConstraints();
            row.setMinHeight(squareLength);
            row.setPrefHeight(squareLength);
            gridPane.getRowConstraints().add(row);
        }
        for(int row = 0; row < Board.GAME_SIZE; row++){
            for (int column = 0; column < Board.GAME_SIZE; column++ ){
                var label = new Label();
                label.setPrefSize(100,100);

                label.getStyleClass().add("column");
                label.getStyleClass().add("labelFont");

                if ( column == 0 ) {
                    label.getStyleClass().add("left");
                }
                if (row == 0) {
                    label.getStyleClass().add("top");
                }

                final Animation creationAnimation = new Transition() {
                    {
                        setCycleDuration(Duration.millis(300L));
                    }
                    @Override
                    protected void interpolate(double progress) {
                        final double total = label.getWidth() / 5.0;
                        final double current = (1.0 - progress) * total;
                        final var fill = label.getBackground().getFills().getFirst().getFill();
                        label.setBackground(new Background(new BackgroundFill(fill, CornerRadii.EMPTY, new Insets(current, current, current, current))));
                    }
                };
                final Animation additionAnimation = new Transition() {
                    {
                        setCycleDuration(Duration.millis(300L));
                    }
                    @Override
                    protected void interpolate(double progress) {
                        final double total = label.getWidth() / 14.0;
                        final double current = (1.0 - progress) * total * -1;
                        final var fill = label.getBackground().getFills().getFirst().getFill();
                        label.setBackground(new Background(new BackgroundFill(fill, CornerRadii.EMPTY, new Insets(current, current, current, current))));
                    }
                };
                label.textProperty().addListener(new ChangeListener<String>() {
                    @Override
                    public void changed(ObservableValue<? extends String> ov, String oldText, String newText) {
                        if(newText != "" && oldText == ""){
                            creationAnimation.playFromStart();
                        } else if (newText != "" && oldText != "") {
                            if (Integer.valueOf(newText) == Integer.valueOf(oldText) * 2){
                                additionAnimation.playFromStart();
                            }
                        }
                    }
                });
                gridPane.add(label, column, row);
                this.labels[row][column] = label;
            }
        }
        vbox.getChildren().add(gridPane);

    }

    @FXML
    protected void onUserInput(KeyEvent keyEvent) {
        moveBoard(keyEvent);
        updateLabels();
    }

    private void updateLabels() {
        var fields = board.getFields();

        for(int row = 0; row < Board.GAME_SIZE; row++){
            for (int column = 0; column < Board.GAME_SIZE; column++ ){
                var field = fields[row][column];
                var label = labels[row][column];

                label.setText(field.getText() );
                label.setBackground(new Background(new BackgroundFill(field.getBackgroundColor(), CornerRadii.EMPTY, Insets.EMPTY)));
                label.setTextFill(field.getColor());
                label.setMaxWidth(Double.MAX_VALUE);
                label.setMaxHeight(Double.MAX_VALUE);
                label.setAlignment(Pos.CENTER);
            }
        }
    }

    private void moveBoard(KeyEvent keyEvent) {
        try {
           board.move(keyEvent.getCode());
           setScore(board.getScore());
        } catch ( Exception  wrongKey ){
            // do nothing
        }
    }

    private void initializeBoard() {
        board = new Board( new ValueRandomizer());
        board.attachGameWonEventListener(this::onGameWon);
        board.attachGameLostEventListener(this::onGameLost);
    }

    public void onGameWon() {
        showResult(resourceBundle.getString("won")).ifPresent(response -> {
            if (response == ButtonType.NO) {
                startGame();
            }else{
                board.keepPlaying();
            }
        });
    }

    private Optional<ButtonType> showResult(String message) {
        updateLabels();

        timer.stop();

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, message, ButtonType.YES, ButtonType.NO);

        alert.setTitle(resourceBundle.getString("alertTitle"));
        alert.setHeaderText(resourceBundle.getString("alertHeader"));

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(
                getClass().getResource("game.css").toExternalForm());
        dialogPane.getStyleClass().add("dialog");
        return alert.showAndWait();
    }

    public void onGameLost() {
        showResult(resourceBundle.getString("lost")).ifPresent(response -> {
            if (response == ButtonType.YES) {
                startGame();
            }else{
                System.exit(0);
            }
        });
    }

}