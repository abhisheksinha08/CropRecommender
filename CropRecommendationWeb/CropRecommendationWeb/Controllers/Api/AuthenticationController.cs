using CropRecommendationWeb.Models;
using CropRecommendationWeb.Models.Api;
using CropRecommendationWeb.Modules;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Text;
using System.Web;
using System.Web.Http;

namespace CropRecommendationWeb.Controllers.Api
{
    public class AuthenticationController : ApiController
    {
        [HttpGet]
        public AuthUser Register(String userId, String userName, String password, String emailId, String phoneNumber)
        {
            using (var db = new DatabaseEntities())
            {
                var data = db.sp_register_user(userId, userName, password, emailId, phoneNumber).ToList();
                if(!data.Any())
                {
                    SendActivattionEmail(userId, password, userName, emailId); //Send Activation Email
                    return new AuthUser { Message = "1,Registration Successful" };
                }
                return new AuthUser { Message = "2, User Id already exists!" };
            }
        }

        [HttpGet]
        public AuthUser Login(String userId, String password, string latitude, string longitude, string otherInfo)
        {
            using(var db= new DatabaseEntities())
            {
                Decimal dLatitude, dLongitude;

                Decimal.TryParse(latitude, out dLatitude);
                Decimal.TryParse(longitude, out dLongitude);

                var ipaddress = HttpContext.Current.Request.UserHostAddress;

                var data = db.sp_login_user(userId, password, dLatitude, dLongitude, ipaddress, otherInfo).ToList();
                
                if(data.Any())
                {
                    if(data.First().HasValue)
                    {
                        if(data.First().Value>0)
                        {
                            return new AuthUser { Message = "1,Login Successful" };
                            
                        }
                    }
                }
            }
            return new AuthUser { Message = "2, Incorrect User Id/Password or not activated." };
        }

        [HttpGet]
        public String ActivateUser(String userId, String hashkey)
        {
            using(var db= new DatabaseEntities())
            {
                var data = db.Users.First(a=>a.UserId.Equals(userId));
                var passHashkey = data.Password.ToString().GetHashCode().ToString();

                if(hashkey.Equals(passHashkey))
                {
                    data.IsActivated = true;
                    db.SaveChanges();
                    return "1, Activated";
                }
            }
            return "2, Incorrect User Id/Password.";
        }

        private void SendActivattionEmail(String userId, String password, String userName, string emailId)
        {
            var hasKey = password.GetHashCode().ToString();
            var message = new StringBuilder();
            message.Append("Dear ").Append(userName);
            message.Append(",<br/><br/>Thanks for registering with Crop Recommender!<br/><br/>In order to activate your account, please click <a href='http://projectapds.ecompliancesuite.com/Home/Activate?userId=");
            message.Append(userId).Append("&hashkey=").Append(hasKey).Append("'>here</a>.<br/><br/>Thanks.<br/><br/><b>Crop Recommeder Team.</b>");

            var email = new Email();
            email.Message = message.ToString();
            email.SendTo.Add(emailId);
            email.SendEmail();
        }
    }
}
