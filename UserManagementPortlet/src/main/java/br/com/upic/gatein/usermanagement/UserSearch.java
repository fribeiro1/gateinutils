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

import java.util.Arrays;

import org.exoplatform.commons.serialization.api.annotations.Serialized;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.services.organization.Query;
import org.exoplatform.services.organization.User;
import org.exoplatform.services.organization.UserHandler;
import org.exoplatform.services.organization.UserProfile;
import org.exoplatform.services.organization.UserProfileHandler;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIComponent;
import org.exoplatform.webui.core.UIContainer;
import org.exoplatform.webui.core.UIGrid;
import org.exoplatform.webui.core.UIPageIterator;
import org.exoplatform.webui.core.UISearch;
import org.exoplatform.webui.core.lifecycle.UIContainerLifecycle;
import org.exoplatform.webui.core.model.SelectItemOption;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;
import org.exoplatform.webui.form.UIFormInputBase;
import org.exoplatform.webui.form.UIFormInputSet;

@ComponentConfig(lifecycle = UIContainerLifecycle.class, events = {
		@EventConfig(name = "Edit", listeners = UserSearch.EditUserActionListener.class),
		@EventConfig(name = "Delete", listeners = UserSearch.DeleteUserActionListener.class, confirm = "UserSearch.confirm.Delete") })
@Serialized
public final class UserSearch extends UISearch {

	public static final class DeleteUserActionListener extends
			EventListener<UserSearch> {

		public void execute(final Event<UserSearch> event) throws Exception {
			final UserSearch search = event.getSource();

			final OrganizationService orgService = search
					.getApplicationComponent(OrganizationService.class);

			final UserHandler userHandler = orgService.getUserHandler();

			userHandler.removeUser(event.getRequestContext()
					.getRequestParameter(OBJECTID), true);

			search.search(search.lastQuery_);

			final UIPageIterator pageIterator = search.<UIGrid> getChild(
					UIGrid.class).getUIPageIterator();

			int currentPage = pageIterator.getCurrentPage();

			while (currentPage > pageIterator.getAvailablePage())
				currentPage--;

			pageIterator.setCurrentPage(currentPage);
		}

	}

	public static final class EditUserActionListener extends
			EventListener<UserSearch> {

		public void execute(final Event<UserSearch> event) throws Exception {
			final UIComponent search = event.getSource();

			search.setRendered(false);

			final UserEditForm form = search.<UIContainer> getParent()
					.getChild(UserEditForm.class);

			final OrganizationService orgService = search
					.getApplicationComponent(OrganizationService.class);

			final UserHandler userHandler = orgService.getUserHandler();

			final User user = userHandler.findUserByName(event
					.getRequestContext().getRequestParameter(OBJECTID));

			form.setUser(user);

			final UserProfileHandler userProfileHandler = orgService
					.getUserProfileHandler();

			final UserProfile userProfile = userProfileHandler
					.findUserProfileByName(user.getUserName());

			form.setUserProfile(userProfile);

			form.init();

			form.setRendered(true);
		}

	}

	private Query lastQuery_ = new Query();

	@SuppressWarnings("unchecked")
	public UserSearch() throws Exception {
		super(Arrays.asList(
				new SelectItemOption<String>("userName", "userName"),
				new SelectItemOption<String>("firstName", "firstName"),
				new SelectItemOption<String>("lastName", "lastName"),
				new SelectItemOption<String>("email", "email")));

		addChild(UIGrid.class, null, null).configure("userName",
				new String[] { "userName", "firstName", "lastName", "email" },
				new String[] { "Edit", "Delete" });
	}

	@Override
	public void advancedSearch(UIFormInputSet advancedSearchInput) {
	}

	@Override
	public void processRender(final WebuiRequestContext context)
			throws Exception {
		final UIPageIterator pageIterator = getChild(UIGrid.class)
				.getUIPageIterator();

		final int currentPage = pageIterator.getCurrentPage();

		search(lastQuery_);

		pageIterator.setCurrentPage(currentPage);

		pageIterator.getCurrentPageData();

		super.processRender(context);
	}

	@Override
	public void quickSearch(final UIFormInputSet quickSearchInput)
			throws Exception {
		final Query query = new Query();

		final String key = quickSearchInput.<UIFormInputBase<String>> getChild(
				1).getValue();

		final String value = quickSearchInput
				.<UIFormInputBase<String>> getChild(0).getValue();

		if (key.equals("userName"))
			query.setUserName(value);
		else if (key.equals("firstName"))
			query.setFirstName(value);
		else if (key.equals("lastName"))
			query.setLastName(value);
		else if (key.equals("email"))
			query.setEmail(value);

		search(query);
	}

	public void search(final Query query) throws Exception {
		lastQuery_ = query;

		final UIPageIterator pageIterator = getChild(UIGrid.class)
				.getUIPageIterator();

		pageIterator.setPageList(new UserPageList(query, 10));
	}

}