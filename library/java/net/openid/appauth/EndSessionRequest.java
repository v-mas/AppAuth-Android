/*
 * Copyright 2015 The AppAuth for Android Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.openid.appauth;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import org.json.JSONException;
import org.json.JSONObject;

import static net.openid.appauth.Preconditions.checkNotNull;

/**
 * An OAuth2 end session request
 */
public class EndSessionRequest {

    @VisibleForTesting
    static final String PARAM_REDIRECT_URI = "redirect_uri";

    private static final String KEY_CONFIGURATION = "configuration";
    private static final String KEY_REDIRECT_URI = "redirectUri";

    /**
     * The service's {@link AuthorizationServiceConfiguration configuration}.
     * This configuration specifies how to connect to a particular OAuth provider.
     * Configurations may be
     * {@link AuthorizationServiceConfiguration#AuthorizationServiceConfiguration(Uri, Uri, Uri)}
     * created manually}, or {@link AuthorizationServiceConfiguration#fetchFromUrl(Uri, * AuthorizationServiceConfiguration.RetrieveConfigurationCallback)} via an OpenID Connect
     * Discovery Document}.
     */
    @NonNull
    public final AuthorizationServiceConfiguration configuration;

    /**
     * The client's redirect URI.
     */
    @NonNull
    private final Uri redirectUri;

    public EndSessionRequest(
            @NonNull AuthorizationServiceConfiguration configuration,
            @NonNull Uri redirectUri) {
        this.configuration = configuration;
        this.redirectUri = redirectUri;
    }

    /**
     * Produces a request URI, that can be used to dispath the end session request.
     */
    @NonNull
    public Uri toUri() {
        Uri.Builder uriBuilder = configuration.endSessionEndpoint.buildUpon()
                .appendQueryParameter(PARAM_REDIRECT_URI, redirectUri.toString());
        return uriBuilder.build();
    }

    /**
     * Produces a JSON representation of the end session request for persistent storage or local
     * transmission (e.g. between activities).
     */
    @NonNull
    public JSONObject jsonSerialize() {
        JSONObject json = new JSONObject();
        JsonUtil.put(json, KEY_CONFIGURATION, configuration.toJson());
        JsonUtil.put(json, KEY_REDIRECT_URI, redirectUri.toString());
        return json;
    }

    /**
     * Produces a JSON string representation of the end session request for persistent storage or
     * local transmission (e.g. between activities). This method is just a convenience wrapper
     * for {@link #jsonSerialize()}, converting the JSON object to its string form.
     */
    public String jsonSerializeString() {
        return jsonSerialize().toString();
    }

    /**
     * Reads an end session request from a JSON string representation produced by
     * {@link #jsonSerialize()}.
     * @throws JSONException if the provided JSON does not match the expected structure.
     */
    @NonNull
    public static EndSessionRequest jsonDeserialize(@NonNull JSONObject json) throws JSONException {
        checkNotNull(json, "json cannot be null");
        return new EndSessionRequest(
            AuthorizationServiceConfiguration.fromJson(json.getJSONObject(KEY_CONFIGURATION)),
            JsonUtil.getUri(json, KEY_REDIRECT_URI)
        );
    }
    /**
     * Reads an end session request from a JSON string representation produced by
     * {@link #jsonSerializeString()}. This method is just a convenience wrapper for
     * {@link #jsonDeserialize(JSONObject)}, converting the JSON string to its JSON object form.
     * @throws JSONException if the provided JSON does not match the expected structure.
     */
    @NonNull
    public static EndSessionRequest jsonDeserialize(@NonNull String jsonStr) throws JSONException {
        checkNotNull(jsonStr, "json string cannot be null");
        return jsonDeserialize(new JSONObject(jsonStr));
    }
}
