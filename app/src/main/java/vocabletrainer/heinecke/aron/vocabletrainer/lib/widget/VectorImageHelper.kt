package vocabletrainer.heinecke.aron.vocabletrainer.lib.widget

import android.content.Context
import android.view.View
import android.widget.Button
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.appcompat.content.res.AppCompatResources

/**
 * Compatibility class with vectorgraphics utilities
 */
class VectorImageHelper(private val context: Context, private val view: View) {
    /**
     * Manually initialize buttons with a drawableLeft<br></br>
     * Workaround for android regression with vector drawable on pre-21 android
     * @param button
     * @param drawableRes
     */
    fun initImageLeft(@IdRes button: Int, @DrawableRes drawableRes: Int) {
        val drawable = AppCompatResources.getDrawable(
            context,
            drawableRes
        )
        val btn = view.findViewById<Button>(button)
        btn.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
    }
}