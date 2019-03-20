/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dgrf.fractal.ui.PSVG;

import java.io.Serializable;
import java.util.Map;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import org.dgrf.cms.core.driver.CMSClientService;
import org.dgrf.cms.constants.CMSConstants;
import org.dgrf.cms.dto.TermDTO;
import org.dgrf.cms.dto.TermInstanceDTO;
import org.dgrf.cms.dto.TermMetaDTO;
import org.dgrf.cms.ui.login.CMSClientAuthCredentialValue;

/**
 *
 * @author dgrfv
 */
@Named(value = "psvgResult")
@ViewScoped
public class PsvgResult implements Serializable {

    /**
     * Creates a new instance of PsvgResult
     */
    private String termSlug;
    private String termName;
    private String termInstanceSlug;
    private Map<String, Object> psvgResultInstance;
    private Map<String, String> termScreenFieldsDesc;

    public PsvgResult() {
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
        //creation of grid
        CMSClientService mts = new CMSClientService();
        TermMetaDTO termMetaDTO = new TermMetaDTO();
        termMetaDTO.setAuthCredentials(CMSClientAuthCredentialValue.AUTH_CREDENTIALS);
        termMetaDTO.setTermSlug(termSlug);
        termMetaDTO = mts.getTermMetaList(termMetaDTO);
        termScreenFieldsDesc = termMetaDTO.getTermMetaFieldLabels();

    }

    public String goToViewPSVGResultList() {
        return "/PSVG/PSVGResultDetails?faces-redirect=true" + "&termslug=" + termSlug + "&terminstanceslug=" + termInstanceSlug;
    }
    public String goToViewPSVGResultChart() {
        return "/PSVG/PSVGResultChart?faces-redirect=true" + "&termslug=" + termSlug + "&terminstanceslug=" + termInstanceSlug;
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

    public Map<String, String> getTermScreenFieldsDesc() {
        return termScreenFieldsDesc;
    }

    public void setTermScreenFieldsDesc(Map<String, String> termScreenFieldsDesc) {
        this.termScreenFieldsDesc = termScreenFieldsDesc;
    }

}
