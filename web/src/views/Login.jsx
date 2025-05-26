import { login } from "../api";
import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { UseEmail } from "../context/EmailProvider";
import { UseAuth } from "../context/AuthProvider";

const LoginView = () => {
  // Handler de submit: previene recarga y podrías disparar aquí tu lógica de login
  const handleSubmit = (e) => {
    e.preventDefault();
    // TODO: Lógica de autenticación
  };

  const {setEmail:setEmailContext} = UseEmail();
  const {setLoged:setLogedContext} = UseAuth();
  const navigate = useNavigate();
  
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState(null);

    const handleLogin = async () => {
        try {
          await login(email, password);
          navigate("/");
          setLogedContext(true)
        } catch (e) {
          if(e.status == 403 || e.response?.data?.status === 'WARN'){            
            setEmailContext(email)
            navigate("/cambiar-contraseña")
          }
          setError(e.data?.message || "Error al iniciar sesión");
        }
    }

  return (
    <main className="grid h-screen items-center justify-center w-full container">
      <form
        onSubmit={handleSubmit}
        className="relative shadow-lg shadow-gray-400 p-6 rounded-lg overflow-hidden"
        aria-labelledby="login-heading"
      >
        <header className="mb-8">
          <h1 id="login-heading" className="title text-2xl font-semibold">
            Inicia Sesión
          </h1>
        </header>

        <div className="mb-4 space-y-4">
          <div>
            <label
              htmlFor="email"
              className="block mb-2 text-sm font-medium text-gray-800"
            >
              Correo Electrónico
            </label>
            <input
              id="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              name="email"
              type="email"
              required
              placeholder="ejemplo@pot.com"
              className="border outline-0 border-gray-300 text-gray-800 text-sm rounded-lg focus:shadow-md focus:shadow-primary-200 focus:border-primary-200 block w-full p-2"
            />
          </div>

          <div>
            <label
              htmlFor="password"
              className="block mb-2 text-sm font-medium text-gray-800"
            >
              Contraseña
            </label>
            <input
              id="password"
              name="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              type="password"
              required
              placeholder="********"
              className="border outline-0 border-gray-300 text-gray-800 text-sm rounded-lg focus:shadow-md focus:shadow-primary-200 focus:border-primary-200 block w-full p-2"
            />
          </div>
        </div>

        <footer className="relative overflow-hidden">
          <button
            type="submit"
            onClick={handleLogin}
            aria-label="Iniciar sesión"
            disabled={!email || !password}
            className="relative w-full bg-primary-400 text-white font-bold py-2 px-4 rounded-lg hover:bg-primary-600 focus:outline-none focus:shadow-outline transition-colors duration-100 ease-in-out cursor-pointer overflow-hidden"
          >
            Iniciar Sesión
          </button>
        </footer>
        {error && (
          <div className="mt-4 text-red-500 text-sm">
            {error}
          </div>
        )}
      </form>
    </main>
  );
};

export default LoginView;
