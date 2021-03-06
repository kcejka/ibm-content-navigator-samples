/*
 * Licensed Materials - Property of IBM (c) Copyright IBM Corp. 2018 All Rights Reserved.
 * 
 * US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with
 * IBM Corp.
 * 
 * DISCLAIMER OF WARRANTIES :
 * 
 * Permission is granted to copy and modify this Sample code, and to distribute modified versions provided that both the
 * copyright notice, and this permission notice and warranty disclaimer appear in all copies and modified versions.
 * 
 * THIS SAMPLE CODE IS LICENSED TO YOU AS-IS. IBM AND ITS SUPPLIERS AND LICENSORS DISCLAIM ALL WARRANTIES, EITHER
 * EXPRESS OR IMPLIED, IN SUCH SAMPLE CODE, INCLUDING THE WARRANTY OF NON-INFRINGEMENT AND THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL IBM OR ITS LICENSORS OR SUPPLIERS BE LIABLE FOR
 * ANY DAMAGES ARISING OUT OF THE USE OF OR INABILITY TO USE THE SAMPLE CODE, DISTRIBUTION OF THE SAMPLE CODE, OR
 * COMBINATION OF THE SAMPLE CODE WITH ANY OTHER CODE. IN NO EVENT SHALL IBM OR ITS LICENSORS AND SUPPLIERS BE LIABLE
 * FOR ANY LOST REVENUE, LOST PROFITS OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE
 * DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, EVEN IF IBM OR ITS LICENSORS OR SUPPLIERS HAVE
 * BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 */

package com.ibm.ecm.extension.viewerToolbar.services;

import javax.servlet.http.HttpServletRequest;
import com.ibm.ecm.extension.PluginLogger;
import com.ibm.ecm.extension.PluginResponseFilter;
import com.ibm.ecm.extension.PluginServiceCallbacks;
import com.ibm.json.java.JSON;
import com.ibm.json.java.JSONObject;

public class ViewOneActionResponseFilter extends PluginResponseFilter {

	@Override
	public String[] getFilteredServices() {
		return new String[] { "/v1/viewoneAction" };
	}

	@Override
	public void filter(String serverType, PluginServiceCallbacks callbacks, HttpServletRequest request, JSONObject jsonResponse) throws Exception {
		PluginLogger logger = callbacks.getLogger();
		
		// load plugin configuration
		String configString = callbacks.loadConfiguration();
		
		if ( configString != null ) {
			try {
			    JSONObject jsonConfig = (JSONObject)JSON.parse(configString);	
				String topButtonTooltip = (String)jsonConfig.get("topButtonTooltip");
				String topButtonImageEnabled = "../../../../" + (String)jsonConfig.get("topButtonImageEnabled");
				String topButtonImageDisabled = "../../../../" + (String)jsonConfig.get("topButtonImageDisabled");
				
				String html = (String) jsonResponse.get("responseHTML");
				
				// currently the last button on the top toolbar is bar1afterButton4
				int lastIndex = 4;
				int index = html.indexOf("<param name=\"bar1afterButton" + lastIndex + "\"");
				
				if (index != -1) {
					// append a custom button. Refer to https://www.ibm.com/support/knowledgecenter/SSTPHR_5.0.3/com.ibm.viewone.configuring/dvopr113.htm
					String executionScript = "viewerToolbarPluginAction()";
					String evalScript = "viewerToolbarPluginActionEval()";
					String value = executionScript + ", " + topButtonTooltip + ", " + topButtonImageEnabled + ", " + topButtonImageDisabled + ", true, " + evalScript;
					String param = "<param name=\"bar1afterButton" + (lastIndex+1) + "\" value=\"" + value + "\"/>";
					html = html.substring(0, index) + param + "\r\n" + html.substring(index);		
					jsonResponse.put("responseHTML", html);			
				}
			    
			} catch (Exception exc) {
				logger.logError(this, "filter", exc);
			}
		}
		
	}

}
