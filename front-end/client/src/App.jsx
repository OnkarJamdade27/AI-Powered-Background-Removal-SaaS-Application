import React from "react";
import Menubar from "./components/Menubar";
import Home from "./components/Home";
import Footer from "./components/Footer"
import { Route, Routes } from "react-router-dom";
import { Toaster } from "react-hot-toast";
import UserSyncHandler from "./components/UserSyncHandler";

const App = () => {
  return (
    <div>
      <UserSyncHandler></UserSyncHandler>
      <Menubar></Menubar>
      <Toaster></Toaster>
      <Routes>
        <Route path="/" element={<Home></Home>}>

        </Route>
      </Routes>
      <Footer></Footer>
    </div>
  )
}

export default App;