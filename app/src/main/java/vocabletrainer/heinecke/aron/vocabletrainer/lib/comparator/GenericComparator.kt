package vocabletrainer.heinecke.aron.vocabletrainer.lib.comparator

/**
 * Generic Comparator<br></br>
 * Allows for comparision over multiple values<br></br>
 *
 * @param <T>
</T> */
open class GenericComparator<T, V: Comparable<V>> internal constructor(private val retrievers: Array<ValueRetriever<T?, V?>>)
    :
    Comparator<T?>
{

    override fun compare(o1: T?, o2: T?): Int {
        var v = 0
        var i = 0
        while (v == 0 && i < retrievers.size) {
            val retrieverValue = retrievers[i]
            val v1 = retrieverValue.getV(o1)
            val v2 = retrieverValue.getV(o2)

            if (v1 != null && v2 != null) {
                v = v1.compareTo(v2)
            }
            i++
        }
        return v
    }

    /**
     * Retrieves values for comparision
     *
     * @param <T>
     * @param <V>
    </V></T> */
    abstract class ValueRetriever<T, V> {
        /**
         * Returns value of object to compare with
         *
         * @param obj
         * @return
         */
        abstract fun getV(obj: T?): V?
    }
}
