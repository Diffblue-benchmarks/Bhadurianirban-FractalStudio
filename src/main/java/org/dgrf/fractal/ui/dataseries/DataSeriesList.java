/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dgrf.fractal.ui.dataseries;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
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
import org.dgrf.fractal.core.client.FractalCoreClient;
import org.dgrf.fractal.termmeta.DataSeriesMeta;
import org.dgrf.fractal.core.dto.FractalDTO;
import org.dgrf.cms.ui.login.CMSClientAuthCredentialValue;

/**
 *
 * @author bhaduri
 */
@Named(value = "dataSeriesList")
@ViewScoped
public class DataSeriesList implements Serializable {

    /**
     * Creates a new instance of DataSeriesList
     */
    private String termSlug;
    private List<Map<String, Object>> screenTermInstanceList;
    private boolean metaDoesNotExistForTerm;
    private Map<String, String> termScreenFieldsDesc;
    private String termName;
    private Map<String, Object> selectedMetaData;
    private String viewCumulative;

    public DataSeriesList() {
    }

    @PostConstruct
    public void init() {

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

    public String goToViewDataSeries(String viewCumulative) {

        setViewCumulative(viewCumulative);
        return "DataSeriesView";
    }

    public String deleteDataSeries() {
        FacesMessage message;
        FractalCoreClient mts = new FractalCoreClient();
        FractalDTO fractalDTO = new FractalDTO();
        fractalDTO.setAuthCredentials(CMSClientAuthCredentialValue.AUTH_CREDENTIALS);
        String selectedTermInstanceSlug = (String) selectedMetaData.get(CMSConstants.TERM_INSTANCE_SLUG);
        fractalDTO.setDataSeriesSlug(selectedTermInstanceSlug);
        //delete dataseries metadata
        fractalDTO = mts.deleteDataseries(fractalDTO);
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
        int seriesId = Integer.parseInt((String) selectedMetaData.get(DataSeriesMeta.DATA_SERIES_ID));

        return "DataSeriesList";
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

    public String getTermName() {
        return termName;
    }

    public void setTermName(String termName) {
        this.termName = termName;
    }

    public Map<String, String> getTermScreenFieldsDesc() {
        return termScreenFieldsDesc;
    }

    public void setTermScreenFieldsDesc(Map<String, String> termScreenFieldsDesc) {
        this.termScreenFieldsDesc = termScreenFieldsDesc;
    }

    public Map<String, Object> getSelectedMetaData() {
        return selectedMetaData;
    }

    public void setSelectedMetaData(Map<String, Object> selectedMetaData) {
        this.selectedMetaData = selectedMetaData;
    }

    public boolean isMetaDoesNotExistForTerm() {
        return metaDoesNotExistForTerm;
    }

    public void setMetaDoesNotExistForTerm(boolean metaDoesNotExistForTerm) {
        this.metaDoesNotExistForTerm = metaDoesNotExistForTerm;
    }

    public String getViewCumulative() {
        return viewCumulative;
    }

    public void setViewCumulative(String viewCumulative) {
        this.viewCumulative = viewCumulative;
    }

}
