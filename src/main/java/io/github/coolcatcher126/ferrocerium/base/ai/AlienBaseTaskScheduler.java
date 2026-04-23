package io.github.coolcatcher126.ferrocerium.base.ai;

import java.util.List;

public class AlienBaseTaskScheduler {
    List<AlienBaseTask> tasks;

    public void add(AlienBaseTask task){
        tasks.add(task);
    }

    public void tick(){
        tasks.forEach(AlienBaseTask::tick);
    }
}
