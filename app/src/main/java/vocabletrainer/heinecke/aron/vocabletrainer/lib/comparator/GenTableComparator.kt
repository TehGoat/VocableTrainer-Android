package vocabletrainer.heinecke.aron.vocabletrainer.lib.comparator

import vocabletrainer.heinecke.aron.vocabletrainer.lib.storage.VList

/**
 * Generic comparator for lists<br></br>
 * Checks for top table objects according to their ID<br></br>
 * can't handle more than one head table
 */
class GenTableComparator
/**
 * Generic VList Comparator
 *
 * @param retrievers retrievers to use for comparision<br></br>
 * passed array order defines the comparision priority
 * @param headID     ID of table to set on top
 */(retrievers: Array<ValueRetriever<VList?, String?>>, private val headID: Long) :
    GenericComparator<VList, String>(retrievers) {
    override fun compare(o1: VList?, o2: VList?): Int {
        if (o1 == null || o2 == null) {
            return super.compare(o1, o2)
        }

        if (o1.id == headID) return -1
        if (o2.id == headID) return 1
        return super.compare(o1, o2)
    }

    companion object {
        /**
         * Name retriever
         */
        @JvmField
        val retName: ValueRetriever<*, *> = object : ValueRetriever<VList?, String?>() {
            public override fun getV(obj: VList?): String? {
                return obj?.name
            }
        }

        /**
         * A retriever
         */
        @JvmField
        val retA: ValueRetriever<*, *> = object : ValueRetriever<VList?, String?>() {
            public override fun getV(obj: VList?): String? {
                return obj?.nameA
            }
        }

        /**
         * B retriever
         */
        @JvmField
        val retB: ValueRetriever<*, *> = object : ValueRetriever<VList?, String?>() {
            public override fun getV(obj: VList?): String? {
                return obj?.nameB
            }
        }
    }
}
