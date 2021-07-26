package com.example.demo;

/**
 * The Controller Class definition for downloading .pkpass file.
 *
 * @auth
 */

/* Other import statements. */

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;

@RestController
@RequestMapping("/passkit")
public class PasskitController {

    @Autowired
    private IPasskitRestService passkitRestService;


    /** The Constant PASSKIT_CONTENT_TYPE. */
    private static final String PASSKIT_CONTENT_TYPE = "application/vnd.apple.pkpass";

    /**
     * Gets the pkpass file for the User
     */
    @RequestMapping(method = RequestMethod.GET)
    public void getPasskit(HttpServletRequest request, HttpServletResponse response) {

        // Code to get the User from UUID which is retrived from request or by any other means
        User user = getUser(request);

        byte[] pkpassFile = passkitRestService.createPasskit(user);

        // Prepare response to start download
        response.setStatus(200);
        response.setContentLength(pkpassFile.length);
        response.setContentType(PASSKIT_CONTENT_TYPE);
        response.setHeader("Content-Disposition", "attachment; filename=\"" + passkitRestService.getFileName(user) + "\"");

        ByteArrayInputStream bais = new ByteArrayInputStream(pkpassFile);
        try {
            IOUtils.copy(bais, response.getOutputStream());
            response.flushBuffer();
        } catch (IOException e) {
            // log the exception.
        }

    }

    public User getUser(HttpServletRequest request) {
        User user = new User();
        user.setFirstName("Nishara");
        user.setLastName("Kavindi");
        user.setUuid("UUID");
        user.setImageURL("https://www.google.com/imgres?imgurl=https%3A%2F%2Fcdn.britannica.com%2F84%2F73184-004-E5A450B5%2FSunflower-field-Fargo-North-Dakota.jpg&imgrefurl=https%3A%2F%2Fwww.britannica.com%2Fscience%2Fflower&tbnid=4eM3sPu0zHHglM&vet=12ahUKEwiHiPufmuXxAhUBFCsKHYoUDGcQMygBegUIARDUAQ..i&docid=Zih9vqAzJOmeTM&w=550&h=340&q=flowers&ved=2ahUKEwiHiPufmuXxAhUBFCsKHYoUDGcQMygBegUIARDUAQ");
        user.setUniqueID("123456");
        return user;
    }
}
