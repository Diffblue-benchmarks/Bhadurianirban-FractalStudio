/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dgrf.fractal.ui.graph;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import org.dgrf.cloud.response.DGRFResponseMessage;
import org.dgrf.cms.constants.CMSConstants;
import org.dgrf.cms.core.driver.CMSClientService;
import org.dgrf.cms.dto.TermDTO;
import org.dgrf.cms.dto.TermInstanceDTO;
import org.dgrf.cms.dto.TermMetaDTO;
import org.dgrf.cms.ui.login.CMSClientAuthCredentialValue;
import org.dgrf.fractal.constants.FractalConstants;
import org.dgrf.fractal.core.client.FractalCoreClient;
import org.dgrf.fractal.core.dto.FractalDTO;
import org.dgrf.fractal.response.FractalResponseCode;

/**
 *
 * @author bhaduri
 */
@Named(value = "edgeListList")
@ViewScoped
public class EdgeListList implements Serializable {

    /**
     * Creates a new instance of EdgeListList
     */
    private String termSlug;
    private List<Map<String, Object>> screenTermInstanceList;
    private boolean metaDoesNotExistForTerm;
    private Map<String, String> termScreenFieldsDesc;
    private String termName;
    private Map<String, Object> selectedMetaData;
    
    private String psvgTermSlug = FractalConstants.TERM_SLUG_PSVG_CALC;
    
    public EdgeListList() {
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

        termScreenFieldsDesc = termMetaDTO.getTermMetaFieldLabels();

        //Creation of grid
        TermInstanceDTO termInstanceDTO = new TermInstanceDTO();
        termInstanceDTO.setAuthCredentials(CMSClientAuthCredentialValue.AUTH_CREDENTIALS);

        termInstanceDTO.setTermSlug(termSlug);
        termInstanceDTO = mts.getTermInstanceList(termInstanceDTO);

        screenTermInstanceList = termInstanceDTO.getTermInstanceList();
        metaDoesNotExistForTerm = !termScreenFieldsDesc.isEmpty();

    }

    public String gotoNetworkStatsOptions() {
        return "NetworkStatsOptions";
    }
    
    public String deleteGraph() {
        FacesMessage message;
        FractalCoreClient mts = new FractalCoreClient();
        FractalDTO fractalDTO = new FractalDTO();
        fractalDTO.setAuthCredentials(CMSClientAuthCredentialValue.AUTH_CREDENTIALS);
        //String selectedTermInstanceSlug = (String) selectedMetaData.get(CMSConstants.TERM_INSTANCE_SLUG);
        fractalDTO.setFractalTermInstance(selectedMetaData);
        //delete dataseries metadata
        fractalDTO = mts.deleteGraph(fractalDTO);
        if (fractalDTO.getResponseCode() == FractalResponseCode.SUCCESS) {
            DGRFResponseMessage responseMessage = new DGRFResponseMessage();
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", responseMessage.getResponseMessage(fractalDTO.getResponseCode()));
            FacesContext f = FacesContext.getCurrentInstance();
            f.getExternalContext().getFlash().setKeepMessages(true);
            f.addMessage(null, message);
        } else {
            DGRFResponseMessage responseMessage = new DGRFResponseMessage();
            message = new FacesMessage(FacesMessage.SEVERITY_FATAL, "Error", responseMessage.getResponseMessage(fractalDTO.getResponseCode()));
            FacesContext f = FacesContext.getCurrentInstance();
            f.getExternalContext().getFlash().setKeepMessages(true);
            f.addMessage(null, message);
        }

        return "EdgeListList";
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

    public Map<String, String> getTermScreenFieldsDesc() {
        return termScreenFieldsDesc;
    }

    public void setTermScreenFieldsDesc(Map<String, String> termScreenFieldsDesc) {
        this.termScreenFieldsDesc = termScreenFieldsDesc;
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

    public String getPsvgTermSlug() {
        return psvgTermSlug;
    }

    public void setPsvgTermSlug(String psvgTermSlug) {
        this.psvgTermSlug = psvgTermSlug;
    }

}
