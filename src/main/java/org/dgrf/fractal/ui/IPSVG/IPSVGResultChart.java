/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dgrf.fractal.ui.IPSVG;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import org.dgrf.cms.constants.CMSConstants;
import org.dgrf.cms.core.driver.CMSClientService;
import org.dgrf.cms.dto.TermDTO;
import org.dgrf.cms.dto.TermInstanceDTO;
import org.dgrf.fractal.core.client.FractalCoreClient;
import org.dgrf.fractal.core.dto.FractalDTO;
import org.dgrf.fractal.termmeta.IPSVGResultsMeta;
import org.dgrf.fractal.core.dto.IpsvgResultsDTO;
import org.dgrf.cms.ui.login.CMSClientAuthCredentialValue;
import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.LineChartModel;
import org.primefaces.model.chart.LineChartSeries;

/**
 *
 * @author bhaduri
 */
@Named(value = "iPSVGResultChart")
@ViewScoped
public class IPSVGResultChart implements Serializable {

    private String termSlug;
    private String termName;
    private String termInstanceSlug;
    private Map<String, Object> ipsvgResultInstance;
    private LineChartModel dataSeriesPlotModel;

    /**
     * Creates a new instance of IPSVGResultChart
     */
    public IPSVGResultChart() {
    }

    public void getPsvgResultData() {
        CMSClientService cmscs = new CMSClientService();

        TermDTO termDTO = new TermDTO();
        termDTO.setAuthCredentials(CMSClientAuthCredentialValue.AUTH_CREDENTIALS);
        termDTO.setTermSlug(termSlug);
        termDTO = cmscs.getTermDetails(termDTO);
        termName = (String) termDTO.getTermDetails().get(CMSConstants.TERM_NAME);

        TermInstanceDTO termInstanceDTO = new TermInstanceDTO();
        termInstanceDTO.setAuthCredentials(CMSClientAuthCredentialValue.AUTH_CREDENTIALS);

        termInstanceDTO.setTermSlug(termSlug);
        termInstanceDTO.setTermInstanceSlug(termInstanceSlug);
        termInstanceDTO = cmscs.getTermInstance(termInstanceDTO);

        ipsvgResultInstance = termInstanceDTO.getTermInstance();

        FractalDTO fractalDTO = new FractalDTO();
        fractalDTO.setAuthCredentials(CMSClientAuthCredentialValue.AUTH_CREDENTIALS);
        fractalDTO.setFractalTermInstance(ipsvgResultInstance);

        FractalCoreClient ipsvgcalcClient = new FractalCoreClient();
        fractalDTO = ipsvgcalcClient.getIpsvgResults(fractalDTO);

        List<IpsvgResultsDTO> ipsvgResultsList = fractalDTO.getIpsvgResultsDTOs();
        dataSeriesPlotModel = new LineChartModel();
        dataSeriesPlotModel.setSeriesColors("000000,0000ff");
        String ipsvg = (String) ipsvgResultInstance.get(IPSVGResultsMeta.IMPROVED_PSVG);

        Double ipsvgDouble = Double.parseDouble(ipsvg);

        LineChartSeries IPSVGValues = new LineChartSeries();
        LineChartSeries IPSVGMean = new LineChartSeries();

        IPSVGValues.setShowLine(false);
        IPSVGValues.setMarkerStyle("filledCircle', size:'3.0");

        IPSVGMean.setShowMarker(false);
        IPSVGMean.setMarkerStyle("filledCircle', size:'3.0");

        for (IpsvgResultsDTO ipsvgresult : ipsvgResultsList) {
            IPSVGValues.set(ipsvgresult.getLengthOfGaps(), ipsvgresult.getPsvgForGaps());
        }
        IPSVGValues.setLabel("PSVG Values");
        IPSVGMean.setLabel("IPSVG Mean = " + ipsvg);

        IPSVGMean.set(ipsvgResultsList.get(0).getLengthOfGaps(), ipsvgDouble);
        IPSVGMean.set(ipsvgResultsList.get(ipsvgResultsList.size() - 1).getLengthOfGaps(), ipsvgDouble);

        String titleLabelText = "Improved PSVG";
        Axis xAxis = dataSeriesPlotModel.getAxis(AxisType.X);
        Axis yAxis = dataSeriesPlotModel.getAxis(AxisType.Y);
        xAxis.setLabel("Gaps");
        yAxis.setLabel("PSVG for Gaps");
        dataSeriesPlotModel.setLegendPosition("s");
        dataSeriesPlotModel.addSeries(IPSVGValues);
        dataSeriesPlotModel.addSeries(IPSVGMean);

        dataSeriesPlotModel.setTitle(titleLabelText);

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

    public String getTermInstanceSlug() {
        return termInstanceSlug;
    }

    public void setTermInstanceSlug(String termInstanceSlug) {
        this.termInstanceSlug = termInstanceSlug;
    }

    public Map<String, Object> getIpsvgResultInstance() {
        return ipsvgResultInstance;
    }

    public void setIpsvgResultInstance(Map<String, Object> ipsvgResultInstance) {
        this.ipsvgResultInstance = ipsvgResultInstance;
    }

    public LineChartModel getDataSeriesPlotModel() {
        return dataSeriesPlotModel;
    }

    public void setDataSeriesPlotModel(LineChartModel dataSeriesPlotModel) {
        this.dataSeriesPlotModel = dataSeriesPlotModel;
    }

}
