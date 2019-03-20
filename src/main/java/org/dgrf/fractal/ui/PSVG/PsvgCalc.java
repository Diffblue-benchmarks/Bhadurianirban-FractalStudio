/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dgrf.fractal.ui.PSVG;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import org.dgrf.fractal.response.FractalResponseCode;
import org.dgrf.cms.core.driver.CMSClientService;
import org.dgrf.cms.constants.CMSConstants;
import org.dgrf.cms.dto.TermDTO;
import org.dgrf.cms.dto.TermInstanceDTO;
import org.dgrf.cms.dto.TermMetaDTO;
import org.dgrf.fractal.core.client.FractalCoreClient;
import org.dgrf.fractal.termmeta.DataSeriesMeta;
import org.dgrf.fractal.constants.FractalConstants;
import org.dgrf.fractal.core.dto.FractalDTO;
import org.dgrf.fractal.termmeta.PSVGResultsMeta;
import org.dgrf.cms.ui.login.CMSClientAuthCredentialValue;

/**
 *
 * @author bhaduri
 */
@Named(value = "psvgCalc")
@ViewScoped
public class PsvgCalc implements Serializable {

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
     * Creates a new instance of PsvgCalc
     */
    public PsvgCalc() {
    }

    public void creteTermForm() {
        CMSClientService mts = new CMSClientService();

        TermDTO termDTO = new TermDTO();
        termDTO.setAuthCredentials(CMSClientAuthCredentialValue.AUTH_CREDENTIALS);
        termDTO.setTermSlug(termSlug);
        termDTO = mts.getTermDetails(termDTO);

        termName = (String) termDTO.getTermDetails().get(CMSConstants.TERM_NAME);
        //creation of grid
        TermMetaDTO termMetaDTO = new TermMetaDTO();
        termMetaDTO.setAuthCredentials(CMSClientAuthCredentialValue.AUTH_CREDENTIALS);
        termMetaDTO.setTermSlug(FractalConstants.TERM_SLUG_PSVG_PARAM);
        termMetaDTO = mts.getTermMetaList(termMetaDTO);
        psvgParamFieldLabels = termMetaDTO.getTermMetaFieldLabels();

        TermInstanceDTO termInstanceDTO = new TermInstanceDTO();
        termInstanceDTO.setAuthCredentials(CMSClientAuthCredentialValue.AUTH_CREDENTIALS);

        termInstanceDTO.setTermSlug(FractalConstants.TERM_SLUG_PSVG_PARAM);

        termInstanceDTO = mts.getTermInstanceList(termInstanceDTO);
        psvgParamDataList = termInstanceDTO.getTermInstanceList();

        //creation of grid
        termMetaDTO.setAuthCredentials(CMSClientAuthCredentialValue.AUTH_CREDENTIALS);
        termMetaDTO.setTermSlug(FractalConstants.TERM_SLUG_DATASERIES);
        termMetaDTO = mts.getTermMetaList(termMetaDTO);

        dataSeriesFieldsLabel = termMetaDTO.getTermMetaFieldLabels();

        termInstanceDTO.setTermSlug(FractalConstants.TERM_SLUG_DATASERIES);

        termInstanceDTO = mts.getTermInstanceList(termInstanceDTO);
        if (termInstanceDTO.getResponseCode() == FractalResponseCode.SUCCESS) {
            List<Map<String, Object>> dataSeriesListAll = termInstanceDTO.getTermInstanceList();
            if (calcType.equals(FractalConstants.PSVG_CALC_TYPE_XY)) {
                dataSeriesList = dataSeriesListAll.stream().filter(ds -> ds.get(DataSeriesMeta.DATA_SERIES_TYPE).equals(DataSeriesMeta.DATA_SERIES_XY)).collect(Collectors.toList());
            } else {
                dataSeriesList = dataSeriesListAll.stream().filter(ds -> ds.get(DataSeriesMeta.DATA_SERIES_TYPE).equals(DataSeriesMeta.DATA_SERIES_UNIFORM)).collect(Collectors.toList());
            }
        }

    }

    public String startCalc() {

        FacesMessage message;
        if (selectedPsvgParamData == null) {
            message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Paramenter required.", "Paramenter required.");
            FacesContext f = FacesContext.getCurrentInstance();
            f.getExternalContext().getFlash().setKeepMessages(true);
            f.addMessage(null, message);
            String redirectUrl = "PSVGCalc";
            return redirectUrl;
        }
        if (selectedDataSeries == null) {
            message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Data required.", "Data required.");
            FacesContext f = FacesContext.getCurrentInstance();
            f.getExternalContext().getFlash().setKeepMessages(true);
            f.addMessage(null, message);
            String redirectUrl = "PSVGCalc";
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
        fractalDTO.setCalcType(calcType);

        Long dataCount = Long.parseLong((String) selectedDataSeries.get(DataSeriesMeta.DATA_SERIES_LENGTH));
        FractalConstants.TEMP_FILE_PATH = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/");
        if (dataCount < FractalConstants.DATA_LIMIT) {
            fractalDTO = fractalCoreClient.calculatePSVG(fractalDTO);
            screenTermInstance = fractalDTO.getFractalTermInstance();
            if (screenTermInstance == null) {
                message = new FacesMessage(FacesMessage.SEVERITY_FATAL, "Something wrong.", "Contact Admin.");
                FacesContext f = FacesContext.getCurrentInstance();
                f.getExternalContext().getFlash().setKeepMessages(true);
                f.addMessage(null, message);
                String redirectUrl = "PSVGCalc";
                return redirectUrl;
            }
        } else {
            fractalDTO = fractalCoreClient.queuePSVGCalculation(fractalDTO);
            screenTermInstance = fractalDTO.getFractalTermInstance();
        }

        String queued = (String) screenTermInstance.get("queued");
        if (queued.equals("Yes")) {
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Data length is more than " + FractalConstants.DATA_LIMIT + ". Your data is queued for processing check after some time.");
        } else {
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", (String) screenTermInstance.get(PSVGResultsMeta.FRACTAL_DIMENSION));
        }

        FacesContext f = FacesContext.getCurrentInstance();
        f.getExternalContext().getFlash().setKeepMessages(true);
        f.addMessage(null, message);
        String redirectUrl = "PSVGCalcList";
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

    public Map<String, Object> getScreenTermInstance() {
        return screenTermInstance;
    }

    public void setScreenTermInstance(Map<String, Object> screenTermInstance) {
        this.screenTermInstance = screenTermInstance;
    }

    public List<Map<String, Object>> getPsvgParamDataList() {
        return psvgParamDataList;
    }

    public void setPsvgParamDataList(List<Map<String, Object>> psvgParamDataList) {
        this.psvgParamDataList = psvgParamDataList;
    }

    public List<Map<String, Object>> getDataSeriesList() {
        return dataSeriesList;
    }

    public void setDataSeriesList(List<Map<String, Object>> dataSeriesList) {
        this.dataSeriesList = dataSeriesList;
    }

    public Map<String, Object> getSelectedPsvgParamData() {
        return selectedPsvgParamData;
    }

    public void setSelectedPsvgParamData(Map<String, Object> selectedPsvgParamData) {
        this.selectedPsvgParamData = selectedPsvgParamData;
    }

    public Map<String, Object> getSelectedDataSeries() {
        return selectedDataSeries;
    }

    public void setSelectedDataSeries(Map<String, Object> selectedDataSeries) {
        this.selectedDataSeries = selectedDataSeries;
    }

    public String getCalcType() {
        return calcType;
    }

    public void setCalcType(String calcType) {
        this.calcType = calcType;
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

}
