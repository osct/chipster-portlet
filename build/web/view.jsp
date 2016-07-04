<%
/**************************************************************************
Copyright (c) 2011-2016:
Istituto Nazionale di Fisica Nucleare (INFN), Italy
Consorzio COMETA (COMETA), Italy
    
See http://www.infn.it and and http://www.consorzio-lato.it for details 
on the copyright holders.
    
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at
    
http://www.apache.org/licenses/LICENSE-2.0
    
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
    
@author <a href="mailto:giuseppe.larocca@ct.infn.it">Giuseppe La Rocca</a>
**************************************************************************/
%>
<%@ page import="com.liferay.portal.kernel.util.WebKeys" %>
<%@ page import="com.liferay.portal.util.PortalUtil" %>
<%@ page import="com.liferay.portal.model.Company" %>
<%@ page import="com.liferay.portal.theme.ThemeDisplay" %>
<%@ page import="com.liferay.portal.model.User" %>
<%@ page import="javax.portlet.*" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<portlet:defineObjects/>

<%
  Company company = PortalUtil.getCompany(request);
  String gateway = company.getName();
  
  ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);        
  User user = themeDisplay.getUser();
  String username = user.getScreenName();
%>

<jsp:useBean id="chipster_EXPIRATION" class="java.lang.String" scope="request"/>
<jsp:useBean id="chipster_ACCOUNT_FILE" class="java.lang.String" scope="request"/>
<jsp:useBean id="chipster_SOFTWARE" class="java.lang.String" scope="request"/>
<jsp:useBean id="TRACKING_DB_HOSTNAME" class="java.lang.String" scope="request"/>
<jsp:useBean id="TRACKING_DB_USERNAME" class="java.lang.String" scope="request"/>
<jsp:useBean id="TRACKING_DB_PASSWORD" class="java.lang.String" scope="request"/>
<jsp:useBean id="SMTP_HOST" class="java.lang.String" scope="request"/>
<jsp:useBean id="SENDER_MAIL" class="java.lang.String" scope="request"/>
<jsp:useBean id="SENDER_ADMIN" class="java.lang.String" scope="request"/>

<script type="text/javascript">     

    $(document).ready(function() 
    {           
        var accOpts = {
            change: function(e, ui) {                       
                $("<div style='width:650px; font-family: Tahoma,Verdana,sans-serif,Arial; font-size: 14px;'>").addClass("notify ui-corner-all").text(ui.newHeader.find("a").text() +
                    " was activated... ").appendTo("#error_message").fadeOut(2500, function(){ $(this).remove(); });               
                // Get the active option                
                var active = $("#accordion").accordion("option", "active");                
            },
            autoHeight: false
        };
        
        // Create the accordions
        //$("#accordion").accordion({ autoHeight: false });
        $("#accordion").accordion(accOpts);             
        
        $("#commentForm").bind('submit', function() 
        {                                
            var flag=true;
            var flag1=true;
            var strength=0;
            
            // Remove the img and text error (if any)
            $("#error_message img:last-child").remove();
            $("#error_message").empty();
            
            // Check alias
            if ($("#chipster_alias").val() == "") flag1=false;
            
            // Check the password
            if (($("#chipster_password1").val() == "") || 
                ($("#chipster_password2").val() == "") || 
                ($("#chipster_password1").val() != $("#chipster_password2").val()) ) 
            { 
                console.log("passwords do not match");
                flag=false;                        
            }
            
            if ((!flag1)) {
                $('#error_message').append("Alias is empty. Please enter a valid one.");
                $("#error_message").css({"color":"red","font-size":"14px"});
                return false;
            }
                        
            if ((!flag)) {
                $('#error_message').append("Passwords are empty or do not match. Please enter a valid one.");
                $("#error_message").css({"color":"red","font-size":"14px"});
                //return false;
            } else {                
                
                //if length is 8 characters or more, increase strength value
                if ($("#chipster_password1").val().length > 7) strength += 2;
                
                //if password contains both lower and uppercase characters, increase strength value
                if ($("#chipster_password1").val().match(/([a-z].*[A-Z])|([A-Z].*[a-z])/)) 
                strength += 1;
            
                //if it has numbers and characters, increase strength value
                if ($("#chipster_password1").val().match(/([a-zA-Z])/) && 
                    $("#chipster_password1").val().match(/([0-9])/))  strength += 1;
		
                //if it has one special character, increase strength value
                if ($("#chipster_password1").val().match(/([!,%,&,@,#,$,^,*,?,_,~])/))  
                strength += 1;
            
                //if it has ':' special character, decrease strength value
                if ($("#chipster_password1").val().match(/([:])/)) strength -= 2;
		
                //if it has two special characters, increase strength value
                if ($("#chipster_password1").val().match(/(.*[!,%,&,@,#,$,^,*,?,_,~].*[!,%,&,@,#,$,^,*,?,_,~])/)) 
                strength += 1;                            
                
                if (strength < 5) {
                $('#error_message').append("The password you have insert does not satisty the requirements");
                $("#error_message").css({"color":"red","font-size":"14px"});                
                flag=false;                
                }
            }                                                           
            
            // Check if the input settings are valid before to
            // display the warning message.
            if (flag) { 
                /*$("#dialog-message").append("<p>Thanks for submitting a new request! <br/><br/>\n\
                Your request has been successfully submitted by the Science Gateway.\n\
                Have a look on MyJobs area to get more information about all your submitted jobs.</p>");
            
                $("#dialog-message").dialog({
                modal: true,
                title: "Notification Message",
                height: 200,
                width: 350
                //buttons: { Ok: function() { $( this ).dialog("close"); } }
                });*/
            
                $("#error_message").css({"color":"red","font-size":"14px", "font-family": "Tahoma,Verdana,sans-serif,Arial"}); 
                $('#error_message').append("Sent request in progress ...")(30000, function(){ $(this).remove(); });
            } else return false; 
        });
                   
        // Roller
        $('#chipster_footer').rollchildren({
            delay_time         : 3000,
            loop               : true,
            pause_on_mouseover : true,
            roll_up_old_item   : true,
            speed              : 'slow'   
        });
        
        $("#stars-wrapper1").stars({
            cancelShow: false,
            captionEl: $("#stars-cap"),
            callback: function(ui, type, value)
            {
                $.getJSON("ratings.php", {rate: value}, function(json)
                {                                        
                    $("#fake-stars-on").width(Math.round( $("#fake-stars-off").width()/ui.options.items*parseFloat(json.avg) ));
                    $("#fake-stars-cap").text(json.avg + " (" + json.votes + ")");
                });
            }
        });         
    });          
        
</script>

<br/>
<div id="dialog-message" title="Notification"></div>

<form enctype="multipart/form-data" 
      id="commentForm" 
      action="<portlet:actionURL><portlet:param name="ActionEvent" 
      value="SUBMIT_CHIPSTER_PORTLET"/></portlet:actionURL>"      
      method="POST">

<fieldset>
<!--legend>[ Chipster Account Generator ] </legend-->
<div style="margin-left:15px" id="error_message"></div>

<!-- Accordions -->
<div id="accordion" style="width:650px; font-family: Tahoma,Verdana,sans-serif,Arial; font-size: 14px;">
<h3><a href="#">
    <img width="35" 
         align="absmiddle"
         src="<%=renderRequest.getContextPath()%>/images/glass_numbers_1.png" />
    
    <b>Welcome to Chipster Account Generator service</b>
    
    <img width="50" 
         align="absmiddle" 
         src="<%=renderRequest.getContextPath()%>/images/about.png"/>
    </a>
</h3>
<div> <!-- Inizio primo accordion -->

<table id="results" border="0">
<tr>
<td>
<p align="center">
<a href="http://155.210.198.118:8081/">
<img width="600" src="<%=renderRequest.getContextPath()%>/images/Chipster-banner.png"/></a></p>
</td>
</tr>

<tr>
    <td>
    <p align="justify">
    Chipster is a user-friendly analysis software for high-throughput data. It contains over 300 analysis tools for next 
    generation sequencing (NGS), microarray, proteomics and sequence data. Users can save and share automatic analysis 
    workflows, and visualize data interactively using a built-in genome browser and many other visualizations. <br/><br/>
    Chipster's client software uses Java Web Start to install itself automatically, and it connects to computing servers 
    for the actual analysis. <br/><br/>
    Please see the <a href="http://chipster.csc.fi/">Chipster main site</a> for courses, updates and other information.<br/><br/>
    From this service user can request from a new chipster account to access the open source platform for data analysis.<br/>
    </p>
    </td>    
</tr>
</table>

<p><img width="20" src="<%=renderRequest.getContextPath()%>/images/help.png" title="Get in touch!"/>
If you need any help, please contact the
<a href="mailto:credentials-admin@ct.infn.it?Subject=Request for Technical Support [<%=gateway%> Science Gateway]&Body=Describe Your Problems&CC=sg-licence@ct.infn.it"> administrator</a>
</p>

<liferay-ui:ratings
    className="<%= it.infn.ct.chipster.Chipster.class.getName()%>"
    classPK="<%= request.getAttribute(WebKeys.RENDER_PORTLET).hashCode()%>" />

</div> <!-- Fine Primo Accordion -->

<h3><a href="#">
    <img width="35" 
         align="absmiddle"
         src="<%=renderRequest.getContextPath()%>/images/glass_numbers_2.png" />
    
    <b>Settings</b>
    <img width="40" 
         align="absmiddle" 
         src="<%=renderRequest.getContextPath()%>/images/icon_small_settings.png"/>
    </a>
</h3>      
    
<div> <!-- Inizio Secondo accordion -->
<p>Please, specify your Chipster credential to access the open source platform</p>

<fieldset style="width: 567px; border: 1px solid green;">
<table border="0" width="590">
    
    <tr><td><br/></td></tr>        
    
    <!--tr>
        <td width="180">
        <img width="30" 
             align="absmiddle"
             src="<%= renderRequest.getContextPath()%>/images/question.png" 
             border="0" title="Login"/>
        
        <label for="chipster_login">Login</label>
        </td>
        
        <td width="250">
        <input type="text"                
               id="chipster_login"
               name="chipster_login"
               readonly="readonly"
               style="padding-left: 1px; border-style: solid; 
                      border-color: grey; border-width: 1px; padding-left: 1px;
                      width:330px;"
               value=<%=username%>                              
               size="33"/>
        </td>           
    </tr-->
    
    <tr>
        <td width="180">
        <img width="30" 
             align="absmiddle"
             src="<%= renderRequest.getContextPath()%>/images/question.png" 
             border="0" title="Alias"/>
        
        <label for="chipster_alias">Alias</label>
        </td>
        
        <td width="250">
        <input type="text"                
               id="chipster_alias"
               name="chipster_alias"               
               style="padding-left: 1px; border-style: solid; 
                      border-color: grey; border-width: 1px; padding-left: 1px;
                      width:330px;"                                           
               size="33"/>
        </td>           
    </tr>
    
    <tr>
        <td width="180">
        <img width="30" 
             align="absmiddle"
             src="<%= renderRequest.getContextPath()%>/images/question.png" 
             border="0" title="Please, insert the password"/>
        
        <label for="chipster_password1">Password</label>
        </td>
        
        <td width="250">
        <input type="password" 
               id="chipster_password1"
               name="chipster_password1"
               style="padding-left: 1px; border-style: solid; 
                      border-color: grey; border-width: 1px; padding-left: 1px;
                      width:330px;"
               value=""
               size="33"/>
        </td>           
    </tr>
    
    <tr>
        <td width="180">
        <img width="30" 
             align="absmiddle"
             src="<%= renderRequest.getContextPath()%>/images/question.png" 
             border="0" title="Please, re-insert the password"/>
        
        <label for="chipster_password2">Re-type Password</label>
        </td>
        
        <td width="250">
        <input type="password" 
               id="chipster_password2"
               name="chipster_password2"
               style="padding-left: 1px; border-style: solid; 
                      border-color: grey; border-width: 1px; padding-left: 1px;
                      width:330px;"
               value=""
               size="33"/>
        </td>           
    </tr>
    
    <tr><td><br/></td></tr>            
    
    <tr>
        <td width="180">
        <img width="30" 
             align="absmiddle"
             src="<%= renderRequest.getContextPath()%>/images/question.png" 
             border="0" title="Enable email notification for the administrator"/>
                                       
        <c:set var="enabled_SMTP" value="<%= SMTP_HOST %>" />
        <c:set var="enabled_SENDER" value="<%= SENDER_MAIL %>" />
        <c:choose>
        <c:when test="${empty enabled_SMTP || empty enabled_SENDER}">
            
        <input type="checkbox" 
               name="EnableNotification"
               id="EnableNotification"
               disabled="disabled"
               value="yes"/> Notification
        </c:when>
        <c:otherwise>
        <input type="checkbox" 
               name="EnableNotification"
               id="EnableNotification"
               value="yes"/> Notification                    
        </c:otherwise>
        </c:choose>
        </td>

        <tr><td><br/></td></tr>
        
        <tr>
            <td colspan="2">
            <img width="20" src="<%=renderRequest.getContextPath()%>/images/help.png"/>
            Password must meet the following requirements:
            </td>
        </tr>
        <tr><td colspan="2">
                <ul>
                    <li>At least <b>one letter</b></li>
                    <li>At least <b>one capital letter</b></li>
                    <li>At least <b>one number</b></li>
                    <li>Be at least <b>8 characters</b></li>
                </ul>
            </td>
        </tr>
        
        <tr><td><br/></td></tr>
    </tr>
                
   <tr>                    
   <td align="left">
        <input type="image" 
               src="<%= renderRequest.getContextPath()%>/images/start-icon.png"
               width="60"                   
               name="submit"
               id ="submit" 
               title="Create credential!" />                    
   </td>
   </tr>
</table>
</fieldset>
</div>	<!-- Fine Secondo Accordion -->
</div> <!-- Fine Accordions -->
</fieldset>    
</form>                                                                         

<div id="chipster_footer" style="width:690px; font-family: Tahoma,Verdana,sans-serif,Arial; font-size: 14px;">
    <div>Chipster Account Generator portlet v0.1.4</div>
    <div>EGI.eu, Amsterdam, The Netherlands</div>    
    <div>Copyright © 2016. All rights reserved</div>        
</div>               