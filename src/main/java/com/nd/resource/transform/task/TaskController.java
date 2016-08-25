package com.nd.resource.transform.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by way on 2016/8/24.
 */
@Controller
public class TaskController {

    @Autowired
    private TaskExecutorExample taskExecutorExample;

    @RequestMapping("/start")
    public String start(){
        taskExecutorExample.printMessages();
        return "task start ";
    }

}
