package wildlog.utils;

import java.io.OutputStream;
import java.io.PrintStream;
import org.apache.logging.log4j.Level;
import wildlog.WildLogApp;

public class LoggingPrintStream extends PrintStream {
    private final Level level;

    public LoggingPrintStream(OutputStream inOutputStream, Level inLevel) {
        super(inOutputStream);
        level = inLevel;
    }

    @Override
    public void print(String x) {
        WildLogApp.LOGGER.log(level, x);
    }

    @Override
    public void print(Object x) {
        WildLogApp.LOGGER.log(level, x);
    }

    @Override
    public void print(boolean x) {
        WildLogApp.LOGGER.log(level, x);
    }

    @Override
    public void print(char x) {
        WildLogApp.LOGGER.log(level, x);
    }

    @Override
    public void println(Object x) {
        WildLogApp.LOGGER.log(level, x);
    }

    @Override
    public void println(String x) {
        WildLogApp.LOGGER.log(level, x);
    }

    @Override
    public void println(char[] x) {
        WildLogApp.LOGGER.log(level, x);
    }

    @Override
    public void println(double x) {
        WildLogApp.LOGGER.log(level, x);
    }

    @Override
    public void println(float x) {
        WildLogApp.LOGGER.log(level, x);
    }

    @Override
    public void println(long x) {
        WildLogApp.LOGGER.log(level, x);
    }

    @Override
    public void println(int x) {
        WildLogApp.LOGGER.log(level, x);
    }

    @Override
    public void println(char x) {
        WildLogApp.LOGGER.log(level, x);
    }

    @Override
    public void println(boolean x) {
        WildLogApp.LOGGER.log(level, x);
    }

    @Override
    public void println() {
        WildLogApp.LOGGER.log(level, System.lineSeparator());
    }

    @Override
    public void print(char[] x) {
        WildLogApp.LOGGER.log(level, x);
    }

    @Override
    public void print(double x) {
        WildLogApp.LOGGER.log(level, x);
    }

    @Override
    public void print(float x) {
        WildLogApp.LOGGER.log(level, x);
    }

    @Override
    public void print(long x) {
        WildLogApp.LOGGER.log(level, x);
    }

    @Override
    public void print(int x) {
        WildLogApp.LOGGER.log(level, x);
    }
    
}
