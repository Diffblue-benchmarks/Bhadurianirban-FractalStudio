/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dgrf.cms.ui.terminstance;

import java.util.Map;
import java.util.regex.Pattern;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import org.dgrf.cms.core.driver.CMSClientService;
import org.dgrf.cms.dto.TermInstanceDTO;
import org.dgrf.cms.ui.login.CMSClientAuthCredentialValue;
import org.primefaces.validate.ClientValidator;

/**
 *
 * @author dgrf-vi
 */
@FacesValidator("custom.instanceSlugDuplicate")
public class InstanceSlugDuplicate implements Validator, ClientValidator {
    private static final String SLUG_PATTERN = "[a-zA-Z]+";
    private Pattern pattern;

    public InstanceSlugDuplicate() {
        pattern = Pattern.compile(SLUG_PATTERN);
    }
    
    @Override
    public void validate(FacesContext fc, UIComponent uic, Object o) throws ValidatorException {
        if (o == null) {
            return;
        }
        //CMSClientService mts = new CMSClientService();
        String termInstanceSlug = (String)o;
        //boolean slugExists = mts.isExistsTermInstanceSlug(termInstanceSlug);
        CMSClientService mts = new CMSClientService();
        TermInstanceDTO termInstanceDTO = new TermInstanceDTO();
        termInstanceDTO.setAuthCredentials(CMSClientAuthCredentialValue.AUTH_CREDENTIALS);
        termInstanceDTO.setAuthCredentials(CMSClientAuthCredentialValue.AUTH_CREDENTIALS);
        termInstanceDTO.setTermInstanceSlug(termInstanceSlug);
        termInstanceDTO = mts.isExistsTermInstanceSlug(termInstanceDTO);
        boolean slugExists;
        if (termInstanceDTO.getResponseCode() == 0) {
            slugExists = true;
        } else {
            slugExists = false;
        }
        //boolean slugExists = mts.isExistsTermInstanceSlug(termInstanceSlug);
        //boolean slugExists = false;
        if (slugExists) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Duplicate Slug",termInstanceSlug + " is already present;"));
        }
        if(!pattern.matcher(o.toString()).matches()) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Data entry error","slug should not contan spaces;"));
        }
        
    }

    @Override
    public Map<String, Object> getMetadata() {
        return null;
    }

    @Override
    public String getValidatorId() {
        return "custom.instanceSlugDuplicate";
    }
    
}
