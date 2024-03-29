/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dgrf.cms.ui.media;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import org.dgrf.cloud.response.DGRFResponseCode;
import org.dgrf.cloud.response.DGRFResponseMessage;

import org.dgrf.cms.core.driver.CMSClientService;
import org.dgrf.cms.constants.CMSConstants;
import org.dgrf.cms.dto.MediaDTO;

import org.dgrf.cms.dto.TermDTO;
import org.dgrf.cms.dto.TermInstanceDTO;
import org.dgrf.cms.dto.TermMetaDTO;
import org.dgrf.cms.ui.login.CMSClientAuthCredentialValue;

/**
 *
 * @author bhaduri
 */
@Named(value = "mediaList")
@ViewScoped
public class MediaList implements Serializable {

    private String termSlug;
    private List<Map<String, Object>> screenTermInstanceList;
    private boolean showGrid;
    private String noShowGridReason;

    private String termName;
    private Map<String, Object> selectedMetaData;
    private Map<String, String> termScreenFieldLabels;

    public MediaList() {
    }

    @PostConstruct
    public void init() {

    }

    public void fillTermMetaData() {
        showGrid = true;
        CMSClientService mts = new CMSClientService();
        TermDTO termDTO = new TermDTO();
        termDTO.setAuthCredentials(CMSClientAuthCredentialValue.AUTH_CREDENTIALS);
        termDTO.setTermSlug(termSlug);
        termDTO = mts.getTermDetails(termDTO);
        termName = (String) termDTO.getTermDetails().get(CMSConstants.TERM_NAME);
        //check aws credentials present
        TermInstanceDTO termInstanceDTO = new TermInstanceDTO();
        termInstanceDTO.setAuthCredentials(CMSClientAuthCredentialValue.AUTH_CREDENTIALS);
        termInstanceDTO.setTermSlug(CMSConstants.AWS_CRED_TERM_SLUG);
        termInstanceDTO.setTermInstanceSlug("awsdefault");
        termInstanceDTO = mts.getTermInstance(termInstanceDTO);
        if (termInstanceDTO.getResponseCode()!= DGRFResponseCode.SUCCESS) {
            showGrid = false;
            noShowGridReason = "AWS Credentials are not updated. Please enter your AWS Credentials and bucket name with a term instance slug of 'awsdefault'";
        }
        
        //Creation of grid
        if (showGrid) {
            TermMetaDTO termMetaDTO = new TermMetaDTO();
            termMetaDTO.setAuthCredentials(CMSClientAuthCredentialValue.AUTH_CREDENTIALS);
            termMetaDTO.setTermSlug(termSlug);
            termMetaDTO = mts.getTermMetaList(termMetaDTO);
            List<Map<String, Object>> termScreenFields = termMetaDTO.getTermMetaFields();

            termScreenFieldLabels = termMetaDTO.getTermMetaFieldLabels();
            //get instance data

            termInstanceDTO.setAuthCredentials(CMSClientAuthCredentialValue.AUTH_CREDENTIALS);
            termInstanceDTO.setTermSlug(termSlug);
            termInstanceDTO = mts.getTermInstanceList(termInstanceDTO);
            screenTermInstanceList = termInstanceDTO.getTermInstanceList();
        }

    }

    public String goToEditTermInstance() {
        return "MediaEdit";
    }

    public String deleteTermMetaData() {
        FacesMessage message;
        CMSClientService mts = new CMSClientService();
        MediaDTO mediaDTO = new MediaDTO();
        mediaDTO.setAuthCredentials(CMSClientAuthCredentialValue.AUTH_CREDENTIALS);
        mediaDTO.setMediaTermInstance(selectedMetaData);
        

        mediaDTO = mts.deleteMediaTermInstance(mediaDTO);
        if (mediaDTO.getResponseCode() == DGRFResponseCode.SUCCESS) {
            DGRFResponseMessage responseMessage = new DGRFResponseMessage();
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", responseMessage.getResponseMessage(mediaDTO.getResponseCode()));
            FacesContext f = FacesContext.getCurrentInstance();
            f.getExternalContext().getFlash().setKeepMessages(true);
            f.addMessage(null, message);
        } else {
            DGRFResponseMessage responseMessage = new DGRFResponseMessage();
            message = new FacesMessage(FacesMessage.SEVERITY_FATAL, "Error", responseMessage.getResponseMessage(mediaDTO.getResponseCode()));
            FacesContext f = FacesContext.getCurrentInstance();
            f.getExternalContext().getFlash().setKeepMessages(true);
            f.addMessage(null, message);
        }

        return "MediaList";
    }

    public String refreshGrid() {
        return "/Media/MediaList?faces-redirect=true" + "&termslug=" + termSlug;
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

    public String getTermName() {
        return termName;
    }

    public void setTermName(String termName) {
        this.termName = termName;
    }

    public Map<String, Object> getSelectedMetaData() {
        return selectedMetaData;
    }

    public void setSelectedMetaData(Map<String, Object> selectedMetaData) {
        this.selectedMetaData = selectedMetaData;
    }

    public boolean isShowGrid() {
        return showGrid;
    }

    public void setShowGrid(boolean showGrid) {
        this.showGrid = showGrid;
    }

    public String getNoShowGridReason() {
        return noShowGridReason;
    }

    public void setNoShowGridReason(String noShowGridReason) {
        this.noShowGridReason = noShowGridReason;
    }

    public Map<String, String> getTermScreenFieldLabels() {
        return termScreenFieldLabels;
    }

    public void setTermScreenFieldLabels(Map<String, String> termScreenFieldLabels) {
        this.termScreenFieldLabels = termScreenFieldLabels;
    }

}
