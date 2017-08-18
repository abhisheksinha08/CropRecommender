using CropRecommendationWeb.Controllers.Api;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Mvc;

namespace CropRecommendationWeb.Controllers
{
    public class HomeController : Controller
    {
        public ActionResult Index()
        {
            return View();
        }

        public ActionResult About()
        {
            ViewBag.Message = "Your application description page.";

            return View();
        }

        public ActionResult Contact()
        {
            ViewBag.Message = "Your contact page.";

            return View();
        }

        [HttpGet]
        public ActionResult Activate(string userId, String hashkey)
        {
            var data = (new AuthenticationController()).ActivateUser(userId, hashkey);
            ViewBag.Message = "Activatation error! Please contact our support!";
            if (data.StartsWith("1"))
            {
                ViewBag.Message = "Activatation completed successfully!";
            }
            return View();
        }
    }
}