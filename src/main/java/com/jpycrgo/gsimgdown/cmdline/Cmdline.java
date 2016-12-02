package com.jpycrgo.gsimgdown.cmdline;

import com.jpycrgo.gsimgdown.command.*;
import io.airlift.airline.Cli;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * 命令行框架入库类
 *
 * @author mengzx
 * @date 2016/11/30
 * @since 1.2.0
 */
public class Cmdline {

    /**
     * 欢迎语
     */
    private String welcomeMsg = "游民星空壁纸下载工具 V1.2.0";

    /**
     * 提示符
     */
    private static String prompt = ">>> ";

    /**
     * 控制台输入
     */
    private Scanner scanner = new Scanner(System.in);

    /**
     * 命令解析
     */
    private Cli<GSCommand> gsParser;

    /**
     * 命令退出标志
     */
    public static boolean exitFlag = false;

    /**
     * 命令行输出日志
     */
    private static Logger out = LogManager.getLogger("cmdline");

    /**
     * 系统换行符
     */
    private static String separator = System.getProperty("line.separator");

    public static void prompt(String prompt) {
        Cmdline.prompt = prompt;
    }

    public Cmdline() {
        Cli.CliBuilder<GSCommand> builder = Cli.<GSCommand>builder(" ").withDescription("游民星空壁纸下载工具");
        List<Class<? extends GSCommand>> classes = new ArrayList<Class<? extends GSCommand>>(){};
        classes.add(HelpCommand.class);
        classes.add(DownloadCommand.class);
        classes.add(ExitCommand.class);
        classes.add(PromptCommand.class);
        builder.withCommands(classes);

        gsParser = builder.build();
    }

    public void loop() {
        out.info(welcomeMsg.concat(separator));

        while (!exitFlag) {
            out.info(prompt);
            String line = scanner.nextLine();
            if (StringUtils.isBlank(line)) continue;

            String[] argArrray = StringUtils.split(line);
            try {
                gsParser.parse(argArrray).execute();
            }
            catch (Exception e) {
                out.info("命令内部错误，详情请查看日志".concat(separator));
            }
        }
    }

}
