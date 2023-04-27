package org.automyjsa.automyjsa.ui.edit.debug;

import com.stardust.automyjsa.rhino.debug.Debugger;

import org.automyjsa.automyjsa.automyjsa.Automyjsa;
import org.mozilla.javascript.ContextFactory;

public class DebuggerSingleton {

    private static Debugger sDebugger = new Debugger(Automyjsa.getInstance().getScriptEngineService(), ContextFactory.getGlobal());

    public static Debugger get(){
        return sDebugger;
    }
}
