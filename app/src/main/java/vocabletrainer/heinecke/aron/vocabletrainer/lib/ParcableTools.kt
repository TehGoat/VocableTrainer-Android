package vocabletrainer.heinecke.aron.vocabletrainer.lib

import android.os.Parcel

/**
 * Tools to help making stuff parable
 */
object ParsableTools {
    /**
     * Read boolean from parcel
     * @param in
     * @return
     */
    @JvmStatic
    fun readParsableBool(`in`: Parcel): Boolean {
        return `in`.readInt() == 1
    }

    /**
     * Write boolean to pacel
     * @param in
     * @param data
     */
    @JvmStatic
    fun writeParsableBool(`in`: Parcel, data: Boolean) {
        `in`.writeInt(if (data) 1 else 0)
    }
}