package vocabletrainer.heinecke.aron.vocabletrainer.lib

import android.content.Context
import android.content.ContextWrapper
import android.database.DatabaseErrorHandler
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteDatabase.CursorFactory
import android.util.Log
import java.io.File


/**
 * Adapted from http://stackoverflow.com/a/9168969
 */
/**
 * Custom database context, ignoring all other specifics and using only the privded file
 */
internal class DatabaseContext
/**
 * Creates a new DatabaseContext, which will just use the specified file for any DB requests
 *
 * @param base
 * @param file
 */(base: Context?, private val file: File?) : ContextWrapper(base) {
    override fun getDatabasePath(name: String?): File? {
        return file
    }

    /* this version is called for android devices >= api-11. thank to @damccull for fixing this. */
    override fun openOrCreateDatabase(
        name: String?,
        mode: Int,
        factory: CursorFactory?,
        errorHandler: DatabaseErrorHandler?
    ): SQLiteDatabase {
        return openOrCreateDatabase(name, mode, factory)
    }

    /* this version is called for android devices < api-11 */
    override fun openOrCreateDatabase(
        name: String?,
        mode: Int,
        factory: CursorFactory?
    ): SQLiteDatabase {
        val result = SQLiteDatabase.openOrCreateDatabase(getDatabasePath(name)!!, null)
        // SQLiteDatabase result = super.openOrCreateDatabase(name, mode, factory);
        if (Log.isLoggable(DEBUG_CONTEXT, Log.WARN)) {
            Log.w(DEBUG_CONTEXT, "openOrCreateDatabase(" + name + ",,) = " + result.getPath())
        }
        return result
    }

    companion object {
        private const val DEBUG_CONTEXT = "DatabaseContext"
    }
}