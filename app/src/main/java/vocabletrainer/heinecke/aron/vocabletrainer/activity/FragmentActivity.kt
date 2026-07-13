package vocabletrainer.heinecke.aron.vocabletrainer.activity

import android.util.Log
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import vocabletrainer.heinecke.aron.vocabletrainer.R
import vocabletrainer.heinecke.aron.vocabletrainer.fragment.BaseFragment

abstract class FragmentActivity : AppCompatActivity() {
    private var currentFragment: Fragment? = null
    private var rootFragment: Fragment? = null

    @IdRes
    private var fragmentContainer: Int = R.id.frame

    /**
     * Interface to implement by fragments that want to be notifified
     */
    interface BackButtonListener {
        /**
         * Called when back button is pressed<br></br>
         * Used to communicate between activity & fragment
         * @return true indicates that the activity can be closed
         */
        fun onBackPressed(): Boolean
    }

    var backButtonListener: BackButtonListener? = null

    override fun onBackPressed() {
        if (backButtonListener != null) {
            if (backButtonListener!!.onBackPressed()) {
                super.onBackPressed()
            }
        } else if (!handleFragmentBack()) {
            super.onBackPressed()
        }
    }

    /**
     * Pops the stack & handles fragment back
     * @return false when it's impossible to go back
     */
    protected fun handleFragmentBack(): Boolean {
        Log.d(TAG, "handling fragment back " + supportFragmentManager.backStackEntryCount)

        if (supportFragmentManager.backStackEntryCount > 0) {
            Log.d(TAG, "popping stack")
            currentFragment = getCurrentFragment()
            if (supportFragmentManager.popBackStackImmediate()) {
                if (currentFragment is BaseFragment) {
                    currentFragment!!.onResume()
                }
                return true
            } else {
                Log.w(TAG, "unable to pop backstack")
                return false
            }
        } else {
            return false
        }
    }

    /**
     * Set container id to use for fragment changes
     * @param container
     */
    protected fun setFragmentContainer(@IdRes container: Int) {
        this.fragmentContainer = container
    }

    /**
     * Returns the current fragment
     * @return
     */
    private fun getCurrentFragment(): Fragment? {
        val fragmentManager = supportFragmentManager
        Log.d(TAG, "fragment stack:" + fragmentManager.backStackEntryCount)
        var fr = fragmentManager.findFragmentById(fragmentContainer)
        if (fr == null) {
            fr = rootFragment
        }
        return fr
    }

    /**
     * Set fragment to show<br></br>
     *
     * Replaces current fragment
     * @param fragment
     */
    fun setFragment(fragment: Fragment) {
        Log.w(TAG, (fragment is BaseFragment).toString() + "" + fragment)
        checkBackButtonListener(fragment)
        supportFragmentManager.beginTransaction()
            .replace(fragmentContainer, fragment).commit()
        currentFragment = fragment
        rootFragment = fragment
    }

    /**
     * Check back button listener
     * @param fragment
     */
    private fun checkBackButtonListener(fragment: Fragment?) {
        backButtonListener = if (fragment is BackButtonListener) {
            fragment as BackButtonListener
        } else {
            null
        }
    }

    companion object {
        private const val TAG = "FragmentActivity"
    }
}
