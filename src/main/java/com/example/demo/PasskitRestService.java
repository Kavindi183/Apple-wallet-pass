package com.example.demo;

/* other import statements */

import de.brendamour.jpasskit.PKBarcode;
import de.brendamour.jpasskit.PKField;
import de.brendamour.jpasskit.PKPass;
import de.brendamour.jpasskit.enums.PKBarcodeFormat;
import de.brendamour.jpasskit.passes.PKGenericPass;
import de.brendamour.jpasskit.signing.*;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;

@Service
public class PasskitRestService implements IPasskitRestService {
    /**
     * The foreground color of pass
     */
    private final String foregroundColor = "rgb(255,255,255)";

    /**
     * The background color of pass
     */
    private final String backgroundColor = "rgb(207,18,45)";

    /**
     * The pass type identifier, taken from the certificate
     */
    private final String passTypeIdentifier = "pass.com.example.id";

    /**
     * The team ID, taken from the certificate
     */
    private final String teamID = "YOUR_TEAM_ID";

    /**
     * version
     */
    private final int version = 1;

    /**
     * The pass description
     */
    private final String description = "Example card";

    /**
     * The name of primary field of the pass
     */
    private final String primaryFieldName = "firstName";

    /**
     * The name of primary header of the pass
     */
    private final String primaryHeader = "Name";

    /**
     * The name of secondary field of the pass
     */
    private final String secondaryFieldName = "ID Number";

    /**
     * The name of secondary header of the pass
     */
    private final String secondaryHeader = "Unique_ID";

    /**
     * The name of organization issuing the pass
     */
    private final String orgName = "YOUR_ORG_NAME";

    /**
     * The Pass certificate path
     */
    private final String passCertificateFileName = "Wallet.p12";

    /**
     * The password for pass certificate
     */
    private final String password = "PASS_CERTIFICATE_PASSWORD";

    /**
     * The Apple developer certificate path
     */
    private final String developerCertificateFileName = "AppleWWDRCA.cer";

    /**
     * location of template folder
     */
    private final String PASS_TEMPLATE_FOLDER = "templateFolder";

    /**
     * Create a .pkpass file for User
     *
     * @param user the user
     */
    @Override
    public byte[] createPasskit(User user) {

        String firstName = user.getFirstName();
        String lastName = user.getLastName();
        String delim = " ";

        PKPass pass = new PKPass();

        PKGenericPass gp = new PKGenericPass();

        // name, label, value
        PKField primaryField = new PKField(primaryFieldName, primaryHeader, firstName + delim + lastName);
        PKField secondaryField = new PKField(secondaryFieldName, secondaryHeader, user.getUniqueID());
        gp.addPrimaryField(primaryField);
        gp.addSecondaryField(secondaryField);

        PKBarcode barcode = new PKBarcode();
        barcode.setFormat(PKBarcodeFormat.PKBarcodeFormatQR);
        barcode.setMessage(user.getUuid().toString());
        barcode.setMessageEncoding(Charset.forName("utf-8"));
        List<PKBarcode> barcodeList = new ArrayList<PKBarcode>();
        barcodeList.add(barcode);

        pass.setBackgroundColor(backgroundColor);
        pass.setFormatVersion(version);
        pass.setPassTypeIdentifier(passTypeIdentifier);
        pass.setSerialNumber(user.getUniqueID().toString());
        pass.setTeamIdentifier(teamID);
        pass.setOrganizationName(orgName);
        pass.setDescription(description);
        pass.setForegroundColor(foregroundColor);
        pass.setGeneric(gp);
        pass.setBarcodes(barcodeList);

        PKSigningInformation pkSigningInformation;

        byte[] signedAndZippedPkPassArchive = new byte[0];
        try {
            InputStream keyStoreInputStream = new FileInputStream(passCertificateFileName);
            InputStream appleWWDRCAFileInputStream = new FileInputStream(developerCertificateFileName);


            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

            URL url = classLoader.getResource(PASS_TEMPLATE_FOLDER);

            IPKPassTemplate pkPassTemplateFolder = new PKPassTemplateFolder(url);

            pkSigningInformation = new PKSigningInformationUtil()
                    .loadSigningInformationFromPKCS12AndIntermediateCertificate(keyStoreInputStream, password, appleWWDRCAFileInputStream);

            PKFileBasedSigningUtil pkSigningUtil = new PKFileBasedSigningUtil();
            signedAndZippedPkPassArchive = pkSigningUtil.createSignedAndZippedPkPassArchive(pass,
                    pkPassTemplateFolder, pkSigningInformation);

        } catch (CertificateException e1) {
            // log the exception.
            e1.printStackTrace();
        } catch (IOException e1) {
            // log the exception.
            e1.printStackTrace();
        } catch (PKSigningException e) {
            // log the exception.
            e.printStackTrace();
        }
        return signedAndZippedPkPassArchive;
    }

    /**
     * Returns the name of the pkpass file
     *
     * @return filename for pkpass file
     */
    @Override
    public String getFileName(User user) {
        return "user_id_card_" + user.getFirstName() + "_" + user.getLastName() + ".pkpass";
    }
}
