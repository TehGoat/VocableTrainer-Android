package vocabletrainer.heinecke.aron.vocabletrainer.lib

import java.io.File
import java.text.DecimalFormat
import kotlin.math.log10
import kotlin.math.pow

/**
 * Formatter<br></br>
 * Help library with consts and custom formats
 */
class Formatter {
    private val formatBytes = DecimalFormat("#,##0.#")

    private var USE_SI = true
    private val units: Array<String> = arrayOf<String>("B", "KB", "MB", "GB", "TB")
    private val unit_si: Array<String> = arrayOf<String>("B", "KiB", "MiB", "GiB", "TiB")

    /**
     * Format bytes to human readable string
     *
     * @param i bytes amount
     * @return String, for ex. 5,4MiB
     */
    fun formatBytes(i: Long): String {
        if (i <= 0) return "0"
        val digitGroups =
            (log10(i.toDouble()) / log10((if (USE_SI) 1024 else 1000).toDouble())).toInt()
        return formatBytes.format(
            i / (if (USE_SI) 1024 else 1000).toDouble().pow(digitGroups.toDouble())
        ) + " " + (if (USE_SI) unit_si[digitGroups] else units[digitGroups])
    }

    /**
     * Format file length
     *
     * @param file
     * @return (empty) String with the file length
     */
    fun formatFileLength(file: File): String? {
        if (file.exists() && file.isFile()) {
            return formatBytes(file.length())
        } else {
            return ""
        }
    }

    /**
     * Change SI notation usage
     *
     * @param use_si
     */
    fun changeSI(use_si: Boolean) {
        USE_SI = use_si
    }

    companion object {
        @Suppress("unused")
        private const val TAG = "Formatter"
    }
}
