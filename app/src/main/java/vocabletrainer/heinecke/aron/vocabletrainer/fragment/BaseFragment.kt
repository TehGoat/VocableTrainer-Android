package vocabletrainer.heinecke.aron.vocabletrainer.fragment

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import vocabletrainer.heinecke.aron.vocabletrainer.activity.FragmentActivity

/**
 * Base fragment to be used
 */
abstract class BaseFragment : Fragment() {
    protected val fragmentActivity: FragmentActivity?
        /**
         * Returns the FragmentActivity<br></br>
         * This assumes the parent activity is instance of FragmentActivity
         * @return FragmentActivity
         */
        get() = activity as FragmentActivity?

    val aCActivity: AppCompatActivity?
        /**
         * Returns the current AppCompatActivity casted via getActivity
         * @return AppCompatActivity
         */
        get() = activity as AppCompatActivity?
}