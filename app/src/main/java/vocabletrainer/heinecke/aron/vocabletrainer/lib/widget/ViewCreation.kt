package vocabletrainer.heinecke.aron.vocabletrainer.lib.widget

import android.view.View
import android.widget.CompoundButton
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.core.view.size


/**
 * Helper Class for view creation
 * @author Aron Heinecke
 */
object ViewCreation {
    /**
     * Init radio group of buttons onCheckedChangeListener
     * @param group Group to init
     * @param fn Function to call on button checked change
     */
    @JvmStatic
    fun initRadioGroup(group: RadioGroup, fn: (View?) -> Unit) {
        for (i in 0..<group.size) {
            val view = group.getChildAt(i)
            if (view is RadioButton) {
                view.setOnCheckedChangeListener { buttonView: CompoundButton?, isChecked: Boolean ->
                    if (isChecked)  // don't double fire (B de-check => A check)
                        fn(buttonView)
                }
            }
        }
    }
}