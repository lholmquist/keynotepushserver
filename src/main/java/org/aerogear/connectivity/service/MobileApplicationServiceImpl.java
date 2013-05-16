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

import javax.inject.Inject;

import org.aerogear.connectivity.api.MobileApplication;
import org.aerogear.connectivity.jpa.dao.MobileApplicationDao;
import org.aerogear.connectivity.model.AbstractMobileApplication;
import org.aerogear.connectivity.model.MobileApplicationInstance;

public class MobileApplicationServiceImpl implements MobileApplicationService {

    @Inject
    private MobileApplicationDao mobileApplicationDao;
    
    @Override
    public MobileApplication findMobileApplicationById(String id) {
        return mobileApplicationDao.find(AbstractMobileApplication.class, id);
    }

    @Override
    public void addInstallation(MobileApplication mobileApp,
            MobileApplicationInstance entity) {
        
        mobileApp.getInstances().add(entity);
        mobileApplicationDao.update(mobileApp);
    }

}
