import React, { useEffect, useState } from "react";
import { Edit, Trash2, Plus } from "lucide-react";
import { useNavigate } from "react-router-dom";
import "../styles/Departments.css";

const Departments = () => {
  const [departments, setDepartments] = useState([]);
  const [departmentCount, setDepartmentCount] = useState(0);
  const [departmentCounts, setDepartmentCounts] = useState({});
  const [editingId, setEditingId] = useState(null);
  const [students, setStudents] = useState([]);
  const [showAddModal, setShowAddModal] = useState(false);

  const [formData, setFormData] = useState({
    name: "",
    code: "",
  });

  const navigate = useNavigate();

  useEffect(() => {
    fetch("http://localhost:8081/api/departments")
      .then((res) => res.json())
      .then((data) => setDepartments(data))
      .catch((err) => console.error(err));

    fetch("http://localhost:8081/api/departments/count")
      .then((res) => res.json())
      .then((data) => setDepartmentCount(data))
      .catch((err) => console.error(err));
    
      fetch("http://localhost:8081/api/students")
      .then((res) => res.json())
      .then((data) => setStudents(data))
      .catch((err) => console.error(err));

      fetch("http://localhost:8081/api/students/count-by-department")
      .then((res) => res.json())
      .then((data) => {
        const counts = {};
    
        data.forEach(([departmentId, count]) => {
          counts[departmentId] = count;
        });
    
        setDepartmentCounts(counts);
      })
      .catch((err) => console.error(err));



  }, []);
  const handleEdit = (department) => {
    setEditingId(department.id);
  
    setFormData({
      name: department.name,
      code: department.code,
    });
  };
  
  const getStudentCountByDepartment = (departmentName) => {
    return students.filter(
      (student) => student.department === departmentName
    ).length;
  };
  
  const handleCreate = async () => {
    if (!formData.name.trim() || !formData.code.trim()) {
      alert("Please fill all required fields");
      return;
    }
  
    try {
      const response = await fetch(
        "http://localhost:8081/api/departments",
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify(formData),
        }
      );
  
      if (!response.ok) {
        throw new Error("Create failed");
      }
  
      const newDepartment = await response.json();
  
      setDepartments((prev) => [...prev, newDepartment]);
      setDepartmentCount((prev) => prev + 1);
  
      setShowAddModal(false);
  
      setFormData({
        name: "",
        code: "",
      });
    } catch (err) {
      console.error(err);
    }
  };
  
  const handleUpdate = async () => {
    if (!formData.name.trim() || !formData.code.trim()) {
      alert("Please fill all required fields");
      return;
    }
  
    try {
      const response = await fetch(
        `http://localhost:8081/api/departments/${editingId}`,
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
  
      const updatedDepartment = await response.json();
  
      setDepartments((prev) =>
        prev.map((department) =>
          department.id === updatedDepartment.id
            ? updatedDepartment
            : department
        )
      );
  
      setEditingId(null);
  
      setFormData({
        name: "",
        code: "",
      });
    } catch (err) {
      console.error(err);
    }
  };
  const handleDelete = async (id) => {
    try {
      const response = await fetch(
        `http://localhost:8081/api/departments/${id}`,
        {
          method: "DELETE",
        }
      );

      if (!response.ok) {
        throw new Error("Delete failed");
      }

      setDepartments((prev) =>
        prev.filter((department) => department.id !== id)
      );

      setDepartmentCount((prev) => prev - 1);
    } catch (err) {
      console.error(err);
    }
  };

  return (
    <div className="department-form-card">
    <div className="form-header">
    {editingId ? (
  <h3>Department Update</h3>):(<h3>Add New Department</h3>)}
</div>

  
    <div className="department-form">
      <div className="form-group">
        <label>Department Name</label>
        <input
          type="text"
          placeholder="e.g. Computer Science" required 
          value={formData.name}
          onChange={(e) =>
            setFormData({
              ...formData,
              name: e.target.value,
            })
          }
        />
      </div>
  
      <div className="form-group">
        <label>Department Code</label>
        <input
          type="text"
          placeholder="e.g. CSE" required 
          value={formData.code}
          onChange={(e) =>
            setFormData({
              ...formData,
              code: e.target.value,
            })
          }
        />
      </div> 

      {editingId ? (
        
  <button
    className="update-department-btn"
    onClick={handleUpdate}
  >
    Update
  </button>
  
) : (

  <button
    className="add-department-btn"
    onClick={handleCreate}
  >
    <Plus size={12} />
    Add
  </button>
)}
{editingId && (
  <button
    className="cancel-btn"
    onClick={() => {
      setEditingId(null);
      setFormData({
        name: "",
        code: "",
      });
    }}
  >
    Cancel
  </button>
)}


    </div>


      <div className="table-card">
        <table className="departments-table">
<thead>
<tr>
  <th>Sr No</th>
  <th>Department Name</th>
  <th>Department Code</th>
  <th>Students</th>
  <th>Edit</th>
  <th>Delete</th>
</tr>
          </thead>

          <tbody>
            {departments.length > 0 ? (
              departments.slice(0, 5).map((department, index) => (
                <tr key={department.id}>
  <td>{index + 1}</td>
  <td>{department.name}</td>
  <td>{department.code}</td>

  <td>
    {getStudentCountByDepartment(department.name)}
  </td>

  <td>
    <button
      className="edit-btn"
      onClick={() => handleEdit(department)}
    >
      <Edit size={16} />
    </button>
  </td>

  <td>
    <button
      className="delete-btn"
      onClick={() => handleDelete(department.id)}
    >
      <Trash2 size={16} />
    </button>
  </td>
</tr>
              ))
            ) : (
              <tr>
                <td colSpan="5" className="no-data">
                  No department found
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default Departments;