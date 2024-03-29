/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dgrf.fractal.ui.PSVG;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import org.dgrf.cms.core.driver.CMSClientService;
import org.dgrf.cms.constants.CMSConstants;
import org.dgrf.cms.dto.TermDTO;
import org.dgrf.cms.dto.TermInstanceDTO;
import org.dgrf.fractal.core.client.FractalCoreClient;
import org.dgrf.fractal.core.dto.FractalDTO;
import org.dgrf.fractal.core.dto.PSVGResultDTO;
import org.dgrf.fractal.termmeta.PSVGResultsMeta;
import org.dgrf.cms.ui.login.CMSClientAuthCredentialValue;
import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.LineChartModel;
import org.primefaces.model.chart.LineChartSeries;

/**
 *
 * @author bhaduri
 */
@Named(value = "psvgResultChart")
@ViewScoped
public class PsvgResultChart implements Serializable {

    private String termSlug;
    private String termName;
    private String termInstanceSlug;
    private Map<String, Object> psvgResultInstance;
    private LineChartModel dataSeriesPlotModel;

    /**
     * Creates a new instance of PsvgResultChart
     */
    public PsvgResultChart() {
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
        
        psvgResultInstance = termInstanceDTO.getTermInstance();
        
        FractalDTO fractalDTO = new FractalDTO();
        fractalDTO.setAuthCredentials(CMSClientAuthCredentialValue.AUTH_CREDENTIALS);
        fractalDTO.setFractalTermInstance(psvgResultInstance);
        
        FractalCoreClient ipsvgcalcClient = new FractalCoreClient();
        fractalDTO = ipsvgcalcClient.getPsvgResults(fractalDTO);
        
        List<PSVGResultDTO> psvgResultsList = fractalDTO.getPsvgResultDTOs();
        dataSeriesPlotModel = new LineChartModel();
        dataSeriesPlotModel.setSeriesColors("ff0000,000000,0000ff");
        String PSVGFractalDimension = (String) psvgResultInstance.get(PSVGResultsMeta.FRACTAL_DIMENSION);
        String PSVGIntercept = (String) psvgResultInstance.get(PSVGResultsMeta.INTERCEPT);

        LineChartSeries PSVGXYScatterRequired = new LineChartSeries();
        LineChartSeries PSVGXYScatterOutlier = new LineChartSeries();
        LineChartSeries PSVGXYTrendLine = new LineChartSeries();

        PSVGXYScatterRequired.setShowLine(false);
        PSVGXYScatterRequired.setMarkerStyle("filledCircle', size:'3.0', color:'#000000");

        PSVGXYScatterOutlier.setShowLine(false);
        PSVGXYScatterOutlier.setMarkerStyle("filledCircle', size:'3.0', color:'#ff0000");

        PSVGXYTrendLine.setShowMarker(false);

        Double trendY, trendX;
        for (PSVGResultDTO psvgresult : psvgResultsList) {
            if (psvgresult.getRequired() == 1) {
                PSVGXYScatterRequired.set(psvgresult.getLogofdegreeval(), psvgresult.getLogofprobofdegreeval());
                trendX = psvgresult.getLogofdegreeval();
                trendY = Double.parseDouble(PSVGFractalDimension) * trendX + Double.parseDouble(PSVGIntercept);
                PSVGXYTrendLine.set(trendX, trendY);

            } else {
                PSVGXYScatterOutlier.set(psvgresult.getLogofdegreeval(), psvgresult.getLogofprobofdegreeval());
            }
        }
        PSVGXYScatterRequired.setLabel("Required");
        PSVGXYScatterOutlier.setLabel("Outliers");
        
        String trendLabelText = "  y = "+PSVGFractalDimension + "x + " + PSVGIntercept;
        String titleLabelText = "  Fractal Dimension : "+ PSVGFractalDimension + " Intercept : " + PSVGIntercept;
        PSVGXYTrendLine.setLabel(trendLabelText);
        Axis xAxis = dataSeriesPlotModel.getAxis(AxisType.X);
        Axis yAxis = dataSeriesPlotModel.getAxis(AxisType.Y);
        xAxis.setLabel("log(1/k)");
        yAxis.setLabel("log(P(k))");
        dataSeriesPlotModel.setLegendPosition("s");
        dataSeriesPlotModel.addSeries(PSVGXYScatterOutlier);
        dataSeriesPlotModel.addSeries(PSVGXYScatterRequired);
        dataSeriesPlotModel.addSeries(PSVGXYTrendLine);
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

    public Map<String, Object> getPsvgResultInstance() {
        return psvgResultInstance;
    }

    public void setPsvgResultInstance(Map<String, Object> psvgResultInstance) {
        this.psvgResultInstance = psvgResultInstance;
    }

    public LineChartModel getDataSeriesPlotModel() {
        return dataSeriesPlotModel;
    }

    public void setDataSeriesPlotModel(LineChartModel dataSeriesPlotModel) {
        this.dataSeriesPlotModel = dataSeriesPlotModel;
    }

}
