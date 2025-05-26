import Router from './router';
import { EmailProvider } from './context/EmailProvider';
import { AuthProvider } from './context/AuthProvider';

function App() {
  return (
    <AuthProvider>
      <EmailProvider>
        <Router />
      </EmailProvider>
    </AuthProvider>
  )
}

export default App
