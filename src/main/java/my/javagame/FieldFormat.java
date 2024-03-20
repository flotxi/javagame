package my.javagame;

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public class FieldFormat {
    private final int value;
    private Paint backgroundColor;
    private Paint fieldColor;
    public FieldFormat(int value){
        this.value = value;
        this.calculateBackgroundColor();
        this.calculateFieldColor();
    }

    private void calculateFieldColor() {
        fieldColor = value == 2 || value == 4 ? Color.rgb(119, 110, 101) : Color.WHITE;
    }

    private void calculateBackgroundColor() {
       backgroundColor =  switch (value){
            case 2 -> Color.rgb(238, 228, 218);
            case 4 -> Color.rgb(237, 224, 200);
            case 8 -> Color.rgb(242, 177, 121);
            case 16 -> Color.rgb(245, 149, 99);
            case 32 -> Color.rgb(246, 124, 95);
            case 64 -> Color.rgb(246, 94, 59);
            case 128 -> Color.rgb(237, 207, 114);
            case 256 -> Color.rgb(237, 204, 97);
            case 512 -> Color.rgb(237, 200, 80);
            case 1024 -> Color.rgb(237, 197, 63);
            case 2048 -> Color.rgb(237, 194, 46);
           default -> Color.rgb(204, 192, 179);
        };
    }

    public Paint getBackgroundColor(){
        return backgroundColor;
    }
    public Paint getFieldColor() {
        return fieldColor;
    }
}
