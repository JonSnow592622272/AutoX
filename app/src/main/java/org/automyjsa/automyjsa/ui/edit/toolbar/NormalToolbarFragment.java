package org.automyjsa.automyjsa.ui.edit.toolbar;

import org.androidannotations.annotations.EFragment;
import org.automyjsa.automyjsx.R;

import java.util.Arrays;
import java.util.List;

@EFragment(R.layout.fragment_normal_toolbar)
public class NormalToolbarFragment extends ToolbarFragment {

    @Override
    public List<Integer> getMenuItemIds() {
        return Arrays.asList(R.id.run, R.id.undo, R.id.redo, R.id.save, R.id.textSizePlus, R.id.textSizeMinus);
    }
}
