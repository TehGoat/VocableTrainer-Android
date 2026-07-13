package vocabletrainer.heinecke.aron.vocabletrainer.lib.comparator

import vocabletrainer.heinecke.aron.vocabletrainer.lib.storage.VEntry

/**
 * Comparator for entries<br></br>
 * Checks for top entry objects according to their ID<br></br>
 * can't handle more than one head entry
 */
class GenEntryComparator
/**
 * Generic VEntry Comparator
 *
 * @param retrievers retrievers to use for comparision<br></br>
 * passed array order defines the comparision priority
 * @param headID     ID of entry to set on top
 */(retrievers: Array<ValueRetriever<VEntry?, String?>>, private val headID: Long) :
    GenericComparator<VEntry?, String>(retrievers) {
    override fun compare(o1: VEntry?, o2: VEntry?): Int {
        if (o1 == null || o2 == null) {
             return super.compare(o1, o2)
        }

        if (o1.id == headID) {
            return -1
        }

        if (o2.id == headID) {
            return 1
        }

        return super.compare(o1, o2)
    }

    companion object {
        /**
         * A retriever
         */
        val retA: ValueRetriever<VEntry?, String?> = object : ValueRetriever<VEntry?, String?>() {
            override fun getV(obj: VEntry?): String? {
                return obj?.aString
            }
        }

        /**
         * B retriever
         */
        val retB: ValueRetriever<VEntry?, String?> = object : ValueRetriever<VEntry?, String?>() {
            override fun getV(obj: VEntry?): String? {
                return obj?.bString
            }
        }

        /**
         * Tip retriever
         */
        val retTip: ValueRetriever<VEntry?, String?> = object : ValueRetriever<VEntry?, String?>() {
            override fun getV(obj: VEntry?): String? {
                return obj?.tip
            }
        }
    }
}
