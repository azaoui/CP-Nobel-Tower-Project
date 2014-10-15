/*
 * Copyright (C) 2003-2014 eXo Platform SAS.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.nobeltower.social.webui.connections;

import org.chromattic.api.query.Ordering;
import org.chromattic.api.query.QueryBuilder;
import org.chromattic.api.query.QueryResult;
import org.chromattic.core.query.QueryImpl;
import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.social.core.chromattic.entity.IdentityEntity;
import org.exoplatform.social.core.chromattic.entity.ProfileEntity;
import org.exoplatform.social.core.chromattic.entity.ProviderEntity;
import org.exoplatform.social.core.identity.IdentityResult;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.identity.model.Profile;
import org.exoplatform.social.core.identity.provider.OrganizationIdentityProvider;
import org.exoplatform.social.core.storage.IdentityStorageException;
import org.exoplatform.social.core.storage.api.IdentityStorage;
import org.exoplatform.social.core.storage.impl.AbstractStorage;
import org.exoplatform.social.core.storage.impl.StorageUtils;
import org.exoplatform.social.core.storage.query.JCRProperties;
import org.exoplatform.social.core.storage.query.PropertyLiteralExpression;
import org.exoplatform.social.core.storage.query.QueryFunction;
import org.exoplatform.social.core.storage.query.WhereExpression;

import java.util.List;




public class BuildingInfoUsersSearch extends AbstractStorage {
    
    private static final Log LOG = ExoLogger.getLogger(BuildingInfoUsersSearch.class);
    public static final String ASTERISK_STR = "*";
    public static final String PERCENT_STR = "%";
    public static final char   ASTERISK_CHAR = '*';
    public static final String SPACE_STR = " ";
    public static final String EMPTY_STR = "";
    public static final String SLASH_STR = "/";
    private int size = 0;

    public List<Identity> search(String searchCondition,List<Identity> excludedIdentityList ,long offset, long limit, String orderBy, String order) throws IdentityStorageException {
        
        QueryBuilder<ProfileEntity> builder = getSession().createQueryBuilder(ProfileEntity.class);
        WhereExpression whereExpression = new WhereExpression();

        ProviderEntity providerEntity = getProviderRoot().getProviders().get(OrganizationIdentityProvider.NAME);
        if (providerEntity != null) {
            whereExpression
                    .like(JCRProperties.path, providerEntity.getPath() + StorageUtils.SLASH_STR + StorageUtils.PERCENT_STR)
                    .and()
                    .not().equals(ProfileEntity.deleted, "true");
        } else {
            whereExpression.not().equals(ProfileEntity.deleted, "true");
        }
        

        
        PropertyLiteralExpression<String> buildingCondition = new PropertyLiteralExpression<String>(String.class, "void-buildingName");
        PropertyLiteralExpression<String> floorCondition = new PropertyLiteralExpression<String>(String.class, "void-floorNumber");
        PropertyLiteralExpression<String> roomCondition = new PropertyLiteralExpression<String>(String.class, "void-roomNumber");

        if (excludedIdentityList != null & excludedIdentityList.size() > 0) {
            for (Identity identity : excludedIdentityList) {
                whereExpression.and().not().equals(ProfileEntity.parentId, identity.getId());
            }
        }
        
        
        if (searchCondition != null && orderBy.equals("void-buildingName")) {
            whereExpression.and().like(
                    whereExpression.callFunction(QueryFunction.LOWER, buildingCondition),PERCENT_STR + searchCondition.toLowerCase() + PERCENT_STR
            );
        }
        
        else if (searchCondition != null && orderBy.equals("void-floorNumber")) {
            whereExpression.and().like(
                    whereExpression.callFunction(QueryFunction.LOWER, floorCondition),PERCENT_STR + searchCondition.toLowerCase() + PERCENT_STR
            );
         
        }
        
        else if (searchCondition != null && orderBy.equals("void-roomNumber")) {
            whereExpression.and().like(
                    whereExpression.callFunction(QueryFunction.LOWER, roomCondition),PERCENT_STR + searchCondition.toLowerCase() + PERCENT_STR
            );
         
        }
        
        
        
        
        builder.where(whereExpression.toString());
        if (!orderBy.equals("")) {
            if(order.equals("asc"))
            builder.orderBy(orderBy, Ordering.ASC);
            if(order.equals("desc"))
                builder.orderBy(orderBy, Ordering.DESC);
        }
        QueryImpl<ProfileEntity> queryImpl = (QueryImpl<ProfileEntity>) builder.get();
        ((org.exoplatform.services.jcr.impl.core.query.QueryImpl) queryImpl.getNativeQuery()).setCaseInsensitiveOrder(true);

        QueryResult<ProfileEntity> results = queryImpl.objects();
        size=results.size();
        if (limit == 0) limit = results.size();
        IdentityResult identityResult = new IdentityResult(offset, limit, results.size());

        //
        while (results.hasNext()) {

            ProfileEntity profileEntity = results.next();

            Identity identity = createIdentityFromEntity(profileEntity.getIdentity());

            Profile profile = getStorage().loadProfile(new Profile(identity));
            identity.setProfile(profile);

            identityResult.add(identity);

            //
            if (identityResult.addMore() == false) {
                break;
            }

        }

        return identityResult.result();

    }

    public int getSize() {
        return size;
    }

    private Identity createIdentityFromEntity(final IdentityEntity identityEntity) {

        //
        return getStorage().findIdentityById(identityEntity.getId());

    }

    private IdentityStorage getStorage() {
        ExoContainer container = ExoContainerContext.getContainerByName("portal");
        return (IdentityStorage) container.getComponentInstanceOfType(IdentityStorage.class);
    }


}