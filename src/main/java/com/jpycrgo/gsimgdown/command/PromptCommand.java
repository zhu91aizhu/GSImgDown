package com.jpycrgo.gsimgdown.command;

import com.jpycrgo.gsimgdown.cmdline.Cmdline;
import io.airlift.airline.Arguments;
import io.airlift.airline.Command;
import io.airlift.airline.Option;

/**
 * @author mengzx
 * @date 2016/12/2
 * @since 1.2.0
 */
@Command(name = "prompt" , description = "设置命令提示符")
public class PromptCommand implements GSCommand {

    @Arguments(required = true, description = "新命令提示符")
    public String prompt;

    @Option(name = {"-b", "--appendblock"}, description = "追加空格")
    public boolean appendBlock;

    @Override
    public void execute() throws Exception {
        if (appendBlock) {
            prompt = prompt.concat(" ");
        }

        Cmdline.prompt(prompt);
    }

}
