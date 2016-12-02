package com.jpycrgo.gsimgdown.command;

import com.jpycrgo.gsimgdown.cmdline.Cmdline;
import io.airlift.airline.Command;

/**
 * @author mengzx
 * @date 2016/12/2
 * @since 1.2.0
 */
@Command(name = "exit", description = "退出程序")
public class ExitCommand implements GSCommand {

    @Override
    public void execute() {
        Cmdline.exitFlag = true;
    }

}
