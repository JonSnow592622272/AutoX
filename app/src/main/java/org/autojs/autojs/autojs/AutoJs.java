package org.automyjsa.automyjsa.automyjsa;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Looper;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.stardust.app.GlobalAppContext;
import com.stardust.automyjsa.core.console.GlobalConsole;
import com.stardust.automyjsa.runtime.ScriptRuntime;
import com.stardust.automyjsa.runtime.accessibility.AccessibilityConfig;
import com.stardust.automyjsa.runtime.api.AppUtils;
import com.stardust.automyjsa.runtime.exception.ScriptException;
import com.stardust.automyjsa.runtime.exception.ScriptInterruptedException;
import com.stardust.view.accessibility.AccessibilityService;
import com.stardust.view.accessibility.LayoutInspector;
import com.stardust.view.accessibility.NodeInfo;

import org.automyjsa.automyjsx.BuildConfig;
import org.automyjsa.automyjsa.Pref;
import org.automyjsa.automyjsx.R;
import org.automyjsa.automyjsa.devplugin.DevPlugin;
import org.automyjsa.automyjsa.external.fileprovider.AppFileProvider;
import org.automyjsa.automyjsa.tool.AccessibilityServiceTool;
import org.automyjsa.automyjsa.ui.floating.FloatyWindowManger;
import org.automyjsa.automyjsa.ui.floating.FullScreenFloatyWindow;
import org.automyjsa.automyjsa.ui.floating.layoutinspector.LayoutBoundsFloatyWindow;
import org.automyjsa.automyjsa.ui.floating.layoutinspector.LayoutHierarchyFloatyWindow;
import org.automyjsa.automyjsa.ui.log.LogActivityKt;
import org.automyjsa.automyjsa.ui.settings.SettingsActivity_;


/**
 * Created by Stardust on 2017/4/2.
 */

public class Automyjsa extends com.stardust.automyjsa.Automyjsa {

    private static Automyjsa instance;

    public static Automyjsa getInstance() {
        return instance;
    }

    private boolean enableDebugLog = false;

    public synchronized static void initInstance(Application application) {
        if (instance != null) {
            return;
        }
        instance = new Automyjsa(application);
    }

    private interface LayoutInspectFloatyWindow {
        FullScreenFloatyWindow create(NodeInfo nodeInfo);
    }

    private BroadcastReceiver mLayoutInspectBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                ensureAccessibilityServiceEnabled();
                String action = intent.getAction();
                if (LayoutBoundsFloatyWindow.class.getName().equals(action)) {
                    capture(LayoutBoundsFloatyWindow::new);
                } else if (LayoutHierarchyFloatyWindow.class.getName().equals(action)) {
                    capture(LayoutHierarchyFloatyWindow::new);
                }
            } catch (Exception e) {
                if (Looper.myLooper() != Looper.getMainLooper()) {
                    throw e;
                }
            }
        }
    };

    private Automyjsa(final Application application) {
        super(application);
        getScriptEngineService().registerGlobalScriptExecutionListener(new ScriptExecutionGlobalListener());
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(LayoutBoundsFloatyWindow.class.getName());
        intentFilter.addAction(LayoutHierarchyFloatyWindow.class.getName());
        LocalBroadcastManager.getInstance(application).registerReceiver(mLayoutInspectBroadcastReceiver, intentFilter);
    }

    private void capture(LayoutInspectFloatyWindow window) {
        LayoutInspector inspector = getLayoutInspector();
        LayoutInspector.CaptureAvailableListener listener = new LayoutInspector.CaptureAvailableListener() {
            @Override
            public void onCaptureAvailable(NodeInfo capture) {
                inspector.removeCaptureAvailableListener(this);
                getUiHandler().post(() ->
                        FloatyWindowManger.addWindow(getApplication().getApplicationContext(), window.create(capture))
                );
            }
        };
        inspector.addCaptureAvailableListener(listener);
        if (!inspector.captureCurrentWindow()) {
            inspector.removeCaptureAvailableListener(listener);
        }
    }

    @Override
    protected AppUtils createAppUtils(Context context) {
        return new AppUtils(context, AppFileProvider.AUTHORITY);
    }

    @Override
    protected GlobalConsole createGlobalConsole() {
        return new GlobalConsole(getUiHandler()) {
            @Override
            public String println(int level, CharSequence charSequence) {
                String log = super.println(level, charSequence);
                DevPlugin.INSTANCE.log(log);
                return log;
            }
        };
    }

    public void ensureAccessibilityServiceEnabled() {
        if (AccessibilityService.Companion.getInstance() != null) {
            return;
        }
        String errorMessage = null;
        if (AccessibilityServiceTool.isAccessibilityServiceEnabled(GlobalAppContext.get())) {
            errorMessage = GlobalAppContext.getString(R.string.text_auto_operate_service_enabled_but_not_running);
        } else {
            if (Pref.shouldEnableAccessibilityServiceByRoot()) {
                if (!AccessibilityServiceTool.enableAccessibilityServiceByRootAndWaitFor(2000)) {
                    errorMessage = GlobalAppContext.getString(R.string.text_enable_accessibility_service_by_root_timeout);
                }
            } else {
                errorMessage = GlobalAppContext.getString(R.string.text_no_accessibility_permission);
            }
        }
        if (errorMessage != null) {
            AccessibilityServiceTool.goToAccessibilitySetting();
            throw new ScriptException(errorMessage);
        }
    }

    @Override
    public void waitForAccessibilityServiceEnabled() {
        if (AccessibilityService.Companion.getInstance() != null) {
            return;
        }
        String errorMessage = null;
        if (AccessibilityServiceTool.isAccessibilityServiceEnabled(GlobalAppContext.get())) {
            errorMessage = GlobalAppContext.getString(R.string.text_auto_operate_service_enabled_but_not_running);
        } else {
            if (Pref.shouldEnableAccessibilityServiceByRoot()) {
                if (!AccessibilityServiceTool.enableAccessibilityServiceByRootAndWaitFor(2000)) {
                    errorMessage = GlobalAppContext.getString(R.string.text_enable_accessibility_service_by_root_timeout);
                }
            } else {
                errorMessage = GlobalAppContext.getString(R.string.text_no_accessibility_permission);
            }
        }
        if (errorMessage != null) {
            AccessibilityServiceTool.goToAccessibilitySetting();
            if (!AccessibilityService.Companion.waitForEnabled(-1)) {
                throw new ScriptInterruptedException();
            }
        }
    }

    @Override
    protected AccessibilityConfig createAccessibilityConfig() {
        AccessibilityConfig config = super.createAccessibilityConfig();
        if (BuildConfig.CHANNEL.equals("coolapk")) {
            config.addWhiteList("com.coolapk.market");
        }
        return config;
    }

    @Override
    protected ScriptRuntime createRuntime() {
        ScriptRuntime runtime = super.createRuntime();
        runtime.putProperty("class.settings", SettingsActivity_.class);
        runtime.putProperty("class.console", LogActivityKt.class);
        runtime.putProperty("broadcast.inspect_layout_bounds", LayoutBoundsFloatyWindow.class.getName());
        runtime.putProperty("broadcast.inspect_layout_hierarchy", LayoutHierarchyFloatyWindow.class.getName());
        return runtime;
    }

    public void debugInfo(String content) {
        if (this.enableDebugLog) {
            Automyjsa.getInstance().getGlobalConsole().println(Log.VERBOSE, content);
        }
    }

    public void setDebugEnabled(boolean enableDebugLog) {
        this.enableDebugLog = enableDebugLog;
    }
}
