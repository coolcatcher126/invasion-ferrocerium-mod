package io.github.coolcatcher126.ferrocerium.base.ai;

import java.util.ArrayList;
import java.util.List;

public class AlienBaseTaskScheduler {
    List<AlienBaseTask> tasks = new ArrayList<>();

    public void add(AlienBaseTask task){
        tasks.add(task);
    }

    public void tick(){
        tasks.forEach(AlienBaseTask::tick);
    }
}
