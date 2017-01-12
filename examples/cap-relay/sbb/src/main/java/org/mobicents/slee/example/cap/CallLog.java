/*
 * TeleStax, Open Source Cloud Communications  Copyright 2012.
 * and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.mobicents.slee.example.cap;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.mobicents.protocols.ss7.cap.api.CAPDialog;
import org.mobicents.protocols.ss7.cap.api.CAPMessage;
import org.mobicents.protocols.ss7.cap.api.service.circuitSwitchedCall.CAPDialogCircuitSwitchedCall;
import org.mobicents.protocols.ss7.cap.api.service.circuitSwitchedCall.InitialDPRequest;
import org.mobicents.protocols.ss7.sccp.parameter.SccpAddress;

/**
 * @author <a href="mailto:info@pro-ids.com">ProIDS sp. z o.o.</a>
 */
public class CallLog {

    private static Logger callLogger = Logger.getLogger("CALL_LOG");


    public static void logCallStart() {
        callLogger.info("--- CALL START --- ");
    }

    public static void logCallEnd() {
        callLogger.info("--- CALL END --- ");
    }

    public static void logOutgoing(String message) {
        callLogger.info("<-- " + message);
    }

    public static void logIncomming(String message) {
        callLogger.info("--> " + message);
    }

    public static void logIDP(CAPDialogCircuitSwitchedCall capDialog, int serviceKey) {
        callLogger.info("       cap.serviceKey=" + serviceKey);
        callLogger.info("       sccp.localAddress=" + capDialog.getLocalAddress());
        callLogger.info("       sccp.remoteAddress=" + capDialog.getRemoteAddress());
    }

    public static void logInternal(String message) {
        callLogger.info("  o " + message);
    }

    public static void setMDC(CAPDialog capDialog) {
        MDC.put("CallID", capDialog.getLocalDialogId());
    }

    public static void clearMDC() {
        MDC.remove("CallID");
    }
}
