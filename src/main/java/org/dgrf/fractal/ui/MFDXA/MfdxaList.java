/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dgrf.fractal.ui.MFDXA;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import org.dgrf.fractal.response.FractalResponseCode;
import org.dgrf.cloud.response.DGRFResponseMessage;
import org.dgrf.cms.core.driver.CMSClientService;
import org.dgrf.cms.constants.CMSConstants;
import org.dgrf.cms.dto.TermDTO;
import org.dgrf.cms.dto.TermInstanceDTO;
import org.dgrf.cms.dto.TermMetaDTO;
import org.dgrf.cms.ui.login.CMSClientAuthCredentialValue;
import org.dgrf.cms.ui.terminstance.TermInstanceUtil;
import org.dgrf.cms.ui.terminstance.TermMetaKeyLabels;


/**
 *
 * @author dgrfv
 */
@Named(value = "mfdxaList")
@ViewScoped
public class MfdxaList implements Serializable {

    private String termSlug;
    private List<Map<String, Object>> screenTermInstanceList;
    private boolean metaDoesNotExistForTerm;
    private List<TermMetaKeyLabels> instanceMetaKeys;
    private String termName;
    private Map<String, Object> selectedMetaData;

    /**
     * Creates a new instance of MfdxaList
     */
    public MfdxaList() {
    }

    public void fillTermMetaData() {

        CMSClientService mts = new CMSClientService();

        TermDTO termDTO = new TermDTO();
        termDTO.setAuthCredentials(CMSClientAuthCredentialValue.AUTH_CREDENTIALS);
        termDTO.setTermSlug(termSlug);
        termDTO = mts.getTermDetails(termDTO);

        termName = (String) termDTO.getTermDetails().get(CMSConstants.TERM_NAME);

        //Creation of grid
        TermMetaDTO termMetaDTO = new TermMetaDTO();
        termMetaDTO.setAuthCredentials(CMSClientAuthCredentialValue.AUTH_CREDENTIALS);
        termMetaDTO.setTermSlug(termSlug);
        termMetaDTO = mts.getTermMetaList(termMetaDTO);

        List<Map<String, Object>> termScreenFields = termMetaDTO.getTermMetaFields();
        
        TermInstanceDTO termInstanceDTO = new TermInstanceDTO();
termInstanceDTO.setAuthCredentials(CMSClientAuthCredentialValue.AUTH_CREDENTIALS);
        
        termInstanceDTO.setTermSlug(termSlug);
        termInstanceDTO = mts.getTermInstanceList(termInstanceDTO);

        screenTermInstanceList = termInstanceDTO.getTermInstanceList();
        
        metaDoesNotExistForTerm = !termScreenFields.isEmpty();
        instanceMetaKeys = TermInstanceUtil.prepareMetaKeyList(termScreenFields);

    }




    public String goToViewMFDXAesult() {
        //String selectedTermInstanceSlug = (String) selectedMetaData.get("termInstanceSlug");
        return "MFDXAResult";
    }

    public String deleteMFDFAResults() {
        FacesMessage message;
        DGRFResponseMessage responseMessage = new DGRFResponseMessage();
        String selectedTermInstanceSlug = (String) selectedMetaData.get("termInstanceSlug");

        CMSClientService cmscs = new CMSClientService();
        TermInstanceDTO termInstanceDTO = new TermInstanceDTO();
termInstanceDTO.setAuthCredentials(CMSClientAuthCredentialValue.AUTH_CREDENTIALS);
        
        termInstanceDTO.setTermSlug(termSlug);
        termInstanceDTO.setTermInstanceSlug(selectedTermInstanceSlug);
        termInstanceDTO = cmscs.deleteTermInstance(termInstanceDTO);
        
        if (termInstanceDTO.getResponseCode() == FractalResponseCode.SUCCESS) {
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", responseMessage.getResponseMessage(termInstanceDTO.getResponseCode()));
            FacesContext f = FacesContext.getCurrentInstance();
            f.getExternalContext().getFlash().setKeepMessages(true);
            f.addMessage(null, message);
        } else {
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", responseMessage.getResponseMessage(termInstanceDTO.getResponseCode()));
            FacesContext f = FacesContext.getCurrentInstance();
            f.getExternalContext().getFlash().setKeepMessages(true);
            f.addMessage(null, message);
        }
        return "MFDXACalcList";
    }

    public String getTermSlug() {
        return termSlug;
    }

    public void setTermSlug(String termSlug) {
        this.termSlug = termSlug;
    }

    public List<Map<String, Object>> getScreenTermInstanceList() {
        return screenTermInstanceList;
    }

    public void setScreenTermInstanceList(List<Map<String, Object>> screenTermInstanceList) {
        this.screenTermInstanceList = screenTermInstanceList;
    }

    public boolean isMetaDoesNotExistForTerm() {
        return metaDoesNotExistForTerm;
    }

    public void setMetaDoesNotExistForTerm(boolean metaDoesNotExistForTerm) {
        this.metaDoesNotExistForTerm = metaDoesNotExistForTerm;
    }

    public List<TermMetaKeyLabels> getInstanceMetaKeys() {
        return instanceMetaKeys;
    }

    public void setInstanceMetaKeys(List<TermMetaKeyLabels> instanceMetaKeys) {
        this.instanceMetaKeys = instanceMetaKeys;
    }

    public String getTermName() {
        return termName;
    }

    public void setTermName(String termName) {
        this.termName = termName;
    }

    public Map<String, Object> getSelectedMetaData() {
        return selectedMetaData;
    }

    public void setSelectedMetaData(Map<String, Object> selectedMetaData) {
        this.selectedMetaData = selectedMetaData;
    }

}
