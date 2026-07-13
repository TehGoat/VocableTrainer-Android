package vocabletrainer.heinecke.aron.vocabletrainer.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import vocabletrainer.heinecke.aron.vocabletrainer.R
import vocabletrainer.heinecke.aron.vocabletrainer.editor.ListActivity
import vocabletrainer.heinecke.aron.vocabletrainer.eximport.ExImportActivity
import vocabletrainer.heinecke.aron.vocabletrainer.lib.Database
import vocabletrainer.heinecke.aron.vocabletrainer.lib.EdgeToEdgeUtils.Companion.handleEdgeToEdge
import vocabletrainer.heinecke.aron.vocabletrainer.trainer.TrainerActivity
import vocabletrainer.heinecke.aron.vocabletrainer.trainer.TrainerSettingsActivity

class MainActivity : AppCompatActivity() {
    lateinit var btnContinue: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setTitle(R.string.app_name)

        btnContinue = findViewById(R.id.bLastSession)

        handleEdgeToEdge(findViewById(R.id.main_content))
    }

    override fun onResume() {
        super.onResume()
        btnContinue.isEnabled = false

        lifecycleScope.launch(Dispatchers.IO) {
            val openSession = Database(baseContext).isSessionStored
            runOnUiThread { btnContinue.isEnabled = openSession }
        }
    }

    /**
     * Open trainer to continue the last session
     *
     * @param view
     */
    fun continueSession(view: View?) {
        val myIntent = Intent(this, TrainerActivity::class.java)
        this.startActivity(myIntent)
    }

    /**
     * Open edit table intent
     *
     * @param view
     */
    fun showEditTable(view: View?) {
        val myIntent = Intent(this, ListActivity::class.java)
        myIntent.putExtra(ListActivity.PARAM_FULL_FEATURESET, true)
        this.startActivity(myIntent)
    }

    /**
     * Open trainer intent
     *
     * @param view
     */
    fun showTrainer(view: View?) {
        val myIntent = Intent(this, TrainerSettingsActivity::class.java)
        this.startActivity(myIntent)
    }

    /**
     * Open about activity
     *
     * @param view
     */
    fun showAbout(view: View?) {
        val myIntent = Intent(this, AboutActivity::class.java)
        this.startActivity(myIntent)
    }

    /**
     * Open export activity
     *
     * @param view
     */
    fun showExport(view: View?) {
        val myIntent = Intent(this, ExImportActivity::class.java)
        myIntent.putExtra(ExImportActivity.PARAM_IMPORT, false)
        this.startActivity(myIntent)
    }

    /**
     * Open import activity
     *
     * @param view
     */
    fun showImport(view: View?) {
        val myIntent = Intent(this, ExImportActivity::class.java)
        myIntent.putExtra(ExImportActivity.PARAM_IMPORT, true)
        this.startActivity(myIntent)
    }

    companion object {
        const val TAG: String = "MainActivity"
        const val PREFS_NAME: String = "voc_prefs"
    }
}
