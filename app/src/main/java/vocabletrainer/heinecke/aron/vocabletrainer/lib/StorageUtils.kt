package vocabletrainer.heinecke.aron.vocabletrainer.lib

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns

/**
 * Storage retriever utilities
 * Copy-Pasta of different approaches, because android _really_ doesn't want you to export/import stuff
 * via files
 */
object StorageUtils {
    // taken from https://developer.android.com/training/data-storage/shared/documents-files#java
    @JvmStatic
    fun getUriName(context: Context, uri: Uri): String? {
        context.contentResolver
            .query(uri, null, null, null, null, null).use { cursor ->
                // moveToFirst() returns false if the cursor has 0 rows. Very handy for
                // "if there's anything to look at, look at it" conditionals.
                if (cursor != null && cursor.moveToFirst()) {
                    // Note it's called "Display Name". This is
                    // provider-specific, and might not necessarily be the file name.

                    return cursor.getString(
                        cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    )
                }
            }
        return "<no file name>"
    }
}