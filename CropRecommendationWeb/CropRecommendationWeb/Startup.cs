using Microsoft.Owin;
using Owin;

[assembly: OwinStartupAttribute(typeof(CropRecommendationWeb.Startup))]
namespace CropRecommendationWeb
{
    public partial class Startup
    {
        public void Configuration(IAppBuilder app)
        {
            ConfigureAuth(app);
        }
    }
}
