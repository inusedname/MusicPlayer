package dev.keego.musicplayer.ui.search

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.webkit.WebResourceResponse
import android.webkit.WebView
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
import com.google.common.net.UrlEscapers
import dev.keego.musicplayer.config.theme.MusicPlayerTheme
import dev.keego.musicplayer.libs.requestdatawebviewclient.WriteHandlingWebResourceRequest
import dev.keego.musicplayer.libs.requestdatawebviewclient.WriteHandlingWebViewClient
import timber.log.Timber
import java.nio.charset.StandardCharsets
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

class DirectDownloadAuthActivity : ComponentActivity() {
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
                                loadUrl(uri.toString())
                                Timber.d("Loading $uri")
                                webViewClient = object : WriteHandlingWebViewClient(this) {
                                    override fun shouldInterceptRequest(
                                        view: WebView?,
                                        request: WriteHandlingWebResourceRequest?,
                                    ): WebResourceResponse? {
                                        request?.let {
                                            if (request.method == "POST" &&
                                                request.url.toString().contains("dl.php")
                                            ) {
                                                val body = request.ajaxData
                                                Timber.d("Body: $body")
                                            }
                                        }
                                        return super.shouldInterceptRequest(view, request)
                                    }
                                }
                            }
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