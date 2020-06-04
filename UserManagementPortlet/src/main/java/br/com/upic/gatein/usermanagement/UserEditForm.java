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
public class UserEditForm extends UIFormTabPane {

	public static class BackActionListener extends
			EventListener<UserEditForm> {

		@Override
		public void execute(Event<UserEditForm> event) throws Exception {
			UIContainer form = event.getSource();

			form.setRendered(false);

			UserSearch search = form.<UIContainer> getParent().getChild(
					UserSearch.class);

			search.setRendered(true);

			UIFormInputSet accountInputSet = form
					.getChild(AccountInputSet.class);

			accountInputSet.reset();

			UIFormInputSet userProfileInputSet = form
					.getChild(UserProfileInputSet.class);

			userProfileInputSet.reset();
		}

	}

	public static class CheckChangePasswordActionListener extends
			EventListener<UserEditForm> {

		public void execute(Event<UserEditForm> event) throws Exception {
			UIContainer form = event.getSource();

			UIFormInputSet accountInputSet = form
					.getChild(AccountInputSet.class);

			UIComponent passwordInput = accountInputSet
					.getUIStringInput("password");

			UICheckBoxInput changePasswordInput = accountInputSet
					.getUICheckBoxInput("changePassword");

			boolean changePassword = changePasswordInput.isChecked();

			passwordInput.setRendered(changePassword);

			UIComponent confirmPasswordInput = accountInputSet
					.getUIStringInput("confirmPassword");

			confirmPasswordInput.setRendered(changePassword);
		}

	}

	public static class SaveActionListener extends
			EventListener<UserEditForm> {

		@Override
		public void execute(Event<UserEditForm> event) throws Exception {
			UserEditForm form = event.getSource();

			UIFormInputSet accountInputSet = form
					.getChild(AccountInputSet.class);

			UIApplication app = event.getRequestContext()
					.getUIApplication();

			UICheckBoxInput changePasswordInput = accountInputSet
					.getUICheckBoxInput("changePassword");

			if (changePasswordInput.isChecked()) {
				String password = accountInputSet.getUIStringInput(
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

			OrganizationService orgService = form
					.getApplicationComponent(OrganizationService.class);

			UserHandler userHandler = orgService.getUserHandler();

			userHandler.saveUser(form.user, true);

			UserProfileHandler userProfileHandler = orgService
					.getUserProfileHandler();

			UIFormInputSet userProfileInputSet = form
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

		UIComponent accountInputSet = new AccountInputSet(
				"AccountInputSet");

		addChild(accountInputSet);

		setSelectedTab(accountInputSet.getId());

		addChild(new UserProfileInputSet("UserProfileInputSet"));

		setActions(new String[] { "Save", "Back" });
	}

	public void init() throws Exception {
		AccountInputSet accountInputSet = getChild(AccountInputSet.class);

		accountInputSet.setUser(user);

		accountInputSet.init();

		UserProfileInputSet userProfileInputSet = getChild(UserProfileInputSet.class);

		userProfileInputSet.setUserProfile(userProfile);

		userProfileInputSet.init();
	}

	public void setUser(User user) {
		this.user = user;
	}

	public void setUserProfile(UserProfile userProfile) {
		this.userProfile = userProfile;
	}

}