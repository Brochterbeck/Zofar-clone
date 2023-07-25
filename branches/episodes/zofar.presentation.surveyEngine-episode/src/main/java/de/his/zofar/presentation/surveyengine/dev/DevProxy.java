/*START HEADER*/
/* Zofar Survey System
* Copyright (C) 2014 Deutsches Zentrum für Hochschul- und Wissenschaftsforschung
* 
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU General Public License for more details.
* 
* You should have received a copy of the GNU General Public License
* along with this program. If not, see <http://www.gnu.org/licenses/>.
*/
/*STOP HEADER*/
package de.his.zofar.presentation.surveyengine.dev;
import java.io.Serializable;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.his.zofar.presentation.surveyengine.util.JsfUtility;
/**
 * Bean to provide Zofar specific EL - Functions
 * 
 * @author meisner
 * 
 */
public class DevProxy implements Serializable {
	private static final long serialVersionUID = -6033696257041042442L;
	private static final Logger LOGGER = LoggerFactory
			.getLogger(DevProxy.class);
	private Map<String,String> registered;
	private Map<String,String> visibleRegistered;
	private Map<String,String> translatedDevRegistered;
	public DevProxy() {
		super();
	}
	@PostConstruct
	private void init() {
		LOGGER.info("init");
		registered = new LinkedHashMap<String,String>();
		visibleRegistered = new LinkedHashMap<String,String>();
		translatedDevRegistered = new LinkedHashMap<String,String>();
	}
	public Object analyze(final String id,final String expression,final String mode){
		return this.analyze(id, expression, mode,false);
	}
	public Object analyze(final String id,final String expression,final String mode,final boolean override){
		final Object result =  JsfUtility.getInstance().evaluateValueExpression(FacesContext.getCurrentInstance(), "#{"+expression+"}", Boolean.class);
		final String key = id+" ("+mode+")";
		final String visibleKey = "form:"+id;
		final String value = expression+" ==> "+result;
		registered.put(key, value);
		visibleRegistered.put(visibleKey.replaceAll(":", "\\\\\\\\:"), expression);
		if(override)return true;
		return result;
	}
	public Object translate(final String expression) {
		if(expression == null)return null;
		String translated = expression;
		translated = translated.replaceAll("true", "#{boolean.true}");
		translated = translated.replaceAll("false", "#{boolean.false}");
		translated = translated.replaceAll(" or ", " #{boolean.or} ");
		translated = translated.replaceAll("and", "#{boolean.and}");
		return "true";
	}
	public boolean isEmpty() {
		return registered.isEmpty();
	}
	public Map<String, String> getRegistered() {
		return registered;
	}
	public boolean isVisiblesEmpty() {
		return visibleRegistered.isEmpty();
	}
	public Map<String, String> getVisibleRegistered() {
		return visibleRegistered;
	}
	public Map<String, String> getTranslatedDevRegistered() {
		return translatedDevRegistered;
	}
	public List<Object> getRegisteredKeys(){
	     return Arrays.asList(registered.keySet().toArray());
	}
	public List<Object> getVisibleRegisteredKeys(){
	     return Arrays.asList(visibleRegistered.keySet().toArray());
	}
}
