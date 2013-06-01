package wildlog.data.utils;


public class WildLogDataState {
    private static int counterForAddOrDelete = 1;
    private static int counterForNameChanges = 1;

    public static int getCounterForAddOrDelete() {
        return counterForAddOrDelete;
    }

    public static void setCounterForAddOrDelete(int inCounterForAddOrDelete) {
        counterForAddOrDelete = inCounterForAddOrDelete;
    }

    public static void increaseCounterForAddOrDelete() {
        if (counterForAddOrDelete < Integer.MAX_VALUE) {
            counterForAddOrDelete++;
        }
        else {
            counterForAddOrDelete = 0;
        }
    }

    public static int getCounterForNameChanges() {
        return counterForNameChanges;
    }

    public static void setCounterForNameChanges(int inCounterForNameChanges) {
        counterForNameChanges = inCounterForNameChanges;
    }

    public static void increaseCounterForNameChanges() {
        if (counterForNameChanges < Integer.MAX_VALUE) {
            counterForNameChanges++;
        }
        else {
            counterForNameChanges = 0;
        }
    }

}
