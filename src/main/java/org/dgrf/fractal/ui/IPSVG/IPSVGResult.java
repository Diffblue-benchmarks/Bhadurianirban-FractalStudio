/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dgrf.fractal.ui.IPSVG;

import java.io.Serializable;
import java.util.Map;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import org.dgrf.cms.constants.CMSConstants;
import org.dgrf.cms.core.driver.CMSClientService;
import org.dgrf.cms.dto.TermDTO;
import org.dgrf.cms.dto.TermInstanceDTO;
import org.dgrf.cms.dto.TermMetaDTO;

import org.dgrf.cms.ui.login.CMSClientAuthCredentialValue;

/**
 *
 * @author bhaduri
 */
@Named(value = "iPSVGResult")
@ViewScoped
public class IPSVGResult implements Serializable {

    private String termSlug;
    private String termName;
    private String termInstanceSlug;
    private Map<String, Object> ipsvgResultInstance;
    private Map<String, String> termScreenFieldsDesc;

    /**
     * Creates a new instance of IPSVGResult
     */
    public IPSVGResult() {
    }

    public void getIPsvgResultData() {
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
        //get ipsvg field labels psvg
        
        TermMetaDTO termMetaDTO = new TermMetaDTO();
        termMetaDTO.setAuthCredentials(CMSClientAuthCredentialValue.AUTH_CREDENTIALS);
        termMetaDTO.setTermSlug(termSlug);
        termMetaDTO = cmscs.getTermMetaList(termMetaDTO);
        termScreenFieldsDesc = termMetaDTO.getTermMetaFieldLabels();

    }

//    public String goToViewPSVGResultList() {
//        return "/IPSVG/IPSVGResultDetails?faces-redirect=true" + "&termslug=" + termSlug + "&terminstanceslug=" + termInstanceSlug;
//    }
//
//    public String goToViewPSVGResultChart() {
//        return "/IPSVG/IPSVGResultChart?faces-redirect=true" + "&termslug=" + termSlug + "&terminstanceslug=" + termInstanceSlug;
//    }
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

    public Map<String, String> getTermScreenFieldsDesc() {
        return termScreenFieldsDesc;
    }

    public void setTermScreenFieldsDesc(Map<String, String> termScreenFieldsDesc) {
        this.termScreenFieldsDesc = termScreenFieldsDesc;
    }

}
