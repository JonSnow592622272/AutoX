package org.automyjsa.automyjsa.ui.main.task;

import com.stardust.app.GlobalAppContext;
import com.stardust.automyjsa.engine.ScriptEngine;
import com.stardust.automyjsa.execution.ScriptExecution;
import com.stardust.automyjsa.script.AutoFileSource;
import com.stardust.automyjsa.script.JavaScriptSource;
import com.stardust.pio.PFiles;

import org.automyjsa.automyjsx.R;
import org.automyjsa.automyjsa.timing.IntentTask;
import org.automyjsa.automyjsa.timing.TimedTask;
import org.automyjsa.automyjsa.timing.TimedTaskManager;

import org.joda.time.format.DateTimeFormat;

import static org.automyjsa.automyjsa.ui.timing.TimedTaskSettingActivity.ACTION_DESC_MAP;

/**
 * Created by Stardust on 2017/11/28.
 */

public abstract class Task {


    public abstract String getName();

    public abstract String getDesc();

    public abstract void cancel();

    public abstract String getEngineName();

    public static class PendingTask extends Task {


        private TimedTask mTimedTask;
        private IntentTask mIntentTask;


        public PendingTask(TimedTask timedTask) {
            mTimedTask = timedTask;
            mIntentTask = null;
        }

        public PendingTask(IntentTask intentTask) {
            mIntentTask = intentTask;
            mTimedTask = null;
        }

        public boolean taskEquals(Object task) {
            if (mTimedTask != null) {
                return mTimedTask.equals(task);
            }
            return mIntentTask.equals(task);
        }

        public TimedTask getTimedTask() {
            return mTimedTask;
        }

        @Override
        public String getName() {
            return PFiles.getSimplifiedPath(getScriptPath());
        }

        @Override
        public String getDesc() {
            if (mTimedTask != null) {
                long nextTime = mTimedTask.getNextTime();
                return GlobalAppContext.getString(R.string.text_next_run_time) + ": " +
                        DateTimeFormat.forPattern("yyyy/MM/dd HH:mm").print(nextTime);
            } else {
                assert mIntentTask != null;
                Integer desc = ACTION_DESC_MAP.get(mIntentTask.getAction());
                if(desc != null){
                    return GlobalAppContext.getString(desc);
                }
                return mIntentTask.getAction();
            }

        }

        @Override
        public void cancel() {
            if (mTimedTask != null) {
                TimedTaskManager.INSTANCE.removeTask(mTimedTask);
            } else {
                TimedTaskManager.INSTANCE.removeTask(mIntentTask);
            }
        }

        private String getScriptPath() {
            if (mTimedTask != null) {
                return mTimedTask.getScriptPath();
            } else {
                assert mIntentTask != null;
                return mIntentTask.getScriptPath();
            }
        }

        @Override
        public String getEngineName() {
            if (getScriptPath().endsWith(".js")) {
                return JavaScriptSource.ENGINE;
            } else {
                return AutoFileSource.ENGINE;
            }
        }

        public void setTimedTask(TimedTask timedTask) {
            mTimedTask = timedTask;
        }

        public void setIntentTask(IntentTask intentTask) {
            mIntentTask = intentTask;
        }

        public long getId() {
            if(mTimedTask != null)
                return mTimedTask.getId();
            return mIntentTask.getId();
        }
    }

    public static class RunningTask extends Task {
        private final ScriptExecution mScriptExecution;

        public RunningTask(ScriptExecution scriptExecution) {
            mScriptExecution = scriptExecution;
        }

        public ScriptExecution getScriptExecution() {
            return mScriptExecution;
        }

        @Override
        public String getName() {
            return mScriptExecution.getSource().getName();
        }

        @Override
        public String getDesc() {
            return mScriptExecution.getSource().toString();
        }

        @Override
        public void cancel() {
            ScriptEngine engine = mScriptExecution.getEngine();
            if (engine != null) {
                engine.forceStop();
            }
        }

        @Override
        public String getEngineName() {
            return mScriptExecution.getSource().getEngineName();
        }
    }
}
