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

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;

import org.json.JSONException;
import org.json.JSONObject;

import static net.openid.appauth.Preconditions.checkNotNull;

/**
 * A response to an End Session request
 *
 * @see EndSessionRequest
 */
public class EndSessionResponse {

    /**
     * The extra string used to store an {@link EndSessionResponse} in an intent by
     * {@link #toIntent()}.
     */
    public static final String EXTRA_RESPONSE = "net.openid.appauth.EndSessionResponse";

    @VisibleForTesting
    static final String KEY_REQUEST = "request";

    /**
     * The end session request associated with this response.
     */
    @NonNull
    public final EndSessionRequest request;

    /**
     * Creates instance of {@link EndSessionResponse}.
     */
    public static final class Builder {

        @NonNull
        private EndSessionRequest mRequest;

        /**
         * Creates an end session builder with the specified mandatory properties.
         */
        public Builder(@NonNull EndSessionRequest request) {
            mRequest = checkNotNull(request, "end session request cannot be null");
        }

        /**
         * Extracts end session response parameters from the query portion of a redirect URI .
         */
        public Builder fromUri(@NonNull Uri uri) {
            return this;
        }

        /**
         * Builds the EndSessionResponse object
         */
        public EndSessionResponse build() {
            return new EndSessionResponse(mRequest);
        }
    }

    private EndSessionResponse(@NonNull EndSessionRequest request) {
        this.request = request;
    }

    /**
     * Produces a JSON representation of the end session response for persistent storage or local
     * transmission (e.g. between activities).
     */
    @NonNull
    public JSONObject jsonSerialize() {
        JSONObject json = new JSONObject();
        JsonUtil.put(json, KEY_REQUEST, request.jsonSerialize());
        return json;
    }

    /**
     * Produces a JSON representation of the end session response for persistent storage or local
     * transmission (e.g. between activities). This method is just a convenience wrapper
     * for {@link #jsonSerialize()}, converting the JSON object to its string form.
     */
    @NonNull
    public String jsonSerializeString() {
        return jsonSerialize().toString();
    }


    /**
     * Reads an end session response from a JSON string representation produced by
     * {@link #jsonSerialize()}.
     *
     * @throws JSONException if the provided JSON does not match the expected structure.
     */
    @NonNull
    public static EndSessionResponse jsonDeserialize(@NonNull JSONObject json)
            throws JSONException {
        if (!json.has(KEY_REQUEST)) {
            throw new IllegalArgumentException(
                    "authorization request not provided and not found in JSON");
        }

        EndSessionRequest request =
                EndSessionRequest.jsonDeserialize(json.getJSONObject(KEY_REQUEST));

        return new EndSessionResponse.Builder(request)
                .build();
    }

    /**
     * Reads an end session request from a JSON string representation produced by
     * {@link #jsonSerializeString()}. This method is just a convenience wrapper for
     * {@link #jsonDeserialize(JSONObject)}, converting the JSON string to its JSON object form.
     *
     * @throws JSONException if the provided JSON does not match the expected structure.
     */
    @NonNull
    public static EndSessionResponse jsonDeserialize(@NonNull String jsonStr)
            throws JSONException {
        return jsonDeserialize(new JSONObject(jsonStr));
    }

    /**
     * Produces an intent containing this end session response. This is used to deliver the
     * end session response to the registered handler.
     */
    @NonNull
    public Intent toIntent() {
        Intent data = new Intent();
        data.putExtra(EXTRA_RESPONSE, this.jsonSerializeString());
        return data;
    }

    /**
     * Extracts an end session response from an intent produced by {@link #toIntent()}. This is
     * used to extract the response from the intent data passed.
     */
    @Nullable
    public static EndSessionResponse fromIntent(@NonNull Intent dataIntent) {
        checkNotNull(dataIntent, "dataIntent must not be null");
        if (!dataIntent.hasExtra(EXTRA_RESPONSE)) {
            return null;
        }

        try {
            return EndSessionResponse.jsonDeserialize(dataIntent.getStringExtra(EXTRA_RESPONSE));
        } catch (JSONException ex) {
            throw new IllegalArgumentException("Intent contains malformed end session response", ex);
        }
    }
}
