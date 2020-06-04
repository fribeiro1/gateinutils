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

public class UserPageList extends PageListAccess<User, Query> {
	private static long serialVersionUID = -2486570712351082042L;

	public UserPageList(Query state, int pageSize) {
		super(state, pageSize);
	}

	@SuppressWarnings("deprecation")
	protected ListAccess<User> create(Query state) throws Exception {
		ExoContainer container = PortalContainer.getInstance();

		OrganizationService orgService = (OrganizationService) container
				.getComponentInstance(OrganizationService.class);

		UserHandler userHandler = orgService.getUserHandler();

		return Safe.unwrap(userHandler.findUsers(state));
	}

}