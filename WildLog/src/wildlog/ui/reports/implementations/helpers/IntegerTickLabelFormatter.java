package wildlog.ui.reports.implementations.helpers;

import javafx.util.StringConverter;


public class IntegerTickLabelFormatter extends StringConverter<Number> {

    public IntegerTickLabelFormatter() {
    }

    @Override
    public String toString(Number object) {
        if(object.intValue() != object.doubleValue()) {
            return "";
        }
        return "" + (object.intValue());
    }

    @Override
    public Number fromString(String string) {
        Number val = Double.parseDouble(string);
        return val.intValue();
    }
    
}
