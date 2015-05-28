/**
 * (c) 2003-2015 MuleSoft, Inc. The software in this package is published under the terms of the CPAL v1.0 license,
 * a copy of which has been included with this distribution in the LICENSE.md file.
 */

package org.mule.modules.monitor.strategy;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.mule.api.ConnectionException;
import org.mule.api.annotations.components.ConnectionManagement;
import org.mule.api.annotations.Configurable;
import org.mule.api.annotations.Connect;
import org.mule.api.annotations.ConnectionIdentifier;
import org.mule.api.annotations.Disconnect;
import org.mule.api.annotations.TestConnectivity;
import org.mule.api.annotations.ValidateConnection;
import org.mule.api.annotations.param.ConnectionKey;
import org.mule.api.annotations.param.Default;

/**
 * Configuration type Strategy
 *
 * @author MuleSoft, Inc.
 */
@ConnectionManagement(configElementName = "config", friendlyName = "Configuration Monitor")
public class ConnectorConnectionStrategy {

	DatagramSocket clientSocket = null ;
	InetAddress IPAddress = null;
    byte[] sendData = new byte[1024];
      
	public InetAddress getIPAddress() {
		return IPAddress;
	}

	public void setIPAddress(InetAddress iPAddress) {
		IPAddress = iPAddress;
	}

	public DatagramSocket getClientSocket() {
		return clientSocket;
	}

	public void setClientSocket(DatagramSocket clientSocket) {
		this.clientSocket = clientSocket;
	}

	@Configurable
	private int port;
	
    @Configurable
    @Default("localhost")
    private String host;

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

    @Connect
    @TestConnectivity
    public void connect(@ConnectionKey Integer connectionNumber) throws ConnectionException  {
        try {
             String serverHostname = getHost();
            IPAddress = InetAddress.getByName(serverHostname);

            clientSocket = new DatagramSocket();

            System.out.println ("Attemping to connect to " + getPort() +
                    ") via UDP port 9090");
            }
        catch (UnknownHostException ex) {
            System.err.println(ex);
        }
        catch (IOException ex) {
            System.err.println(ex);
        }
    }
    
    @Disconnect
    public void disConnect(){
    	clientSocket.close();
    }
    
    @ValidateConnection
    public boolean isConnected() {
    	if (clientSocket != null){
	        if ( clientSocket.isConnected() ) {
	        	return true;
	        } else {
	        	return false;
	        }
    	}
    	return false;
    }
  
    @ConnectionIdentifier
    public String connectionId() {
        return "1";
    }
    

}