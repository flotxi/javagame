package my.javagame;

import java.util.Random;

public class ValueRandomizer implements Randomizer{
    private final Random random;
    public ValueRandomizer(){
        random = new Random();
    }
    @Override
    public Integer getNextNumber() {
        return this.random.nextBoolean() ? 2 : 4;
    }
    @Override
    public FieldCoordinates getNextFieldCoordinates() {
        int maxGameLength = Board.GAME_SIZE;
        return new FieldCoordinates(random.nextInt(maxGameLength),random.nextInt(maxGameLength));
    }
}
