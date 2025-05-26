import { useEffect, useState } from "react";

const DoneCheck = () => {
  const [show, setShow] = useState(false);

  useEffect(() => {
    // Dispara la animación al cargar el componente
    setTimeout(() => setShow(true), 50);
  }, []);

  return (
    <div className="flex items-center justify-center">
      <div
        className={`transition-all duration-700 ease-out transform ${
          show ? "scale-100 opacity-100" : "scale-50 opacity-0"
        }`}
      >
        <div className="w-24 h-24 bg-green-500 rounded-full flex items-center justify-center shadow-lg animate-bounce">
          <svg
            className="w-12 h-12 text-white"
            fill="none"
            stroke="currentColor"
            strokeWidth="3"
            viewBox="0 0 24 24"
          >
            <path d="M5 13l4 4L19 7" strokeLinecap="round" strokeLinejoin="round" />
          </svg>
        </div>
      </div>
    </div>
  );
};

export default DoneCheck;
