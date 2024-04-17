package my.javagame;

public class GameConfig {
    public static final Integer GAME_SIZE = 4;

    private static Integer gameEndValue = 2048;

    public Integer getGameEndValue() {
        return gameEndValue;
    }

    public void setGameEndValue(Integer gameEndValue) {
        this.gameEndValue = gameEndValue;
    }

}
