package org.poker.analyzers.deprecated;

import org.poker.core.Combination;

import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.function.Predicate;

@SuppressWarnings("unused")
public class Logger {
    private PrintStream out;
    private SimpleDateFormat format;
    boolean INFO = true;
    boolean LOG = true;
    boolean FATAL = true;
    boolean WARNING = true;
    boolean DEBUG = true;
    boolean ERROR = true;
    boolean TRACE = true;
    boolean ONLY_INFO = false;
    String append = "";
    private boolean All;
    private int logPower = 5;
    private int logCounter = 0;
    private Predicate<Combination> predicate = combination -> true;
    final static String DO_NOT_USE_DATE = "don\'t use date";
    final static String STANDARD_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    Logger (PrintStream out, String dateFormat) {
        All = out != null;
        if (dateFormat.equals(DO_NOT_USE_DATE)) format = null;
        else format = new SimpleDateFormat(dateFormat);
        this.out = out;
    }
    Logger () {
        setAll(false);
        All = false;
        this.out = null;
        this.format = null;
        this.logPower = Integer.MAX_VALUE;
    }
    private String[] toString (Object[] args) {
        StringBuilder build = new StringBuilder();
        for (Object o: args)
            build.append(o.toString());
        return build.toString().split("\n");
    }
    private void print (Object[] args, String print){
        if (!All) return;
        String[] lines = toString(args);
        for (String s: lines)
            out.println(((format == null)? "" : format.format(new Date())).concat("[").concat(print.concat("]").concat(s)));
    }
    private void print (Object[] args, String print, boolean flag) {
        if (flag) {
            if (!All) return;
            String[] lines = toString(args);
            for (int i = 0; i < lines.length; i++) {
                String s = lines[i];
                if (i == lines.length - 1)
                    out.print(((format == null) ? "" : format.format(new Date())).concat("[").concat(print.concat("]").concat(s)));
                else out.println(((format == null) ? "" : format.format(new Date())).concat("[").concat(print.concat("]").concat(s)));

            }
        } else print (args, print);
    }
    void NL () {
        if (!All) return;
        out.print("\n");
    }
    void info (Object... args) {
        if (!INFO) return;
        print(args, "INFO");
    }
    void infoWithBackR (Object... args) {
        if (!All) return;
        if (!INFO) return;
        out.print("\r");
        print(args, "INFO", true);
    }
    void error (Object... args) {
        if (!ERROR) return;
        print(args, "ERROR");
    }
    void fatal (Object... args) {
        if (!FATAL) return;
        print(args, "FATAL");
    }
    void warning (Object...args) {
        if (!WARNING) return;
        print(args, "WARNING");
    }
    void debug (Object...args) {
        if (!DEBUG) return;
        print(args, "DEBUG");
    }
    void logCounterIncrement () {
        logCounter++;
    }
    void log (Object... args) {
        if (!LOG) return;
        if (logCounter % logPower == 0)
                print(args, "LOG");
    }
    void trace(Object... args) {
        if (!TRACE) return;
        print(args, "TRACE");
    }
    void setLogPower (int value) {
        this.logPower = value;
    }
    void setLogCounterNull () {
        logCounter = 0;
    }
    void setAll (boolean type) {
        if (!All) type = false;
        ERROR = type;
        FATAL = type;
        LOG = type;
        WARNING = type;
        DEBUG = type;
        INFO = type;
        TRACE = type;
        ONLY_INFO = !type;
    }
    void flush () {
        if (out == null || !All) return;
        out.flush();
    }
    void setTest (Predicate predicate) {
        this.predicate = predicate;
    }
    boolean test (Combination comb) {
        return predicate.test(comb);
    }
}
