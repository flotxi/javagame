package my.javagame;

import javafx.scene.input.KeyCode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

class BoardMovementTests extends TestHelper {

    @BeforeEach
    void setUp() {
        initializeBoardWithMocker(new FieldCoordinatesWithValue(0, 0, 2),
                new FieldCoordinatesWithValue(1, 1, 4));
    }

    @Test
    void board_should_be_initialized_with_2_values() {
        fieldsAreEqualTo(new FieldCoordinatesWithValue(0, 0, 2),
                new FieldCoordinatesWithValue(1, 1, 4));
    }


    @Test
    void board_should_be_moved_right() {
        board.move(KeyCode.RIGHT);

        fieldsAreEqualTo(new FieldCoordinatesWithValue(0, end, 2),
                new FieldCoordinatesWithValue(1, end, 4));
    }

    @Test
    void board_should_be_moved_left() {
        board.move(KeyCode.LEFT);

        fieldsAreEqualTo(new FieldCoordinatesWithValue(0, 0, 2),
                new FieldCoordinatesWithValue(1, 0, 4));
    }

    @Test
    void board_should_be_moved_up() {
        board.move(KeyCode.UP);

        fieldsAreEqualTo(new FieldCoordinatesWithValue(0, 0, 2),
                new FieldCoordinatesWithValue(0, 1, 4));
    }

    @Test
    void board_should_be_moved_down() {
        board.move(KeyCode.DOWN);

        fieldsAreEqualTo(new FieldCoordinatesWithValue(end, 0, 2),
                new FieldCoordinatesWithValue(end, 1, 4));
    }

    @Test
    void no_new_field_should_be_added() {
        addNewFieldToBoard(new FieldCoordinatesWithValue(0, 1, 8));
        addNewFieldToBoard(new FieldCoordinatesWithValue(0, 2, 16));
        addNewFieldToBoard(new FieldCoordinatesWithValue(0, 3, 32));

        board.move(KeyCode.UP);

        fieldsAreEqualTo(new FieldCoordinatesWithValue(1, 1, 4),
                new FieldCoordinatesWithValue(1, 2, 0),
                new FieldCoordinatesWithValue(1, 3, 0),
                new FieldCoordinatesWithValue(2, 0, 0),
                new FieldCoordinatesWithValue(2, 1, 0),
                new FieldCoordinatesWithValue(2, 2, 0),
                new FieldCoordinatesWithValue(2, 3, 0),
                new FieldCoordinatesWithValue(3, 0, 0),
                new FieldCoordinatesWithValue(3, 1, 0),
                new FieldCoordinatesWithValue(3, 2, 0),
                new FieldCoordinatesWithValue(3, 3, 0));
    }

}

class AdvancedBoardMovementTests extends TestHelper {

    @Test
    void no_double_addition_expected() {
        int column = 1;
        initializeBoardWithMocker(new FieldCoordinatesWithValue(0, column, 4),
                new FieldCoordinatesWithValue(1, column, 2));
        addNewFieldToBoard(new FieldCoordinatesWithValue(2, column, 2));

        setMockFields(new FieldCoordinatesWithValue(2, 3, 4));

        board.move(KeyCode.DOWN);

        fieldsAreEqualTo(new FieldCoordinatesWithValue(2, column, 4),
                new FieldCoordinatesWithValue(3, column, 4),
                new FieldCoordinatesWithValue(2, 3, 4));
    }

}

class BoardRulesTests extends TestHelper {

    private Boolean[] eventSpy;

    @BeforeEach
    void setUp() {
        fillBoardCompletely();
        eventSpy = hasEventHappened();
    }

    @Test
    void game_should_be_over() {
        addNewFieldToBoard(new FieldCoordinatesWithValue(0, 3, 2));

        board.move(KeyCode.DOWN);

        Assertions.assertTrue(eventSpy[1], "Game LOST event was NOT triggered!");
    }

    @Test
    void game_should_be_won() {
        addNewFieldToBoard(new FieldCoordinatesWithValue(0, 3, 2048));

        board.move(KeyCode.DOWN);

        Assertions.assertTrue(eventSpy[0], "Game WON event was NOT triggered!");
    }

    private Boolean[] hasEventHappened() {
        final Boolean[] result = {false, false};
        board.attachGameWonEventListener(() -> result[0] = true);
        board.attachGameLostEventListener(() -> result[1] = true);
        return result;
    }

    private void fillBoardCompletely() {
        initializeBoardWithMocker(
                new FieldCoordinatesWithValue(0, 0, 2),
                new FieldCoordinatesWithValue(0, 1, 4));

        FieldCoordinatesWithValue[] fieldCoordinates = {new FieldCoordinatesWithValue(0, 2, 8),
                new FieldCoordinatesWithValue(0, 3, 16),
                new FieldCoordinatesWithValue(1, 0, 32),
                new FieldCoordinatesWithValue(1, 1, 64),
                new FieldCoordinatesWithValue(1, 2, 128),
                new FieldCoordinatesWithValue(1, 3, 64),
                new FieldCoordinatesWithValue(2, 0, 2),
                new FieldCoordinatesWithValue(2, 1, 4),
                new FieldCoordinatesWithValue(2, 2, 8),
                new FieldCoordinatesWithValue(2, 3, 16),
                new FieldCoordinatesWithValue(3, 0, 32),
                new FieldCoordinatesWithValue(3, 1, 64),
                new FieldCoordinatesWithValue(3, 2, 128)};

        for (var fieldCoordinate : fieldCoordinates) {
            addNewFieldToBoard(fieldCoordinate);
        }
    }

}


record FieldCoordinatesWithValue(Integer rowToCheck, Integer columnToCheck, Integer expectedFieldValue) {
}

class RandomizerMock implements Randomizer {

    public FieldCoordinatesWithValue[] mockFields = new FieldCoordinatesWithValue[GameConfig.GAME_SIZE * GameConfig.GAME_SIZE];

    public Integer currentIndex = 0;
    public Integer currentNumberIndex = 0;

    @Override
    public Integer getNextNumber() {
        Integer result;
        try {
            result = mockFields[currentNumberIndex].expectedFieldValue();
            currentNumberIndex++;
            if (currentNumberIndex == (GameConfig.GAME_SIZE * GameConfig.GAME_SIZE - 1)) {
                currentNumberIndex = 0;
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            result = mockFields[0].expectedFieldValue();
        }

        return result;
    }

    @Override
    public FieldCoordinates getNextFieldCoordinates() {
        try {
            var coordinatesWithValues = mockFields[currentIndex];
            currentIndex++;
            if (currentIndex == (GameConfig.GAME_SIZE * GameConfig.GAME_SIZE - 1)) {
                currentIndex = 0;
            }
            return new FieldCoordinates(coordinatesWithValues.rowToCheck(), coordinatesWithValues.columnToCheck());
        } catch (ArrayIndexOutOfBoundsException e) {
            var coordinatesWithValues = mockFields[0];
            return new FieldCoordinates(coordinatesWithValues.rowToCheck(), coordinatesWithValues.columnToCheck());
        }
    }
}

class TestHelper {
    final int end = GameConfig.GAME_SIZE - 1;
    Board board;
    RandomizerMock mock;

    protected void addNewFieldToBoard(FieldCoordinatesWithValue fieldCoordinatesWithValue) {
        Method addNewField;
        try {
            addNewField = Board.class.getDeclaredMethod("addNewField");
            addNewField.setAccessible(true);
            setMockFields(fieldCoordinatesWithValue);
            addNewField.invoke(board);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    class MyAssertion implements Executable {
        private final String expectedText;
        private final String actualText;
        private final String assertMessage;
        public MyAssertion(String expectedText, String actualText, String assertMessage){
            this.actualText = actualText;
            this.expectedText = expectedText;
            this.assertMessage = assertMessage;
        }
        @Override
        public void execute() {
            Assertions.assertEquals(expectedText, actualText, assertMessage);
        }
    }

    protected void fieldsAreEqualTo(FieldCoordinatesWithValue... fieldCoordinatesWithValue) {

        Executable[] myAssertions = new Executable[fieldCoordinatesWithValue.length];

        int index = 0;
        var actual = board.getFields();
        for (var expected : fieldCoordinatesWithValue) {
            var expectedText = expected.expectedFieldValue().toString();
            var actualText = actual[expected.rowToCheck()][expected.columnToCheck()].getValue().toString();
            var assertMessage = "Wrong value in row: " + expected.rowToCheck() + " column: " + expected.columnToCheck();
            myAssertions[index] = new MyAssertion(expectedText, actualText, assertMessage);
            index++;
        }
        Assertions.assertAll( myAssertions );
    }


    protected void initializeBoardWithMocker(FieldCoordinatesWithValue... fieldCoordinatesWithValue) {
        mock = new RandomizerMock();
        setMockFields(fieldCoordinatesWithValue);
        board = new Board(mock);
    }

    protected void setMockFields(FieldCoordinatesWithValue... fieldCoordinatesWithValue) {
        mock.mockFields = fieldCoordinatesWithValue;
    }

}