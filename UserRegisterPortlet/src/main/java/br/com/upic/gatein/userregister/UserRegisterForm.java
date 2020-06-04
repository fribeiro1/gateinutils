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
import org.exoplatform.webui.exception.MessageException;
import org.exoplatform.webui.form.UIFormInput;
import org.exoplatform.webui.form.UIFormInputSet;
import org.exoplatform.webui.form.UIFormTabPane;
import org.exoplatform.webui.form.validator.MandatoryValidator;
import org.exoplatform.webui.form.validator.Validator;

@ComponentConfig(lifecycle = UIFormLifecycle.class, template = "system:/groovy/webui/form/UIFormTabPane.gtmpl", events = {
		@EventConfig(listeners = UserRegisterForm.RegisterActionListener.class),
		@EventConfig(listeners = UserRegisterForm.ResetActionListener.class, phase = Phase.DECODE),
		@EventConfig(name = "CheckAvailability", listeners = UserRegisterForm.CheckAvailabilityActionListener.class, phase = Phase.DECODE) })
public class UserRegisterForm extends UIFormTabPane {

	public static class CheckAvailabilityActionListener extends
			EventListener<UserRegisterForm> {

		@Override
		public void execute(Event<UserRegisterForm> event)
				throws Exception {
			Validator validator = new MandatoryValidator();

			UIContainer form = event.getSource();

			UIFormInput<String> userNameInput = form.getChild(
					AccountInputSet.class).getUIStringInput("userName");

			UIApplication app = event.getRequestContext()
					.getUIApplication();

			try {
				validator.validate(userNameInput);
			} catch (MessageException e) {
				app.addMessage(e.getDetailMessage());

				return;
			}

			OrganizationService orgService = form
					.getApplicationComponent(OrganizationService.class);

			UserHandler userHandler = orgService.getUserHandler();

			if (userHandler.findUserByName(userNameInput.getValue()) != null)
				app.addMessage(new ApplicationMessage(
						"UserRegisterForm.msg.user-exist", null));
			else
				app.addMessage(new ApplicationMessage(
						"UserRegisterForm.msg.user-not-exist", null));

		}

	}

	public static class RegisterActionListener extends
			EventListener<UserRegisterForm> {

		@Override
		public void execute(Event<UserRegisterForm> event)
				throws Exception {
			UIContainer form = event.getSource();

			UIFormInputSet accountInputSet = form
					.getChild(AccountInputSet.class);

			String password = accountInputSet
					.getUIStringInput("password").getValue();

			UIApplication app = event.getRequestContext()
					.getUIApplication();

			if (!password.equals(accountInputSet.getUIStringInput(
					"confirmPassword").getValue())) {
				app.addMessage(new ApplicationMessage(
						"UserRegisterForm.msg.password-is-not-match", null,
						ApplicationMessage.ERROR));

				return;
			}

			OrganizationService orgService = form
					.getApplicationComponent(OrganizationService.class);

			UserHandler userHandler = orgService.getUserHandler();

			String userName = accountInputSet
					.getUIStringInput("userName").getValue();

			if (userHandler.findUserByName(userName) != null) {
				app.addMessage(new ApplicationMessage(
						"UserRegisterForm.msg.user-exist", null,
						ApplicationMessage.ERROR));

				return;
			}

			User user = userHandler.createUserInstance(userName);

			user.setPassword(password);

			user.setFirstName(accountInputSet.getUIStringInput("firstName")
					.getValue());

			user.setLastName(accountInputSet.getUIStringInput("lastName")
					.getValue());

			user.setEmail(accountInputSet.getUIStringInput("email").getValue());

			userHandler.createUser(user, true);

			UserProfileHandler userProfileHandler = orgService
					.getUserProfileHandler();

			UserProfile userProfile = userProfileHandler
					.createUserProfileInstance(user.getUserName());

			UIFormInputSet userProfileInputSet = form
					.getChild(UserProfileInputSet.class);

			userProfile.setAttribute("sampleAttribute", userProfileInputSet
					.getUIStringInput("sampleAttribute").getValue());

			userProfileHandler.saveUserProfile(userProfile, true);

			accountInputSet.reset();

			userProfileInputSet.reset();

			app.addMessage(new ApplicationMessage(
					"UserRegisterForm.registerWithSuccess.message", null));
		}

	}

	public static class ResetActionListener extends
			EventListener<UserRegisterForm> {

		@Override
		public void execute(Event<UserRegisterForm> event)
				throws Exception {
			UIContainer form = event.getSource();

			UIFormInputSet accountInputSet = form
					.getChild(AccountInputSet.class);

			accountInputSet.reset();

			UIFormInputSet userProfileInputSet = form
					.getChild(UserProfileInputSet.class);

			userProfileInputSet.reset();
		}

	}

	public UserRegisterForm() throws Exception {
		super("UserRegisterForm");

		UIComponent accountInputSet = new AccountInputSet(
				"AccountInputSet");

		addChild(accountInputSet);

		setSelectedTab(accountInputSet.getId());

		addChild(new UserProfileInputSet("UserProfileInputSet"));

		setActions(new String[] { "Register", "Reset" });
	}

}