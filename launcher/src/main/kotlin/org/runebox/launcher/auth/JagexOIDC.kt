package org.runebox.launcher.auth

import com.nimbusds.oauth2.sdk.AuthorizationRequest
import com.nimbusds.oauth2.sdk.ResponseType
import com.nimbusds.oauth2.sdk.Scope
import com.nimbusds.oauth2.sdk.id.ClientID
import com.nimbusds.oauth2.sdk.id.State
import com.nimbusds.oauth2.sdk.pkce.CodeChallengeMethod
import com.nimbusds.oauth2.sdk.pkce.CodeVerifier
import com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata
import java.net.URI
import java.net.URL
import java.util.*

class JagexOIDC {

    companion object {
        const val OIDC_CONFIG_URL = "https://account.jagex.com/.well-known/openid-configuration"
        const val CLIENT_ID = "com_jagex_auth_desktop_launcher"
        val REDIRECT_URI = URI.create("https://secure.runescape.com/m=weblogin/launcher-redirect")
    }

    lateinit var codeVerifier: CodeVerifier private set


    val oidcConfig: OIDCProviderMetadata =
        URL(OIDC_CONFIG_URL).openStream().use {
            val s = Scanner(it).useDelimiter("\\A")
            val info = if (s.hasNext()) s.next() else ""
            OIDCProviderMetadata.parse(info)
        }

    val authenticationRequestUri: URI get() {
        val state = State()
        this.codeVerifier = CodeVerifier()
        val request = AuthorizationRequest.Builder(
            ResponseType("code"),
            ClientID(CLIENT_ID)
        )
            .endpointURI(oidcConfig.authorizationEndpointURI)
            .redirectionURI(REDIRECT_URI)
            .scope(Scope("openid offline gamesso.token.create user.profile.read"))
            .state(state)
            .codeChallenge(codeVerifier, CodeChallengeMethod.S256)
            .customParameter("flow", "launcher")
            .customParameter("theme", "dual")
            .customParameter("auth_method", "")
            .customParameter("login_type", "")
            .build()
        return request.toURI()
    }

    fun authenticationGameSessionRequestUri(idToken: String): URI {
        val state = State()
        val request = AuthorizationRequest.Builder(
            ResponseType("id_token", "code"),
            ClientID("1fddee4e-b100-4f4e-b2b0-097f9088f9d2")
        )
            .endpointURI(oidcConfig.authorizationEndpointURI)
            .redirectionURI(URI.create("http://localhost"))
            .scope(Scope("openid offline"))
            .state(state)
            .customParameter("id_token_hint", idToken)
            .customParameter("nonce", "G2Y1MzQ1YmQtOWRhNy00NzgyLWExZGQtYjVjZmM1ZjdmZGUz")
            .build()
        return request.toURI()
    }
}