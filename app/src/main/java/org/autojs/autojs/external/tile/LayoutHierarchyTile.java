package org.automyjsa.automyjsa.external.tile;

import android.os.Build;
import androidx.annotation.RequiresApi;

import com.stardust.view.accessibility.NodeInfo;

import org.automyjsa.automyjsa.ui.floating.FullScreenFloatyWindow;
import org.automyjsa.automyjsa.ui.floating.layoutinspector.LayoutHierarchyFloatyWindow;

@RequiresApi(api = Build.VERSION_CODES.N)
public class LayoutHierarchyTile extends LayoutInspectTileService {
    @Override
    protected FullScreenFloatyWindow onCreateWindow(NodeInfo capture) {
        return new LayoutHierarchyFloatyWindow(capture) {
            @Override
            public void close() {
                super.close();
                inactive();
            }
        };
    }
}
