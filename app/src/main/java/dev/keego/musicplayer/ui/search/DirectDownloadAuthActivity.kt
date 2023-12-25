package dev.keego.musicplayer.ui.search

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import com.acsbendi.requestinspectorwebview.RequestInspectorWebViewClient
import com.acsbendi.requestinspectorwebview.WebViewRequest
import com.google.common.net.UrlEscapers
import com.google.gson.JsonParser
import dev.keego.musicplayer.config.theme.MusicPlayerTheme
import dev.keego.musicplayer.pref.AppPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import java.nio.charset.StandardCharsets
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

class DirectDownloadAuthActivity : ComponentActivity() {
    @SuppressLint("SetJavaScriptEnabled")
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val uri = intent.data!!

        setContent {
            MusicPlayerTheme {
                Scaffold(
                    topBar = {
                        TopAppBar(title = { Text(text = uri.path.toString()) }, navigationIcon = {
                            IconButton(onClick = this::finish) {
                                Icon(Icons.Rounded.ArrowBack, contentDescription = "Back")
                            }
                        })
                    }
                ) { paddingValues ->
                    AndroidView(modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxSize(),
                        factory = { context ->
                            WebView(context).apply {
                                settings.apply {
                                    javaScriptEnabled = true
                                    domStorageEnabled = true
                                }
                                webViewClient = object : RequestInspectorWebViewClient(this@apply) {
                                    override fun shouldInterceptRequest(
                                        view: WebView,
                                        webViewRequest: WebViewRequest,
                                    ): WebResourceResponse? {
                                        webViewRequest.let {
                                            if (it.method == "POST" &&
                                                it.url.contains("dl.php")
                                            ) {
                                                val body = it.body
                                                val json = JsonParser.parseString(body).asJsonObject
                                                val token = json["h"].asString
                                                AppPreferences.directDownloadToken = token

                                                lifecycleScope.launch(Dispatchers.Main) {
                                                    Toast.makeText(
                                                        this@DirectDownloadAuthActivity,
                                                        "Done! Let's continue our work...",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                    delay(2000)
                                                    finish()
                                                }
                                            }
                                        }
                                        return super.shouldInterceptRequest(view, webViewRequest)
                                    }

                                    override fun onReceivedError(
                                        view: WebView?,
                                        request: WebResourceRequest?,
                                        error: WebResourceError?,
                                    ) {
                                        Timber.e("${error?.errorCode}: ${error?.description}")
                                        super.onReceivedError(view, request, error)
                                    }

                                    override fun onReceivedHttpError(
                                        view: WebView?,
                                        request: WebResourceRequest?,
                                        errorResponse: WebResourceResponse?,
                                    ) {
                                        Timber.e("${errorResponse?.statusCode}: ${errorResponse?.reasonPhrase}")
                                        super.onReceivedHttpError(view, request, errorResponse)
                                    }

                                    override fun onPageFinished(view: WebView?, url: String?) {
                                        super.onPageFinished(view, url)
                                        view?.evaluateJavascript(
                                            """
                                            function scrollToElement(id) {
                                                var elem = document.getElementById(id);
                                                var x = 0;
                                                var y = 0;

                                                while (elem != null) {
                                                    x += elem.offsetLeft;
                                                    y += elem.offsetTop;
                                                    elem = elem.offsetParent;
                                                }
                                                window.scrollTo({top: y, behavior: 'smooth'});
                                            }
                                            
                                            scrollToElement('song-preview');
                                            """.trimIndent(),
                                            null
                                        )
                                    }
                                }
                            }
                        }, update = { webView ->
                            webView.loadUrl(
                                uri.toString(),
                                mapOf("Referer" to "https://free-mp3-download.net/")
                            )
                            Timber.d("Loading $uri")
                        })
                }
            }
        }
    }

    companion object {
        @OptIn(ExperimentalEncodingApi::class)
        fun start(context: Context, songId: String, queryString: String) {
            val urlEncoded = UrlEscapers.urlPathSegmentEscaper().escape(queryString)
            val base64Encoded = Base64.encode(urlEncoded.toByteArray(StandardCharsets.UTF_8))
            Intent(context, DirectDownloadAuthActivity::class.java).apply {
                data =
                    "https://free-mp3-download.net/download.php?id=$songId&q=${base64Encoded}".toUri()
                context.startActivity(this)
            }
        }
    }
}