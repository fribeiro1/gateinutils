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

import org.exoplatform.commons.utils.ListAccess;
import org.exoplatform.commons.utils.PageListAccess;
import org.exoplatform.commons.utils.Safe;
import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.services.organization.Query;
import org.exoplatform.services.organization.User;
import org.exoplatform.services.organization.UserHandler;

public final class UserPageList extends PageListAccess<User, Query> {
	private static final long serialVersionUID = -2486570712351082042L;

	public UserPageList(final Query state, final int pageSize) {
		super(state, pageSize);
	}

	@SuppressWarnings("deprecation")
	protected ListAccess<User> create(final Query state) throws Exception {
		final ExoContainer container = PortalContainer.getInstance();

		final OrganizationService orgService = (OrganizationService) container
				.getComponentInstance(OrganizationService.class);

		final UserHandler userHandler = orgService.getUserHandler();

		return Safe.unwrap(userHandler.findUsers(state));
	}

}