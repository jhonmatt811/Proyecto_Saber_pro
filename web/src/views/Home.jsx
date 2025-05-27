import { useEffect } from "react";
import { UseAuth } from "../context/AuthProvider";
import { useNavigate } from "react-router-dom";

const Home = () => {
    const {loged} = UseAuth();
    const navigate = useNavigate();
    useEffect(() => {
        if(!loged){
            navigate('/login');
        }
    })
  return (
    <div className="h-auto">
      <section className="grid h-[80vh]">
        <main className="relative bg-[url('/test.jpg')] bg-cover bg-center">
          <div className="absolute inset-0 bg-black/70"></div>
          <div className="relative z-10 flex flex-col items-center justify-center h-full space-y-8 container text-center px-4">
            <h1 className="text-white text-4xl md:text-8xl font-extrabold tracking-wide font-poppins">
              ICFES PROJECT
            </h1>
            <p className="text-white text-xl max-w-3xl">
              La herramienta diseñada para que instituciones educativas analicen y gestionen de forma integral los resultados de las pruebas Saber Pro del ICFES.
            </p>
            <button className="bg-primary-500 text-white px-6 py-2 rounded-lg hover:bg-primary-700 transition-colors font-semibold cursor-pointer">
              <a>Obtener Plataforma</a>
            </button>
          </div>
        </main>
      </section>

      {/* Información del sistema */}
      <section className="py-16 px-4 bg-white text-gray-800">
        <div className="max-w-5xl mx-auto space-y-12 text-center">
          <h2 className="text-3xl font-bold text-primary-600">¿Qué ofrece la plataforma?</h2>
          <div className="grid md:grid-cols-2 gap-10 text-left">
            <div>
              <h3 className="text-xl font-semibold mb-2">📊 Análisis y visualización de datos</h3>
              <p>Filtra y analiza resultados por año, cohorte, área, estudiante o programa académico. Obtén gráficas y tablas para tomar decisiones informadas.</p>
            </div>
            <div>
              <h3 className="text-xl font-semibold mb-2">🧩 Gestión estructurada de resultados</h3>
              <p>Importa y organiza los resultados de pruebas Saber Pro. Compatible con archivos o conexión directa al CFES.</p>
            </div>
            <div>
              <h3 className="text-xl font-semibold mb-2">👥 Control de usuarios con roles</h3>
              <p>Asignación de permisos a decanos, directores y coordinadores. Gestión segura y segmentada de la información.</p>
            </div>
            <div>
              <h3 className="text-xl font-semibold mb-2">📁 Exportación de reportes</h3>
              <p>Descarga informes detallados en PDF o Excel para compartir y archivar análisis de desempeño.</p>
            </div>
          </div>
        </div>
      </section>
      <section className="py-16 px-4 bg-white text-gray-800">

      </section>
    </div>
  );
};

export default Home;
