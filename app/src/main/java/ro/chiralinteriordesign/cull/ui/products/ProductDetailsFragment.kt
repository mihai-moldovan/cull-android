package ro.chiralinteriordesign.cull.ui.products

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayoutMediator
import ro.chiralinteriordesign.cull.R
import ro.chiralinteriordesign.cull.databinding.ProductDetailsFragmentBinding
import ro.chiralinteriordesign.cull.databinding.ProductDetailsPhotoItemBinding
import ro.chiralinteriordesign.cull.model.shop.Product

/**
 * Created by Mihai Moldovan on 14/02/2021.
 */
private const val ARG_PRODUCT = "product"
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
            font-size: 12px;
            color: white;
            margin: 0;
            padding: 0 0 2px 0;
        }
    </style>
</head>
<body>
@content@
</body>
</html>
"""

class ProductDetailsFragment : Fragment() {

    private lateinit var product: Product
    private var binding: ProductDetailsFragmentBinding? = null

    companion object {
        @JvmStatic
        fun newInstance(product: Product) =
            ProductDetailsFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_PRODUCT, product)
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            product = it.getSerializable(ARG_PRODUCT) as Product
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ProductDetailsFragmentBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = binding ?: return
        binding.navBar.titleView.setText(R.string.products_details_title)
        binding.productTitleView.text = product.title
        binding.productPriceView.text = product.priceString
        val html = product.bodyHtml
            .replace("[tab]", "<p>")
            .replace("[/tab]", "</p>")



        binding.productDescriptionView.setBackgroundColor(Color.TRANSPARENT)
        binding.productDescriptionView.settings.javaScriptEnabled = true
        binding.productDescriptionView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                view.loadUrl("javascript:MyApp.resize(document.body.getBoundingClientRect().height)")
                super.onPageFinished(view, url)
            }
        }
        binding.productDescriptionView.addJavascriptInterface(this, "MyApp")
        binding.productDescriptionView.loadDataWithBaseURL(
            "", HTML_TEMPLATE.replace("@content@", html),
            "text/html", "utf-8", null
        )

        binding.photosRecyclerView.adapter = PhotoAdapter(product.images)

        binding.navBar.btnBack.setOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.navBar.btnClose.setOnClickListener {
            requireActivity().onBackPressed()
        }
        TabLayoutMediator(binding.dotsIndicator, binding.photosRecyclerView) { _, _ -> }.attach()
    }

    @JavascriptInterface
    fun resize(height: Float) = binding?.productDescriptionView?.let { webView ->
        webView.post {
            val lParams = webView.layoutParams
            lParams.height = (height * resources.displayMetrics.density).toInt()
            webView.layoutParams = lParams
        }
    }


    class PhotoViewHolder(val binding: ProductDetailsPhotoItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        var photoUrl: String? = null
            set(newValue) {
                field = newValue
                newValue?.let {
                    Glide.with(binding.imageView)
                        .load(it)
                        .into(binding.imageView)
                }
            }
    }

    class PhotoAdapter(private val photos: List<String>) : RecyclerView.Adapter<PhotoViewHolder>() {
        override fun getItemCount(): Int {
            return photos.size
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
            return PhotoViewHolder(
                ProductDetailsPhotoItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent, false
                )
            )
        }

        override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
            holder.photoUrl = photos[position]
        }
    }
}



