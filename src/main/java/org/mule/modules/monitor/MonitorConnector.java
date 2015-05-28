/**
 * (c) 2003-2015 MuleSoft, Inc. The software in this package is published under the terms of the CPAL v1.0 license,
 * a copy of which has been included with this distribution in the LICENSE.md file.
 */

package org.mule.modules.monitor;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.mule.api.annotations.ConnectionStrategy;
import org.mule.api.annotations.Connector;
import org.mule.api.annotations.Configurable;
import org.mule.api.annotations.Processor;
import org.mule.api.annotations.Source;
import org.mule.api.annotations.param.Default;
import org.mule.api.callback.SourceCallback;
import org.mule.modules.monitor.strategy.ConnectorConnectionStrategy;


/**
 * Anypoint Connector
 *
 * @author MuleSoft, Inc.
 */
@Connector(name="monitor", friendlyName="Monitor")
public class MonitorConnector {
    
	DatagramSocket cs;
	byte[] sendData = new byte[1024];
	
    @ConnectionStrategy
    ConnectorConnectionStrategy connectionStrategy;

    @Source
    public void inbound(final SourceCallback callback) {
    	
    	try {
			callback.process();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    @Processor
    public void outbound(String msg, String id) {
    	cs = getConnectionStrategy().getClientSocket();
    	InetAddress ip = getConnectionStrategy().getIPAddress();
    	int p = getConnectionStrategy().getPort();
    	
    	try {
            sendData = msg.getBytes();

            System.out.println ("Sending data to " + sendData.length +
                    " bytes to server.");
            System.out.println ("ID " + id);

            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, ip , p);

            cs.send(sendPacket);
            }
        catch (UnknownHostException ex) {
            System.err.println(ex);
        }
        catch (IOException ex) {
            System.err.println(ex);
        }
        
    }   
    
	public ConnectorConnectionStrategy getConnectionStrategy() {
        return connectionStrategy;
    }

    public void setConnectionStrategy(ConnectorConnectionStrategy connectionStrategy) {
        this.connectionStrategy = connectionStrategy;
    }

}