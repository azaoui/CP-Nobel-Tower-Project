/**
 * Copyright (C) 2003-2007 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.nobeltower.social.webui.profile;

import java.util.List;

import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.identity.model.Profile;
import org.exoplatform.social.webui.Utils;
import org.nobeltower.social.webui.profile.UIBuildingInfoSection;
import org.nobeltower.social.webui.profile.UICustomProfileSection;
import org.nobeltower.social.webui.profile.UITitleBar;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIComponent;
import org.exoplatform.webui.core.lifecycle.UIFormLifecycle;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.Event.Phase;
import org.exoplatform.webui.form.UIFormInput;
import org.exoplatform.webui.form.UIFormStringInput;


/**
 * Component manages basic information. This is one part of profile management
 * beside contact and experience.<br>
 * Modified : dang.tung tungcnw@gmail.com Aug 11, 2009
 */

@ComponentConfig(
  lifecycle = UIFormLifecycle.class,
  template = "classpath:groovy/social/webui/profile/UIBuildingInfoSection.gtmpl",
  events = {
    @EventConfig(listeners = UIBuildingInfoSection.EditActionListener.class, phase = Phase.DECODE),
    @EventConfig(listeners = UIBuildingInfoSection.SaveActionListener.class, csrfCheck = true),
    @EventConfig(listeners = UIBuildingInfoSection.CancelActionListener.class, phase = Phase.DECODE)
  }
)
public class UIBuildingInfoSection extends UICustomProfileSection {
	
	public static final String BUILDING_CHILD = "building";
	public static final String Floor_CHILD = "floor";
	public static final String Room_CHILD = "room";
//  public String lastloadUser;

 
  public UIBuildingInfoSection() throws Exception {
    addChild(UITitleBar.class, null, null);

    

   // addUIFormInput(new UIFormStringInput(Profile.BUILDING_NAME, Profile.BUILDING_NAME, null));
    /*addUIFormInput(new UIFormStringInput(Floor_CHILD, null, Profile.FLOOR_NUMBER));*/
   // addUIFormInput(new UIFormStringInput(Profile.ROOM_NUMBER, Profile.ROOM_NUMBER, null));
   // addUIFormInput(new UIFormStringInput(Profile.FLOOR_NUMBER, Profile.FLOOR_NUMBER, null));
    
    addUIFormInput(new UIFormStringInput(BUILDING_CHILD, "no value entred", null));
    addUIFormInput(new UIFormStringInput(Floor_CHILD, "no value entred", null));
    addUIFormInput(new UIFormStringInput(Room_CHILD, "no value entred", null));
    

    /*addUIFormInput(new UIFormStringInput(Profile.FLOOR_NUMBER,
                                         Profile.FLOOR_NUMBER,
                                         null).
                   addValidator(MandatoryValidator.class).addValidator(PersonalNameValidator.class).
                   addValidator(StringLengthValidator.class, 1, 45));

    addUIFormInput(new UIFormStringInput(Profile.ROOM_NUMBER, Profile.ROOM_NUMBER, null).
                   addValidator(MandatoryValidator.class).
                   addValidator(EmailAddressValidator.class));*/
  }

  /**
   * Reloads basic info in each request call
   */
  public void reloadBasicInfo() {
	  if (isFirstLoad() == false) {
	      Identity ownerIdentity = Utils.getOwnerIdentity(false);
	      Profile profile = ownerIdentity.getProfile();
	      this.getUIStringInput(BUILDING_CHILD).setValue((String) profile.getProperty("buildingName"));
	      this.getUIStringInput(Floor_CHILD).setValue((String) profile.getProperty("floorNumber"));
	      this.getUIStringInput(Room_CHILD).setValue((String) profile.getProperty("roomNumber"));
	      
	      if (isEditMode())
	        setFirstLoad(true);
	    }
  }

  /**
   * Gets and sort all uicomponents.<br>
   *
   * @return All children in order.
   */
  public List<UIComponent> getChilds() {
    return getChildren();
  }

  /**
   * Changes form into edit mode when user click edit button.<br>
   */
  public static class EditActionListener extends UICustomProfileSection.EditActionListener {

    @Override
    public void execute(Event<UICustomProfileSection> event) throws Exception {
      super.execute(event);
      UICustomProfileSection sect = event.getSource();
      UIBuildingInfoSection uiForm = (UIBuildingInfoSection) sect;
      WebuiRequestContext requestContext = event.getRequestContext();
      requestContext.addUIComponentToUpdateByAjax(uiForm);
      requestContext.addUIComponentToUpdateByAjax(sect);
      sect.setFirstLoad(false);
    }
  }

  /**
   * Stores profile information into database when form is submitted.<br>
   */
  public static class SaveActionListener extends UICustomProfileSection.SaveActionListener {

    @Override
    public void execute(Event<UICustomProfileSection> event) throws Exception {
      super.execute(event);

      UIBuildingInfoSection uiForm = (UIBuildingInfoSection) event.getSource();
      

      /* Profile toBeUpdatedProfile = getProfile();

    toBeUpdatedProfile.setProperty(Profile.GENDER, getGenderChild().getValue());
    toBeUpdatedProfile.setProperty(Profile.CONTACT_PHONES, getProfileForSave(phoneCount, getPhoneChilds(), PHONE));
    toBeUpdatedProfile.setProperty(Profile.CONTACT_IMS, getProfileForSave(imCount, getImsChilds(), IM));
    toBeUpdatedProfile.setProperty(Profile.CONTACT_URLS, getProfileForSave(urlCount, getUrlChilds(), URL));
*/
      
      /*
       *   public final UIFormInput<String> getGenderChild() {
    return (UIFormInput<String>)getChildById(GENDER_CHILD);
       * */
      
      String buildingName = uiForm.getUIStringInput(BUILDING_CHILD).getValue();
    String floorNumber = uiForm.getUIStringInput(Floor_CHILD).getValue();
     String roomNumber = uiForm.getUIStringInput(Room_CHILD).getValue();
      
      
    /* String buildingName = uiForm.getUIStringInput(Profile.BUILDING_NAME).getValue();
     String floorNumber = uiForm.getUIStringInput(Profile.FLOOR_NUMBER).getValue();
       String roomNumber = uiForm.getUIStringInput(Profile.ROOM_NUMBER).getValue();
    */  
      Identity viewerIdentity = Utils.getViewerIdentity(true);
      Profile profile = viewerIdentity.getProfile();
      boolean profileHasUpdated = false;
      if (buildingName!= null) {
        profile.setProperty("buildingName", buildingName);
        profileHasUpdated = true;
      }
      if (floorNumber!= null) {
       // profile.setProperty(Profile.FLOOR_NUMBER, floorNumber);
    	  profile.setProperty("floorNumber", floorNumber);
        profileHasUpdated = true;
      }
      if (roomNumber!= null) {
    	  profile.setProperty("roomNumber", roomNumber);
        profileHasUpdated = true;
      }
      if (profileHasUpdated) {
        Utils.getIdentityManager().updateProfile(profile);
        //updates profile
        Utils.getOwnerIdentity(true);
      }
      
      uiForm.setFirstLoad(false);
      Utils.updateWorkingWorkSpace();
    }
  }
  
  
}