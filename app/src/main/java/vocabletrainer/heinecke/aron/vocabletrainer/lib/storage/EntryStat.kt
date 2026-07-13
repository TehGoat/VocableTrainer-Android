package vocabletrainer.heinecke.aron.vocabletrainer.lib.storage

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class EntryStat(val entryUUID: UUID, val date: Long, val tipNeeded: Boolean, val isCorrect: Boolean): Parcelable
