package vocabletrainer.heinecke.aron.vocabletrainer.lib

import android.os.Build
import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding

class EdgeToEdgeUtils {
    companion object {
        fun handleEdgeToEdge(view: View) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
                    val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.ime())
                    v.updatePadding(
                        left = systemBars.left,
                        top = systemBars.top,
                        right = systemBars.right,
                        bottom = systemBars.bottom
                    )

                    insets
                }
            }
        }
    }
}