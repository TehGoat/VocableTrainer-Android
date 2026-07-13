package vocabletrainer.heinecke.aron.vocabletrainer.lib.widget

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Log
import com.google.android.material.textfield.TextInputEditText

/**
 * Custom EditText that does not display setError messages
 */
class CEditText : TextInputEditText {
    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    override fun setError(error: CharSequence?, icon: Drawable?) {
        Log.d("CEditText", "called setError")
        setCompoundDrawables(null, null, icon, null)
    }
}