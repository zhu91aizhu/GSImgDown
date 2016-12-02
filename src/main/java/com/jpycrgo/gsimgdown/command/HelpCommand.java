package com.jpycrgo.gsimgdown.command;

import io.airlift.airline.Command;
import io.airlift.airline.Help;

/**
 * @author mengzx
 * @date 2016/12/2
 * @since 1.2.0
 */
@Command(name = "help", description = "显示帮助信息")
public class HelpCommand extends Help implements GSCommand {

    @Override
    public void execute() throws Exception {
        run();
    }

}
