/*
 * Copyright 2012 Upic
 * 
 * This file is part of GateIn Utils.
 *
 * GateIn Utils is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * GateIn Utils is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with GateIn Utils. If not, see <http://www.gnu.org/licenses/>.
 */
package br.com.upic.gatein.usermanagement;

import java.util.Map;

import org.exoplatform.commons.serialization.api.annotations.Serialized;
import org.exoplatform.services.organization.UserProfile;
import org.exoplatform.webui.form.UIFormInput;
import org.exoplatform.webui.form.UIFormInputSet;
import org.exoplatform.webui.form.UIFormStringInput;

@Serialized
public final class UserProfileInputSet extends UIFormInputSet {
	private UserProfile userProfile;

	public UserProfileInputSet(final String id) throws Exception {
		super(id);

		addChild(new UIFormStringInput("sampleAttribute", "sampleAttribute",
				null));
	}

	public void init() throws Exception {
		final UIFormInput<String> sampleAttributeInput = getUIStringInput("sampleAttribute");

		final Map<String, String> userInfoMap = userProfile.getUserInfoMap();

		sampleAttributeInput.setValue(userInfoMap.get("sampleAttribute"));
	}

	public void setUserProfile(final UserProfile userProfile) {
		this.userProfile = userProfile;
	}

}