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

import org.exoplatform.commons.serialization.api.annotations.Serialized;
import org.exoplatform.services.organization.User;
import org.exoplatform.webui.core.UIComponent;
import org.exoplatform.webui.form.UIFormInput;
import org.exoplatform.webui.form.UIFormInputSet;
import org.exoplatform.webui.form.UIFormStringInput;
import org.exoplatform.webui.form.input.UICheckBoxInput;
import org.exoplatform.webui.form.validator.EmailAddressValidator;
import org.exoplatform.webui.form.validator.MandatoryValidator;
import org.exoplatform.webui.form.validator.PasswordStringLengthValidator;
import org.exoplatform.webui.form.validator.StringLengthValidator;

@Serialized
public final class AccountInputSet extends UIFormInputSet {
	private User user;

	public AccountInputSet(final String id) throws Exception {
		super(id);

		final UICheckBoxInput changePasswordInput = new UICheckBoxInput(
				"changePassword", null, false);

		changePasswordInput.setOnChange("CheckChangePassword");

		addChild(changePasswordInput);

		final UIComponent passwordInput = new UIFormStringInput("password",
				null, null).setType(UIFormStringInput.PASSWORD_TYPE)
				.addValidator(MandatoryValidator.class)
				.addValidator(PasswordStringLengthValidator.class, 6, 30);

		passwordInput.setRendered(false);

		addChild(passwordInput);

		final UIComponent confirmPasswordInput = new UIFormStringInput(
				"confirmPassword", null, null)
				.setType(UIFormStringInput.PASSWORD_TYPE)
				.addValidator(MandatoryValidator.class)
				.addValidator(PasswordStringLengthValidator.class, 6, 30);

		confirmPasswordInput.setRendered(false);

		addChild(confirmPasswordInput);

		addChild(new UIFormStringInput("firstName", "firstName", null)
				.addValidator(MandatoryValidator.class).addValidator(
						StringLengthValidator.class, 1, 45));
		addChild(new UIFormStringInput("lastName", "lastName", null)
				.addValidator(StringLengthValidator.class, 1, 45));
		addChild(new UIFormStringInput("email", "email", null).addValidator(
				EmailAddressValidator.class).addValidator(
				MandatoryValidator.class));
	}

	public void init() throws Exception {
		final UIFormInput<String> firstNameInput = getUIStringInput("firstName");

		firstNameInput.setValue(user.getFirstName());

		final UIFormInput<String> lastNameInput = getUIStringInput("lastName");

		lastNameInput.setValue(user.getLastName());

		final UIFormInput<String> emailInput = getUIStringInput("email");

		emailInput.setValue(user.getEmail());
	}

	@Override
	public void reset() {
		super.reset();

		final UICheckBoxInput changePasswordInput = getUICheckBoxInput("changePassword");

		changePasswordInput.setChecked(false);

		final UIComponent passwordInput = getUIStringInput("password");

		passwordInput.setRendered(false);

		final UIComponent confirmPasswordInput = getUIStringInput("confirmPassword");

		confirmPasswordInput.setRendered(false);
	}

	public void setUser(final User user) {
		this.user = user;
	}

}