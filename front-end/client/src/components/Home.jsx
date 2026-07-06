import Header from "./Header";
import BgRemovalSteps from "./BgRemovalSteps";
import BgSlider from "./BgSlider";
import Pricing from "./Pricing";
import Testimonials from "./Testimonials";
import Trynow from "./Trynow";

const Home = () => {
    return (
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-16 font-['Outfit']">
            {/* // Hero Section */}

            <Header></Header>

            {/* // Bg removal steps section */}

            <BgRemovalSteps></BgRemovalSteps>

            {/* // bg removal slider section  */}

            <BgSlider></BgSlider>

            {/* // buy credits plan section */}

            <Pricing></Pricing>

            {/* // user testimonial section */}

            <Testimonials></Testimonials>

            {/* // try now section */}

            <Trynow></Trynow>

        </div>
    )
}

export default Home;