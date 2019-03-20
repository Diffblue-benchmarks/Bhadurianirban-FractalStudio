/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dgrf.fractal.ui.dataseries;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import org.dgrf.fractal.response.FractalResponseCode;
import org.dgrf.cloud.response.DGRFResponseMessage;
import org.dgrf.cms.core.driver.CMSClientService;
import org.dgrf.cms.constants.CMSConstants;
import org.dgrf.cms.dto.TermDTO;
import org.dgrf.cms.dto.TermMetaDTO;
import org.dgrf.fractal.core.client.FractalCoreClient;
import org.dgrf.fractal.termmeta.DataSeriesMeta;
import org.dgrf.fractal.core.dto.FractalDTO;
import org.dgrf.cms.ui.login.CMSClientAuthCredentialValue;
import org.dgrf.cms.ui.login.LoginController;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

/**
 *
 * @author dgrfv
 */
@Named(value = "dataSeriesAdd")
@ViewScoped
public class DataSeriesAdd implements Serializable {

    private String termSlug;
    private String termName;
    private String tempFilePath;
    private boolean fileUploaded;
    List<Map<String, Object>> termScreenFields;
    Map<String, Object> screenTermInstance;
    @Inject
    LoginController loginController;

    /**
     * Creates a new instance of DataSeriesAdd
     */
    public DataSeriesAdd() {
        fileUploaded = false;
    }

    public void creteTermForm() {
        CMSClientService mts = new CMSClientService();

        TermDTO termDTO = new TermDTO();
        termDTO.setAuthCredentials(CMSClientAuthCredentialValue.AUTH_CREDENTIALS);
        termDTO.setTermSlug(termSlug);
        termDTO = mts.getTermDetails(termDTO);
        termName = (String) termDTO.getTermDetails().get(CMSConstants.TERM_NAME);
        
        TermMetaDTO termMetaDTO = new TermMetaDTO();
        termMetaDTO.setAuthCredentials(CMSClientAuthCredentialValue.AUTH_CREDENTIALS);
        termMetaDTO.setTermSlug(termSlug);
        termMetaDTO = mts.getTermMetaList(termMetaDTO);
        termScreenFields = termMetaDTO.getTermMetaFields();

        
        FractalCoreClient fractalCoreClient = new  FractalCoreClient();
        FractalDTO fractalDTO = new FractalDTO();
        fractalDTO.setAuthCredentials(CMSClientAuthCredentialValue.AUTH_CREDENTIALS);
        
        fractalDTO = fractalCoreClient.createDataSeriesTermInstance(fractalDTO);
        screenTermInstance = fractalDTO.getFractalTermInstance();
        
    }


    public void handleFileUpload(FileUploadEvent event) {
        UploadedFile uploadedFile = event.getFile();
        String fileName  = uploadedFile.getFileName();
        screenTermInstance.put(DataSeriesMeta.DATA_SERIES_ORIGINAL_FILENAME, fileName);
        byte[] bytes = null;

        if (null != uploadedFile) {
            bytes = uploadedFile.getContents();
            String tempPath = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/");
            
            tempFilePath = tempPath + "temp"+screenTermInstance.get("user")+".csv";
            
            FileOutputStream fo;

            try {
                File tempFile = new File(tempFilePath);
                fo = new FileOutputStream(tempFile);
                try (BufferedOutputStream stream = new BufferedOutputStream(fo)) {
                    stream.write(bytes);
                    stream.close();

                    fileUploaded = true;

                }

            } catch (FileNotFoundException ex) {
                Logger.getLogger(DataSeriesAdd.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(DataSeriesAdd.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }

    public String addTermInstance() {

        
        FractalCoreClient dss = new FractalCoreClient();
        FacesMessage message;

        if (!fileUploaded) {
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "File is mandatory.", "File is mandatory.");
            FacesContext f = FacesContext.getCurrentInstance();
            f.getExternalContext().getFlash().setKeepMessages(true);
            f.addMessage(null, message);
            String redirectUrl =  "DataSeriesAdd";
            return redirectUrl;
        }
        
        FractalDTO fractalDTO = new FractalDTO();
        fractalDTO.setAuthCredentials(CMSClientAuthCredentialValue.AUTH_CREDENTIALS);
        fractalDTO.setCsvFilePath(tempFilePath);
        fractalDTO.setFractalTermInstance(screenTermInstance);
        fractalDTO = dss.uploadDataSeries(fractalDTO);
        if (fractalDTO.getResponseCode() == FractalResponseCode.SUCCESS) {
            DGRFResponseMessage responseMessage = new DGRFResponseMessage();
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", responseMessage.getResponseMessage(fractalDTO.getResponseCode()));
            FacesContext f = FacesContext.getCurrentInstance();
            f.getExternalContext().getFlash().setKeepMessages(true);
            f.addMessage(null, message);
            //String redirectUrl = "/DataSeries/DataSeriesList?faces-redirect=true&termslug=" + termSlug;
            return "DataSeriesList";
        } else {
            DGRFResponseMessage responseMessage = new DGRFResponseMessage();
            message = new FacesMessage(FacesMessage.SEVERITY_FATAL, "Data Error", responseMessage.getResponseMessage(fractalDTO.getResponseCode()));
            FacesContext f = FacesContext.getCurrentInstance();
            f.getExternalContext().getFlash().setKeepMessages(true);
            f.addMessage(null, message);
            String redirectUrl = "DataSeriesAdd";
            return redirectUrl;
        }


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

    public List<Map<String, Object>> getTermScreenFields() {
        return termScreenFields;
    }

    public void setTermScreenFields(List<Map<String, Object>> termScreenFields) {
        this.termScreenFields = termScreenFields;
    }

    public Map<String, Object> getScreenTermInstance() {
        return screenTermInstance;
    }

    public void setScreenTermInstance(Map<String, Object> screenTermInstance) {
        this.screenTermInstance = screenTermInstance;
    }


}
