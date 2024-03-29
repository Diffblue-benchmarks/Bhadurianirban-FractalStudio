/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dgrf.cms.ui.terminstance;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import org.dgrf.cloud.response.DGRFResponseMessage;
import org.dgrf.cms.core.driver.CMSClientService;
import org.dgrf.cms.constants.CMSConstants;
import org.dgrf.cms.dto.TermDTO;
import org.dgrf.cms.dto.TermInstanceDTO;
import org.dgrf.cms.dto.TermMetaDTO;
import org.dgrf.cms.ui.login.CMSClientAuthCredentialValue;
import org.primefaces.extensions.model.fluidgrid.FluidGridItem;

/**
 *
 * @author dgrf-iv
 */
@Named(value = "termInstanceEdit")
@ViewScoped
public class TermInstanceEdit implements Serializable {

    private String termSlug;
    private String termName;
    private String termInstanceSlug;
    
    private List<FluidGridItem> formItems;
    private Map<String, Object> screenTermInstance;
    List<Map<String, Object>> termScreenFields;

    public TermInstanceEdit() {
    }

    public void createMetaDataEditForm() {
        CMSClientService mts = new CMSClientService();
        formItems = new ArrayList<>();
        TermDTO termDTO = new TermDTO();
        termDTO.setAuthCredentials(CMSClientAuthCredentialValue.AUTH_CREDENTIALS);
        termDTO.setTermSlug(termSlug);
        termDTO = mts.getTermDetails(termDTO);
        termName = (String)termDTO.getTermDetails().get(CMSConstants.TERM_NAME);
        
        //Get screen data
        TermInstanceDTO termInstanceDTO = new TermInstanceDTO();
        termInstanceDTO.setAuthCredentials(CMSClientAuthCredentialValue.AUTH_CREDENTIALS);
        
        termInstanceDTO.setTermSlug(termSlug);
        termInstanceDTO.setTermInstanceSlug(termInstanceSlug);
        termInstanceDTO = mts.getTermInstance(termInstanceDTO);
        screenTermInstance = termInstanceDTO.getTermInstance();
        
        //Get screen fields
        TermMetaDTO termMetaDTO = new TermMetaDTO();
        termMetaDTO.setAuthCredentials(CMSClientAuthCredentialValue.AUTH_CREDENTIALS);
        termMetaDTO.setTermSlug(termSlug);
        termMetaDTO = mts.getTermMetaList(termMetaDTO);
        termScreenFields = termMetaDTO.getTermMetaFields();
        
        
        
        //assign to fluid grid items
        for (Map<String, Object> termScreenField : termScreenFields) {
            Map<String,String> selectTermList = (Map<String,String> ) termScreenField.get("selectTermList");
            List<SelectItem> selectItems;
            if (selectTermList != null) {
                selectItems = selectTermList.entrySet().stream().map(selectTerm -> {
                    SelectItem selectItem = new SelectItem(selectTerm.getKey(), selectTerm.getValue());
                    return selectItem;
                }).collect(Collectors.toList());
            } else {
                selectItems = null;
            }

            if(termScreenField.get("metaKey").toString().equals("termInstanceSlug")) {
                termScreenField.put("disableOnScreen", true);
            }
            FormField formField = new FormField((String)termScreenField.get("description"), screenTermInstance.get((String) termScreenField.get("metaKey")), (String) termScreenField.get("metaKey"), (Boolean)termScreenField.get("mandatory"), (Boolean) termScreenField.get("disableOnScreen"), selectItems);

            FluidGridItem formFieldItem = new FluidGridItem(formField, (String)termScreenField.get("dataType"));
            formItems.add(formFieldItem);

        }

    }

    public String editTermInstance() {
        FacesMessage message;
        String termMetaKey;
        CMSClientService mts = new CMSClientService();
        for (FluidGridItem fluidGridItem : formItems) {
            FormField formField = (FormField) fluidGridItem.getData();
            termMetaKey = formField.getMetaKey();
            screenTermInstance.put(termMetaKey,formField.getValue());
            
        }

        TermInstanceDTO termInstanceDTO = new TermInstanceDTO();
        termInstanceDTO.setAuthCredentials(CMSClientAuthCredentialValue.AUTH_CREDENTIALS);
        termInstanceDTO.setTermInstance(screenTermInstance);
        termInstanceDTO = mts.saveTermInstance(termInstanceDTO);
        if (termInstanceDTO.getResponseCode() != 0) {
            String redirectUrl = "TermInstanceEdit";
            DGRFResponseMessage responseMessage = new DGRFResponseMessage();
            message = new FacesMessage(FacesMessage.SEVERITY_FATAL, "Error", responseMessage.getResponseMessage(termInstanceDTO.getResponseCode()));
            FacesContext f = FacesContext.getCurrentInstance();
            f.getExternalContext().getFlash().setKeepMessages(true);
            f.addMessage(null, message);
            return redirectUrl;
        } else {
            String redirectUrl = "TermInstanceList";
            DGRFResponseMessage responseMessage = new DGRFResponseMessage();
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", responseMessage.getResponseMessage(termInstanceDTO.getResponseCode()));
            FacesContext f = FacesContext.getCurrentInstance();
            f.getExternalContext().getFlash().setKeepMessages(true);
            f.addMessage(null, message);
            return redirectUrl;
        }
    }

    public String getTermSlug() {
        return termSlug;
    }

    public void setTermSlug(String termSlug) {
        this.termSlug = termSlug;
    }

    public String getTermInstanceSlug() {
        return termInstanceSlug;
    }

    public void setTermInstanceSlug(String termInstanceSlug) {
        this.termInstanceSlug = termInstanceSlug;
    }



    public List<FluidGridItem> getFormItems() {
        return formItems;
    }

    public void setFormItems(List<FluidGridItem> formItems) {
        this.formItems = formItems;
    }

    public String getTermName() {
        return termName;
    }

    public void setTermName(String termName) {
        this.termName = termName;
    }

}
