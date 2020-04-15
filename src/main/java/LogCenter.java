import Logic.Models.Player;
import org.apache.log4j.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogCenter {
    private static LogCenter logCenter = null;
    private Logger logger;
    private PatternLayout layout;
    private String dataPath = "src"+File.separator+"Log";

    private LogCenter() {
        layout = new PatternLayout();
        String conversionPattern = "%-7p %d - %m%n";
        layout.setConversionPattern(conversionPattern);
        /*
        ConsoleAppender consoleAppender = new ConsoleAppender();
        consoleAppender.setLayout(layout);
        consoleAppender.activateOptions();
        Logger rootLogger = Logger.getRootLogger();
        rootLogger.addAppender(consoleAppender);
        */
        logger = Logger.getLogger(LogCenter.class);
    }

    static public LogCenter getInstance(){
        if(logCenter == null){
            logCenter = new LogCenter();
        }
        return logCenter;
    }

    public Logger getLogger(){
        return logger;
    }

    public void createLogFile(Player player) throws IOException {
        FileWriter fileWriter = new FileWriter(new File(dataPath+File.separator+player.getUsername()+"-"+player.getId()+".log"));
        fileWriter.write("USER: "+player.getUsername()+"\n");
        fileWriter.write("ID: "+player.getId()+"\n");
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        fileWriter.write("CREATED_AT: "+ dateFormat.format(new Date())+"\n");
        fileWriter.write("PASSWORD: "+player.getPassword()+"\n\n");
        fileWriter.flush();
        fileWriter.close();
    }

    public void setLogFile(Player player){
        Logger rootLogger = Logger.getRootLogger();
        rootLogger.removeAllAppenders();
        FileAppender fileAppender = new FileAppender();
        fileAppender.setFile(dataPath+File.separator+player.getUsername()+"-"+player.getId()+".log");
        fileAppender.setLayout(layout);
        fileAppender.activateOptions();
        rootLogger.addAppender(fileAppender);
    }
}
