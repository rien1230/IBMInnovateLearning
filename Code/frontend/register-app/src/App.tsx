import {
  BrowserRouter as Router,
  Route,
  Routes,
  useLocation,
} from "react-router-dom";
import Login from "./login";
import PasswordReset from "./PasswordReset";
import SpiralBackground from "./SpiralBackground";
import Register from "./register";
import Users from "./User"; // Import Users.tsx

function App() {
  return (
    <Router>
      <MainContent />
    </Router>
  );
}

function MainContent() {
  const location = useLocation();

  return (
    <div className="relative">
      {/* Only show SpiralBackground on the login page */}
      {location.pathname === "/" && <SpiralBackground />}

      <Routes>
        <Route path="/" element={<Login />} />
        <Route path="/forgot-password" element={<PasswordReset />} />
        <Route path="/register" element={<Register />} />
        <Route path="/users" element={<Users />} /> {/* New Route for Users */}
      </Routes>
    </div>
  );
}

export default App;
