import { BrowserRouter as Router, Routes, Route, Link } from 'react-router-dom';
import { useState } from 'react';
import Dashboard from './components/Dashboard';
import AddVehicleForm from './components/AddVehicleForm';
import AddCargoForm from './components/AddCargoForm';

function App() {
  const [menuOpen, setMenuOpen] = useState(false);

  const handleLinkClick = () => setMenuOpen(false);
  return (
    <Router>
      <nav className="navbar navbar-expand-lg navbar-dark mb-4" style={{ background: 'rgba(15, 23, 42, 0.8)', borderBottom: '1px solid rgba(255,255,255,0.1)', backdropFilter: 'blur(10px)' }}>
        <div className="container">
          <Link className="navbar-brand fs-4" to="/" onClick={handleLinkClick}>LRP Engine</Link>
          <button className="navbar-toggler" type="button" onClick={() => setMenuOpen(!menuOpen)} style={{ border: 'none', outline: 'none' }}>
            <span className="navbar-toggler-icon"></span>
          </button>
          <div className={`collapse navbar-collapse ${menuOpen ? 'show' : ''}`}>
            <ul className="navbar-nav ms-auto text-center mt-3 mt-lg-0">
              <li className="nav-item">
                <Link className="nav-link py-2" to="/" onClick={handleLinkClick}>Dashboard</Link>
              </li>
              <li className="nav-item">
                <Link className="nav-link py-2" to="/add-vehicle" onClick={handleLinkClick}>Add Vehicle</Link>
              </li>
              <li className="nav-item">
                <Link className="nav-link py-2" to="/add-cargo" onClick={handleLinkClick}>Add Cargo</Link>
              </li>
            </ul>
          </div>
        </div>
      </nav>

      <div className="app-container">
        <Routes>
          <Route path="/" element={<Dashboard />} />
          <Route path="/add-vehicle" element={<AddVehicleForm />} />
          <Route path="/add-cargo" element={<AddCargoForm />} />
        </Routes>
      </div>
    </Router>
  );
}

export default App;
