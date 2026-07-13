package vocabletrainer.heinecke.aron.vocabletrainer.activity

import vocabletrainer.heinecke.aron.vocabletrainer.R
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.text.SpannableStringBuilder
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.URLSpan
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import vocabletrainer.heinecke.aron.vocabletrainer.lib.EdgeToEdgeUtils.Companion.handleEdgeToEdge

open class AboutActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        setTitle(R.string.About_Title)

        val toolbar = findViewById<Toolbar?>(R.id.toolbar)
        setSupportActionBar(toolbar)
        val ab = supportActionBar
        ab?.setDisplayHomeAsUpEnabled(true)

        val pInfo = packageManager.getPackageInfo(packageName, 0)
        val versionName = pInfo.versionName

        val text = getText(R.string.About_Msg).toString().replace("\\n".toRegex(), "<br>")
            .replace("%v".toRegex(), versionName!!)

        val msgTextbox = findViewById<TextView>(R.id.etAboutMsg)
        setTextViewHTML(msgTextbox, text)


        handleEdgeToEdge(findViewById(R.id.main_content))

    }

    /**
     * Make a link clickable
     * @param strBuilder
     * @param span
     */
    protected fun makeLinkClickable(strBuilder: SpannableStringBuilder, span: URLSpan) {
        val start = strBuilder.getSpanStart(span)
        val end = strBuilder.getSpanEnd(span)
        val flags = strBuilder.getSpanFlags(span)
        val clickable: ClickableSpan = object : ClickableSpan() {
            override fun onClick(view: View) {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(span.getURL()))
                if (browserIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(browserIntent)
                } else {
                    Log.e("ABOUT", "unable to open a browser!")
                }
            }
        }
        strBuilder.setSpan(clickable, start, end, flags)
        strBuilder.removeSpan(span)
    }

    /**
     * Set text view content to HTML
     * @param text
     * @param html HTML sourcecode
     */
    protected fun setTextViewHTML(text: TextView, html: String?) {
        val sequence: CharSequence =
            Html.fromHtml(html, 0) //TODO: use API level 24 or above to correct this
        val strBuilder = SpannableStringBuilder(sequence)
        val urls = strBuilder.getSpans(0, sequence.length, URLSpan::class.java)
        for (span in urls) {
            makeLinkClickable(strBuilder, span)
        }
        text.text = strBuilder
        text.movementMethod = LinkMovementMethod.getInstance()
    }

    /**
     * Called by ok button<br></br>
     * go back to main activity
     *
     * @param view
     */
    fun exitAbout(view: View?) {
        this.finish()
    }
}
