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
package br.com.upic.gatein.userregister;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.commons.serialization.api.annotations.Serialized;
import org.exoplatform.webui.form.UIFormInputWithActions;
import org.exoplatform.webui.form.UIFormStringInput;
import org.exoplatform.webui.form.validator.EmailAddressValidator;
import org.exoplatform.webui.form.validator.ExpressionValidator;
import org.exoplatform.webui.form.validator.MandatoryValidator;
import org.exoplatform.webui.form.validator.PasswordStringLengthValidator;
import org.exoplatform.webui.form.validator.ResourceValidator;
import org.exoplatform.webui.form.validator.StringLengthValidator;

@Serialized
public final class AccountInputSet extends UIFormInputWithActions {

	public AccountInputSet(final String id) throws Exception {
		super(id);

		addChild(new UIFormStringInput("userName", "userName", null)
				.addValidator(ExpressionValidator.class,
						"^[\\p{L}][\\p{L}._\\-\\d]+$")
				.addValidator(ResourceValidator.class)
				.addValidator(MandatoryValidator.class)
				.addValidator(StringLengthValidator.class, 3, 30));
		addChild(new UIFormStringInput("password", null, null)
				.setType(UIFormStringInput.PASSWORD_TYPE)
				.addValidator(MandatoryValidator.class)
				.addValidator(PasswordStringLengthValidator.class, 6, 30));
		addChild(new UIFormStringInput("confirmPassword", null, null)
				.setType(UIFormStringInput.PASSWORD_TYPE)
				.addValidator(MandatoryValidator.class)
				.addValidator(PasswordStringLengthValidator.class, 6, 30));
		addChild(new UIFormStringInput("firstName", "firstName", null)
				.addValidator(MandatoryValidator.class).addValidator(
						StringLengthValidator.class, 1, 45));
		addChild(new UIFormStringInput("lastName", "lastName", null)
				.addValidator(StringLengthValidator.class, 1, 45));
		addChild(new UIFormStringInput("email", "email", null).addValidator(
				EmailAddressValidator.class).addValidator(
				MandatoryValidator.class));

		final List<ActionData> actions = new ArrayList<ActionData>();

		final ActionData checkAvailability = new ActionData();

		checkAvailability.setActionListener("CheckAvailability");

		checkAvailability.setActionName("CheckAvailability");

		checkAvailability.setActionType(ActionData.TYPE_ICON);

		checkAvailability.setCssIconClass("SearchIcon");

		actions.add(checkAvailability);

		setActionField("userName", actions);
	}

}