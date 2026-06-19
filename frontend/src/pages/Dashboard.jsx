import React, { useEffect, useState } from "react";
import { Users, Building } from "lucide-react";

import "../styles/Dashboard.css";

const Dashboard = () => {
  const [students, setStudents] = useState([]);
  const [studentCount, setStudentCount] = useState(0);
  const [departmentCount, setDepartmentCount] = useState(0);

  useEffect(() => {
    fetch("http://localhost:8081/api/students")
      .then((res) => {
        if (!res.ok) {
          throw new Error("Failed to fetch students");
        }
        return res.json();
      })
      .then((data) => setStudents(data))
      .catch((err) => console.error(err));

    fetch("http://localhost:8081/api/students/count")
      .then((res) => res.json())
      .then((data) => setStudentCount(data))
      .catch((err) => console.error(err));

    fetch("http://localhost:8081/api/departments/count")
      .then((res) => res.json())
      .then((data) => setDepartmentCount(data))
      .catch((err) => console.error(err));
  }, []);

  return (
    <div className="content-area">
      <div className="overview-header">
        <h1>Dashboard Overview</h1>
      </div>
      <div className="stats-cards">
        <div
          className="stat-card clickable"
          onClick={() => console.log("Students")}
        >
          <div className="stat-info">
            <span className="stat-label">Total Students</span>
            <span className="stat-value">{studentCount}</span>
          </div>

          <div className="stat-icon blue-icon">
            <Users size={24} color="white" />
          </div>
        </div>

        <div
          className="stat-card clickable"
          onClick={() => console.log("Departments")}
        >
          <div className="stat-info">
            <span className="stat-label">Total Departments</span>
            <span className="stat-value">{departmentCount}</span>
          </div>

          <div className="stat-icon green-icon">
            <Building size={24} color="white" />
          </div>
        </div>
      </div>

      {/* Recent Students */}
      <div className="table-card">
        <div className="table-header">
          <h3>Recent Students</h3>
        </div>

        <table className="students-table">
          <thead>
            <tr>
              <th>Registration No</th>
              <th>Student Name</th>
              <th>Email Id</th>
              <th>Department</th>
            </tr>
          </thead>

          <tbody>
            {students.length > 0 ? (
              students.slice(0, 5).map((student) => (
                <tr
                  key={student.id}
                  className="student-row"
                  onClick={() => console.log(student.id)}
                >
                  <td>{student.regNo}</td>
                  <td>{student.name}</td>
                  <td>{student.email}</td>
                  <td>
                    <span className="dept-badge">
                      {student.department}
                    </span>
                  </td>
                </tr>
              ))
            ) : (
              <tr>
                <td colSpan={4} className="no-data">
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