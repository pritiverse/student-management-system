import React, { useEffect, useState, useCallback } from "react";
import { Edit, Trash2, Plus } from "lucide-react";
import "../styles/Students.css";
import { useNavigate } from "react-router-dom";

const Dashboard = () => {
  const [students, setStudents] = useState([]);
  const [studentCount, setStudentCount] = useState(0);
  const [search, setSearch] = useState("");
  const [departmentCount, setDepartmentCount] = useState(0);
  const [departments, setDepartments] = useState([]);
  const [departmentFilter, setDepartmentFilter] = useState("");
  const [editingId, setEditingId] = useState(null);
  const navigate = useNavigate();

  const [formData, setFormData] = useState({
    name: "",
    email: "",
    phone: "",
    dob: "",
    department: "",
  });

  useEffect(() => {
    fetch("http://localhost:8081/api/students/count")
      .then((res) => res.json())
      .then((data) => setStudentCount(data))
      .catch((err) => console.error(err));

    fetch("http://localhost:8081/api/departments/count")
      .then((res) => res.json())
      .then((data) => setDepartmentCount(data))
      .catch((err) => console.error(err));

    fetch("http://localhost:8081/api/departments")
      .then((res) => res.json())
      .then((data) => setDepartments(data))
      .catch((err) => console.error(err));
  }, []);

  const loadStudents = useCallback(async () => {
    const normalizedSearch = search.trim();
    const response = await fetch(
      `http://localhost:8081/api/students?search=${encodeURIComponent(normalizedSearch)}&department=${encodeURIComponent(departmentFilter)}`
    );
    const data = await response.json();
    setStudents(data);
  }, [search, departmentFilter]);

  useEffect(() => {
    loadStudents();
  }, [loadStudents]);

  const handleEdit = (student) => {
    setEditingId(student.id);
    setFormData({
      name: student.name,
      email: student.email,
      phone: student.phone,
      dob: student.dob,
      department: student.department,
    });
  };

  const handleUpdate = async () => {
    try {
      const response = await fetch(
        `http://localhost:8081/api/students/${editingId}`,
        {
          method: "PUT",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify(formData),
        }
      );

      if (!response.ok) {
        throw new Error("Update failed");
      }

      const updatedStudent = await response.json();

      setStudents((prev) =>
        prev.map((student) =>
          student.id === updatedStudent.id ? updatedStudent : student
        )
      );

      setEditingId(null);

      setFormData({
        name: "",
        email: "",
        phone: "",
        dob: "",
        department: "",
      });
    } catch (err) {
      console.error(err);
    }
  };

  const handleDelete = async (id) => {
    try {
      await fetch(`http://localhost:8081/api/students/${id}`, {
        method: "DELETE",
      });

      setStudents((prev) => prev.filter((student) => student.id !== id));
    } catch (err) {
      console.error(err);
    }
  };

  return (
    <div className="content-area">
      <div className="overview-header">
        <h1>Student Management</h1>

        <button
          className="add-student-btn"
          onClick={() => navigate("/students/add")}
        >
          <Plus size={18} />
          Add Student
        </button>
      </div>

      {editingId && (
        <div className="modal-overlay">
          <div className="edit-modal">
            <h2>Edit Student</h2>

            <input
              type="text"
              placeholder="Name"
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
              value={formData.dob}
              onChange={(e) =>
                setFormData({
                  ...formData,
                  dob: e.target.value,
                })
              }
            />

            <select
              value={formData.department}
              onChange={(e) =>
                setFormData({
                  ...formData,
                  department: e.target.value,
                })
              }
            >
              <option value="">Select Department</option>
              {departments.map((dept) => (
                <option key={dept.id} value={dept.name}>
                  {dept.name}
                </option>
              ))}
            </select>

            <div className="form-buttons">
              <button onClick={handleUpdate}>Save</button>
              <button onClick={() => setEditingId(null)}>Cancel</button>
            </div>
          </div>
        </div>
      )}

      <div className="search-filter">
        <input
          type="text"
          placeholder="Search Name, Email, Phone, Reg No"
          value={search}
          onChange={(e) => setSearch(e.target.value)}
        />

        <select
          value={departmentFilter}
          onChange={(e) => setDepartmentFilter(e.target.value)}
        >
          <option value="">All Departments</option>
          {departments.map((dept) => (
            <option key={dept.id} value={dept.name}>
              {dept.name}
            </option>
          ))}
        </select>

        <button onClick={loadStudents}>Search</button>
      </div>

      <div className="table-card">
        <table className="students-table">
          <thead>
            <tr>
              <th>Registration No</th>
              <th>Student Name</th>
              <th>Date of Birth</th>
              <th>Email Id</th>
              <th>Phone No</th>
              <th>Department</th>
              <th>Edit</th>
              <th>Delete</th>
            </tr>
          </thead>

          <tbody>
            {students.length > 0 ? (
              students.map((student) => (
                <tr key={student.id} className="student-row">
                  <td>{student.regNo}</td>
                  <td>{student.name}</td>
                  <td>{student.dob}</td>
                  <td>{student.email}</td>
                  <td>{student.phone}</td>
                  <td>{student.department}</td>

                  <td>
                    <button
                      className="edit-btn"
                      onClick={(e) => {
                        e.stopPropagation();
                        handleEdit(student);
                      }}
                    >
                      <Edit size={12} />
                    </button>
                  </td>

                  <td>
                    <button
                      className="delete-btn"
                      onClick={(e) => {
                        e.stopPropagation();
                        handleDelete(student.id);
                      }}
                    >
                      <Trash2 size={18} />
                    </button>
                  </td>
                </tr>
              ))
            ) : (
              <tr>
                <td colSpan="8" className="no-data">
                  No students found
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default Dashboard;
