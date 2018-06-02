package gmsis;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class Log {

    public static final int NONE = 5;
    public static final int ERROR = 4;
    public static final int WARN = 3;
    public static final int INFO = 2;
    public static final int DEBUG = 1;
    public static final int TRACE = 0;

    private static int LEVEL = INFO;

    private static Logger logger = new DefaultLogger();

    public static void setLevel(int level) {
        LEVEL = level;
    }

    public static void setLogger(Logger logger) {
        Log.logger = logger;
    }

    public static void log(int level, String message, Object... args) {
        if(level >= LEVEL) logger.log(level, message, args);
    }

    public static void error(String message, Object... args) {
        log(ERROR, message, args);
    }

    public static void warn(String message, Object... args) {
        log(WARN, message, args);
    }

    public static void info(String message, Object... args) {
        log(INFO, message, args);
    }

    public static void debug(String message, Object... args) {
        log(DEBUG, message, args);
    }

    public static void trace(String message, Object... args) {
        log(TRACE, message, args);
    }


    public static class DefaultLogger implements Logger {

        private DateTimeFormatter format = DateTimeFormatter.ofPattern("HH:mm:ss");

        @Override
        public void log(int level, String message, Object... args) {
            StringBuilder output = new StringBuilder(256);
            LocalTime now = LocalTime.now();
            output.append(now.format(format)).append(' ');

            switch (level) {
                case ERROR:
                    output.append("[ERROR]: ");
                    break;
                case WARN:
                    output.append("[WARN]: ");
                    break;
                case INFO:
                    output.append("[INFO]: ");
                    break;
                case DEBUG:
                    output.append("[DEBUG]: ");
                    break;
                case TRACE:
                    output.append("[TRACE]: ");
                    break;
            }
            output.append(MessageFormat.format(message, args));

            System.out.println(output.toString());
        }
    }

    public interface Logger {
        void log(int level, String message, Object... args);
    }
}
