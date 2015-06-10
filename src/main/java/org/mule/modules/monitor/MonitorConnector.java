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
import java.util.Map;

import jl.json.JsonArray;
import jl.json.JsonObject;

import org.apache.log4j.Logger;
import org.mule.api.annotations.ConnectionStrategy;
import org.mule.api.annotations.Connector;
import org.mule.api.annotations.Processor;
import org.mule.api.annotations.Source;
import org.mule.api.callback.SourceCallback;
import org.mule.modules.monitor.strategy.ConnectorConnectionStrategy;


/**
 * Anypoint Connector
 *
 * @author MuleSoft, Inc.
 */
@Connector(name="monitor", friendlyName="Monitor")
public class MonitorConnector {
    
	final static Logger logger = Logger.getLogger(MonitorConnector.class);
	DatagramSocket cs;
	byte[] sendData = new byte[1024];
	
    @ConnectionStrategy
    ConnectorConnectionStrategy connectionStrategy;

    @Source
    public void inbound(final SourceCallback callback) {
    	
    	try {
			callback.process();
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
    }

    @SuppressWarnings("rawtypes")
	@Processor
    public void outbound(String id, String title, String type, String status, Map data) {
    	cs = getConnectionStrategy().getClientSocket();
    	InetAddress ip = getConnectionStrategy().getIPAddress();
    	int p = getConnectionStrategy().getPort();
    	
    	try {
    	   
    	   
    	   JsonObject object = new JsonObject();
    	   object.putString("Id", id);
    	   object.putString("Name", title);
    	   object.putString("Type", type);
    	   object.putString("Status", status);
    	   
    	   JsonArray array = new JsonArray();
    	   for (Object o : data.keySet()) {
    	      JsonObject property = new JsonObject();
    	      property.putString("name", o.toString());
    	      String value = data.get(o).toString();
    	      String valueType = "" + value.charAt(0);
    	      value = value.substring(2);
    	      property.putString("type", valueType);
    	      property.putString("value", value);
    	      array.addJsonObject(property);
    	   }
    	   
    	   object.putJsonArray("data", array);
    	   
    	   
            sendData = object.toString().getBytes();

            // System.out.println ("Sending data to " + sendData.length + " bytes to server.");
            // System.out.println ("ID " + id);

            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, ip , p);

            cs.send(sendPacket);
            }
        catch (UnknownHostException ex) {
            logger.error(ex.getMessage());
        }
        catch (IOException ex) {
        	logger.error(ex.getMessage());
        }
        
    }   
    
	public ConnectorConnectionStrategy getConnectionStrategy() {
        return connectionStrategy;
    }

    public void setConnectionStrategy(ConnectorConnectionStrategy connectionStrategy) {
        this.connectionStrategy = connectionStrategy;
    }

}