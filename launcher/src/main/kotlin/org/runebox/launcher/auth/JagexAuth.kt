package org.runebox.launcher.auth

import com.nimbusds.oauth2.sdk.pkce.CodeVerifier
import com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata
import javafx.application.Platform
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonArray
import me.friwi.jcefmaven.CefAppBuilder
import me.friwi.jcefmaven.EnumProgress
import me.friwi.jcefmaven.MavenCefAppHandlerAdapter
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.cef.CefApp
import org.cef.browser.CefMessageRouter
import org.runebox.launcher.Launcher
import org.runebox.launcher.config.Character
import org.runebox.launcher.config.JagexAccount
import org.runebox.launcher.ui.view.AccountsView
import org.runebox.launcher.util.getInt
import org.runebox.launcher.util.getLong
import org.runebox.launcher.util.getString
import org.tinylog.kotlin.Logger
import tornadofx.DIContainer
import tornadofx.FX
import tornadofx.getInstance
import java.io.File
import java.util.concurrent.LinkedBlockingQueue
import javax.swing.JFrame
import javax.swing.JTextField
import javax.swing.SwingUtilities
import kotlin.concurrent.thread

class JagexAuth {

    private val httpClient = OkHttpClient()
    private var frame = JFrame("Jagex Account Authorization")
    private val cef = CefAppBuilder()
    private var cefApp: CefApp
    private lateinit var jagexOIDC: JagexOIDC

    init {
        frame.defaultCloseOperation = JFrame.HIDE_ON_CLOSE

        val progressText = JTextField("")
        frame.add(progressText)
        frame.setSize(350, 100)
        frame.setLocationRelativeTo(null)
        frame.isVisible = true

        cef.setInstallDir(File(System.getProperty("user.home"), ".runebox/launcher/"))
        cef.setProgressHandler { state, percent ->
            if(state == EnumProgress.DOWNLOADING) {
                progressText.text = "Downloading... $percent%"
            } else {
                progressText.text = "Initializing. Please wait..."
            }
        }

        cef.cefSettings.also { settings ->
            settings.windowless_rendering_enabled = false
        }
        cef.setAppHandler(object : MavenCefAppHandlerAdapter() {})
        cefApp = cef.build()
        frame.isVisible = false
    }

    fun openLoginPage() {
        Logger.info("Opening Jagex Login page in embedded browser")

        jagexOIDC = JagexOIDC()
        val loginUrl = jagexOIDC.authenticationRequestUri.toURL().toString()
        val oidcConfig = jagexOIDC.oidcConfig
        val verifier = jagexOIDC.codeVerifier

        val client = cefApp.createClient()
        client.addMessageRouter(CefMessageRouter.create())

        val browser = client.createBrowser(loginUrl, false, false)
        frame.setSize(400, 725)
        frame.setLocationRelativeTo(null)
        frame.add(browser.uiComponent, "Center")
        frame.isVisible = true

        val queue = LinkedBlockingQueue<JsonElement>()
        val thread = thread(start = false) {
            try {
                while (true) {
                    val currentUrl = browser.url
                    if (currentUrl.startsWith("jagex:")) break
                    Thread.sleep(100L)
                }
                val base = browser.url.replace("jagex:", "")
                val code = base.split(",").firstOrNull()?.replace("code=", "") ?: error("Code was blank.")
                val token = fetchAccountToken(oidcConfig, verifier, code)
                queue.put(token)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        SwingUtilities.invokeLater { thread.start() }

        val token = try {
            queue.take()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        token as JsonElement
        val gameSessionUrl = jagexOIDC.authenticationGameSessionRequestUri(token.getString("id_token")).toURL().toString()
        browser.loadURL(gameSessionUrl)

        val localhostQueue = LinkedBlockingQueue<String>()
        val localhostThread = thread(start = false) {
            while(true) {
                val currentUrl = browser.url
                if(currentUrl.startsWith("http://localhost")) break
                Thread.sleep(100L)
            }
            try {
                localhostQueue.put(browser.url)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
        SwingUtilities.invokeLater { localhostThread.start() }

        val localhostUrl = try {
            localhostQueue.take()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        localhostUrl as String
        val params = localhostUrl.substringAfter("#")
            .split("&")
            .associate {
                it.split("=").let { (k, v) -> k to v }
            }

        val code = params["code"] ?: error("Code was blank.")
        val idToken = params["id_token"] ?: error("IdToken was blank.")

        val sessionId = fetchSessionId(idToken) ?: error("SessionID was invalid.")
        val characters = fetchCharacters(sessionId)?.jsonArray ?: error("Accounts malformed.")

        val jagexAccount = JagexAccount(
            name = "Covex",
            accessToken = token.getString("access_token"),
            refreshToken = token.getString("refresh_token"),
            expiresIn = token.getInt("expires_in"),
            sessionId = sessionId,
            characters = characters.map {
                Character(
                    displayName = it.getString("displayName"),
                    characterId = it.getString("accountId"),
                    userHash = it.getString("userHash")
                )
            }.toList()
        )
        Launcher.config.accounts.add(jagexAccount)
        Launcher.config.save()

        frame.isVisible = false
        cefApp.dispose()

        Platform.runLater {
            Logger.info("Reloading the launcher accounts view.")
            val accountsView = FX.find<AccountsView>()
            accountsView.reload()
        }

        Logger.info("Added ${jagexAccount.name} Jagex account to launcher.")
    }

    fun fetchAccountToken(oidc: OIDCProviderMetadata, verifier: CodeVerifier, code: String): JsonElement? {
        val body = FormBody.Builder()
            .add("grant_type", "authorization_code")
            .add("client_id", "com_jagex_auth_desktop_launcher")
            .add("code_verifier", verifier.value)
            .add("code", code)
            .add("redirect_uri", JagexOIDC.REDIRECT_URI.toURL().toString())
            .build()
        val request = Request.Builder()
            .url(oidc.tokenEndpointURI.toURL())
            .addHeader("content-type", "application/x-www-form-urlencoded")
            .post(body)
            .build()
        return httpClient.newCall(request).execute().use { response: Response ->
            val responseBody = response.body ?: return null
            val responseString = responseBody.string()
            Json.parseToJsonElement(responseString)
        }
    }

    fun fetchSessionId(idToken: String): String? {
        val request = Request.Builder()
            .url("https://auth.jagex.com/game-session/v1/sessions")
            .addHeader("content-type", "application/json")
            .post(RequestBody.create(
                "application/json".toMediaTypeOrNull(),
                "{ \"idToken\": \"$idToken\" }"))
            .build()
        httpClient.newCall(request).execute().use { response: Response ->
            val responseBody = response.body ?: return null
            return Json.parseToJsonElement(responseBody.string()).getString("sessionId")
        }
    }

    fun fetchCharacters(sessionId: String):JsonElement? {
        val request = Request.Builder()
            .url("https://auth.jagex.com/game-session/v1/accounts")
            .addHeader("Authorization", "Bearer $sessionId")
            .get()
            .build()
        httpClient.newCall(request).execute().use { response: Response ->
            val responseBody = response.body ?: return null
            return Json.parseToJsonElement(responseBody.string())
        }
    }
}