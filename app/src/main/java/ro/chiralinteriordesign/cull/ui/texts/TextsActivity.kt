package ro.chiralinteriordesign.cull.ui.texts

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import ro.chiralinteriordesign.cull.App
import ro.chiralinteriordesign.cull.databinding.TextsActivityBinding
import ro.chiralinteriordesign.cull.model.text.Text
import ro.chiralinteriordesign.cull.services.ResultWrapper
import ro.chiralinteriordesign.cull.ui.BaseActivity


private const val ARG_CONTENT = "content"
private const val ARG_TEXT_KEY = "text_key"
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
            textKey: Text.Key? = null,
            url: String? = null
        ): Intent {
            return Intent(context, TextsActivity::class.java)
                .apply {
                    content?.let { putExtra(ARG_CONTENT, it) }
                    textKey?.let { putExtra(ARG_TEXT_KEY, it) }
                    putExtra(ARG_TITLE, title)
                    url?.let { data = Uri.parse(it) }
                }
        }
    }


    private lateinit var binding: TextsActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = TextsActivityBinding.inflate(layoutInflater)
        binding.navBar.titleView.text = intent.getStringExtra(ARG_TITLE)
        binding.webview.setBackgroundColor(Color.TRANSPARENT)

        intent.getStringExtra(ARG_CONTENT)?.let {
            binding.webview.loadDataWithBaseURL(
                "", HTML_TEMPLATE.replace("@content@", it),
                "text/html", "utf-8", null
            )
        } ?: (intent.getSerializableExtra(ARG_TEXT_KEY) as? Text.Key)?.let {
            lifecycleScope.launch {
                val result = App.instance.dataRepository.textRepository.getText(it)
                if (result is ResultWrapper.Success) {
                    binding.webview.loadDataWithBaseURL(
                        "", HTML_TEMPLATE.replace("@content@", result.value.value),
                        "text/html", "utf-8", null
                    )
                } else {
                    finish()
                }
            }
        } ?: intent.data?.let {
            binding.webview.loadUrl(it.toString())
        }

        binding.navBar.btnBack.setOnClickListener { onBackPressed() }
        binding.btnOk.setOnClickListener { onBackPressed() }
        binding.navBar.btnClose.setOnClickListener { onBackPressed() }
        setContentView(binding.root)
    }
}