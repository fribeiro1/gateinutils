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
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.services.organization.User;
import org.exoplatform.services.organization.UserHandler;
import org.exoplatform.services.organization.UserProfile;
import org.exoplatform.services.organization.UserProfileHandler;
import org.exoplatform.web.application.ApplicationMessage;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIApplication;
import org.exoplatform.webui.core.UIComponent;
import org.exoplatform.webui.core.UIContainer;
import org.exoplatform.webui.core.lifecycle.UIFormLifecycle;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.Event.Phase;
import org.exoplatform.webui.event.EventListener;
import org.exoplatform.webui.form.UIFormInputSet;
import org.exoplatform.webui.form.UIFormTabPane;
import org.exoplatform.webui.form.input.UICheckBoxInput;

@ComponentConfig(lifecycle = UIFormLifecycle.class, template = "system:/groovy/webui/form/UIFormTabPane.gtmpl", events = {
		@EventConfig(listeners = UserEditForm.SaveActionListener.class),
		@EventConfig(listeners = UserEditForm.BackActionListener.class, phase = Phase.DECODE),
		@EventConfig(name = "CheckChangePassword", listeners = UserEditForm.CheckChangePasswordActionListener.class, phase = Phase.DECODE) })
@Serialized
public final class UserEditForm extends UIFormTabPane {

	public static final class BackActionListener extends
			EventListener<UserEditForm> {

		@Override
		public void execute(final Event<UserEditForm> event) throws Exception {
			final UIContainer form = event.getSource();

			form.setRendered(false);

			final UserSearch search = form.<UIContainer> getParent().getChild(
					UserSearch.class);

			search.setRendered(true);

			final UIFormInputSet accountInputSet = form
					.getChild(AccountInputSet.class);

			accountInputSet.reset();

			final UIFormInputSet userProfileInputSet = form
					.getChild(UserProfileInputSet.class);

			userProfileInputSet.reset();
		}

	}

	public static final class CheckChangePasswordActionListener extends
			EventListener<UserEditForm> {

		public void execute(final Event<UserEditForm> event) throws Exception {
			final UIContainer form = event.getSource();

			final UIFormInputSet accountInputSet = form
					.getChild(AccountInputSet.class);

			final UIComponent passwordInput = accountInputSet
					.getUIStringInput("password");

			final UICheckBoxInput changePasswordInput = accountInputSet
					.getUICheckBoxInput("changePassword");

			final boolean changePassword = changePasswordInput.isChecked();

			passwordInput.setRendered(changePassword);

			final UIComponent confirmPasswordInput = accountInputSet
					.getUIStringInput("confirmPassword");

			confirmPasswordInput.setRendered(changePassword);
		}

	}

	public static final class SaveActionListener extends
			EventListener<UserEditForm> {

		@Override
		public void execute(final Event<UserEditForm> event) throws Exception {
			final UserEditForm form = event.getSource();

			final UIFormInputSet accountInputSet = form
					.getChild(AccountInputSet.class);

			final UIApplication app = event.getRequestContext()
					.getUIApplication();

			final UICheckBoxInput changePasswordInput = accountInputSet
					.getUICheckBoxInput("changePassword");

			if (changePasswordInput.isChecked()) {
				final String password = accountInputSet.getUIStringInput(
						"password").getValue();

				if (!password.equals(form.getUIStringInput("confirmPassword")
						.getValue())) {
					app.addMessage(new ApplicationMessage(
							"UserEditForm.msg.password-is-not-match", null,
							ApplicationMessage.ERROR));

					return;
				}

				form.user.setPassword(password);
			}

			form.user.setFirstName(accountInputSet
					.getUIStringInput("firstName").getValue());

			form.user.setLastName(accountInputSet.getUIStringInput("lastName")
					.getValue());

			form.user.setEmail(accountInputSet.getUIStringInput("email")
					.getValue());

			final OrganizationService orgService = form
					.getApplicationComponent(OrganizationService.class);

			final UserHandler userHandler = orgService.getUserHandler();

			userHandler.saveUser(form.user, true);

			final UserProfileHandler userProfileHandler = orgService
					.getUserProfileHandler();

			final UIFormInputSet userProfileInputSet = form
					.getChild(UserProfileInputSet.class);

			form.userProfile.setAttribute("sampleAttribute",
					userProfileInputSet.getUIStringInput("sampleAttribute")
							.getValue());

			userProfileHandler.saveUserProfile(form.userProfile, true);

			accountInputSet.reset();

			userProfileInputSet.reset();

			app.addMessage(new ApplicationMessage(
					"UserEditForm.editWithSuccess.message", null));
		}

	}

	private User user;

	private UserProfile userProfile;

	public UserEditForm() throws Exception {
		super("UserEditForm");

		final UIComponent accountInputSet = new AccountInputSet(
				"AccountInputSet");

		addChild(accountInputSet);

		setSelectedTab(accountInputSet.getId());

		addChild(new UserProfileInputSet("UserProfileInputSet"));

		setActions(new String[] { "Save", "Back" });
	}

	public void init() throws Exception {
		final AccountInputSet accountInputSet = getChild(AccountInputSet.class);

		accountInputSet.setUser(user);

		accountInputSet.init();

		final UserProfileInputSet userProfileInputSet = getChild(UserProfileInputSet.class);

		userProfileInputSet.setUserProfile(userProfile);

		userProfileInputSet.init();
	}

	public void setUser(final User user) {
		this.user = user;
	}

	public void setUserProfile(final UserProfile userProfile) {
		this.userProfile = userProfile;
	}

}