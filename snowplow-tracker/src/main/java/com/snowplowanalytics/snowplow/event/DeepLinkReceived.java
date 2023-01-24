/*
 * Copyright (c) 2015-2022 Snowplow Analytics Ltd. All rights reserved.
 *
 * This program is licensed to you under the Apache License Version 2.0,
 * and you may not use this file except in compliance with the Apache License Version 2.0.
 * You may obtain a copy of the Apache License Version 2.0 at http://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the Apache License Version 2.0 is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Apache License Version 2.0 for the specific language governing permissions and limitations there under.
 */

package com.snowplowanalytics.snowplow.event;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.installreferrer.api.ReferrerDetails;

import java.util.HashMap;
import java.util.Map;

/** A deep-link received in the app. */
public class DeepLinkReceived extends AbstractSelfDescribing {

    public final static String SCHEMA = "iglu:com.snowplowanalytics.mobile/deep_link_received/jsonschema/1-0-0";

    public final static String PARAM_REFERRER = "referrer";
    public final static String PARAM_URL = "url";

    /** Referrer URL, source of this deep-link. */
    @Nullable
    public String referrer;
    /** URL in the received deep-link. */
    @NonNull
    public final String url;

    /**
     * Creates a deep-link received event.
     * @param url URL in the received deep-link.
     */
    public DeepLinkReceived(@NonNull String url) {
        this.url = url;
    }

    /**
     * Convenient factory method that generates the event from the Intent received by the Activity.
     * The Activity can store in the Intent the deep link url and the referrer url (if available).
     * @param intent Intent received by the Activity.
     * @return a new event.
     */
    @Nullable
    public static DeepLinkReceived fromIntent(@Nullable Intent intent) {
        if (intent == null) {
            return null;
        }
        Uri uri = intent.getData();
        if (uri == null) {
            return null;
        }
        String deepLinkUrl = uri.toString();
        String referrer = null;
        Bundle extras = intent.getExtras();
        if (extras != null) {
            Uri referrerUri = (Uri) extras.get(Intent.EXTRA_REFERRER);
            if (referrerUri != null) {
                referrer = referrerUri.toString();
            }
        }
        return new DeepLinkReceived(deepLinkUrl).referrer(referrer);
    }

    /**
     * Convenient factory method that generates the event from the ReferrerDetails generated by
     * the Install Referrer Library.
     * @apiNote It requires a dependency to `com.android.installreferrer:installreferrer`.
     * @param referrerDetails Details received by the InstallReferrer.
     * @return a new event.
     */
    @Nullable
    public static DeepLinkReceived fromReferrerDetails(@Nullable ReferrerDetails referrerDetails) {
        if (referrerDetails == null) {
            return null;
        }
        String deepLinkUrl = referrerDetails.getInstallReferrer();
        if (deepLinkUrl == null) {
            return null;
        }
        return new DeepLinkReceived(deepLinkUrl);
    }

    // Builder methods

    /** Referrer URL, source of this deep-link. */
    @NonNull
    public DeepLinkReceived referrer(@Nullable String referrer) {
        this.referrer = referrer;
        return this;
    }

    // Tracker methods

    @Override
    public @NonNull Map<String, Object> getDataPayload() {
        HashMap<String,Object> payload = new HashMap<>();
        payload.put(PARAM_URL, url);
        if (referrer != null) {
            payload.put(PARAM_REFERRER, referrer);
        }
        return payload;
    }

    @Override
    public @NonNull String getSchema() {
        return SCHEMA;
    }
}

