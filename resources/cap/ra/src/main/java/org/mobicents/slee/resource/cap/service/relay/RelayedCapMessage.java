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
package org.mobicents.slee.resource.cap.service.relay;

import org.mobicents.protocols.ss7.cap.api.CAPApplicationContext;
import org.mobicents.protocols.ss7.cap.api.CAPException;
import org.mobicents.protocols.ss7.cap.api.CAPMessage;
import org.mobicents.protocols.ss7.indicator.RoutingIndicator;
import org.mobicents.protocols.ss7.sccp.impl.parameter.SccpAddressImpl;
import org.mobicents.protocols.ss7.sccp.parameter.SccpAddress;

/**
 * @author <a href="mailto:info@pro-ids.com">ProIDS sp. z o.o.</a>
 *
 */
public class RelayedCapMessage {

    private int newServiceKey;
    private CAPMessage capMessage;
    private CAPApplicationContext capApplicationContext;
    private SccpAddress remoteAddress;
    private SccpAddress localAddress;
    private long remoteDialogId;

    public RelayedCapMessage(int newServiceKey, SccpAddress origAddress, SccpAddress destAddress, CAPMessage capMessage) throws CAPException {
        this.newServiceKey = newServiceKey;
        this.capMessage = capMessage;
        this.capApplicationContext = capMessage.getCAPDialog().getApplicationContext();
        if (destAddress!=null) {
            this.remoteAddress = destAddress;
        }
        else {
            this.remoteAddress = capMessage.getCAPDialog().getRemoteAddress();
        }
        if (origAddress!=null) {
            this.localAddress = origAddress;
        }
        else {
            this.localAddress = new SccpAddressImpl(RoutingIndicator.ROUTING_BASED_ON_GLOBAL_TITLE,
                    capMessage.getCAPDialog().getLocalAddress().getGlobalTitle(),
                    0,
                    capMessage.getCAPDialog().getLocalAddress().getSubsystemNumber()
            );
        }
        this.remoteDialogId = capMessage.getCAPDialog().getRemoteDialogId();
        this.sanityCheck();
    }

    public int getNewServiceKey() {
        return newServiceKey;
    }

    public CAPMessage getCapMessage() {
        return capMessage;
    }

    public SccpAddress getRemoteAddress() {
        return remoteAddress;
    }

    public SccpAddress getLocalAddress() {
        return localAddress;
    }

    public long getRemoteDialogId() {
        return remoteDialogId;
    }

    public CAPApplicationContext getCapApplicationContext() {
        return capApplicationContext;
    }

    @Override
    public String toString() {
        return "RelayedCapMessage{" +
                "newServiceKey=" + newServiceKey +
                ", sccp.remoteAddress.GT=" + remoteAddress.getGlobalTitle() +
                ", sccp.localAddress.GT=" + localAddress.getGlobalTitle() +
                ", tcap.remoteDialogId=" + remoteDialogId +
                ", cap.invokeId=" + capMessage.getInvokeId() +
                '}';
    }

    private void sanityCheck() throws CAPException{
        // TODO
    }
}
