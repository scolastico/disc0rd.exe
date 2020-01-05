package com.scolastico.discord_exe.event.handlers;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public interface ScheduleHandler {

    @Retention(RetentionPolicy.RUNTIME)
    public static @interface ScheduleTime {
        public int tick() default 1;
        public boolean runAsync() default true;
    }

    public void scheduledTask();

}

