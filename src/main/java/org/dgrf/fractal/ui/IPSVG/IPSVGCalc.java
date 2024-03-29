/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dgrf.fractal.ui.IPSVG;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import org.dgrf.fractal.response.FractalResponseCode;
import org.dgrf.cms.constants.CMSConstants;
import org.dgrf.cms.core.driver.CMSClientService;

import org.dgrf.cms.dto.TermDTO;
import org.dgrf.cms.dto.TermInstanceDTO;
import org.dgrf.cms.dto.TermMetaDTO;
import org.dgrf.fractal.core.client.FractalCoreClient;
import org.dgrf.fractal.termmeta.DataSeriesMeta;
import org.dgrf.fractal.constants.FractalConstants;
import org.dgrf.fractal.core.dto.FractalDTO;
import org.dgrf.fractal.termmeta.IPSVGResultsMeta;
import org.dgrf.cms.ui.login.CMSClientAuthCredentialValue;

/**
 *
 * @author dgrfv
 */
@Named(value = "iPSVGCalc")
@ViewScoped
public class IPSVGCalc implements Serializable {

    private String termSlug;
    private String termName;
    private String calcType;
    private List<Map<String, Object>> psvgParamDataList;
    private Map<String, Object> selectedPsvgParamData;
    private List<Map<String, Object>> dataSeriesList;
    private Map<String, Object> selectedDataSeries;
    private Map<String, String> psvgParamFieldLabels;
    private Map<String, String> dataSeriesFieldsLabel;
    private Map<String, Object> screenTermInstance;

    /**
     * Creates a new instance of IPSVGCalc
     */
    public IPSVGCalc() {
    }

    public void creteTermForm() {
        CMSClientService mts = new CMSClientService();
        //get term name
        TermDTO termDTO = new TermDTO();
        termDTO.setAuthCredentials(CMSClientAuthCredentialValue.AUTH_CREDENTIALS);
        termDTO.setTermSlug(termSlug);
        termDTO = mts.getTermDetails(termDTO);
        termName = (String) termDTO.getTermDetails().get(CMSConstants.TERM_NAME);

        //get psvg param field lables psvg and IPSVG params are same
        TermMetaDTO termMetaDTO = new TermMetaDTO();
        termMetaDTO.setAuthCredentials(CMSClientAuthCredentialValue.AUTH_CREDENTIALS);
        termMetaDTO.setTermSlug(FractalConstants.TERM_SLUG_PSVG_PARAM);
        termMetaDTO = mts.getTermMetaList(termMetaDTO);
        psvgParamFieldLabels = termMetaDTO.getTermMetaFieldLabels();

        //get dataseries field labels
        termMetaDTO.setAuthCredentials(CMSClientAuthCredentialValue.AUTH_CREDENTIALS);
        termMetaDTO.setTermSlug(FractalConstants.TERM_SLUG_DATASERIES);
        termMetaDTO = mts.getTermMetaList(termMetaDTO);
        dataSeriesFieldsLabel = termMetaDTO.getTermMetaFieldLabels();

        //get psvg param field data
        TermInstanceDTO termInstanceDTO = new TermInstanceDTO();
        termInstanceDTO.setAuthCredentials(CMSClientAuthCredentialValue.AUTH_CREDENTIALS);
        termInstanceDTO.setAuthCredentials(CMSClientAuthCredentialValue.AUTH_CREDENTIALS);
        termInstanceDTO.setTermSlug(FractalConstants.TERM_SLUG_PSVG_PARAM);
        termInstanceDTO = mts.getTermInstanceList(termInstanceDTO);
        psvgParamDataList = termInstanceDTO.getTermInstanceList();

        //get dataseries field list
        termInstanceDTO.setTermSlug(FractalConstants.TERM_SLUG_DATASERIES);
        termInstanceDTO = mts.getTermInstanceList(termInstanceDTO);
        if (termInstanceDTO.getResponseCode() == FractalResponseCode.SUCCESS) {
            List<Map<String, Object>> dataSeriesListAll = termInstanceDTO.getTermInstanceList();
            dataSeriesList = dataSeriesListAll.stream().filter(ds -> ds.get(DataSeriesMeta.DATA_SERIES_TYPE).equals(DataSeriesMeta.DATA_SERIES_UNIFORM)).collect(Collectors.toList());
        }

    }

    public String startCalc() {

        FacesMessage message;
        if (selectedPsvgParamData == null) {
            message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Paramenter required.", "Paramenter required.");
            FacesContext f = FacesContext.getCurrentInstance();
            f.getExternalContext().getFlash().setKeepMessages(true);
            f.addMessage(null, message);
            String redirectUrl = "IPSVGCalc";
            return redirectUrl;
        }
        if (selectedDataSeries == null) {
            message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Data required.", "Data required.");
            FacesContext f = FacesContext.getCurrentInstance();
            f.getExternalContext().getFlash().setKeepMessages(true);
            f.addMessage(null, message);
            String redirectUrl = "IPSVGCalc";
            return redirectUrl;
        }
        String psvgParamSlug = (String) selectedPsvgParamData.get("termInstanceSlug");
        String dataSeriesSlug = (String) selectedDataSeries.get("termInstanceSlug");

        //populate PSVG results instance
        FractalCoreClient fractalCoreClient = new FractalCoreClient();
        FractalDTO fractalDTO = new FractalDTO();
        fractalDTO.setAuthCredentials(CMSClientAuthCredentialValue.AUTH_CREDENTIALS);
        fractalDTO.setParamSlug(psvgParamSlug);
        fractalDTO.setDataSeriesSlug(dataSeriesSlug);

        Long dataCount = Long.parseLong((String) selectedDataSeries.get(DataSeriesMeta.DATA_SERIES_LENGTH));
        if (dataCount < FractalConstants.DATA_LIMIT) {

            fractalDTO = fractalCoreClient.calculateImprovedPSVG(fractalDTO);
            screenTermInstance = fractalDTO.getFractalTermInstance();

            if (screenTermInstance == null) {
                message = new FacesMessage(FacesMessage.SEVERITY_FATAL, "Something wrong.", "Contact Admin.");
                FacesContext f = FacesContext.getCurrentInstance();
                f.getExternalContext().getFlash().setKeepMessages(true);
                f.addMessage(null, message);
                String redirectUrl = "IPSVGCalc";
                return redirectUrl;
            }
        } else {
            fractalDTO = fractalCoreClient.queueImprovedPSVGCalculation(fractalDTO);
            screenTermInstance = fractalDTO.getFractalTermInstance();
        }

        String queued = (String) screenTermInstance.get("queued");
        if (queued.equals("Yes")) {
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Data length is more than " + FractalConstants.DATA_LIMIT + ". Your data is queued for processing check after some time.");
        } else {
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "IPSVG = " + screenTermInstance.get(IPSVGResultsMeta.IMPROVED_PSVG));
        }

        FacesContext f = FacesContext.getCurrentInstance();
        f.getExternalContext().getFlash().setKeepMessages(true);
        f.addMessage(null, message);
        String redirectUrl = "IPSVGCalcList";
        return redirectUrl;

    }

    public String getTermSlug() {
        return termSlug;
    }

    public void setTermSlug(String termSlug) {
        this.termSlug = termSlug;
    }

    public String getTermName() {
        return termName;
    }

    public void setTermName(String termName) {
        this.termName = termName;
    }

    public String getCalcType() {
        return calcType;
    }

    public void setCalcType(String calcType) {
        this.calcType = calcType;
    }

    public List<Map<String, Object>> getPsvgParamDataList() {
        return psvgParamDataList;
    }

    public void setPsvgParamDataList(List<Map<String, Object>> psvgParamDataList) {
        this.psvgParamDataList = psvgParamDataList;
    }

    public Map<String, Object> getSelectedPsvgParamData() {
        return selectedPsvgParamData;
    }

    public void setSelectedPsvgParamData(Map<String, Object> selectedPsvgParamData) {
        this.selectedPsvgParamData = selectedPsvgParamData;
    }

    public List<Map<String, Object>> getDataSeriesList() {
        return dataSeriesList;
    }

    public void setDataSeriesList(List<Map<String, Object>> dataSeriesList) {
        this.dataSeriesList = dataSeriesList;
    }

    public Map<String, Object> getSelectedDataSeries() {
        return selectedDataSeries;
    }

    public void setSelectedDataSeries(Map<String, Object> selectedDataSeries) {
        this.selectedDataSeries = selectedDataSeries;
    }

    public Map<String, String> getPsvgParamFieldLabels() {
        return psvgParamFieldLabels;
    }

    public void setPsvgParamFieldLabels(Map<String, String> psvgParamFieldLabels) {
        this.psvgParamFieldLabels = psvgParamFieldLabels;
    }

    public Map<String, String> getDataSeriesFieldsLabel() {
        return dataSeriesFieldsLabel;
    }

    public void setDataSeriesFieldsLabel(Map<String, String> dataSeriesFieldsLabel) {
        this.dataSeriesFieldsLabel = dataSeriesFieldsLabel;
    }

    public Map<String, Object> getScreenTermInstance() {
        return screenTermInstance;
    }

    public void setScreenTermInstance(Map<String, Object> screenTermInstance) {
        this.screenTermInstance = screenTermInstance;
    }

}
