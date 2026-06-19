import "../styles/AddStudent.css";
import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";

const AddStudent = () => {
  const navigate = useNavigate();

  const [departments, setDepartments] = useState([]);

  const [formData, setFormData] = useState({
    name: "",
    email: "",
    phone: "",
    dob: "",
    department: "",
  });

  useEffect(() => {
    fetch("http://localhost:8081/api/departments")
      .then((res) => res.json())
      .then((data) => setDepartments(data))
      .catch((err) => console.error(err));
  }, []);

  const handleSubmit = async (e) => {
    e.preventDefault();

    try {
      const response = await fetch(
        "http://localhost:8081/api/students",
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify(formData),
        }
      );

      if (!response.ok) {
        throw new Error("Failed to add student");
      }

      navigate("/students");
    } catch (err) {
      console.error(err);
    }
  };

  return (
    <div className="content-area">
      <h1>Add Student</h1>

      <form
        className="student-form"
        onSubmit={handleSubmit}
      >
        <input
          type="text"
          placeholder="Student Name"
          required
          value={formData.name}
          onChange={(e) =>
            setFormData({
              ...formData,
              name: e.target.value,
            })
          }
        />

        <input
          type="email"
          placeholder="Email"
          required
          value={formData.email}
          onChange={(e) =>
            setFormData({
              ...formData,
              email: e.target.value,
            })
          }
        />

        <input
          type="text"
          placeholder="Phone"
          required
          value={formData.phone}
          onChange={(e) =>
            setFormData({
              ...formData,
              phone: e.target.value,
            })
          }
        />

        <input
          type="date"
          required
          value={formData.dob}
          onChange={(e) =>
            setFormData({
              ...formData,
              dob: e.target.value,
            })
          }
        />

        <select
          required
          value={formData.department}
          onChange={(e) =>
            setFormData({
              ...formData,
              department: e.target.value,
            })
          }
        >
          <option value="">
            Select Department
          </option>

          {departments.map((dept) => (
            <option
              key={dept.id}
              value={dept.name}
            >
              {dept.name}
            </option>
          ))}
        </select>

        <div className="form-buttons">
          <button type="submit">
            Add Student
          </button>

          <button
            type="button"
            onClick={() => navigate("/students")}
          >
            Cancel
          </button>
        </div>
      </form>
    </div>
  );
};

export default AddStudent;