/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dgrf.cms.ui.media;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import org.dgrf.cloud.response.DGRFResponseMessage;
import org.dgrf.cms.core.driver.CMSClientService;
import org.dgrf.cms.constants.CMSConstants;
import org.dgrf.cms.dto.TermDTO;
import org.dgrf.cms.dto.TermInstanceDTO;
import org.dgrf.cms.constants.MediaMeta;
import org.dgrf.cms.dto.TermMetaDTO;
import org.dgrf.cms.ui.login.CMSClientAuthCredentialValue;
import org.primefaces.extensions.model.fluidgrid.FluidGridItem;

/**
 *
 * @author dgrf-iv
 */
@Named(value = "mediaEdit")
@ViewScoped
public class MediaEdit implements Serializable {

    private String termSlug;
    private String termName;
    private String termInstanceSlug;
    
    private Map<String, Object> screenTermInstance;
    Map<String, String> termScreenFieldLabels;

    public MediaEdit() {
    }

    public void createMetaDataEditForm() {
        CMSClientService mts = new CMSClientService();

        TermDTO termDTO = new TermDTO();
        termDTO.setAuthCredentials(CMSClientAuthCredentialValue.AUTH_CREDENTIALS);
        termDTO.setTermSlug(termSlug);
        termDTO = mts.getTermDetails(termDTO);
        termName = (String)termDTO.getTermDetails().get(CMSConstants.TERM_NAME);
        
        //Get screen data
        TermInstanceDTO termInstanceDTO = new TermInstanceDTO();
        termInstanceDTO.setAuthCredentials(CMSClientAuthCredentialValue.AUTH_CREDENTIALS);
        
        termInstanceDTO.setTermSlug(termSlug);
        termInstanceDTO.setTermInstanceSlug(termInstanceSlug);
        termInstanceDTO = mts.getTermInstance(termInstanceDTO);
        screenTermInstance = termInstanceDTO.getTermInstance();
        
        //Get screen fields
        TermMetaDTO termMetaDTO = new TermMetaDTO();
        termMetaDTO.setAuthCredentials(CMSClientAuthCredentialValue.AUTH_CREDENTIALS);
        termMetaDTO.setTermSlug(termSlug);
        termMetaDTO = mts.getTermMetaList(termMetaDTO);
        //termScreenFields = termMetaDTO.getTermMetaFields();
        termScreenFieldLabels = termMetaDTO.getTermMetaFieldLabels();
        
    }

    public String editTermInstance() {
        FacesMessage message;
        String termMetaKey;
        CMSClientService mts = new CMSClientService();

            
            


        TermInstanceDTO termInstanceDTO = new TermInstanceDTO();
        termInstanceDTO.setAuthCredentials(CMSClientAuthCredentialValue.AUTH_CREDENTIALS);
        termInstanceDTO.setTermInstance(screenTermInstance);
        termInstanceDTO = mts.saveTermInstance(termInstanceDTO);
        if (termInstanceDTO.getResponseCode() != 0) {
            String redirectUrl = "MediaEdit";
            DGRFResponseMessage responseMessage = new DGRFResponseMessage();
            message = new FacesMessage(FacesMessage.SEVERITY_FATAL, "Error", responseMessage.getResponseMessage(termInstanceDTO.getResponseCode()));
            FacesContext f = FacesContext.getCurrentInstance();
            f.getExternalContext().getFlash().setKeepMessages(true);
            f.addMessage(null, message);
            return redirectUrl;
        } else {
            String redirectUrl = "MediaList";
            DGRFResponseMessage responseMessage = new DGRFResponseMessage();
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", responseMessage.getResponseMessage(termInstanceDTO.getResponseCode()));
            FacesContext f = FacesContext.getCurrentInstance();
            f.getExternalContext().getFlash().setKeepMessages(true);
            f.addMessage(null, message);
            return redirectUrl;
        }
    }

    public String getTermSlug() {
        return termSlug;
    }

    public void setTermSlug(String termSlug) {
        this.termSlug = termSlug;
    }

    public String getTermInstanceSlug() {
        return termInstanceSlug;
    }

    public void setTermInstanceSlug(String termInstanceSlug) {
        this.termInstanceSlug = termInstanceSlug;
    }

    public Map<String, Object> getScreenTermInstance() {
        return screenTermInstance;
    }

    public void setScreenTermInstance(Map<String, Object> screenTermInstance) {
        this.screenTermInstance = screenTermInstance;
    }

    public Map<String, String> getTermScreenFieldLabels() {
        return termScreenFieldLabels;
    }

    public void setTermScreenFieldLabels(Map<String, String> termScreenFieldLabels) {
        this.termScreenFieldLabels = termScreenFieldLabels;
    }


    public String getTermName() {
        return termName;
    }

    public void setTermName(String termName) {
        this.termName = termName;
    }

}
