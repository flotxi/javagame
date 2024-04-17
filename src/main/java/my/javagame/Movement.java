package my.javagame;

import javafx.scene.input.KeyCode;

public class Movement {
    private final KeyResults keyResult;
    public Movement(KeyCode key){
        keyResult = switch (key){
            case KeyCode.LEFT -> new LeftKey();
            case KeyCode.UP-> new UpKey();
            case KeyCode.DOWN -> new DownKey();
            case KeyCode.RIGHT -> new RightKey();
            default-> throw new UnsupportedKeyException() ;
        };
    }
    public FieldCoordinates getNeighbour(){
        return keyResult.getNeighbour();
    }

    public Integer getStartPoint(){
        return keyResult.getStartPoint();
    }
    public Integer getEndPoint(){
        return keyResult.getEndPoint();
    }
    public Integer getDirection(){
        return keyResult.getDirection();
    }

    private interface KeyResults{
        Integer getDirection();
        Integer getEndPoint();
        Integer getStartPoint();
        FieldCoordinates getNeighbour();
    }
    private class LeftKey implements KeyResults{
        @Override
        public Integer getDirection() {
            return -1;
        }
        @Override
        public Integer getEndPoint() {
            return -1;
        }
        @Override
        public Integer getStartPoint() {
            return GameConfig.GAME_SIZE - 1;
        }
        @Override
        public FieldCoordinates getNeighbour() {
            return new FieldCoordinates(0,1);
        }
    }
    private class DownKey implements KeyResults{
        @Override
        public Integer getDirection() {
            return -1;
        }
        @Override
        public Integer getEndPoint() {
            return -1;
        }
        @Override
        public Integer getStartPoint() {
            return GameConfig.GAME_SIZE - 1;
        }
        @Override
        public FieldCoordinates getNeighbour() {
            return new FieldCoordinates(-1,0);
        }
    }
    private class RightKey implements KeyResults{
        @Override
        public Integer getDirection() {
            return 1;
        }
        @Override
        public Integer getEndPoint() {
            return GameConfig.GAME_SIZE;
        }
        @Override
        public Integer getStartPoint() {
            return 0;
        }
        @Override
        public FieldCoordinates getNeighbour() {
            return new FieldCoordinates(0,-1);
        }
    }
    private class UpKey implements KeyResults{
        @Override
        public Integer getDirection() {
            return 1;
        }
        @Override
        public Integer getEndPoint() {
            return GameConfig.GAME_SIZE;
        }
        @Override
        public Integer getStartPoint() {
            return 0;
        }
        @Override
        public FieldCoordinates getNeighbour() {
            return new FieldCoordinates(1,0);
        }
    }
}
