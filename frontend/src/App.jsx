import { BrowserRouter as Router, Routes, Route, Link } from 'react-router-dom';
import Dashboard from './components/Dashboard';
import AddVehicleForm from './components/AddVehicleForm';
import AddCargoForm from './components/AddCargoForm';

function App() {
  return (
    <Router>
      <nav className="navbar navbar-expand-lg navbar-dark mb-4" style={{ background: 'rgba(15, 23, 42, 0.8)', borderBottom: '1px solid rgba(255,255,255,0.1)', backdropFilter: 'blur(10px)' }}>
        <div className="container">
          <Link className="navbar-brand fs-4" to="/">LRP Engine</Link>
          <div className="collapse navbar-collapse">
            <ul className="navbar-nav ms-auto">
              <li className="nav-item">
                <Link className="nav-link" to="/">Dashboard</Link>
              </li>
              <li className="nav-item">
                <Link className="nav-link" to="/add-vehicle">Add Vehicle</Link>
              </li>
              <li className="nav-item">
                <Link className="nav-link" to="/add-cargo">Add Cargo</Link>
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
