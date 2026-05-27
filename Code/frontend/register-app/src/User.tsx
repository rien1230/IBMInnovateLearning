import React, { useEffect, useState } from "react";
import axios from "axios";

interface User {
  id: number;
  userName: string;
  email: string;
  password: string;
}

const Users: React.FC = () => {
  const [users, setUsers] = useState<User[]>([]);
  const [newUser, setNewUser] = useState({ userName: "", email: "", password: "" });

  useEffect(() => {
    fetchUsers();
  }, []);

  const fetchUsers = () => {
    axios.get("http://localhost:8080/api/users")
      .then(response => {
        console.log("Fetched users:", response.data); // Debugging
        setUsers(Array.isArray(response.data) ? response.data : [response.data]);
      })
      .catch(error => console.error("Error fetching users:", error));
  };
  
  

  const handleRegister = async (event: React.FormEvent) => {
    event.preventDefault();
    console.log("Sending user data:", newUser); // Debugging
  
    try {
      const response = await axios.post("http://localhost:8080/api/register", newUser, {
        headers: { "Content-Type": "application/json" },
      });
      console.log("Registration successful:", response.data);
      fetchUsers();
      setNewUser({ userName: "", email: "", password: "" });
    } catch (error) {
      if (axios.isAxiosError(error)) {
        console.error("Error registering user:", error.response?.data || error.message);
      } else {
        console.error("Error registering user:", (error as Error).message);
      }
      alert("Registration failed!");
    }
  };
  

  return (
    <div>
      <h1>Users List</h1>
      <ul>
        {users.map(user => (
          <li key={user.id}>{user.userName} - {user.email}</li>
        ))}
      </ul>

      <h2>Register a New User</h2>
      <form onSubmit={handleRegister}>
        <input
          type="text"
          placeholder="Username"
          value={newUser.userName}
          onChange={(e) => setNewUser({ ...newUser, userName: e.target.value })}
          required
        />
        <input
          type="email"
          placeholder="Email"
          value={newUser.email}
          onChange={(e) => setNewUser({ ...newUser, email: e.target.value })}
          required
        />
        <input
          type="password"
          placeholder="Password"
          value={newUser.password}
          onChange={(e) => setNewUser({ ...newUser, password: e.target.value })}
          required
        />
        <button type="submit">Register</button>
      </form>
    </div>
  );
};

export default Users;
