/*
*************************************************************************
  Copyright 2016 EGI Foundation
 
  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.

@author <a href="mailto:giuseppe.larocca@egi.eu">Giuseppe La Rocca</a>
***************************************************************************
*/
package it.infn.ct.chipster;

// import liferay libraries
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;

import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpProgressMonitor;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.Company;
import com.liferay.portal.model.User;
import com.liferay.portal.theme.ThemeDisplay;

// import DataEngine libraries
import com.liferay.portal.util.PortalUtil;

// import generic Java libraries
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

// import portlet libraries
//import javax.mail.Session;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.GenericPortlet;
import javax.portlet.PortletException;
import javax.portlet.PortletMode;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

// Importing Apache libraries
import javax.swing.ProgressMonitor;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.portlet.PortletFileUpload;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Chipster extends GenericPortlet {

    private static Log log = LogFactory.getLog(Chipster.class);   

    @Override
    protected void doEdit(RenderRequest request,
            RenderResponse response)
            throws PortletException, IOException
    {

        PortletPreferences portletPreferences =
                (PortletPreferences) request.getPreferences();

        response.setContentType("text/html");                

        // Getting the CHIPSTER APPID from the portlet preferences
        String chipster_EXPIRATION = portletPreferences.getValue("chipster_EXPIRATION", "30");
        // Getting the LOG LEVEL from the portlet preferences
        String chipster_LOGLEVEL = portletPreferences.getValue("chipster_LOGLEVEL", "INFO");
        // Getting the METADATA METADATA_HOST from the portlet preferences
        String chipster_HOST = portletPreferences.getValue("chipster_HOST", "N/A");
        // Getting the CHIPSTER OUTPUT_PATH from the portlet preferences
        String chipster_ACCOUNT_FILE = portletPreferences.getValue("chipster_ACCOUNT_FILE", "N/A");
        // Getting the SMTP_HOST from the portlet preferences
        String SMTP_HOST = portletPreferences.getValue("SMTP_HOST", "N/A");
        // Getting the SENDER MAIL from the portlet preferences
        String SENDER_MAIL = portletPreferences.getValue("SENDER_MAIL", "N/A");
        // Getting the SENDER ADMIN from the portlet preferences
        String SENDER_ADMIN = portletPreferences.getValue("SENDER_ADMIN", "N/A");
        
        request.setAttribute("chipster_EXPIRATION", chipster_EXPIRATION.trim());
        request.setAttribute("chipster_LOGLEVEL", chipster_LOGLEVEL.trim());
        request.setAttribute("chipster_HOST", chipster_HOST.trim());
        request.setAttribute("chipster_ACCOUNT_FILE", chipster_ACCOUNT_FILE.trim());
        request.setAttribute("SMTP_HOST", SMTP_HOST.trim());
        request.setAttribute("SENDER_MAIL", SENDER_MAIL.trim());
        request.setAttribute("SENDER_ADMIN", SENDER_ADMIN.trim());

        PortletRequestDispatcher dispatcher =
                getPortletContext().getRequestDispatcher("/edit.jsp");

        dispatcher.include(request, response);
    }

    @Override
    protected void doView(RenderRequest request, RenderResponse response)
            throws PortletException, IOException 
    {

        PortletPreferences portletPreferences =
                (PortletPreferences) request.getPreferences();

        response.setContentType("text/html");

        //java.util.Enumeration listPreferences = portletPreferences.getNames();
        PortletRequestDispatcher dispatcher = null;        
                
        // Getting the CHIPSTER APPID from the portlet preferences
        String chipster_EXPIRATION = portletPreferences.getValue("chipster_EXPIRATION", "30");
        // Getting the LOGLEVEL from the portlet preferences
        String chipster_LOGLEVEL = portletPreferences.getValue("chipster_LOGLEVEL", "INFO");
        // Getting the METADATA METADATA_HOST from the portlet preferences
        String chipster_HOST = portletPreferences.getValue("chipster_HOST", "N/A");
        // Getting the CHIPSTER OUTPUT_PATH from the portlet preferences
        String chipster_ACCOUNT_FILE = portletPreferences.getValue("chipster_ACCOUNT_FILE", "N/A");
        // Getting the SMTP_HOST from the portlet preferences
        String SMTP_HOST = portletPreferences.getValue("SMTP_HOST", "N/A");
        // Getting the SENDER_MAIL from the portlet preferences
        String SENDER_MAIL = portletPreferences.getValue("SENDER_MAIL", "N/A");       
        // Getting the SENDER_ADMIN from the portlet preferences
        String SENDER_ADMIN = portletPreferences.getValue("SENDER_ADMIN", "N/A");
        
        // Save the portlet preferences
        request.setAttribute("chipster_EXPIRATION", chipster_EXPIRATION.trim());
        request.setAttribute("chipster_LOGLEVEL", chipster_LOGLEVEL.trim());
        request.setAttribute("chipster_HOST", chipster_HOST.trim());
        request.setAttribute("chipster_ACCOUNT_FILE", chipster_ACCOUNT_FILE.trim());
        request.setAttribute("SMTP_HOST", SMTP_HOST.trim());
        request.setAttribute("SENDER_MAIL", SENDER_MAIL.trim());
        request.setAttribute("SENDER_ADMIN", SENDER_ADMIN.trim());
                        
         dispatcher = getPortletContext().getRequestDispatcher("/view.jsp");       
         dispatcher.include(request, response);
    }

    // The init method will be called when installing for the first time the portlet
    // This is the right time to setup the default values into the preferences
    @Override
    public void init() throws PortletException {
        super.init();
    }

    @Override
    public void processAction(ActionRequest request,
                              ActionResponse response)
                throws PortletException, IOException 
    {
        PortletRequestDispatcher dispatcher = null;        
        
        try 
        {
            String action = "";
            
            File temp = null;
            File credfile = null;            

            // Getting the action to be processed from the request
            action = request.getParameter("ActionEvent");

            // Determine the username and the email address
            ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);        
            User user = themeDisplay.getUser();
            
            String username = user.getScreenName();
            String user_emailAddress = user.getDisplayEmailAddress();
            Company company = PortalUtil.getCompany(request);
            String gateway = company.getName();

            PortletPreferences portletPreferences =
                    (PortletPreferences) request.getPreferences();

            if (action.equals("CONFIG_CHIPSTER_PORTLET")) {
                log.info("\nPROCESS ACTION => " + action);
                
                // Getting the CHIPSTER APPID from the portlet request
                String chipster_EXPIRATION = request.getParameter("chipster_EXPIRATION");
                // Getting the LOGLEVEL from the portlet request
                String chipster_LOGLEVEL = request.getParameter("chipster_LOGLEVEL");
                // Getting the CHIPSTER_METADATA_HOST from the portlet request
                String chipster_HOST = request.getParameter("chipster_HOST");
                // Getting the CHIPSTER OUTPUT_PATH from the portlet request
                String chipster_ACCOUNT_FILE = request.getParameter("chipster_ACCOUNT_FILE");
                // Getting the SMTP_HOST from the portlet request
                String SMTP_HOST = request.getParameter("SMTP_HOST");
                // Getting the SENDER_MAIL from the portlet request
                String SENDER_MAIL = request.getParameter("SENDER_MAIL");
                // Getting the SENDER_ADMIN from the portlet request
                String SENDER_ADMIN = request.getParameter("SENDER_ADMIN");
                //String[] infras = new String[6];
                
                log.info("\n\nPROCESS ACTION => " + action
                        + "\nchipster_EXPIRATION: " + chipster_EXPIRATION
                        + "\nchipster_LOGLEVEL: " + chipster_LOGLEVEL
                        + "\nchipster_HOST: " + chipster_HOST
                        + "\nchipster_ACCOUNT_FILE: " + chipster_ACCOUNT_FILE
                        + "\nSMTP_HOST: " + SMTP_HOST
                        + "\nSENDER_MAIL: " + SENDER_MAIL
                        + "\nSENDER_ADMIN: " + SENDER_ADMIN);
                                
                portletPreferences.setValue("chipster_EXPIRATION", chipster_EXPIRATION.trim());
                portletPreferences.setValue("chipster_LOGLEVEL", chipster_LOGLEVEL.trim());
                portletPreferences.setValue("chipster_ACCOUNT_FILE", chipster_ACCOUNT_FILE.trim());
                portletPreferences.setValue("chipster_HOST", chipster_HOST.trim());
                portletPreferences.setValue("SMTP_HOST", SMTP_HOST.trim());
                portletPreferences.setValue("SENDER_MAIL", SENDER_MAIL.trim());
                portletPreferences.setValue("SENDER_ADMIN", SENDER_ADMIN.trim());
                               
                portletPreferences.store();
                response.setPortletMode(PortletMode.VIEW);
            } // end PROCESS ACTION [ CONFIG_CHIPSTER_PORTLET ]
            

            if (action.equals("SUBMIT_CHIPSTER_PORTLET")) {
                log.info("\nPROCESS ACTION => " + action);                              
                // Getting the CHIPSTER APPID from the portlet preferences
                String chipster_EXPIRATION = portletPreferences.getValue("chipster_EXPIRATION", "30");
                // Getting the LOGLEVEL from the portlet preferences
                String chipster_LOGLEVEL = portletPreferences.getValue("chipster_LOGLEVEL", "INFO");
                // Getting the CHIPSTER_METADATA_HOST from the portlet preferences
                String chipster_HOST = portletPreferences.getValue("chipster_HOST", "INFO");
                // Getting the CHIPSTER OUTPUT_PATH from the portlet preferences
                String chipster_ACCOUNT_FILE = portletPreferences.getValue("chipster_ACCOUNT_FILE", "N/A");
                // Getting the SMTP_HOST from the portlet request
                String SMTP_HOST = portletPreferences.getValue("SMTP_HOST","N/A");
                // Getting the SENDER_MAIL from the portlet request
                String SENDER_MAIL = portletPreferences.getValue("SENDER_MAIL","N/A");
                // Getting the SENDER_ADMIN from the portlet request
                String SENDER_ADMIN = portletPreferences.getValue("SENDER_ADMIN","N/A");
                
                log.info("\n\nPROCESS ACTION => " + action
                        + "\nchipster_EXPIRATION: " + chipster_EXPIRATION
                        + "\nchipster_LOGLEVEL: " + chipster_LOGLEVEL
                        + "\nchipster_HOST: " + chipster_HOST
                        + "\nchipster_ACCOUNT_FILE: " + chipster_ACCOUNT_FILE
                        + "\nSMTP_HOST: " + SMTP_HOST
                        + "\nSENDER_MAIL: " + SENDER_MAIL
                        + "\nSENDER_ADMIN: " + SENDER_ADMIN);                                
                
                String[] CHIPSTER_Parameters = new String [4];                

                // Upload the input settings for the application
                CHIPSTER_Parameters = uploadChipsterSettings( request, response, username );
                
                //Set the public key for SSH connections
                String PublicKey = 
                    System.getProperty("user.home") + 
                    System.getProperty("file.separator") + 
                    ".ssh/id_rsa.pub";
            
                //Set the private key for SSH connections
                String PrivateKey = 
                    System.getProperty("user.home") + 
                    System.getProperty("file.separator") + 
                    ".ssh/id_rsa";

                log.info("\n\n [ Settings ]");
                log.info("\n- Input Parameters: ");                
                //log.info("\n- UserID = " + CHIPSTER_Parameters[0]);
                log.info("\n- Alias = " + CHIPSTER_Parameters[3]);
                //log.info("\n- Password = " + CHIPSTER_Parameters[1]);
                log.info("\n- Chipster Front node server = " + chipster_HOST);
                log.info("\n- Chipster accounting file = " + chipster_ACCOUNT_FILE);
                log.info("\n- Expiration date = " + chipster_EXPIRATION);
                log.info("\n- SSH Public Key file = " + PublicKey);
                log.info("\n- SSH Private Key file = " + PrivateKey);
                log.info("\n- Admin e-mail address = " + SENDER_ADMIN);
                log.info("\n- Enable Notification = " + CHIPSTER_Parameters[2]);
                
                Date date = new Date();
                SimpleDateFormat ft = new SimpleDateFormat ("yyyy-MM-dd"); 
                
                Calendar c = Calendar.getInstance();
                c.setTime(new Date()); // Now use today date.
                c.add(Calendar.DATE, Integer.parseInt(chipster_EXPIRATION));
                String output = ft.format(c.getTime());
                log.info("Date = " + output);                
                
                /*String credential = CHIPSTER_Parameters[0]
                        + ":" + CHIPSTER_Parameters[1];*/
                
                /*String credential = CHIPSTER_Parameters[0]
                        + ":" + CHIPSTER_Parameters[3]
                        + ":" + CHIPSTER_Parameters[1];*/

		String credential = CHIPSTER_Parameters[3]
                        + ":" + CHIPSTER_Parameters[1];
                                        
                try {
                    temp = File.createTempFile("file_", ".chipster");
                    log.info("\n- Creating a temporary file = " + temp);
                    
                    // Getting a copy of the remote file
                    doSFTP(CHIPSTER_Parameters, "get", 
                            chipster_HOST, chipster_ACCOUNT_FILE, null, temp);                    
                    
                    // Checking if the credential is already available
                    //if (checkChipsterCredential(temp, CHIPSTER_Parameters[0]))
                    if (checkChipsterCredential(temp, CHIPSTER_Parameters[3], CHIPSTER_Parameters[1])) {
                        log.info("\n- The user's credentials [ " 
                                + CHIPSTER_Parameters[3] 
                                + " ] does already exist");
                        
                        SessionErrors.add(request, "user-found");
                    } else {
                        log.info("\n- No credentials have been found!");
                                                
                        try {
                            credential += ":" + output;
                            credfile = File.createTempFile("cred_", ".chipster");
                            BufferedWriter writer = new BufferedWriter(new FileWriter(credfile));
                            writer.write(credential + "\n");
                            writer.close();
                        
                            // Register the credential
                            doSFTP(CHIPSTER_Parameters, "put-append", 
                                chipster_HOST, chipster_ACCOUNT_FILE, credfile, temp);                            
                            
                            // Send a notification email to the user if enabled.
                            if (CHIPSTER_Parameters[2]!=null)
                                if ( (SMTP_HOST==null) || 
                                     (SMTP_HOST.trim().equals("")) ||
                                     (SMTP_HOST.trim().equals("N/A")) ||
                                     (SENDER_MAIL==null) || 
                                     (SENDER_MAIL.trim().equals("")) ||
                                     (SENDER_MAIL.trim().equals("N/A"))
                                )
                                log.info ("\nThe Notification Service is not properly configured!!");
                            else
                                    sendHTMLEmail(username, 
                                      SENDER_ADMIN,
                                      SENDER_MAIL,
                                      SMTP_HOST,
                                      "Chipster Account Generator",
				      user_emailAddress,
                                      CHIPSTER_Parameters[3] + ":" + CHIPSTER_Parameters[1],
                                      chipster_HOST);
                            
                            credfile.deleteOnExit();
                        } catch (IOException ex) { log.error(ex); } 
                          finally { credfile.delete(); }
                        
                        SessionMessages.add(request, "user-not-found");
                    }
                                        
                    temp.deleteOnExit();
                } catch (IOException ex) { log.error(ex); } 
                finally { temp.delete(); }                
            } // end PROCESS ACTION [ SUBMIT_CHIPSTER_PORTLET ]
            
            // Hide default Liferay success/error messages
            /*PortletConfig portletConfig = (PortletConfig) request
                    .getAttribute(JavaConstants.JAVAX_PORTLET_CONFIG);

            LiferayPortletConfig liferayPortletConfig = (LiferayPortletConfig) portletConfig;
            
            SessionMessages.add(request, liferayPortletConfig.getPortletName()
                    + SessionMessages.KEY_SUFFIX_HIDE_DEFAULT_ERROR_MESSAGE);*/
                       
                
        } catch (PortalException ex) {
            Logger.getLogger(Chipster.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SystemException ex) {
            Logger.getLogger(Chipster.class.getName()).log(Level.SEVERE, null, ex);
        }                
    }
    
    public boolean checkChipsterCredential (File file, String login, String pass)
    {
        BufferedReader br = null;
        boolean flag = false;
        
        String cred = login + ":" + pass;
            
        try {            
            
            String line;
            br = new BufferedReader(new FileReader(file));

            while (((line = br.readLine()) != null) && (!flag)) 
            { 
                if (line.contains(login)) flag = true;
                else if (line.contains(cred)) flag = true;
            }
        } catch (IOException ex) {
            Logger.getLogger(Chipster.class.getName())
                  .log(Level.SEVERE, null, ex);
        } 
        
        return (flag);
    }
    
    public void doSFTP(String[] Parameters, 
                       String cmd, 
                       String frontNode,
                       String p1,
                       File credfile,
                       File temp)
    {                
        Integer port = 22;        
        
        // p1 = filename for put/get operation        
        // Remote path. Default '.'        
        String p2 = temp.toString();       
        
        JSch jsch = new JSch();        
        
        try 
        {
            // Getting the privateKey ...
            jsch.addIdentity(System.getProperty("user.home") +
                             System.getProperty("file.separator") +
                             ".ssh/id_rsa");
            
            jsch.setKnownHosts(System.getProperty("user.home") +
                             System.getProperty("file.separator") +
                             ".ssh/known_hosts");
                        
            String user = "root";            
            
            log.info("\n Connecting to " + frontNode);
            
            log.info("Please wait this operation may take few minutes ...");
            Session session = jsch.getSession(user, frontNode, port);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();
            
            Channel channel = session.openChannel("sftp");
            channel.connect();
            ChannelSftp c = (ChannelSftp)channel;           
                        
            if (cmd.equals("get") ||
                cmd.equals("get-resume") ||
                cmd.equals("get-append") ||
                cmd.equals("put") ||
                cmd.equals("put-resume") ||
                cmd.equals("put-append"))
            {
                SftpProgressMonitor monitor = new MyProgressMonitor();
                if (cmd.startsWith("get")) 
                {                    
                    int mode = ChannelSftp.OVERWRITE;
                    if (cmd.equals("get-resume")){ mode = ChannelSftp.RESUME; }
                    else if (cmd.equals("get-append")){ mode = ChannelSftp.APPEND; }
                    //c.get("/tmp/" + p1, p2, monitor, mode);
                    c.get(p1, p2, monitor, mode);
                } else {
                    log.info("Uploading credentials to server");
                    int mode = ChannelSftp.OVERWRITE;
                    if (cmd.equals("put-resume")){ mode = ChannelSftp.RESUME; }
                    else if (cmd.equals("put-append")){ mode = ChannelSftp.APPEND; }                    
                    //c.put(credfile.toString(), "/tmp/README", monitor, mode);
                    c.put(credfile.toString(), p1, monitor, mode);
                }                
                                
                c.exit();
            }
            
            session.disconnect();
        } catch(Exception ex){ 
            Logger.getLogger(Chipster.class.getName())
            .log(Level.SEVERE, null, ex); 
        }                
    }
    
    public static class MyProgressMonitor implements SftpProgressMonitor
    {
        ProgressMonitor monitor;
        long count=0;
        long max=0;
        private long percent=-1;

        public void init(int op, String src, String dest, long max)
        {
            this.max=max;
            monitor=new ProgressMonitor(null,
                      ((op==SftpProgressMonitor.PUT)?
                      "put" : "get")+": "+src,
                      "",  0, (int)max);
            count=0;
            percent=-1;
            monitor.setProgress((int)this.count);
            monitor.setMillisToDecideToPopup(1000);
        }

        public boolean count(long count)
        {
            this.count+=count;

            if(percent>=this.count*100/max){ return true; }
            percent=this.count*100/max;

            monitor.setNote("Completed "+this.count+"("+percent+"%) out of "+max+".");
            monitor.setProgress((int)this.count);

            return !(monitor.isCanceled());
        }

        public void end(){ monitor.close(); }
    }        

    // Upload CHIPSTER input files
    public String[] uploadChipsterSettings(ActionRequest actionRequest,
                        ActionResponse actionResponse, String username)
    {
        String[] CHIPSTER_Parameters = new String [4];
        boolean status;

        // Check that we have a file upload request
        boolean isMultipart = PortletFileUpload.isMultipartContent(actionRequest);

        if (isMultipart) {
            // Create a factory for disk-based file items.
            DiskFileItemFactory factory = new DiskFileItemFactory();

            // Set factory constrains
            File CHIPSTER_Repository = new File ("/tmp");
            if (!CHIPSTER_Repository.exists()) status = CHIPSTER_Repository.mkdirs();
            factory.setRepository(CHIPSTER_Repository);

            // Create a new file upload handler.
            PortletFileUpload upload = new PortletFileUpload(factory);

            try {
                    // Parse the request
                    List items = upload.parseRequest(actionRequest);
                    // Processing items
                    Iterator iter = items.iterator();

                    while (iter.hasNext())
                    {
                        FileItem item = (FileItem) iter.next();
                        String fieldName = item.getFieldName();
                        
                        DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
                        //String timeStamp = dateFormat.format(Calendar.getInstance().getTime());

                        // Processing a regular form field
                        if ( item.isFormField() )
                        {                                                                             
                            //if (fieldName.equals("chipster_login"))                                
                            //        CHIPSTER_Parameters[0]=item.getString();
                            
                            if (fieldName.equals("chipster_password1"))                                
                                    CHIPSTER_Parameters[1]=item.getString();
                            
                        } 
                        
                        if (fieldName.equals("EnableNotification"))
                                CHIPSTER_Parameters[2]=item.getString();
                        
                        if (fieldName.equals("chipster_alias"))
                                CHIPSTER_Parameters[3]=item.getString();
                                                
                    } // end while
            } catch (FileUploadException ex) {
              Logger.getLogger(Chipster.class.getName()).log(Level.SEVERE, null, ex);
            } catch (Exception ex) {
              Logger.getLogger(Chipster.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return CHIPSTER_Parameters;
    }           
    
    private void sendHTMLEmail (String USERNAME,
                                String TO, 
                                String FROM, 
                                String SMTP_HOST, 
                                String ApplicationAcronym,
                                String user_emailAddress,
                                String credential,
                                String chipster_HOST)
    {
                
        log.info("\n- Sending email notification to the user " + USERNAME + " [ " + TO + " ]");
        
        log.info("\n- SMTP Server = " + SMTP_HOST);
        log.info("\n- Sender = " + FROM);
        log.info("\n- Receiver = " + TO);
        log.info("\n- Application = " + ApplicationAcronym);
        log.info("\n- User's email = " + user_emailAddress);        
        
        // Assuming you are sending email from localhost
        String HOST = "localhost";
        
        // Get system properties
        Properties properties = System.getProperties();
        properties.setProperty(SMTP_HOST, HOST);        
        properties.setProperty("mail.debug", "true");
        
        //properties.setProperty("mail.smtp.auth", "false");        
        
        // Get the default Session object.
        javax.mail.Session session = javax.mail.Session.getDefaultInstance(properties);
        
        try {
         // Create a default MimeMessage object.
         javax.mail.internet.MimeMessage message = new javax.mail.internet.MimeMessage(session);

         // Set From: header field of the header.
         message.setFrom(new javax.mail.internet.InternetAddress(FROM));

         // Set To: header field of the header.
         message.addRecipient(javax.mail.Message.RecipientType.TO, 
                        new javax.mail.internet.InternetAddress(TO));
         message.addRecipient(javax.mail.Message.RecipientType.CC,
                 new javax.mail.internet.InternetAddress(user_emailAddress));
		//new javax.mail.internet.InternetAddress("glarocca75@gmail.com")); // <== Change here!

         // Set Subject: header field
         message.setSubject(" Chipster Account Generator service notification ");

	 Date currentDate = new Date();
	 currentDate.setTime (currentDate.getTime());

         // Send the actual HTML message, as big as you like
         message.setContent(
	 "<br/><H4>" +         
         "<img src=\"http://scilla.man.poznan.pl:8080/confluence/download/attachments/5505438/egi_logo.png\" width=\"100\">" +
	 "</H4><hr><br/>" +
         "<b>Description:</b> " + ApplicationAcronym + " notification service <br/><br/>" +         
         "<i>A request to create a new temporary chipster account has been successfully sent from the LToS Science Gateway</i><br/><br/>" +
	 "<b>Chipster Front Node:</b> " + chipster_HOST + "<br/>" +
         "<b>Credentials:</b> " + credential + "<br/><br/>" +
         "<b>TimeStamp:</b> " + currentDate + "<br/><br/>" +
	 "<b>Disclaimer:</b><br/>" +
	 "<i>This is an automatic message sent by the Catania Science Gateway (CSG) tailored for the EGI Long of Tail Science.<br/><br/>",
	 "text/html");

         // Send message
         javax.mail.Transport.send(message);         
      } catch (javax.mail.MessagingException ex) { 
          Logger.getLogger(Chipster.class.getName()).log(Level.SEVERE, null, ex);          
      }
    }      
}
