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
package de.his.zofar.presentation.surveyengine.ui.renderer.answers.multiplechoice.options;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.Renderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.his.zofar.presentation.surveyengine.ui.util.JsfUtility;
public abstract class ZofarCheckboxOptionRenderer extends Renderer{
	private static final Logger LOGGER = LoggerFactory
			.getLogger(ZofarCheckboxOptionRenderer.class);
	protected ZofarCheckboxOptionRenderer() {
		super();
	}
	@Override
	public boolean getRendersChildren() {
		return false;
	}
	@Override
	public synchronized void decode(FacesContext context, UIComponent component) {
		if (!component.isRendered()) {
			return;
		}
		String clientId = null;
        if (clientId == null) {
            clientId = component.getClientId(context);
        }
        assert(clientId != null);
        Map<String, String> requestParameterMap = context.getExternalContext()
              .getRequestParameterMap();
        boolean isChecked = isChecked(requestParameterMap.get(clientId));
        setSubmittedValue(component, isChecked);
	}
    public synchronized void setSubmittedValue(UIComponent component, Object value) {
        if (component instanceof UIInput) {
            ((UIInput) component).setValue(value);
            if (LOGGER.isDebugEnabled()) {
            	LOGGER.debug("set checkbox value: {}", value);
            }
        }
    }
    /**
     * @param value the submitted value
     * @return "true" if the component was checked, otherise "false"
     */
    private synchronized boolean isChecked(String value) {
    	if (LOGGER.isDebugEnabled()) {
    		LOGGER.debug("raw value from request map: {}", value);
    	}
        return value!=null;
    }
    protected synchronized boolean hasInput(final FacesContext context,
            final UIComponent component) throws IOException {
        if (!component.isRendered()) {
            return false;
        }
        final UIComponent parent = (UIComponent) component.getAttributes().get(
                "parentResponseDomain");
        if (parent == null) {
            return false;
        }
        return true;
    }
	protected synchronized void encodeInputMatrix(final FacesContext context, final UIComponent component)
			throws IOException {
		LOGGER.info("encodeInputMatrix");
		if (!component.isRendered()) {
			return;
		}
        final UIComponent parent = (UIComponent) component.getParent();
        if (parent == null) {
            return;
        }
		Boolean isSelected = Boolean.valueOf(component.getAttributes().get("value").toString());
		Boolean isExclusive = Boolean.valueOf(component.getAttributes().get("exclusive").toString());
		final ResponseWriter writer = context.getResponseWriter();
		writer.startElement("input", component);
		writer.writeAttribute("type", "checkbox", null);
		writer.writeAttribute("name", component.getClientId(), null);
		writer.writeAttribute("id", component.getClientId(), null);
		writer.writeAttribute("value", component.getId(), "value");
		final StringBuffer trigger = new StringBuffer();
		final StringBuffer attachedScripts = new StringBuffer();
		if(this.hasOpenQuestion(context, component)){
			List<UIComponent> openQuestions = getOpenQuestion(context,component);
			for(UIComponent openQuestion:openQuestions){
				String script = "zofar_multipleChoice_register_exclusive_attached('"+ parent.getClientId() + "', '" + component.getClientId() + "', '" + openQuestion.getClientId()+":aoq',"+isExclusive+");\n";
				attachedScripts.append(script+"\n");
				trigger.append("zofar_multipleChoice_triggerCheck('"+component.getClientId()+"','"+openQuestion.getClientId()+":"+openQuestion.getAttributes().get("inputId")+"');");
			}
		}
		if(isExclusive){
			trigger.append("zofar_multipleChoice_triggerExclusive('"+component.getClientId()+"','"+parent.getClientId()+"');");
		}
		else{
			trigger.append("zofar_multipleChoice_triggerNotExclusive('"+component.getClientId()+"','"+parent.getClientId()+"');");
		}
		if(trigger.length() > 0){
			writer.writeAttribute("onchange", "javascript:"+trigger.toString(),null);
		}
		if (isSelected) {
			writer.writeAttribute("checked", "checked", null);
		}
		writer.endElement("input");
		writer.startElement("script", component);
		writer.writeAttribute("type", "text/javascript", null);
		writer.write(attachedScripts.toString());
		writer.endElement("script");
		insertExclusiveJavaScript(context,component,parent);
		return;
	}
	protected synchronized void encodeInput(final FacesContext context, final UIComponent component)
			throws IOException {
		LOGGER.info("encodeImput");
		if (!component.isRendered()) {
			return;
		}
        final UIComponent parent = (UIComponent) component.getAttributes().get(
                "parentResponseDomain");
        if (parent == null) {
            return;
        }
		Boolean isSelected = Boolean.valueOf(component.getAttributes().get("value").toString());
		Boolean isExclusive = Boolean.valueOf(component.getAttributes().get("exclusive").toString());
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("selectedValue {}", isSelected);
		}
		final ResponseWriter writer = context.getResponseWriter();
		writer.startElement("input", component);
		writer.writeAttribute("type", "checkbox", null);
		writer.writeAttribute("name", component.getClientId(), null);
		writer.writeAttribute("id", component.getClientId(), null);
		writer.writeAttribute("value", component.getId(), "value");
		final StringBuffer trigger = new StringBuffer();
		if(this.hasOpenQuestion(context, component)){
			List<UIComponent> openQuestions = getOpenQuestion(context,component);
			for(UIComponent openQuestion:openQuestions){
				trigger.append("zofar_multipleChoice_triggerCheck('"+component.getClientId()+"','"+openQuestion.getClientId()+":"+openQuestion.getAttributes().get("inputId")+"');");
			}
		}
		if(isExclusive){
			trigger.append("zofar_multipleChoice_triggerExclusive('"+component.getClientId()+"','"+parent.getClientId()+"');");
		}
		else{
			trigger.append("zofar_multipleChoice_triggerNotExclusive('"+component.getClientId()+"','"+parent.getClientId()+"');");
		}
		if(trigger.length() > 0){
			writer.writeAttribute("onchange", "javascript:"+trigger.toString(),null);
		}
		if (isSelected) {
			writer.writeAttribute("checked", "checked", null);
		}
		writer.endElement("input");
		UIComponent responsedomain = JsfUtility.getInstance().getParentResponseDomain(component);
		insertExclusiveJavaScript(context,component,responsedomain);
		return;
	}
	protected synchronized boolean hasLabel(final FacesContext context, final UIComponent component)
			throws IOException {
		if (!component.isRendered()) {
			return false;
		}
        final UIComponent parent = (UIComponent) component.getAttributes().get(
                "parentResponseDomain");
        if (parent == null) {
            return false;
        }
		final String itemLabel = (String) component.getAttributes()
				.get("label");
		final String label = JsfUtility.getInstance().evaluateValueExpression(
				context, itemLabel, String.class);
		if(label.equals(""))return false;
		return true;
	}
	protected synchronized void encodeLabel(final FacesContext context, final UIComponent component)
			throws IOException {
		if (!component.isRendered()) {
			return ;
		}
        final UIComponent parent = (UIComponent) component.getAttributes().get(
                "parentResponseDomain");
        if (parent == null) {
            return;
        }
		final String itemLabel = (String) component.getAttributes()
				.get("label");
		final String label = JsfUtility.getInstance().evaluateValueExpression(
				context, itemLabel, String.class);
		final ResponseWriter writer = context.getResponseWriter();
		writer.startElement("label", component);
		writer.writeAttribute("for", component.getClientId(), null);
		writer.write(label);
		writer.endElement("label");
		return ;
	}
	protected synchronized boolean hasValueLabel(final FacesContext context, final UIComponent component)
			throws IOException {
		if (!component.isRendered()) {
			return false;
		}
        final UIComponent parent = (UIComponent) component.getAttributes().get(
                "parentResponseDomain");
        if (parent == null) {
            return false;
        }
		final Boolean showValues = (Boolean) parent.getAttributes().get(
				"showValues");
		if((showValues == null)||(!showValues))return false;
		final String itemValue = (String) component.getAttributes().get("value");
		final String label = JsfUtility.getInstance().evaluateValueExpression(
				context, itemValue, String.class);
		if(label.equals(""))return false;
		return true;
	}
	protected synchronized void encodeValueLabel(final FacesContext context, final UIComponent component)
			throws IOException {
		if (!component.isRendered()) {
			return;
		}
        final UIComponent parent = (UIComponent) component.getAttributes().get(
                "parentResponseDomain");
        if (parent == null) {
            return;
        }
		final Boolean showValues = (Boolean) parent.getAttributes().get(
				"showValues");
		if((showValues == null)||(!showValues))return;
		final String itemValue = (String) component.getAttributes().get("value");
		final String label = JsfUtility.getInstance().evaluateValueExpression(
				context, itemValue, String.class);
		if(label.equals(""))return;
		final ResponseWriter writer = context.getResponseWriter();
		writer.startElement("label", component);
		writer.writeAttribute("for", component.getClientId(), null);
		writer.write(""+label+"");
		writer.endElement("label");
		return;
	}
	protected synchronized boolean hasOpenQuestion(final FacesContext context, final UIComponent component)
			throws IOException {
		if (!component.isRendered()) {
			return false;
		}
		for (@SuppressWarnings("unused") final UIComponent child : component.getChildren()) {
			return true;
		}
		return false;
	}
	private List<UIComponent> getOpenQuestion(final FacesContext context, final UIComponent component)
			throws IOException {
		if (!component.isRendered()) {
			return null;
		}
		List<UIComponent> openQuestions = null;
		for (@SuppressWarnings("unused") final UIComponent child : component.getChildren()) {
			if(openQuestions == null)openQuestions = new ArrayList<UIComponent>();
			openQuestions.add(child);
		}
		return openQuestions;
	}
	protected synchronized boolean encodeOpenQuestion(final FacesContext context, final UIComponent component)
			throws IOException {
		if (!component.isRendered()) {
			return false;
		}
		final ResponseWriter writer = context.getResponseWriter();
		Boolean isExclusive = Boolean.valueOf(component.getAttributes().get("exclusive").toString());
		UIComponent responsedomain = JsfUtility.getInstance().getParentResponseDomain(component);
		boolean flag = false;
		for (final UIComponent child : component.getChildren()) {
			writer.startElement("script", component);
			writer.writeAttribute("type", "text/javascript", null);
			String script = "zofar_multipleChoice_register_exclusive_attached('"+ responsedomain.getClientId() + "', '" + component.getClientId() + "', '" + child.getClientId()+":aoq',"+isExclusive+");\n";
			writer.write(script);
			writer.endElement("script");
			child.encodeAll(context);
			flag = true;
		}
		return flag;
	}
	private void insertExclusiveJavaScript(final FacesContext context,final UIComponent component,UIComponent responsedomain) throws IOException {
		final ResponseWriter writer = context.getResponseWriter();
		writer.startElement("script", component);
		writer.writeAttribute("type", "text/javascript", null);
		Boolean isExclusive = Boolean.valueOf(component.getAttributes().get("exclusive").toString());
		String script = "zofar_multipleChoice_register_exclusive('"+ responsedomain.getClientId() + "', '" + component.getClientId()+"',"+isExclusive+");\n";
		writer.write(script);
		writer.endElement("script");
	}
}
