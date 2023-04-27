package com.stardust.automyjsa.core.ui.inflater;

import com.stardust.automyjsa.core.ui.inflater.util.Drawables;

/**
 * Created by Stardust on 2018/1/24.
 */

public class ResourceParser {


    private final Drawables mDrawables;

    public ResourceParser(Drawables drawables) {
        mDrawables = drawables;
    }

    public Drawables getDrawables() {
        return mDrawables;
    }
}
