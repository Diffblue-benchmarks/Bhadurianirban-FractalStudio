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
import org.dgrf.cms.ui.login.CMSClientAuthCredentialValue;

/**
 *
 * @author dgrfv
 */
@Named(value = "psvgResultDetails")
@ViewScoped
public class PsvgResultDetails implements Serializable{
    private String termSlug;
    private String termName;
    private String termInstanceSlug;
    private Map<String, Object> psvgResultInstance;
    
    /**
     * 
     */
    private List<PSVGResultDTO> psvgResultsList;
    /**
     * Creates a new instance of PsvgResultDetails
     */
    public PsvgResultDetails() {
    }
    public void getPsvgResultData () {
        CMSClientService mts = new CMSClientService();

        TermDTO termDTO = new TermDTO();
        termDTO.setAuthCredentials(CMSClientAuthCredentialValue.AUTH_CREDENTIALS);
        termDTO.setTermSlug(termSlug);
        termDTO = mts.getTermDetails(termDTO);
        termName = (String) termDTO.getTermDetails().get(CMSConstants.TERM_NAME);
        
        TermInstanceDTO termInstanceDTO = new TermInstanceDTO();
termInstanceDTO.setAuthCredentials(CMSClientAuthCredentialValue.AUTH_CREDENTIALS);
        
        termInstanceDTO.setTermSlug(termSlug);
        termInstanceDTO.setTermInstanceSlug(termInstanceSlug);
        
        termInstanceDTO = mts.getTermInstance(termInstanceDTO);
        
        psvgResultInstance = termInstanceDTO.getTermInstance();
        
        FractalDTO fractalDTO = new FractalDTO();
        fractalDTO.setAuthCredentials(CMSClientAuthCredentialValue.AUTH_CREDENTIALS);
        fractalDTO.setFractalTermInstance(psvgResultInstance);
        FractalCoreClient fractalCoreClient = new FractalCoreClient();
        
        fractalDTO = fractalCoreClient.getPsvgResults(fractalDTO);
        psvgResultsList = fractalDTO.getPsvgResultDTOs();
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

    public List<PSVGResultDTO> getPsvgResultsList() {
        return psvgResultsList;
    }

    public void setPsvgResultsList(List<PSVGResultDTO> psvgResultsList) {
        this.psvgResultsList = psvgResultsList;
    }

    
}
