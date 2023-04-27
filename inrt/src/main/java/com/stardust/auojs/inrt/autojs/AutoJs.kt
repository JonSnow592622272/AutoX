package com.stardust.auojs.inrt.automyjsa

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import com.stardust.app.GlobalAppContext
import com.stardust.auojs.inrt.LogActivity
import com.stardust.auojs.inrt.Pref
import com.stardust.auojs.inrt.SettingsActivity
import com.stardust.auojs.inrt.pluginclient.DevPluginService
import com.stardust.automyjsa.core.console.GlobalConsole
import com.stardust.automyjsa.runtime.ScriptRuntime
import com.stardust.automyjsa.runtime.api.AppUtils
import com.stardust.automyjsa.runtime.exception.ScriptException
import com.stardust.automyjsa.runtime.exception.ScriptInterruptedException
import com.stardust.automyjsa.script.JavaScriptSource
import com.stardust.view.accessibility.AccessibilityService
import com.stardust.view.accessibility.AccessibilityServiceUtils
import org.automyjsa.automyjsx.inrt.R


/**
 * Created by Stardust on 2017/4/2.
 */

class Automyjsa private constructor(application: Application) : com.stardust.automyjsa.Automyjsa(application) {

    init {
        scriptEngineService.registerGlobalScriptExecutionListener(ScriptExecutionGlobalListener())
    }

    override fun createAppUtils(context: Context): AppUtils {
        return AppUtils(context, context.packageName + ".fileprovider")
    }


    override fun ensureAccessibilityServiceEnabled() {
        if (AccessibilityService.instance != null) {
            return
        }
        var errorMessage: String? = null
        if (AccessibilityServiceUtils.isAccessibilityServiceEnabled(application, AccessibilityService::class.java)) {
            errorMessage = GlobalAppContext.getString(R.string.text_auto_operate_service_enabled_but_not_running)
        } else {
            if (Pref.shouldEnableAccessibilityServiceByRoot()) {
                if (!AccessibilityServiceTool.enableAccessibilityServiceByRootAndWaitFor(application, 2000)) {
                    errorMessage = GlobalAppContext.getString(R.string.text_enable_accessibility_service_by_root_timeout)
                }
            } else {
                errorMessage = GlobalAppContext.getString(R.string.text_no_accessibility_permission)
            }
        }
        if (errorMessage != null) {
            AccessibilityServiceTool.goToAccessibilitySetting()
            throw ScriptException(errorMessage)
        }
    }

    override fun waitForAccessibilityServiceEnabled() {
        if (AccessibilityService.instance != null) {
            return
        }
        var errorMessage: String? = null
        if (AccessibilityServiceUtils.isAccessibilityServiceEnabled(application, AccessibilityService::class.java)) {
            errorMessage = GlobalAppContext.getString(R.string.text_auto_operate_service_enabled_but_not_running)
        } else {
            if (Pref.shouldEnableAccessibilityServiceByRoot()) {
                if (!AccessibilityServiceTool.enableAccessibilityServiceByRootAndWaitFor(application, 2000)) {
                    errorMessage = GlobalAppContext.getString(R.string.text_enable_accessibility_service_by_root_timeout)
                }
            } else {
                errorMessage = GlobalAppContext.getString(R.string.text_no_accessibility_permission)
            }
        }
        if (errorMessage != null) {
            AccessibilityServiceTool.goToAccessibilitySetting()
            if (!AccessibilityService.waitForEnabled(-1)) {
                throw ScriptInterruptedException()
            }
        }
    }

    override fun createGlobalConsole(): GlobalConsole {
        return object : GlobalConsole(uiHandler) {
            override fun println(level: Int, charSequence: CharSequence): String {
                val log = super.println(level, charSequence)
                DevPluginService.getInstance().log(log)
                return log
            }
        }
    }

    override fun initScriptEngineManager() {
        super.initScriptEngineManager()
        scriptEngineManager.registerEngine(JavaScriptSource.ENGINE) {
            val engine = XJavaScriptEngine(application)
            engine.runtime = createRuntime()
            engine
        }
    }

    override fun createRuntime(): ScriptRuntime {
        val runtime = super.createRuntime()
        runtime.putProperty("class.settings", SettingsActivity::class.java)
        runtime.putProperty("class.console", LogActivity::class.java)
        return runtime
    }

    companion object {

        @SuppressLint("StaticFieldLeak")
        lateinit var instance: Automyjsa
            private set

        fun initInstance(application: Application) {
            instance = Automyjsa(application)
        }
    }
}
