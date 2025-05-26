import api from "./config";
import { setAuthToken } from "./config";

export const login = async (email, password) => {
    try {
        const response = await api.post("/usuarios/inicio-sesion", {
            email,
            password,
        });
        setAuthToken(response.data.token)
        return response.data;
    } catch (e) {
        throw e.response
    }
}

export const sendCode = async (email) => {
    try {
        const response = await api.post("/usuarios/contraseña/olvidado",{
            email
        })
        return response.data;
    } catch (error) {
        throw error.response
    }
}

export const changePassword = async (email, password, code) => {
    try {
        const response = await api.put("/usuarios/contraseña", {
            email,
            password,
            code
        })
        return response.data;
    } catch (error) {
        throw error.response
    }
}