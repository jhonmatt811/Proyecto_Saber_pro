import { Routes,Route } from "react-router-dom";
import  LoginView  from "../views/Login";
import Home from "../views/Home"
import ChangePassword from "../views/ChangePassword";

const Router = () => {
    return (
        <Routes>
            <Route path="/" element={<Home/>}/>
            <Route path="/login" element={<LoginView/>}/>
            <Route path="/cambiar-contraseña" element={<ChangePassword/>}/>
        </Routes>
    )
}

export default Router;