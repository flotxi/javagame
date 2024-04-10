package my.javagame;

import javafx.scene.input.KeyCode;

import java.util.ArrayList;
import java.util.List;

public class Board {
    public static final Integer GAME_SIZE = 4;
    private Integer gameEndValue = 2048;
    private final Randomizer randomizer;
    private final Field[][] fields = new Field[GAME_SIZE][GAME_SIZE];
    private Integer score = 0;
    private final List<GameWonListener> gameWonListeners;
    private final List<GameLostListener> gameLostListeners;

    public Board(Randomizer randomizer) {
        gameWonListeners = new ArrayList<>();
        gameLostListeners = new ArrayList<>();
        this.randomizer = randomizer;
        initializeFields();
        addNewField();
        addNewField();
    }
    public Integer getScore() {
        return score;
    }
    public void keepPlaying() {
        gameEndValue = gameEndValue * 2;
    }
    public void attachGameWonEventListener(GameWonListener eventListener) {
        this.gameWonListeners.add(eventListener);
    }

    public void attachGameLostEventListener(GameLostListener eventListener) {
        this.gameLostListeners.add(eventListener);
    }

    private void initializeFields() {
        for (int row = 0; row < GAME_SIZE; row++) {
            for (int column = 0; column < GAME_SIZE; column++) {
                fields[row][column] = new Field(0);
            }
        }
    }

    private void addNewField() {
        FieldCoordinates fieldCoordinates;
        int tries = 0;
        int maxTries = Board.GAME_SIZE * Board.GAME_SIZE;
        do {
            fieldCoordinates = randomizer.getNextFieldCoordinates();
            tries++;
            if (tries == maxTries) {
                fieldCoordinates = justTryToGetOneEmptyField();
                break;
            }
        }
        while (fields[fieldCoordinates.row()][fieldCoordinates.column()].getValue() != 0);

        if (fields[fieldCoordinates.row()][fieldCoordinates.column()].getValue() == 0) {
            fields[fieldCoordinates.row()][fieldCoordinates.column()].setValue(this.randomizer.getNextNumber());
        }
    }

    private FieldCoordinates justTryToGetOneEmptyField() {
        for (var row = 0; row < GAME_SIZE; row++){
            for (var column = 0; column < GAME_SIZE; column++){
               var field = fields[row][column];
               if(isFieldEmpty(field)){
                   return new FieldCoordinates(row,column);
               }
            }
        }
        return new FieldCoordinates(0,0);
    }


    public void move(KeyCode key) {
        var fieldsGotMoved = tryMovement(key, false);

        if (fieldsGotMoved) {
            addNewField();
        }

        if (isGameWon()) {
            gameWonListeners.forEach(GameWonListener::onGameWon);
        } else if (isGameOver()) {
            score = 0;
            gameLostListeners.forEach(GameLostListener::onGameLost);
        }
    }

    private boolean tryMovement(KeyCode key, boolean justSimulate) {
        boolean fieldsGotMoved = false;

        Field first;
        Field second;
        Field third;
        Field fourth;

        for (int i = 0; i < GAME_SIZE; i++) {

            switch (key) {
                case LEFT:
                    first = fields[i][0];
                    second = fields[i][1];
                    third = fields[i][2];
                    fourth = fields[i][3];
                    break;
                case RIGHT:
                    first = fields[i][3];
                    second = fields[i][2];
                    third = fields[i][1];
                    fourth = fields[i][0];
                    break;
                case UP:
                    first = fields[0][i];
                    second = fields[1][i];
                    third = fields[2][i];
                    fourth = fields[3][i];
                    break;
                case DOWN:
                    first = fields[3][i];
                    second = fields[2][i];
                    third = fields[1][i];
                    fourth = fields[0][i];
                    break;
                default:
                    continue;
            }
            // todo Simulation is not yet fully implemented
            // 1. Additionen
            if (areFieldsMergeable(first, second)) {
                if(!justSimulate) {
                    updateScore(first, second);
                    mergeFields(first, second);
                }
                fieldsGotMoved = true;

            } else if (areFieldsMergeable(first, third) && second.getValue() == 0) {
                if(!justSimulate) {
                updateScore(first, third);
                mergeFields(first, third);
                }
                fieldsGotMoved = true;

            } else if (areFieldsMergeable(first, fourth) && second.getValue() == 0 && third.getValue() == 0) {
                if(!justSimulate) {
                    updateScore(first, fourth);
                    mergeFields(first, fourth);
                }
                fieldsGotMoved = true;

            } else if (areFieldsMergeable(second, third)) {
                if(!justSimulate) {
                    updateScore(second, third);
                    mergeFields(second, third);
                }
                fieldsGotMoved = true;

            } else if (areFieldsMergeable(second, fourth) && third.getValue() == 0) {
                if(!justSimulate) {
                    updateScore(second, fourth);
                    mergeFields(second, fourth);
                }
                fieldsGotMoved = true;

            } else if (areFieldsMergeable(third, fourth)) {
                if(!justSimulate) {
                    updateScore(third, fourth);
                    mergeFields(third, fourth);
                }
                fieldsGotMoved = true;
            }

            // 2. Verschiebungen
            for ( var move = 0; move < GAME_SIZE; move++) {
                if (isFieldEmpty(first) && !isFieldEmpty(second)  ) {
                    if(!justSimulate) {
                        mergeFields(first, second);
                    }
                    fieldsGotMoved = true;
                }
                if (isFieldEmpty(second) && !isFieldEmpty(third) ) {
                    if(!justSimulate) {
                        mergeFields(second, third);
                    }
                    fieldsGotMoved = true;
                }
                if (isFieldEmpty(third) && !isFieldEmpty(fourth) ) {
                    if(!justSimulate) {
                        mergeFields(third, fourth);
                    }
                    fieldsGotMoved = true;
                }
            }
            
        }

        return fieldsGotMoved ;
    }

    private void updateScore(Field first, Field second) {
        if(first.getValue() != 0 && second.getValue() != 0) {
            score += second.getValue();
        }
    }

    private boolean isFieldEmpty(Field field) {
        return field.getValue() == 0;
    }

    private boolean areFieldsMergeable(Field first, Field second) {
        return first.getValue().equals(second.getValue()) && first.getValue() != 0;
    }

    private void mergeFields(Field first, Field second) {
        first.setValue(first.getValue() + second.getValue());
        second.setValue(0);
    }

    // Game is only over when the board filled is and there is no more movement possible
    private boolean isGameOver() {
        if (isValueInFields(0)) return false;
        return !isAnyMovementPossible();
    }

    private boolean isAnyMovementPossible() {
        return  tryMovement(KeyCode.LEFT, true) ||
                tryMovement(KeyCode.UP, true) ||
                tryMovement(KeyCode.DOWN, true) ||
                tryMovement(KeyCode.RIGHT, true);
    }

    private boolean isValueInFields(int value) {
        for (var row = 0; row < GAME_SIZE; row++) {
            for (var column = 0; column < GAME_SIZE; column++) {
                if (fields[row][column].getValue() == value) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isGameWon() {
        return isValueInFields(gameEndValue);
    }

    public Field[][] getFields() {
        return this.fields;
    }
}
