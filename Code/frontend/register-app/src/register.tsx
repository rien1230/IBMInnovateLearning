import React, { useState, useRef, useEffect } from "react";
import { gsap } from "gsap";
import { useNavigate } from "react-router-dom";

const Register: React.FC = () => {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [Name, setName] = useState("");
  const circlesRef = useRef<HTMLDivElement[]>([]);
  const navigate = useNavigate(); // ✅ Use navigate for redirection

  useEffect(() => {
    if (circlesRef.current.length === 0) return;

    requestAnimationFrame(() => {
      circlesRef.current.forEach((circle: HTMLDivElement) => {
        gsap.to(circle, {
          rotation: Math.random() > 0.5 ? 360 : -360,
          scale: Math.random() * 0.5 + 0.75,
          x: Math.random() * 100 - 50,
          y: Math.random() * 100 - 50,
          duration: Math.random() * 4 + 2,
          repeat: -1,
          yoyo: true,
          ease: "power1.inOut",
        });
      });
    });
  }, []);

  const handleSubmit = async (event: React.FormEvent) => {
    event.preventDefault(); // Prevent page reload

    const userData = { Name, email, password };

    try {
      const response = await fetch("http://localhost:8080/api/register", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(userData),
      });

      if (response.ok) {
        const data = await response.json();
        console.log("Registration successful:", data);
        navigate("/users"); // ✅ Redirect to Users page
      } else {
        console.error("Registration failed");
      }
    } catch (error) {
      console.error("Error connecting to backend:", error);
    }
  };

  return (
    <div className="relative flex items-center justify-center min-h-screen overflow-hidden">
      {/* Background Gradient */}
      <div className="absolute inset-0 -z-20 bg-gradient-to-r from-blue-900 via-purple-600 to-green-400"></div>

      {/* Animated Background Circles */}
      <div className="absolute inset-0 -z-10 overflow-hidden">
        {[...Array(12)].map((_, i) => (
          <div
            key={i}
            ref={(el) => {
              if (el) circlesRef.current[i] = el;
            }}
            className="absolute rounded-full opacity-40 blur-xl"
            style={{
              width: `${Math.random() * 80 + 40}px`,
              height: `${Math.random() * 80 + 40}px`,
              top: `${Math.random() * 100}%`,
              left: `${Math.random() * 100}%`,
              backgroundColor: [
                "#4285F4", // Google Blue (IBM-style)
                "#6A0DAD", // Deep Purple
                "#34A853", // Google Green
                "#FFFFFF", // White
              ][Math.floor(Math.random() * 4)], // Randomly pick from colors
              willChange: "transform",
            }}
          />
        ))}
      </div>

      <div className="flex items-center justify-center min-h-screen">
        <div className="bg-white shadow-2xl rounded-lg p-6 w-96 bg-none">
          <h2 className="text-2xl font-bold mb-4 text-center">
            Register New User
          </h2>
          <form onSubmit={handleSubmit}>
            <div className="mb-3">
              <label
                htmlFor="Name"
                className="block text-sm font-medium text-gray-700"
              >
                Name
              </label>
              <input
                type="text"
                id="Name"
                className="w-full p-2 mt-1 border rounded-md focus:ring focus:ring-blue-200"
                value={Name}
                onChange={(e) => setName(e.target.value)}
                required
              />
            </div>
            <div className="mb-3">
              <label
                htmlFor="email"
                className="block text-sm font-medium text-gray-700"
              >
                Email address
              </label>
              <input
                type="email"
                id="email"
                className="w-full p-2 mt-1 border rounded-md focus:ring focus:ring-blue-200"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                required
              />
            </div>
            <div className="mb-3">
              <label
                htmlFor="password"
                className="block text-sm font-medium text-gray-700"
              >
                Password
              </label>
              <input
                type="password"
                id="password"
                className="w-full p-2 mt-1 border rounded-md focus:ring focus:ring-blue-200"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                required
              />
            </div>
            <button
              type="submit"
              className="w-full bg-blue-500 text-white py-2 rounded-md hover:bg-blue-600 transition"
            >
              Submit
            </button>
          </form>
        </div>
      </div>
    </div>
  );
};

export default Register;
