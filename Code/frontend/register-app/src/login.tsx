import React, { useState } from "react";
import { Link } from "react-router-dom";

const Login: React.FC = () => {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");

  const handleSubmit = async (event: React.FormEvent) => {
    event.preventDefault(); // Prevent page reload
  
    const userData = { email: username, password }; // Changed "username" to "email" to match backend
  
    try {
      const response = await fetch("http://localhost:8080/api/login", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(userData),
      });
  
      const data = await response.text(); // Backend returns plain text
  
      if (response.ok) {
        console.log("Login successful:", data);
        alert("Login successful"); // Temporary success message
      } else {
        console.error("Login failed:", data);
        alert("Invalid credentials");
      }
    } catch (error) {
      console.error("Error connecting to backend:", error);
    }
  };
  


  return (
    <div className="flex items-center justify-center min-h-screen">
      <div className="bg-none shadow-2xl outline-white outline-solid rounded-lg p-6 w-96 transform transition duration-300 hover:scale-105">
        <h2 className="text-2xl font-bold mb-4 text-center">Login</h2>
        <form onSubmit={handleSubmit}>
          <div className="mb-3 text-black">
            <label
              htmlFor="username"
              className="block text-sm font-medium text-black"
            >
              Username/Email
            </label>
            <input
              type="text"
              id="username"
              className="w-full p-2 mt-1 border rounded-md focus:ring focus:ring-blue-200"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              required
            />
          </div>
          <div className="mb-3 text-black">
            <label
              htmlFor="password"
              className="block text-sm font-medium text-black"
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
        <div className="my-4 text-center text-gray-500">OR</div>
        <div className="flex justify-center">
          <button
            onClick={() =>
              (window.location.href =
                "http://localhost:8080/oauth2/authorization/google")
            }
            className="w-12 h-12 rounded-full bg-white flex items-center justify-center shadow-md hover:shadow-lg transition"
          >
            <img src="/public/google.jpeg" alt="Google" className="w-8 h-8" />
          </button>
        </div>
        <div className="mt-4 text-center">
          <Link to="/forgot-password" className="text-red-800 underline">
            Forgot password?
          </Link>
        </div>
        <div className="mt-4 text-center">
          <Link to="/register" className="text-red-800 underline">
            Don't have an account? Register
          </Link>
        </div>
      </div>
    </div>
  );
};

export default Login;
