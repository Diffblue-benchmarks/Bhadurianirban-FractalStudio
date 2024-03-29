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
import org.dgrf.fractal.core.dto.IpsvgResultsDTO;
import org.dgrf.cms.ui.login.CMSClientAuthCredentialValue;

/**
 *
 * @author bhaduri
 */
@Named(value = "iPSVGResultDetails")
@ViewScoped
public class IPSVGResultDetails implements Serializable {

    private String termSlug;
    private String termName;
    private String termInstanceSlug;
    private Map<String, Object> ipsvgResultInstance;
    private List<IpsvgResultsDTO> IPSVGResultsList;

    /**
     * Creates a new instance of IPSVGResultDetails
     */
    public IPSVGResultDetails() {
    }
    public void getIPsvgResultData () {
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
                
        ipsvgResultInstance = termInstanceDTO.getTermInstance();
        
        FractalDTO fractalDTO = new FractalDTO();
        fractalDTO.setAuthCredentials(CMSClientAuthCredentialValue.AUTH_CREDENTIALS);
        fractalDTO.setFractalTermInstance(ipsvgResultInstance);
        FractalCoreClient fractalCoreClient = new FractalCoreClient();
        fractalDTO = fractalCoreClient.getIpsvgResults(fractalDTO);
        IPSVGResultsList = fractalDTO.getIpsvgResultsDTOs();
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

    public List<IpsvgResultsDTO> getIPSVGResultsList() {
        return IPSVGResultsList;
    }

    public void setIPSVGResultsList(List<IpsvgResultsDTO> IPSVGResultsList) {
        this.IPSVGResultsList = IPSVGResultsList;
    }
    
}
