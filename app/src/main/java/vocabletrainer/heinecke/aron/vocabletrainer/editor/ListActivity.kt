package vocabletrainer.heinecke.aron.vocabletrainer.editor

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import vocabletrainer.heinecke.aron.vocabletrainer.R
import vocabletrainer.heinecke.aron.vocabletrainer.activity.FragmentActivity
import vocabletrainer.heinecke.aron.vocabletrainer.lib.EdgeToEdgeUtils.Companion.handleEdgeToEdge
import vocabletrainer.heinecke.aron.vocabletrainer.lib.storage.VList
import vocabletrainer.heinecke.aron.vocabletrainer.lib.view_model.ListPickerViewModel
import vocabletrainer.heinecke.aron.vocabletrainer.listpicker.ListPickerFragment

class ListActivity : FragmentActivity(), ListPickerFragment.FinishListener {
    private var multiselect = false
    private var fullFeatures = false
    var listPickerFragment: ListPickerFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_activity)

        val toolbar = findViewById<Toolbar?>(R.id.toolbar)
        setSupportActionBar(toolbar)
        val ab = supportActionBar!!
        ab.setDisplayHomeAsUpEnabled(true)

        val intent = getIntent()
        // handle passed params
        multiselect = intent.getBooleanExtra(PARAM_MULTI_SELECT, false)
        fullFeatures = intent.getBooleanExtra(PARAM_FULL_FEATURESET, false)
        if (fullFeatures) {
            ab.setTitle(R.string.Lists_Title)
        } else {
            ab.setTitle(R.string.List_Select_Title)
        }
        val preselected: ArrayList<VList?>? = if (intent.hasExtra(PARAM_SELECTED)) {
            intent.getParcelableArrayListExtra<VList?>(PARAM_SELECTED)
        } else {
            ArrayList()
        }

        listPickerFragment = if (savedInstanceState != null) {
            //Restore the fragment's instance
            supportFragmentManager.getFragment(
                savedInstanceState,
                ListPickerFragment.TAG
            ) as ListPickerFragment?
        } else {
            ListPickerFragment.newInstance(multiselect, !fullFeatures, preselected)
        }

        setFragment(listPickerFragment!!)


        handleEdgeToEdge(findViewById(R.id.main_content))
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        //Save the fragment's instance
        getSupportFragmentManager().putFragment(
            outState,
            ListPickerFragment.TAG,
            listPickerFragment!!
        )
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    public override fun onBackPressed() {
        if (fullFeatures) {
            super.onBackPressed()
        } else {
            val returnIntent = Intent()
            setResult(RESULT_CANCELED, returnIntent)
            finish()
        }
    }

    override fun selectionUpdate(selected: ArrayList<VList?>) {
        if (fullFeatures) {
            val listPickerViewModel: ListPickerViewModel = ViewModelProvider(this)[ListPickerViewModel::class.java]

            listPickerViewModel.setDataInvalidated() // editor changed entry

            val myIntent = Intent(this, EditorActivity::class.java)
            myIntent.putExtra(EditorActivity.PARAM_NEW_TABLE, false)
            val lst = selected[0]
            myIntent.putExtra(EditorActivity.PARAM_TABLE, lst)
            this.startActivity(myIntent)
        } else {
            val returnIntent = Intent()
            if (multiselect) {
                returnIntent.putExtra(RETURN_LISTS, selected)
            } else {
                returnIntent.putExtra(RETURN_LISTS, selected[0])
            }
            setResult(RESULT_OK, returnIntent)
            finish()
        }
    }

    override fun cancel() {
        onBackPressed()
    }

    companion object {
        /**
         * Set whether multi-select is enabled or not<br></br>
         * Boolean expected
         */
        const val PARAM_MULTI_SELECT: String = "multiselect"

        /**
         * Param key for return of selected lists<br></br>
         * This key contains a [VList] object or a [List] of [VList]
         */
        const val RETURN_LISTS: String = "selected"

        /**
         * Pass this flag as true to call this with creation & deletion capabilities
         */
        const val PARAM_FULL_FEATURESET: String = "full_features"

        /**
         * Optional Param key for already selected lists, available when multiselect is set<br></br>
         * Expects a [List] of [VList]<br></br>
         * This can be null, if nothing is selected
         */
        const val PARAM_SELECTED: String = "selected"
    }
}
