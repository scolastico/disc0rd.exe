package com.scolastico.discord_exe.etc;

import com.scolastico.discord_exe.event.handlers.ScheduleHandler;

import java.util.ArrayList;

@ScheduleHandler.ScheduleTime(tick = 1, runAsync = false)
public class ScheduleTask implements ScheduleHandler {


    private ArrayList<ScheduledTask> tasks = new ArrayList<>();
    private Integer idCounter = 1000;
    private static ScheduleTask instance = null;

    private ScheduleTask() {}
    public static ScheduleTask getInstance() {
        if (instance == null) {
            instance = new ScheduleTask();
        }
        return instance;
    }

    public int runScheduledTaskRepeat(Runnable runnable, Integer ticks, Integer delay, boolean async) {
        idCounter++;
        ScheduledTask task = new ScheduledTask(idCounter, runnable, false, async, ticks, delay);
        tasks.add(task);
        return idCounter;
    }

    public boolean killScheduledTask(int id) {
        ArrayList<ScheduledTask> tasks = cloneList(this.tasks);
        ScheduledTask toDelete = null;
        for (ScheduledTask task:tasks) {
            if (task.getId() == id) {
                toDelete = task;
            }
        }
        if (toDelete != null) {
            this.tasks.remove(toDelete);
        }
        return false;
    }

    public int runScheduledTask(Runnable runnable, Integer ticks, boolean async) {
        idCounter++;
        ScheduledTask task = new ScheduledTask(idCounter, runnable, true, async, ticks, ticks);
        tasks.add(task);
        return idCounter;
    }

    public void scheduledTask() {
        ArrayList<ScheduledTask> tasks = cloneList(this.tasks);
        try {
            for (ScheduledTask task:tasks) {
                task.executeTask();
            }
        } catch (Exception e) {
            ErrorHandler.getInstance().handle(e);
        }
    }

    public ArrayList<ScheduledTask> cloneList(ArrayList<ScheduledTask> list) {
        ArrayList<ScheduledTask> clone = new ArrayList<ScheduledTask>(list.size());
        clone.addAll(list);
        return clone;
    }

    private static class ScheduledTask {
        private int id;
        private Runnable runnable;
        private boolean onlyOnce;
        private boolean async;
        private int time;
        private int firstRunAfter;
        private boolean isFirstRun = true;

        private int tick = 0;

        public ScheduledTask(int id, Runnable runnable, boolean onlyOnce, boolean async, int time, int firstRunAfter) {
            this.id = id;
            this.runnable = runnable;
            this.onlyOnce = onlyOnce;
            this.async = async;
            this.time = time;
            this.firstRunAfter = firstRunAfter;
        }

        public int getId() {
            return id;
        }

        public void executeTask() {
            tick++;
            if (isFirstRun) {
                if (tick >= firstRunAfter) {
                    isFirstRun = false;
                    run();
                }
            } else {
                if (tick >= time) {
                    tick = 0;
                    run();
                }
            }
        }

        private void run() {
            if (async) {
                Thread thread = new Thread(runnable);
                thread.start();
            } else {
                runnable.run();
            }
            if (onlyOnce) {
                ScheduleTask.getInstance().killScheduledTask(id);
            }
        }
    }
}
