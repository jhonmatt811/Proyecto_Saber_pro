import { createContext, useContext, useState } from "react";

const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
  const [loged, setLoged] = useState(false);

  return (
    <AuthContext.Provider value={{ loged, setLoged }}>
      {children}
    </AuthContext.Provider>
  );
};

export const UseAuth = () => useContext(AuthContext);
