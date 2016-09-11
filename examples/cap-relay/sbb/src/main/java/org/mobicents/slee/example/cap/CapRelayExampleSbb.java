/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc. and individual contributors
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

import javax.slee.ActivityContextInterface;
import javax.slee.RolledBackContext;
import javax.slee.SbbContext;
import javax.slee.facilities.TimerFacility;
import javax.slee.facilities.TimerOptions;
import javax.slee.facilities.TimerPreserveMissed;
import javax.slee.facilities.Tracer;
import javax.slee.resource.ResourceAdaptorTypeID;
import javax.slee.serviceactivity.ServiceStartedEvent;

import org.apache.log4j.Logger;
import org.mobicents.protocols.ss7.cap.api.CAPParameterFactory;
import org.mobicents.protocols.ss7.cap.api.CAPProvider;
import org.mobicents.protocols.ss7.cap.api.errors.CAPErrorMessageFactory;
import org.mobicents.protocols.ss7.cap.api.service.circuitSwitchedCall.*;
import org.mobicents.slee.SbbContextExt;
import org.mobicents.slee.resource.cap.CAPContextInterfaceFactory;
import org.mobicents.slee.resource.cap.events.DialogRelease;

/**
 * @author <a href="mailto:info@pro-ids.com">ProIDS sp. z o.o.</a>
 */
public abstract class CapRelayExampleSbb implements javax.slee.Sbb {

	private static final ResourceAdaptorTypeID capRATypeID = new ResourceAdaptorTypeID("CAPResourceAdaptorType", "org.mobicents", "2.0");
	private static final String capRALink = "CAPRA";
	private CAPContextInterfaceFactory capActivityContextInterfaceFactory;
	private CAPProvider capProvider;
	private CAPParameterFactory capParameterFactory;
	private CAPErrorMessageFactory capErrorMessageFactory;
	private TimerFacility timerFacility;

	private static TimerOptions timerOptions;
	private static Tracer tracer;

	private SbbContextExt sbbContext; // This SBB's SbbContext

	private Logger logger = Logger.getLogger(CapRelayExampleSbb.class);

	private final static int SERVICE_KEY_NOT_SERVED = 1;
	private final static int SERVICE_KEY_RELAY = 5;

	// SbbObject lifecycle methods

	public void setSbbContext(SbbContext context) {
		sbbContext = (SbbContextExt) context;
		capActivityContextInterfaceFactory = (CAPContextInterfaceFactory) sbbContext.getActivityContextInterfaceFactory(capRATypeID);
		capProvider = (CAPProvider) sbbContext.getResourceAdaptorInterface(capRATypeID, capRALink);
		capParameterFactory = capProvider.getCAPParameterFactory();
		capErrorMessageFactory = capProvider.getCAPErrorMessageFactory();
		timerFacility = sbbContext.getTimerFacility();
	}

	public void unsetSbbContext() {
		this.sbbContext = null;
	}

	public void sbbCreate() throws javax.slee.CreateException {
	}

	public void sbbPostCreate() throws javax.slee.CreateException {
	}

	public void sbbActivate() {
	}

	public void sbbPassivate() {
	}

	public void sbbRemove() {
	}

	public void sbbLoad() {
	}

	public void sbbStore() {
	}

	public void sbbExceptionThrown(Exception exception, Object event,
			ActivityContextInterface activity) {
	}

	public void sbbRolledBack(RolledBackContext context) {
	}

	// some helper methods to deal with lazy init of static fields

	private TimerOptions getTimerOptions() {
		if (timerOptions == null) {
			timerOptions = new TimerOptions();
			timerOptions.setPreserveMissed(TimerPreserveMissed.ALL);
		}
		return timerOptions;
	}

	// event handlers, the service's logic

	/**
	 * Event handler method for the event signaling the service activation.
	 * 
	 * @param event
	 * @param aci
	 */
	public void onServiceStartedEvent(ServiceStartedEvent event,
			ActivityContextInterface aci) {
		logger.info("Service activated, now run traffic generator.");
	}

	/**
	 * Event handler method for the InitialDP CAP message.
	 * 
	 * @param event
	 * @param aci
	 */
	public void onInitialDP(InitialDPRequest event, ActivityContextInterface aci) {
		logger.info("IDP received (serviceKey="+event.getServiceKey()+")");
		logger.trace(event);
		CallLog.setMDC(event.getCAPDialog());
		CallLog.logCallStart();
		CallLog.logIncomming("IDP");
		CallLog.logIDP(event.getCAPDialog(), event.getServiceKey());

		try {
			if (event.getServiceKey()== SERVICE_KEY_NOT_SERVED) {
				logger.warn("IDP will be relayed to serviceKey=" + SERVICE_KEY_RELAY);
				capProvider.relayCapMessage(SERVICE_KEY_RELAY, event);
				CallLog.logOutgoing("IDP");
			}
			else if (event.getServiceKey()== SERVICE_KEY_RELAY) {
				logger.warn("IDP dropped (for secure of loop in sccp routing)");
				event.getCAPDialog().release();
			}
			else {
				logger.warn("IDP dropped (unsupported serviceKey received)");
				event.getCAPDialog().release();
			}

		} catch (Exception ex) {
			logger.error("failure while processing InitialDP", ex);
		}
		finally {
			CallLog.clearMDC();
		}
	}

	public void onDialogRelease(DialogRelease event, ActivityContextInterface aci) {
		try {
			CallLog.setMDC(event.getCAPDialog());
			logger.info("DialogRelease received");
			logger.trace(event);
			CallLog.logCallEnd();
		} catch (Exception e) {
			logger.trace("ignored failure on onDialogRelease: "+ e.getMessage());
		} finally {
			CallLog.clearMDC();
		}
	}
}
