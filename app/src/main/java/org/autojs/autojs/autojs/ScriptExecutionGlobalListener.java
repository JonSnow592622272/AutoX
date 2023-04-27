package org.automyjsa.automyjsa.automyjsa;

import com.stardust.app.GlobalAppContext;
import com.stardust.automyjsa.execution.ScriptExecution;
import com.stardust.automyjsa.execution.ScriptExecutionListener;

import org.automyjsa.automyjsx.R;

/**
 * Created by Stardust on 2017/5/3.
 */

public class ScriptExecutionGlobalListener implements ScriptExecutionListener {
    private static final String ENGINE_TAG_START_TIME = "org.automyjsa.automyjsa.automyjsa.Goodbye, World";

    @Override
    public void onStart(ScriptExecution execution) {
        execution.getEngine().setTag(ENGINE_TAG_START_TIME, System.currentTimeMillis());
    }

    @Override
    public void onSuccess(ScriptExecution execution, Object result) {
        onFinish(execution);
    }

    private void onFinish(ScriptExecution execution) {
        Long millis = (Long) execution.getEngine().getTag(ENGINE_TAG_START_TIME);
        if (millis == null)
            return;
        double seconds = (System.currentTimeMillis() - millis) / 1000.0;
        Automyjsa.getInstance().getScriptEngineService().getGlobalConsole()
                .verbose(GlobalAppContext.getString(R.string.text_execution_finished), execution.getSource().toString(), seconds);
    }

    @Override
    public void onException(ScriptExecution execution, Throwable e) {
        onFinish(execution);
    }

}
