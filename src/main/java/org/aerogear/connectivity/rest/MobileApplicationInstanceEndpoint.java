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

package org.aerogear.connectivity.rest;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.aerogear.connectivity.api.MobileApplication;
import org.aerogear.connectivity.model.MobileApplicationInstance;
import org.aerogear.connectivity.service.MobileApplicationInstanceService;
import org.aerogear.connectivity.service.MobileApplicationService;

@Stateless
@Path("/registry/device")
@TransactionAttribute
public class MobileApplicationInstanceEndpoint
{
    @Inject private Logger logger;
	@Inject
    private MobileApplicationInstanceService mobileApplicationInstanceService;

    @Inject
    private MobileApplicationService mobileApplicationService;

    @POST
    @Consumes("application/json")
    public MobileApplicationInstance registerInstallation(
            @HeaderParam("ag-mobile-app") String mobileAppId, 
            MobileApplicationInstance entity) {

        List<MobileApplicationInstance> instances = mobileApplicationInstanceService.findMobileApplicationInstancesByToken(entity.getDeviceToken());
		if (instances.isEmpty()) {
	        // store the installation:
	        entity = mobileApplicationInstanceService.addMobileApplicationInstance(entity);
	        // find the matching variation:
	        MobileApplication mobileApp = mobileApplicationService.findMobileApplicationById(mobileAppId);
	        // add installation to the matching variant
	        mobileApplicationService.addInstallation(mobileApp, entity);
		} else {
            logger.warning("UPDATE ON POST.......");

            if (instances.size()>1) {
               logger.severe("Too many registration for one installation");
           }
		   // update the entity:
           entity = this.updateMobileApplicationInstance(instances.get(0), entity);		   
        }

        return entity;
   }

   private MobileApplicationInstance updateMobileApplicationInstance(MobileApplicationInstance toUpdate, MobileApplicationInstance postedVariant) {
       toUpdate.setCategory(postedVariant.getCategory());
       toUpdate.setDeviceToken(postedVariant.getDeviceToken());
       toUpdate.setClientIdentifier(postedVariant.getClientIdentifier());
       toUpdate.setDeviceType(postedVariant.getDeviceType());
       toUpdate.setMobileOperatingSystem(postedVariant.getMobileOperatingSystem());
       toUpdate.setOsVersion(postedVariant.getOsVersion());

       //update
       return mobileApplicationInstanceService.updateMobileApplicationInstance(toUpdate);
   }
}