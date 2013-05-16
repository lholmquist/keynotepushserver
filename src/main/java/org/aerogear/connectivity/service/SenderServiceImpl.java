/**
 * JBoss, Home of Professional Open Source
 * Copyright Red Hat, Inc., and individual contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.aerogear.connectivity.service;

import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Sender;
import com.notnoop.apns.APNS;
import com.notnoop.apns.ApnsService;
import org.aerogear.connectivity.model.AndroidApplication;
import org.aerogear.connectivity.model.MobileApplicationInstance;
import org.aerogear.connectivity.model.PushApplication;
import org.aerogear.connectivity.model.iOSApplication;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.*;

public class SenderServiceImpl implements SenderService {

    @Override
    public void broadcast(PushApplication pushApp,
            Map<String, String> jsonMessage) {
        // TODO: DISPATCH TO A QUEUE .....
        Set<iOSApplication> iOSapps = pushApp.getIOSApps();
        for (iOSApplication iOSApp : iOSapps) {

            // service PER iOS app
            ApnsService service = APNS
                    .newService()
                    .withCert(
                            new ByteArrayInputStream(iOSApp.getCertificate()),
                            iOSApp.getPassphrase()).withSandboxDestination()
                    .asQueued().build();

            // get all the tokens:
            final Set<String> iOStokenz = new HashSet<String>();
            Set<MobileApplicationInstance> iOSinstallations = iOSApp
                    .getInstances();
            for (MobileApplicationInstance mobileApplicationInstance : iOSinstallations) {
                iOStokenz.add(mobileApplicationInstance.getDeviceToken());
            }

            String msg = APNS.newPayload()
                    .alertBody(jsonMessage.get("alert")) // payload from the message....
                    .customField("title",jsonMessage.get("title"))
                    .customField("id", jsonMessage.get("id"))
                    .badge(2) // could submitted, on the payload - but hard coded for testing...
                    .sound("default") // could submitted, on the payload - but hard coded for testing...
                    .build();

            // send it out:
            service.push(iOStokenz, msg);
        }

        // TODO: DISPATCH TO A QUEUE .....
        Set<AndroidApplication> androidApps = pushApp.getAndroidApps();
        for (AndroidApplication androidApplication : androidApps) {

            // service PER android app:
            Sender sender = new Sender(androidApplication.getGoogleKey());

            final List<String> androidtokenz = new ArrayList<String>();
            Set<MobileApplicationInstance> androidApplications = androidApplication
                    .getInstances();
            for (MobileApplicationInstance mobileApplicationInstance : androidApplications) {
                androidtokenz.add(mobileApplicationInstance.getDeviceToken());
            }

            // payload builder:
            Message msg = new Message.Builder()

            .addData("text", jsonMessage.get("alert"))
                    // could submitted, on the payload... -
                    // but hard coded for testing...
                    // (no meaning,here...)
                    .addData("message",jsonMessage.get("alert")) //this will show in the android notification center
                    .addData("title", jsonMessage.get("title"))
                    .addData("id", jsonMessage.get("id"))
                    .build();

            // send it out.....
            try {
                sender.send(msg, androidtokenz, 0);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
