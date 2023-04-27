package org.automyjsa.automyjsa.ui.main

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import org.automyjsa.automyjsa.ui.main.scripts.ScriptListFragment
import org.automyjsa.automyjsa.ui.main.task.TaskManagerFragmentKt
import org.automyjsa.automyjsa.ui.main.web.WebViewFragment

class ViewPager2Adapter(
    fragmentActivity: FragmentActivity,
    private val scriptListFragment: ScriptListFragment,
    private val taskManagerFragment: TaskManagerFragmentKt,
    private val webViewFragment: WebViewFragment
) : FragmentStateAdapter(fragmentActivity) {

    override fun createFragment(position: Int): Fragment {
        val fragment = when (position) {
            0 -> {
                scriptListFragment
            }
            1 -> {
                taskManagerFragment
            }
            else -> {
                webViewFragment
            }
        }
        return fragment
    }

    override fun getItemCount(): Int {
        return 3
    }
}