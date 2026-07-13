package vocabletrainer.heinecke.aron.vocabletrainer.lib.widget

import android.content.Context
import android.util.AttributeSet
import androidx.preference.EditTextPreference
import vocabletrainer.heinecke.aron.vocabletrainer.R

/**
 * Custom EditTextPreference for single character settings
 * Created by Aron Heinecke
 */
class CharacterPreference(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
    EditTextPreference(context, attrs, defStyleAttr) {
    override fun getDialogLayoutResource(): Int {
        return R.layout.dialog_character_preference
    }
}