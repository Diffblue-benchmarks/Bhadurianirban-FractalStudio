/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dgrf.fractal.ui.graph;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import org.dgrf.cms.constants.CMSConstants;
import org.dgrf.cms.core.driver.CMSClientService;
import org.dgrf.cms.dto.TermDTO;
import org.dgrf.cms.dto.TermInstanceDTO;
import org.dgrf.cms.dto.TermMetaDTO;
import org.dgrf.cms.ui.login.CMSClientAuthCredentialValue;
import org.dgrf.cms.ui.terminstance.TermInstanceUtil;
import org.dgrf.cms.ui.terminstance.TermMetaKeyLabels;
import org.dgrf.fractal.constants.FractalConstants;
import org.dgrf.fractal.core.client.FractalCoreClient;
import org.dgrf.fractal.core.dto.FractalDTO;
import org.dgrf.fractal.response.FractalResponseCode;
import org.dgrf.fractal.termmeta.GraphMeta;
import org.dgrf.fractal.termmeta.PSVGResultsMeta;

/**
 *
 * @author dgrfi
 */
@Named(value = "importEdgeListFromVG")
@ViewScoped
public class ImportEdgeListFromVG implements Serializable {

    private String termSlug;
    private List<Map<String, Object>> screenTermInstanceList;
    private boolean metaDoesNotExistForTerm;
    private List<TermMetaKeyLabels> instanceMetaKeys;
    private String termName;
    private Map<String, Object> selectedVGGraph;
    private String graphTermSlug = FractalConstants.TERM_SLUG_GRAPH;
    private String graphName;
    private String edgeLengthType;

    /**
     * Creates a new instance of ImportEdgeListFromVG
     */
    public ImportEdgeListFromVG() {
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

        //populate data from grid
        termInstanceDTO.setTermSlug(termSlug);
        termInstanceDTO = mts.getTermInstanceList(termInstanceDTO);
        List<Map<String, Object>> tempScreenTermInstanceList = termInstanceDTO.getTermInstanceList();
        screenTermInstanceList = new ArrayList<>();
        for (int i = 0; i < tempScreenTermInstanceList.size(); i++) {
            Map<String, Object> tempScreenTermInstance = tempScreenTermInstanceList.get(i);
            if (((String) tempScreenTermInstance.get(PSVGResultsMeta.QUEUED)).equals("No")) {
                screenTermInstanceList.add(tempScreenTermInstance);
            }
        }

        metaDoesNotExistForTerm = !termScreenFields.isEmpty();
        instanceMetaKeys = TermInstanceUtil.prepareMetaKeyList(termScreenFields);

    }

    public String importVGIntoGraph() {
        FacesMessage message;
        if (selectedVGGraph == null) {
            message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Paramenter required.", "Paramenter required.");
            FacesContext f = FacesContext.getCurrentInstance();
            f.getExternalContext().getFlash().setKeepMessages(true);
            f.addMessage(null, message);
            String redirectUrl = "VGGraphList";
            return redirectUrl;
        }
        String importFromVGSlug = (String) selectedVGGraph.get(CMSConstants.TERM_INSTANCE_SLUG);
        FractalDTO fractalDTO = new FractalDTO();
        fractalDTO.setAuthCredentials(CMSClientAuthCredentialValue.AUTH_CREDENTIALS);
        fractalDTO.setImportFromVGInstanceSlug(importFromVGSlug);
        Map<String, Object> graphTermInstance = new HashMap<>();
        graphTermInstance.put(GraphMeta.NAME, graphName);
        graphTermInstance.put("edgeLengthTypeForImport", edgeLengthType);
        fractalDTO.setFractalTermInstance(graphTermInstance);
        FractalCoreClient fractalCoreClient = new FractalCoreClient();
        fractalDTO = fractalCoreClient.importPSVGGraph(fractalDTO);
        if (fractalDTO.getResponseCode() != FractalResponseCode.SUCCESS) {
            message = new FacesMessage(FacesMessage.SEVERITY_FATAL, "Something wrong.", "Contact Admin.");
            FacesContext f = FacesContext.getCurrentInstance();
            f.getExternalContext().getFlash().setKeepMessages(true);
            f.addMessage(null, message);
            String redirectUrl = "VGGraphList";
            return redirectUrl;
        } else {
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Success","Graph Imported.");
            FacesContext f = FacesContext.getCurrentInstance();
            f.getExternalContext().getFlash().setKeepMessages(true);
            f.addMessage(null, message);
            String redirectUrl = "EdgeListList";
            return redirectUrl;
        }

    }

    public String getGraphName() {
        return graphName;
    }

    public void setGraphName(String graphName) {
        this.graphName = graphName;
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

    public Map<String, Object> getSelectedVGGraph() {
        return selectedVGGraph;
    }

    public void setSelectedVGGraph(Map<String, Object> selectedVGGraph) {
        this.selectedVGGraph = selectedVGGraph;
    }

    public String getGraphTermSlug() {
        return graphTermSlug;
    }

    public void setGraphTermSlug(String graphTermSlug) {
        this.graphTermSlug = graphTermSlug;
    }

    public String getEdgeLengthType() {
        return edgeLengthType;
    }

    public void setEdgeLengthType(String edgeLengthType) {
        this.edgeLengthType = edgeLengthType;
    }

}
