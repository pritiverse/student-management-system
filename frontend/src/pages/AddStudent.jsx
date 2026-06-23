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

  const validate = () => {
    const email = String(formData.email || "").trim();
    const phone = String(formData.phone || "").trim();
    const enrolledYearRaw = formData.enrolledYear;

    // Email syntax validation
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (email && !emailRegex.test(email)) return "Enter a valid email address.";

    // Indian phone validation (10 digits) with optional +91
    // Accepts: 9876543210 or +919876543210
    const phoneRegex = /^(?:\+?91)?[6-9]\d{9}$/;
    if (phone && !phoneRegex.test(phone)) return "Enter a valid Indian phone number (e.g., 9876543210).";

    // enrolledYear validation: reasonable range
    const enrolledYear = Number(enrolledYearRaw);
    if (!Number.isInteger(enrolledYear)) return "Enter enrolled year as a whole number.";
    if (enrolledYear < 1900 || enrolledYear > 2100)
      return "Enrolled year must be between 1900 and 2100.";

    // DOB required and cannot be in the future
    const dobStr = formData.dob;
    if (!dobStr) return "DOB is required.";
    const dob = new Date(dobStr);
    const now = new Date();
    if (dob.toString() === "Invalid Date") return "Enter a valid DOB.";
    if (dob > now) return "DOB cannot be in the future.";

    return null;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    const errorMsg = validate();
    if (errorMsg) {
      alert(errorMsg);
      return;
    }

    try {
      // Ensure correct types
      const payload = {
        ...formData,
        enrolledYear: Number(formData.enrolledYear),
      };

      const response = await fetch("http://localhost:8081/api/students", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(payload),
      });

      if (!response.ok) {
        const text = await response.text();
        throw new Error(text || "Failed to add student");
      }

      navigate("/students");
    } catch (err) {
      console.error(err);
      alert(err.message || "Failed to add student");
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

<input
          type="text"
          placeholder="enrolledYear"
          required
          value={formData.enrolledYear}
          onChange={(e) =>
            setFormData({
              ...formData,
              enrolledYear: e.target.value,
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