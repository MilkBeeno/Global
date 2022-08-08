/*
 * Copyright (C) 2016-2020 Tobias Brunner
 * HSR Hochschule fuer Technik Rapperswil
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation; either version 2 of the License, or (at your
 * option) any later version.  See <http://www.fsf.org/copyleft/gpl.txt>.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * for more details.
 */

package com.freetech.vpn.utils;

public final class Constants {
    /**
     * Limits for MTU
     */
    public static final int MTU_MAX = 1500;


    public static final String SHARE_CFG_NAME = "setting.cnf";
    public static final String SHARE_DISCONNECT_KEY = "dis_interval";

    public static final String DEFAULT_VPN_TYPE = "ikev2-eap";
}
