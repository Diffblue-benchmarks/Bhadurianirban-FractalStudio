/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dgrf.cms.ui.terminstance;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import org.dgrf.cloud.response.DGRFResponseCode;
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
 * @author bhaduri
 */
@Named(value = "termInstanceAdd")
@ViewScoped
public class TermInstanceAdd implements Serializable {

    private String termSlug;
    private String termName;
    private List<FluidGridItem> formItems;
    Map<String, Object> screenTermInstance;
    List<Map<String, Object>> termScreenFields;

    /**
     * Creates a new instance of TermInstanceAdd
     */
    public TermInstanceAdd() {
    }

    public String getTermSlug() {
        return termSlug;
    }

    public void setTermSlug(String termSlug) {
        this.termSlug = termSlug;
    }

    public void creteTermForm() {
        CMSClientService mts = new CMSClientService();
        TermDTO termDTO = new TermDTO();
        termDTO.setAuthCredentials(CMSClientAuthCredentialValue.AUTH_CREDENTIALS);
        termDTO.setTermSlug(termSlug);
        termDTO = mts.getTermDetails(termDTO);
        termName = (String) termDTO.getTermDetails().get(CMSConstants.TERM_NAME);

        formItems = new ArrayList<>();
        TermMetaDTO termMetaDTO = new TermMetaDTO();
        termMetaDTO.setAuthCredentials(CMSClientAuthCredentialValue.AUTH_CREDENTIALS);
        termMetaDTO.setTermSlug(termSlug);
        termMetaDTO = mts.getTermMetaList(termMetaDTO);
        termScreenFields = termMetaDTO.getTermMetaFields();

        for (Map<String, Object> termScreenField : termScreenFields) {
            Map<String, String> selectTermList = (Map<String, String>) termScreenField.get("selectTermList");
            List<SelectItem> selectItems;
            if (selectTermList != null) {
                selectItems = selectTermList.entrySet().stream().map(selectTerm -> {
                    SelectItem selectItem = new SelectItem(selectTerm.getKey(), selectTerm.getValue());
                    return selectItem;
                }).collect(Collectors.toList());
            } else {
                selectItems = null;
            }
            //List<SelectItem> selectItems = TermUtil.convertMapToSelectItem(termMetaBean.getSelectTermList());
            FormField formField = new FormField((String) termScreenField.get("description"), null, (String) termScreenField.get("metaKey"), (Boolean) termScreenField.get("mandatory"), (Boolean) termScreenField.get("disableOnScreen"), selectItems);
            FluidGridItem formFieldItem = new FluidGridItem(formField, (String) termScreenField.get("dataType"));
            formItems.add(formFieldItem);
        }

    }

    public String addTermInstance() {

        String termMetaKey;
        screenTermInstance = new HashMap<>();
        screenTermInstance.put("termSlug", termSlug);
        CMSClientService mts = new CMSClientService();
        FacesMessage message;

        for (FluidGridItem fluidGridItem : formItems) {
            FormField formField = (FormField) fluidGridItem.getData();
            termMetaKey = formField.getMetaKey();
            screenTermInstance.put("metaKey", termMetaKey);
            screenTermInstance.put(termMetaKey, formField.getValue());
        }
        TermInstanceDTO termInstanceDTO = new TermInstanceDTO();
        termInstanceDTO.setAuthCredentials(CMSClientAuthCredentialValue.AUTH_CREDENTIALS);
        termInstanceDTO.setAuthCredentials(CMSClientAuthCredentialValue.AUTH_CREDENTIALS);
        termInstanceDTO.setTermInstance(screenTermInstance);
        //DBResponse dBResponse = mts.saveTermInstance(termScreenFields, screenTermInstance);
        termInstanceDTO = mts.saveTermInstance(termInstanceDTO);
        //DBResponse dBResponse = mts.saveScreenTermInstance(termMetaDataBeans);

        if (termInstanceDTO.getResponseCode() != DGRFResponseCode.SUCCESS) {
            String redirectUrl = "TermInstanceAdd";
            DGRFResponseMessage responseMessage = new DGRFResponseMessage();
            message = new FacesMessage(FacesMessage.SEVERITY_FATAL, "Error", responseMessage.getResponseMessage(termInstanceDTO.getResponseCode()));
            FacesContext f = FacesContext.getCurrentInstance();
            f.getExternalContext().getFlash().setKeepMessages(true);
            f.addMessage(null, message);
            return redirectUrl;

        } else {
            DGRFResponseMessage responseMessage = new DGRFResponseMessage();
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", responseMessage.getResponseMessage(termInstanceDTO.getResponseCode()));
            FacesContext f = FacesContext.getCurrentInstance();
            f.getExternalContext().getFlash().setKeepMessages(true);
            f.addMessage(null, message);
            return "TermInstanceList";
        }

    }

    public String getTermName() {
        return termName;
    }

    public void setTermName(String termName) {
        this.termName = termName;
    }

    public List<FluidGridItem> getFormItems() {
        return formItems;
    }

    public void setFormItems(List<FluidGridItem> formItems) {
        this.formItems = formItems;
    }

}
