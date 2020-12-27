package ro.chiralinteriordesign.cull.ui.texts

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import ro.chiralinteriordesign.cull.databinding.ActivityTextsBinding
import ro.chiralinteriordesign.cull.ui.BaseActivity

private const val ARG_CONTENT = "content"
private const val ARG_TITLE = "title"
private const val HTML_TEMPLATE = """
<!DOCTYPE html>
<html>
<head lang="en">
    <meta charset="UTF-8">
    <title></title>
    <style type="text/css">
        @font-face {
            font-family: 'KievitCompPro';
            src: url('file:///android_res/font/kievitcomppro.ttf');
        }
        body {
            font-family: "KievitCompPro";
            font-size: 14px;
            color: white;
            margin: 0;
            padding: 0;
        }
    </style>
</head>
<body>
@content@
</body>
</html>
"""

class TextsActivity : BaseActivity() {

    companion object {
        fun getIntent(
            context: Context,
            title: String,
            content: String? = null,
            url: String? = null
        ): Intent {
            return Intent(context, TextsActivity::class.java)
                .apply {
                    content?.let { putExtra(ARG_CONTENT, it) }
                    putExtra(ARG_TITLE, title)
                    url?.let { data = Uri.parse(it) }
                }
        }
    }


    private lateinit var binding: ActivityTextsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTextsBinding.inflate(layoutInflater)
        binding.titleView.text = intent.getStringExtra(ARG_TITLE)
        binding.webview.setBackgroundColor(Color.TRANSPARENT)
        val content = intent.getStringExtra(ARG_CONTENT)
        content?.let {
            binding.webview.loadData(
                HTML_TEMPLATE.replace("@content@", it),
                "text/html", "utf-8"
            )
        } ?: intent.data?.let {
            binding.webview.loadUrl(it.toString())
        }

        binding.btnBack.setOnClickListener { onBackPressed() }
        binding.btnOk.setOnClickListener { onBackPressed() }
        binding.btnClose.setOnClickListener { onBackPressed() }
        setContentView(binding.root)
    }
}