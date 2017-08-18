using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.Mail;
using System.Web;

namespace CropRecommendationWeb.Modules
{
    public class Email
    {
        /// <summary>
        /// Message
        /// </summary>
        public String Message { get; set; }

        /// <summary>
        /// Send To Address
        /// </summary>
        public MailAddressCollection SendTo { get; set; }

        /// <summary>
        /// From Address
        /// </summary>
        public String From { get; set; }

        /// <summary>
        /// If default from is not used, enter password
        /// </summary>
        public String Password { get; set; }
        /// <summary>
        /// Subject
        /// </summary>
        public String Subject { get; set; }


        /// <summary>
        /// Default Constructor
        /// </summary>
        public Email()
        {
            SendTo = new MailAddressCollection();
            From = "";
            Message = "";
            Subject = "";
        }
        /// <summary>
        /// Send Email
        /// </summary>
        /// <returns></returns>
        public Boolean SendEmail()
        {
            if (String.IsNullOrEmpty(Message)) return false;

            if (SendTo.Count == 0) return false;

            try
            {
                var newMessage = new MailMessage();
                foreach (var toAddress in SendTo)
                {
                    newMessage.To.Add(toAddress);
                }

                if (String.IsNullOrEmpty(From))
                {
                    var fromAddress = new MailAddress("account@projectapds.ecompliancesuite.com", "Crop Recommender");
                    newMessage.Sender = fromAddress;
                    newMessage.From = fromAddress;
                }
                else
                {
                    var fromAddress = new MailAddress(From);
                    newMessage.Sender = fromAddress;
                    newMessage.From = fromAddress;
                }
                newMessage.IsBodyHtml = true;
                newMessage.Body = Message;
                newMessage.Subject = Subject;

                var sc = new SmtpClient { Host = "mail.eComplianceSuite.com", Port = 25, DeliveryMethod = SmtpDeliveryMethod.Network };
                var cr = new NetworkCredential("account@projectapds.ecompliancesuite.com", "apds@iimc");
                sc.UseDefaultCredentials = true;
                sc.Credentials = cr;

                sc.Send(newMessage);
            }
            catch (Exception)
            {
                return false;
            }

            return true;
        }
    }
}