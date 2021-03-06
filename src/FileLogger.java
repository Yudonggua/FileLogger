import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.ArrayList;
import java.util.logging.*;
import java.io.*;

import static java.lang.Thread.sleep;

class LoggerFormatter extends Formatter {                                   //覆寫Formatter類別
    //將xml格式更改為自訂格式
    @Override
    public String format(LogRecord record) {
        return "[" + new Date() + "]" + " [" + record.getLevel() + "] "
                + record.getClass() + "：" + record.getMessage() + "\n";
    }
}

public class FileLogger {
    private static final Logger LOGGER = Logger.getLogger(FileLogger.class.getName());
    private static final File FILE = new File(System.getProperty("java.io.tmpdir"));            //監控位置

    public static void main(String[] args) throws IOException, InterruptedException {

        File delFile;                                                           //用來刪除

        new File("./log").mkdirs();

        FileHandler fileHandler = new FileHandler("./log/" + new Date().getTime() + ".log");    //建立Log檔案

        ConsoleHandler consoleHandler = new ConsoleHandler();
        fileHandler.setFormatter(new LoggerFormatter());
        ArrayList original_list = new ArrayList<String>();
        ArrayList contrast_list = new ArrayList<String>();

        LOGGER.setLevel(Level.ALL);
        LOGGER.addHandler(consoleHandler);
        LOGGER.addHandler(fileHandler);
        //TODO:
        // - While開始 等待時間(10s)
        while (true) {

            LOGGER.info("讀取檔案");
            original_list.addAll(Arrays.asList(FILE.list()));

            sleep(10000);

            LOGGER.info("讀取對比檔案");
            contrast_list.addAll(Arrays.asList(FILE.list()));
            //TODO:
            // - 比較
            // - 寫入LOG & 執行刪除

            if (contrast_list.size() > original_list.size()) {

                for (int i = 0; i < contrast_list.size(); i++) {

                    if (!(original_list.contains(contrast_list.get(i)))) {
                        LOGGER.info("新增：" + contrast_list.get(i).toString());

                        if (contrast_list.get(i).toString().matches("PKM(.*)tmp")) {   //LOL的暫存檔格式
                            delFile = new File(System.getProperty("java.io.tmpdir") + contrast_list.get(i).toString());

                            while (delFile.exists()) {
                                delFile.delete();
                                LOGGER.info("刪除：" + contrast_list.get(i).toString());
                                sleep(1000);
                            }
                        }
                    }
                }
            } else {
                LOGGER.info("無新增項目");
            }
            original_list.clear();
            contrast_list.clear();
            System.gc();
        }
    }
}

