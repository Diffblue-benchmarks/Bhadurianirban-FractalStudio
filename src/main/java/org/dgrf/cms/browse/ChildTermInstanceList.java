/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dgrf.cms.browse;

import org.dgrf.cms.ui.terminstance.TermMetaKeyLabels;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
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
import org.dgrf.cms.ui.terminstance.TermInstanceUtil;


/**
 *
 * @author bhaduri
 */
@Named(value = "childTermInstanceList")
@ViewScoped
public class ChildTermInstanceList implements Serializable {

    private String parentTermSlug;
    private String parentTermInstanceSlug;
    private String childTermSlug;
    private String childTermMetaKey;
//    
//    private boolean backPress;


    private List<Map<String, Object>> screenTermInstanceList;
    private boolean metaDoesNotExistForTerm;
    private List<TermMetaKeyLabels> instanceMetaKeys;
    private String childTermName;
    private String parentTermName;
    private String parentTermInstanceName;
    private Map<String, Object> selectedTermInstance;

    public ChildTermInstanceList() {
    }

    @PostConstruct
    public void init() {

    }

    public void fillTermMetaData() {
        //get the following for the header line
        //get child term name
        CMSClientService mts = new CMSClientService();
        TermDTO termDTO = new TermDTO();
        termDTO.setAuthCredentials(CMSClientAuthCredentialValue.AUTH_CREDENTIALS);
        termDTO.setTermSlug(childTermSlug);
        termDTO = mts.getTermDetails(termDTO);
        childTermName = (String)termDTO.getTermDetails().get(CMSConstants.TERM_NAME);
        
        //get parent term name
        termDTO.setAuthCredentials(CMSClientAuthCredentialValue.AUTH_CREDENTIALS);
        termDTO.setTermSlug(parentTermSlug);
        termDTO = mts.getTermDetails(termDTO);
        parentTermName = (String)termDTO.getTermDetails().get(CMSConstants.TERM_NAME);
        
        //get parent term instance name
        TermInstanceDTO termInstanceDTO = new TermInstanceDTO();
        termInstanceDTO.setAuthCredentials(CMSClientAuthCredentialValue.AUTH_CREDENTIALS);
        termInstanceDTO.setTermSlug(parentTermSlug);
        termInstanceDTO.setTermInstanceSlug(parentTermInstanceSlug);
        termInstanceDTO = mts.getTermInstance(termInstanceDTO);
        parentTermInstanceName = termInstanceDTO.getTermInstanceFirstField();
        
        
        //Creation of grid
        //Get grid column names
        TermMetaDTO termMetaDTO = new TermMetaDTO();
        termMetaDTO.setAuthCredentials(CMSClientAuthCredentialValue.AUTH_CREDENTIALS);
        termMetaDTO.setTermSlug(childTermSlug);
        termMetaDTO = mts.getTermMetaList(termMetaDTO);
        List<Map<String, Object>> termScreenFields = termMetaDTO.getTermMetaFields();
        
        
        //get instance data
        termInstanceDTO.setAuthCredentials(CMSClientAuthCredentialValue.AUTH_CREDENTIALS);
        termInstanceDTO.setParentTermInstanceSlug(parentTermInstanceSlug);
        termInstanceDTO.setParentTermSlug(parentTermSlug);
        termInstanceDTO.setTermSlug(childTermSlug);
        termInstanceDTO.setChildTermMetaKey(childTermMetaKey);
        termInstanceDTO = mts.getChildTermInstanceList(termInstanceDTO);
        screenTermInstanceList = termInstanceDTO.getTermInstanceList();
        metaDoesNotExistForTerm = !termScreenFields.isEmpty();
        instanceMetaKeys = TermInstanceUtil.prepareMetaKeyList(termScreenFields);

        

    }

    public String browseTermInstance () {
        CMSClientService mts = new CMSClientService();
        TermMetaDTO termMetaDTO = new TermMetaDTO();
        termMetaDTO.setAuthCredentials(CMSClientAuthCredentialValue.AUTH_CREDENTIALS);
        String pts = (String)selectedTermInstance.get(CMSConstants.TERM_SLUG);
        String ptis = (String)selectedTermInstance.get(CMSConstants.TERM_INSTANCE_SLUG);
        termMetaDTO.setTermSlug(pts);
        termMetaDTO = mts.getChildTermMetaList(termMetaDTO);
        List<Map<String, Object>> termMetaListInMap;
        if (termMetaDTO.getResponseCode() == DGRFResponseCode.SUCCESS) {
            termMetaListInMap = termMetaDTO.getTermMetaFields();
            
            if (termMetaListInMap.size()==1) {
                Map<String,Object> selectedTermMetaObj = termMetaListInMap.get(0);
                parentTermSlug = pts;
                parentTermInstanceSlug = ptis;
                childTermSlug = (String)selectedTermMetaObj.get(CMSConstants.TERM_SLUG);
                childTermMetaKey = (String)selectedTermMetaObj.get(CMSConstants.META_KEY);
                return "ChildTermInstanceList";
            } else {
                return "ChildTermList";
            }
        } else {
            FacesMessage message;
            String redirectUrl = "ChildTermInstanceList";
            DGRFResponseMessage responseMessage = new DGRFResponseMessage();
            message = new FacesMessage(FacesMessage.SEVERITY_FATAL, "Error", responseMessage.getResponseMessage(termMetaDTO.getResponseCode()));
            FacesContext f = FacesContext.getCurrentInstance();
            f.getExternalContext().getFlash().setKeepMessages(true);
            f.addMessage(null, message);
            return redirectUrl;
        }
        
    }

    public List<Map<String, Object>> getScreenTermInstanceList() {
        return screenTermInstanceList;
    }

    public void setScreenTermInstanceList(List<Map<String, Object>> screenTermInstanceList) {
        this.screenTermInstanceList = screenTermInstanceList;
    }


    public List<TermMetaKeyLabels> getInstanceMetaKeys() {
        return instanceMetaKeys;
    }

    public void setInstanceMetaKeys(List<TermMetaKeyLabels> instanceMetaKeys) {
        this.instanceMetaKeys = instanceMetaKeys;
    }

    public Map<String, Object> getSelectedTermInstance() {
        return selectedTermInstance;
    }

    public void setSelectedTermInstance(Map<String, Object> selectedTermInstance) {
        this.selectedTermInstance = selectedTermInstance;
    }


    public boolean isMetaDoesNotExistForTerm() {
        return metaDoesNotExistForTerm;
    }

    public void setMetaDoesNotExistForTerm(boolean metaDoesNotExistForTerm) {
        this.metaDoesNotExistForTerm = metaDoesNotExistForTerm;
    }

    public String getParentTermSlug() {
        return parentTermSlug;
    }

    public void setParentTermSlug(String parentTermSlug) {
        this.parentTermSlug = parentTermSlug;
    }

    public String getParentTermInstanceSlug() {
        return parentTermInstanceSlug;
    }

    public void setParentTermInstanceSlug(String parentTermInstanceSlug) {
        this.parentTermInstanceSlug = parentTermInstanceSlug;
    }

    public String getChildTermSlug() {
        return childTermSlug;
    }

    public void setChildTermSlug(String childTermSlug) {
        this.childTermSlug = childTermSlug;
    }

    


    public String getChildTermMetaKey() {
        return childTermMetaKey;
    }

    public void setChildTermMetaKey(String childTermMetaKey) {
        this.childTermMetaKey = childTermMetaKey;
    }

    public String getChildTermName() {
        return childTermName;
    }

    public void setChildTermName(String childTermName) {
        this.childTermName = childTermName;
    }

    public String getParentTermName() {
        return parentTermName;
    }

    public void setParentTermName(String parentTermName) {
        this.parentTermName = parentTermName;
    }

    public String getParentTermInstanceName() {
        return parentTermInstanceName;
    }

    public void setParentTermInstanceName(String parentTermInstanceName) {
        this.parentTermInstanceName = parentTermInstanceName;
    }


}
