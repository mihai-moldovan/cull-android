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
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayoutMediator
import ro.chiralinteriordesign.cull.R
import ro.chiralinteriordesign.cull.databinding.ProductDetailsFragmentBinding
import ro.chiralinteriordesign.cull.databinding.ProductDetailsPhotoItemBinding
import ro.chiralinteriordesign.cull.model.shop.Cart
import ro.chiralinteriordesign.cull.model.shop.Product

/**
 * Created by Mihai Moldovan on 14/02/2021.
 */
private const val ARG_PRODUCT = "product"
private const val ARG_CART_INDEX = "cart_index"
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


    companion object {
        @JvmStatic
        fun newInstance(product: Product, cartIndex: Int?) =
            ProductDetailsFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_PRODUCT, product)
                    cartIndex?.let { putInt(ARG_CART_INDEX, it) }
                }
            }
    }

    private var binding: ProductDetailsFragmentBinding? = null
    private val viewModel: ProductDetailsViewModel by viewModels()
    private val adapter = PhotoAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            viewModel.product.value = it.getSerializable(ARG_PRODUCT) as Product
            viewModel.setCartIndex(it.getInt(ARG_CART_INDEX))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ProductDetailsFragmentBinding.inflate(inflater, container, false)
        return binding?.root
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
        binding.productDescriptionView.setBackgroundColor(Color.TRANSPARENT)
        binding.productDescriptionView.settings.javaScriptEnabled = true
        binding.productDescriptionView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                view.loadUrl("javascript:MyApp.resize(document.body.getBoundingClientRect().height)")
                super.onPageFinished(view, url)
            }
        }
        binding.productDescriptionView.addJavascriptInterface(this, "MyApp")



        viewModel.product.observe(viewLifecycleOwner) { product ->
            val product = product ?: return@observe
            binding.productTitleView.text = product.title
            binding.productPriceView.text = product.priceString
            val html = product.bodyHtml
                .replace("[tab]", "<p>")
                .replace("[/tab]", "</p>")
            binding.productDescriptionView.loadDataWithBaseURL(
                "", HTML_TEMPLATE.replace("@content@", html),
                "text/html", "utf-8", null
            )
            adapter.photos = product.images
        }

        viewModel.isAdded.observe(viewLifecycleOwner) { isAdded ->
            binding.btnAddToCart.setText(if (isAdded) R.string.remove_from_cart else R.string.add_to_cart)
        }

        binding.navBar.btnBack.setOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.navBar.btnClose.setOnClickListener {
            requireActivity().onBackPressed()
        }
        binding.photosRecyclerView.adapter = adapter
        TabLayoutMediator(binding.dotsIndicator, binding.photosRecyclerView) { _, _ -> }.attach()

        binding.btnAddToCart.setOnClickListener {
            viewModel.addRemoveProduct()
        }
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

    class PhotoAdapter : RecyclerView.Adapter<PhotoViewHolder>() {

        var photos: List<String>? = null
            set(newValue) {
                field = newValue
                notifyDataSetChanged()
            }

        override fun getItemCount(): Int {
            return photos?.size ?: 0
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
            holder.photoUrl = photos?.get(position)
        }
    }
}



