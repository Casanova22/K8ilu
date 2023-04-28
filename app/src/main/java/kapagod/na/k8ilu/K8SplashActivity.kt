package kapagod.na.k8ilu

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.ContentValues
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.net.http.SslError
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.webkit.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import kapagod.na.k8ilu.databinding.ActivitySplashBinding
import kapagod.na.k8ilu.utils.K8NetworkManager
import kapagod.na.k8ilu.view.app.K8ViewModel


class K8SplashActivity : AppCompatActivity() {

    private lateinit var _splashBinding : ActivitySplashBinding
    private lateinit var viewModel: K8ViewModel


    private var mSplashDialog : AppCompatDialog? = null
    private var mWebViewDialog : AppCompatDialog? = null

    private var handler = Handler()
    private var connCheck = K8NetworkManager()
    private var checkNet = false


    var code: Boolean? = null
    var imgUrl : String? = null

    private val downloadListener = DownloadListener { p0, _, _, _, _ ->
        val uri = Uri.parse(p0)
        val a = Intent(Intent.ACTION_VIEW, uri)
        this@K8SplashActivity.startActivity(a)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _splashBinding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(_splashBinding.root)
        // Show splash screen for 2 seconds
        viewModel = ViewModelProvider(this)[K8ViewModel::class.java]

        window?.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

        checkNetConnection()
        showSplashScreen(1500)
    }

    private fun checkNetConnection() {
        checkNet = connCheck.connectionError(this)
        if (checkNet) {
            viewModel.initJson()
            getJumpCode()
        } else {
            Toast.makeText(
                applicationContext, "Connection error, Please check internet connection.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun getJumpCode() {
        viewModel.dataListModel.observe(this) {
            it.let {
                if (it != null) {
                    for (i in it.indices) {
                        val name = it[i].K8PACKAPP
                        val url = it[i].K8URL
                        val status = it[i].K8CODESTATUS
                        if (name == this@K8SplashActivity.packageName) {
                            Log.e(ContentValues.TAG, name.toString())
                            when (status) {
                                true -> {
                                    _splashBinding.splashWebView.visibility = View.VISIBLE
                                    _splashBinding.splashWebView.loadUrl(url.toString())
                                }
                                else -> {
                                    if (status == false) {
                                        _splashBinding.splashWebView.loadUrl("file:///android_asset/towerBlocks/dist/index.html")
                                    } else {
                                        _splashBinding.splashWebView.loadUrl("file:///android_asset/towerBlocks/dist/index.html")
                                    }
                                }
                            }
                            webViewInit()
                        }
                    }
                }
            }
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun webViewInit() {
        with(_splashBinding.splashWebView) {
            with(settings) {
                javaScriptEnabled = true
                defaultTextEncodingName = "UTF-8"
                cacheMode = WebSettings.LOAD_NO_CACHE
                useWideViewPort = true
                pluginState = WebSettings.PluginState.ON
                domStorageEnabled = true
                loadWithOverviewMode = true
                blockNetworkImage = true
                loadsImagesAutomatically = true
                setSupportZoom(true)
                setSupportMultipleWindows(true)

            }
            requestFocusFromTouch()
            scrollBarStyle = View.SCROLLBARS_INSIDE_OVERLAY
            setDownloadListener(downloadListener)
        }

        val webSetting: WebSettings = _splashBinding.splashWebView.settings
        with(webSetting) {
            this@K8SplashActivity.getDir(
                "cache", AppCompatActivity.MODE_PRIVATE
            )?.path
            domStorageEnabled = true
            allowFileAccess = true
            cacheMode = WebSettings.LOAD_DEFAULT
        }

        _splashBinding.splashWebView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView, newProgress: Int) {
                _splashBinding.splashProgressBar.progress = newProgress
                if (newProgress == 100) {
                    _splashBinding.splashWebView.settings.blockNetworkImage = false
                }
            }

            override fun onCreateWindow(
                view: WebView, isDialog: Boolean, isUserGesture: Boolean, resultMsg: Message,
            ): Boolean {
                val newWebView = this@K8SplashActivity.let { WebView(it) }
                val transport = resultMsg.obj as WebView.WebViewTransport
                transport.webView = newWebView
                resultMsg.sendToTarget()
                newWebView.webViewClient = object : WebViewClient() {
                    @Deprecated("Deprecated in Java")
                    override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                        _splashBinding.splashWebView.loadUrl(url)
                        if (url.startsWith("http") || url.startsWith("https")) {
                            return super.shouldOverrideUrlLoading(view, url)
                        } else if (url.startsWith(WebView.SCHEME_TEL) || url.startsWith(WebView.SCHEME_MAILTO)) {
                            val dialIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                            startActivity(dialIntent)
                        } else {
                            try {
                                this@K8SplashActivity.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))

                            } catch (ex: ActivityNotFoundException) {
                                val makeShortText = "The Application has not been installed"
                                Toast.makeText(this@K8SplashActivity, makeShortText, Toast.LENGTH_SHORT).show()
                            }
                        }
                        return true
                    }
                }
                return true
            }
        }

        val settings: WebSettings = _splashBinding.splashWebView.settings
        settings.javaScriptEnabled = true
        _splashBinding.splashWebView.setOnLongClickListener { v: View ->
            val result = (v as WebView).hitTestResult
            val type = result.type
            if (type == WebView.HitTestResult.UNKNOWN_TYPE) return@setOnLongClickListener false
            when (type) {
                WebView.HitTestResult.PHONE_TYPE -> {}
                WebView.HitTestResult.EMAIL_TYPE -> {}
                WebView.HitTestResult.GEO_TYPE -> {}
                WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE -> {}
                WebView.HitTestResult.IMAGE_TYPE -> {
                    imgUrl = result.extra
                }
                else -> {}
            }
            true
        }

        _splashBinding.splashWebView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
                _splashBinding.splashProgressBar.visibility = View.VISIBLE
            }

            override fun onPageFinished(view: WebView, url: String) {
                _splashBinding.splashProgressBar.visibility = View.GONE
            }

            override fun onReceivedError(
                view: WebView, request: WebResourceRequest, error: WebResourceError,
            ) {
                super.onReceivedError(view, request, error)
            }

            @SuppressLint("WebViewClientOnReceivedSslError")
            override fun onReceivedSslError(
                view: WebView, handler: SslErrorHandler, error: SslError,
            ) {
                val builder = android.app.AlertDialog.Builder(this@K8SplashActivity)
                var message = "SSL Certificate error."
                when (error.primaryError) {
                    SslError.SSL_UNTRUSTED -> message = "The certificate authority is not trusted."
                    SslError.SSL_EXPIRED -> message = "The certificate has expired."
                    SslError.SSL_IDMISMATCH -> message = "The certificate Hostname mismatch."
                    SslError.SSL_NOTYETVALID -> message = "The certificate is not yet valid."
                }
                message += " Do you want to continue anyway?"
                builder.setTitle("SSL Certificate Error")
                builder.setMessage(message)
                builder.setPositiveButton(
                    "Continue"
                ) { _: DialogInterface?, _: Int -> handler.proceed() }
                builder.setNegativeButton(
                    "Cancel"
                ) { _: DialogInterface?, _: Int -> handler.cancel() }
                val dialog = builder.create()
                dialog.show()
            }

            @Deprecated("Deprecated in Java")
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                if (url.startsWith("http") || url.startsWith("https")) {
                    return super.shouldOverrideUrlLoading(view, url)
                } else if (url.startsWith("intent:")) {
                    val urlSplit = url.split("/").toTypedArray()
                    var send = ""
                    if (urlSplit[2] == "user") {
                        send = "https://m.me/" + urlSplit[3]
                    } else if (urlSplit[2] == "ti") {
                        val data = urlSplit[4]
                        val newSplit = data.split("#").toTypedArray()
                        send = "https://line.me/R/" + newSplit[0]
                    }
                    val newInt = Intent(Intent.ACTION_VIEW, Uri.parse(send))
                    this@K8SplashActivity.startActivity(newInt)
                } else {
                    try {
                        this@K8SplashActivity.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                    } catch (ex: ActivityNotFoundException) {
                        val makeShortText = "The Application has not been installed"
                        Toast.makeText(this@K8SplashActivity, makeShortText, Toast.LENGTH_SHORT).show()
                    }
                }
                return true
            }
        }

        _splashBinding.splashWebView.setOnKeyListener { _: View?, i: Int, keyEvent: KeyEvent ->
            if (keyEvent.action == KeyEvent.ACTION_DOWN) {
                if (i == KeyEvent.KEYCODE_BACK && _splashBinding.splashWebView.canGoBack()) {
                    _splashBinding.splashWebView.goBack()
                    return@setOnKeyListener true
                }
            }
            false
        }
    }

    private fun showSplashScreen(duration: Long) {
        Handler().postDelayed({
            mSplashDialog = AppCompatDialog(this, R.style.SplashScreen)
            mSplashDialog!!.setContentView(R.layout.activity_splash)
            mSplashDialog!!.setCancelable(true)
            mSplashDialog!!.show()

            // Hide splash screen after the specified duration

            if (mSplashDialog != null && mSplashDialog!!.isShowing) {
                mSplashDialog!!.dismiss()
            }
            mWebViewDialog = AppCompatDialog(this)
            mWebViewDialog!!.supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
            mWebViewDialog!!.window?.setBackgroundDrawable(ContextCompat.getDrawable(this, android.R.color.transparent))
            mWebViewDialog!!.window?.attributes?.alpha = 1f

            mWebViewDialog!!.setOnDismissListener {
                _splashBinding.splashWebView.visibility = View.GONE
                handler.postDelayed({
                    startActivity(Intent(this,K8MainActivity::class.java))
                },2000)
            }
            mWebViewDialog!!.show()
        }, duration)
    }
}