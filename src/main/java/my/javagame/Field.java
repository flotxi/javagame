package my.javagame;

import javafx.scene.paint.Paint;

public class Field {
    private Integer value;
    public Field( Integer startValue ){ value = startValue; }
    public void setValue(Integer value){
        this.value = value;
    }
    public Integer getValue(){
        return value;
    }
    public String getText(){
        return value == 0 ? "" : value.toString();
    }
    public Paint getBackgroundColor(){
        return new FieldFormat(value).getBackgroundColor();
    }
    public Paint getColor(){
        return new FieldFormat(value).getFieldColor();
    }

}
