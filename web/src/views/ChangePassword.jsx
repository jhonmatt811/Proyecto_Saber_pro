import { useState } from "react";
import { useNavigate } from "react-router-dom";
import DoneUi from "../util/doneUi"
import { UseEmail } from "../context/EmailProvider";
import { sendCode,changePassword } from "../api";

// Paso 1
const AskChangePassword = ({ email,onNext,onError}) => {
  const [done,setDone] = useState(false);  
  const handleClick = () => {
    sendCodeAux();
    setTimeout(()=>{
      onNext();
    }, 2000)
  }
  const sendCodeAux = async () => {
    try {
      await sendCode(email);
      setDone(true);
    } catch (e) {
      onError(e.response?.data?.message || e.message || "Error al enviar el código");
    }
  }
  return(
      <div className="text-center space-y-6 flex flex-col items-center">
        {done && <DoneUi/>}
        <h1 className="text-2xl font-bold">🔐 Debe cambiar la contraseña</h1>
        <p className="text-gray-500">Haz click para enviarte un codigo de seguridad.</p>
        <button
          onClick={handleClick}
          className="bg-primary-500 text-white px-6 py-2 rounded hover:bg-primary-700 transition"
        >
          Cambiar contraseña
        </button>
      </div>
    )
  };

// Paso 2
const PasswordForm = ({ password, confirmPassword, setPassword, setConfirmPassword, onNext, onError,code,setCode,email }) => {
  
  const hanldePasswordAux = async () => {
    let error;
        if (password.length < 6) {
          error = "La contraseña debe tener al menos 6 caracteres."
          onError(error);
          throw new Error(error);
        }
        if (password !== confirmPassword) {
          error = "Las contraseñas no coinciden.";
          onError(error);
          throw new Error(error)
        }
        await changePassword(email, password, code);
  }

  const handleSubmit = async() => {
    try{    
      await hanldePasswordAux();
      onNext();
    }catch(error){
      onError(error.response?.data?.message || error.message || "Error al cambiar la contraseña");
    }
  };

  return (
    <div className="space-y-4 text-center">
      <h2 className="text-xl font-semibold">Ingrese su nueva contraseña</h2>
      <input
        type="password"
        placeholder="Nueva contraseña"
        value={password}
        onChange={(e) => setPassword(e.target.value)}
        className="border p-2 rounded w-full"
      />      
      <input
        type="password"
        placeholder="Confirmar contraseña"
        value={confirmPassword}
        onChange={(e) => setConfirmPassword(e.target.value)}
        className="border p-2 rounded w-full"
      />
      <input
        type='number'
        placeholder="Código de seguridad"
        value={code}
        onChange={(e) => setCode(e.target.value)}
        className="border p-2 rounded w-full"
        />
      <button
        onClick={handleSubmit}
        className="bg-primary-500 text-white px-4 py-2 rounded hover:bg-primary-700 transition cursor-pointer"
      >
        Confirmar
      </button>
    </div>
  );
};

// Paso 3
const PasswordChanged = () => {
    Promise.resolve().then(() => {
      setTimeout(() => {
      }, 1000);
    });
    return (
    <div className="text-center space-y-4 flex flex-col items-center">
      <DoneUi/>
      <h2 className="text-2xl font-bold text-green-600">✅ Contraseña actualizada</h2>
      <p>Redirigiendo al inicio de sesión...</p>
    </div>
    )
  };

// Stepper visual
const Stepper = ({ steps, currentStep }) => (
  <div className="flex items-center justify-center mb-6">
    {steps.map((label, index) => (
      <div key={index} className="flex items-center">
        <div
          className={`w-8 h-8 rounded-full flex items-center justify-center text-white text-sm font-semibold ${
            index <= currentStep ? "bg-primary-500" : "bg-gray-300"
          }`}
        >
          {index + 1}
        </div>
        {index < steps.length - 1 && (
          <div className="w-10 h-1 bg-gray-300 mx-2 rounded">
            <div
              className={`h-full rounded ${
                index < currentStep ? "bg-primary-500" : ""
              }`}
            ></div>
          </div>
        )}
      </div>
    ))}
  </div>
);

// Componente principal
const ChangePassword = () => {
  const [step, setStep] = useState(0);
  const [password, setPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [error, setError] = useState(null);
  const [code,setCode] = useState(0);

  const navigate = useNavigate();
  const {email} = UseEmail();
  if(email === null || email === undefined){
   navigate("/login")
  }
  const goToLogin = () => {
    setTimeout(() => navigate("/login"), 1500);
  };
  
  const steps = [
    "Requerimiento",
    "Cambiar contraseña",
    "Confirmación",
  ];

  const arrayTemplates = [
    <AskChangePassword email={email} onNext={() => setStep(1)} onError={setError}/>,
    <PasswordForm
      password={password}
      confirmPassword={confirmPassword}
      setPassword={setPassword}
      setCode={setCode}
      code={code}
      setConfirmPassword={setConfirmPassword}
      email={email}
      onNext={() => {
        setStep(2);
        goToLogin();
      }}
      onError={setError}
    />,
    <PasswordChanged />,
  ];

  return (
    <main className="grid h-screen items-center justify-center px-4">
      <div className="w-full max-w-md p-6 bg-white rounded-lg shadow-md">
        <Stepper steps={steps} currentStep={step} />
        {arrayTemplates[step]}
        {error && <p className="text-red-600 text-sm mt-2">{error}</p>}
        {step > 0 && step < 2 && (
          <button
            onClick={() => setStep(step - 1)}
            className="mt-4 text-sm text-gray-500 hover:underline"
          >
            ⬅ Volver
          </button>
        )}
      </div>
    </main>
  );
};

export default ChangePassword;
