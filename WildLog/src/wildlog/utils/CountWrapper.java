package wildlog.utils;


public class CountWrapper {
    private int count;

    public int getCount() {
        return count;
    }

    public void setCount(int inCount) {
        count = inCount;
    }
    
    public void increaseCount() {
        count++;
    }
    
    public void decreaseCount() {
        count--;
    }
    
}
