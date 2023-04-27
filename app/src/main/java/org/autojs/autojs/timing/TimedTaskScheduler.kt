package org.automyjsa.automyjsa.timing

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.automyjsa.automyjsa.App
import org.automyjsa.automyjsa.Pref
import org.automyjsa.automyjsa.automyjsa.Automyjsa
import org.automyjsa.automyjsa.external.ScriptIntents
import org.automyjsa.automyjsa.timing.work.WorkProvider
import org.automyjsa.automyjsa.timing.work.WorkManagerProvider
import org.automyjsa.automyjsa.timing.work.AndroidJobProvider
import org.automyjsa.automyjsa.timing.work.AlarmManagerProvider
import java.lang.Exception
import java.util.concurrent.TimeUnit

/**
 * Created by Stardust on 2017/11/27.
 * Improvedd by TonyJiangWJ(https://github.com/TonyJiangWJ).
 * From [TonyJiangWJ/Automyjsa.js](https://github.com/TonyJiangWJ/Automyjsa.js)
 */
abstract class TimedTaskScheduler : WorkProvider {

    @SuppressLint("CheckResult")
    override fun checkTasks(context: Application, force: Boolean) {
        automyjsaLog("check tasks: force = $force")
        TimedTaskManager.allTasks
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { timedTask: TimedTask -> scheduleTaskIfNeeded(context, timedTask, force) }
    }

    override fun scheduleTaskIfNeeded(context: Application, timedTask: TimedTask, force: Boolean) {
        val millis = timedTask.nextTime
        if (millis <= System.currentTimeMillis()) {
            automyjsaLog("task out date, just run it: $timedTask")
            runTask(context, timedTask)
            return
        }
        if (!force && timedTask.isScheduled || millis - System.currentTimeMillis() > SCHEDULE_TASK_MIN_TIME) {
            return
        }
        scheduleTask(context, timedTask, millis, force)
        TimedTaskManager.notifyTaskScheduled(timedTask)
    }

    /**
     * only available in WorkManagerProvider and AndroidJobProvider
     *
     * @param context
     * @param timedTask
     * @param millis
     * @param force
     */
    @Synchronized
    override fun scheduleTask(
        context: Application,
        timedTask: TimedTask,
        millis: Long,
        force: Boolean
    ) {
        if (!force && timedTask.isScheduled) {
            return
        }
        val timeWindow = millis - System.currentTimeMillis()
        timedTask.isScheduled = true
        TimedTaskManager.updateTaskWithoutReScheduling(timedTask)
        automyjsaLog("schedule task: task = $timedTask, millis = $millis, timeWindow = $timeWindow")
        getWorkProvider(context).enqueueWork(timedTask, timeWindow)
    }

    open fun automyjsaLog(content: String) {
        Automyjsa.getInstance().debugInfo(content)
    }

    companion object {
        @JvmStatic
        val LOG_TAG = "TimedTaskScheduler"
        private val SCHEDULE_TASK_MIN_TIME = TimeUnit.DAYS.toMillis(2)

        @JvmStatic
        protected val JOB_TAG_CHECK_TASKS = "checkTasks"
//        @JvmStatic
//        fun cancel(context: Application,timedTask: TimedTask) {
//            getWorkProvider(context).cancel(timedTask)
//        }

        fun init(context: Application) {
            createCheckWorker(context, 20)
            getWorkProvider(context).checkTasks(context, true)
        }

        private fun createCheckWorker(context: Application, delay: Int) {
            automyjsaLog("创建定期检测任务")
            getWorkProvider(context).enqueuePeriodicWork(delay)
        }

        @JvmStatic
        protected fun runTask(context: Application, task: TimedTask) {
            automyjsaLog("run task: task = $task")
            val intent = task.createIntent()
            ScriptIntents.handleIntent(context, intent)
            TimedTaskManager.notifyTaskFinished(task.id)
            // 如果队列中有任务正在等待，直接取消
            getWorkProvider(context).cancel(context, task)
        }

        @Synchronized
        fun ensureCheckTaskWorks(context: Application) {
            try {
                val workFine = getWorkProvider(context).isCheckWorkFine
                // 校验是否有超时未执行的
                val currentMillis = System.currentTimeMillis()
                val anyLost = TimedTaskManager.allTasks.any { task: TimedTask ->
                    if (task.nextTime < currentMillis) {
                        automyjsaLog("task timeout: " + task.toString() + " nextTime:" + task.nextTime + " current millis:" + currentMillis)
                        return@any true
                    } else {
                        return@any false
                    }
                }.blockingGet()
                if (!workFine || anyLost) {
                    automyjsaLog("ensureCheckTaskWorks: " + if (workFine) "PeriodicWork works fine, but missed some work" else "PeriodicWork died")
                    createCheckWorker(context, 0)
                    getWorkProvider(context).checkTasks(context, true)
                }
            } catch (e: Exception) {
                Log.e(LOG_TAG, "获取定时校验任务失败")
            }
        }

        @JvmStatic
        fun getWorkProvider(context: Application = App.app): WorkProvider {
            return when (Pref.getTaskManager()) {
                0 -> {
                    Log.d(LOG_TAG, "The currently enabled scheduled task method is Work Manager")
                    WorkManagerProvider
                }
                1 -> {
                    Log.d(LOG_TAG, "The currently enabled scheduled task method is Android Job")
                    AndroidJobProvider
                }
                else -> {
                    Log.d(LOG_TAG, "The currently enabled scheduled task mode is Alarm Manager")
                    AlarmManagerProvider
                }
            }
        }

        private fun automyjsaLog(content: String) {
            Log.d(LOG_TAG, content)
            Automyjsa.getInstance().debugInfo(content)
        }
    }

}