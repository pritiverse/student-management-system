import React from "react";
import { NavLink, Outlet,Link } from "react-router-dom";

import {
  LayoutDashboard,
  Users,
  Building,
  Search,
  ChevronDown,
  LogOut,
} from "lucide-react";

import "../styles/Navbar.css";

const Layout = () => {
  return (
    <div className="layout">
      {/* Top Navbar */}
      <header className="top-navbar">
        <div className="navbar-left">
          <img
            src="https://img.icons8.com/?size=100&id=sqd7DWqAQksh&format=png&color=000000"
            alt="logo"
            width="35"
          />

          <div>
            <h2 className="logo-title">SMS</h2>
            <p className="logo-subtitle">Student Management</p>
          </div>
        </div>

        <div className="navbar-right">
          

          <div className="user-profile">
            <div className="avatar">AD</div>
            <span>Admin</span>
            <ChevronDown size={16} />
          </div>
        </div>
      </header>

      {/* Main Body */}
      <div className="body-container">
        {/* Sidebar */}
        <aside className="sidebar">
          <NavLink
            to="/dashboard"
            className={({ isActive }) =>
              isActive ? "sidebar-link active" : "sidebar-link"
            }
          >
            <LayoutDashboard size={20} />
            <span>Dashboard</span>
          </NavLink>

          <NavLink
            to="/students"
            className={({ isActive }) =>
              isActive ? "sidebar-link active" : "sidebar-link"
            }
          >
            <Users size={20} />
            <span>Students</span>
          </NavLink>

          <NavLink
            to="/departments"
            className={({ isActive }) =>
              isActive ? "sidebar-link active" : "sidebar-link"
            }
          >
            <Building size={20} />
            <span>Departments</span>
          </NavLink>

          <div className="sidebar-footer">
          <Link to="/">
            <button className="logout-btn">
              <LogOut size={20} />
              <span>Logout</span>
            </button> </Link>
          </div>
        </aside>

        {/* Page Content */}
        <main className="content">
          <Outlet />
        </main>
      </div>
    </div>
  );
};

export default Layout;