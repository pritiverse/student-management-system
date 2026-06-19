import { Routes, Route } from "react-router-dom";

import Login from "./pages/Login";
import Dashboard from "./pages/Dashboard";
import Layout from "./pages/Navbar";
import Students from "./pages/Students";
import Departments from "./pages/Departments";
import AddStudent from "./pages/AddStudent";
function App() {
  return (
    <Routes>
      <Route path="/" element={<Login />} />

      <Route element={<Layout />}>
        <Route path="/dashboard" element={<Dashboard />} />
        <Route path="/students" element={<Students />} />
        <Route path="/departments" element={<Departments />} />
        <Route path="/students/add" element={<AddStudent />} />
      </Route>
    </Routes>
  );
}

export default App;