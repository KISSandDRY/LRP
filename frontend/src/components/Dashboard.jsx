import { useState, useEffect } from 'react';
import { getVehicles, getCargo, runOptimization, getRoutes } from '../services/api';

const Dashboard = () => {
  const [vehicles, setVehicles] = useState([]);
  const [cargos, setCargos] = useState([]);
  const [routes, setRoutes] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [fuelPrice, setFuelPrice] = useState(1.50);

  useEffect(() => {
    if (routes.length === 0 || !fuelPrice) return;
    const bounce = setTimeout(() => {
      handleRunOptimization();
    }, 100);
    return () => clearTimeout(bounce);
  // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [fuelPrice]);

  const fetchData = async () => {
    try {
      const [vRes, cRes] = await Promise.all([getVehicles(), getCargo()]);
      setVehicles(vRes.data);
      setCargos(cRes.data);

    } catch (err) {
      console.error(err);
      setError("Failed to fetch initial data.");
    }
  };

  useEffect(() => {
    fetchData();
  }, []);

  const handleRunOptimization = async () => {
    setLoading(true);
    setError(null);
    try {
      const res = await runOptimization(fuelPrice);
      setRoutes(res.data);
      if (res.data.length === 0) {
        setError("Algorithm ran successfully, but no valid routes could be generated with available resources.");
      }
      // Refresh vehicles and cargo statuses
      fetchData();
    } catch (err) {
      console.error(err);
      setError(err.response?.data?.error || "Error running optimization");
    } finally {
      setLoading(false);
    }
  };

  const isOptimizationDisabled = loading || cargos.length === 0 || vehicles.length === 0;

  return (
    <div>
      <div className="d-flex justify-content-between align-items-center mb-5">
        <div>
          <h2 className="display-6 fw-bold mb-0 text-white">Logistics Dashboard</h2>
          <p className="mt-2 fs-5 text-light opacity-75 mb-1">Manage assignments utilizing the Simplex Transportation Engine.</p>
        </div>
        <div className="d-flex align-items-center gap-3">
          <div className="glass-input-wrapper shadow-sm">
            <span className="text-info fw-bold opacity-75">$</span>
            <input 
              type="number" 
              className="glass-input" 
              value={fuelPrice} 
              onChange={e => setFuelPrice(e.target.value)} 
              step="0.01"
              min="0.01"
              placeholder="Price"
            />
            <span className="text-info fw-bold opacity-75">/L</span>
          </div>
          <button 
            className="btn btn-primary shadow-lg d-flex align-items-center gap-2" 
            onClick={handleRunOptimization} 
            disabled={isOptimizationDisabled}
          >
            {loading ? (
               <>
                 <span className="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span>
                 Optimizing Data...
               </>
            ) : (
              'BUILD ROUTES'
            )}
          </button>
        </div>
      </div>

      {error && <div className="alert alert-danger shadow border-0 mb-4">{error}</div>}

      <div className="row g-4 mb-4">
        <div className="col-12 col-xl-6">
          <div className="glass-card h-100">
            <h4 className="mb-4 text-info fw-bold">Active Fleet</h4>
            <div className="table-responsive">
              <table className="table table-hover align-middle">
                <thead>
                  <tr>
                    <th>Type</th>
                    <th>Subclass</th>
                    <th>Capacity</th>
                    <th>Status</th>
                  </tr>
                </thead>
                <tbody>
                  {vehicles.map(v => (
                    <tr key={v.id}>
                      <td className="fw-medium">#{v.id}</td>
                      <td>
                        {v.type === 'Truck' ? (
                           <span className="text-light">Truck <small className="text-light opacity-50">(Max Axle: {v.maxAxleLoad}kg)</small></span>
                        ) : v.type === 'RefrigeratedVan' ? (
                           <span className="text-info">Ref. Van <small className="text-light opacity-50">(Min Temp: {v.minTemperature}°C)</small></span>
                        ) : 'Unknown'}
                      </td>
                      <td>{v.capacityWeight} kg</td>
                      <td><span className={`badge bg-${v.status}`}>{v.status.replace('_', ' ')}</span></td>
                    </tr>
                  ))}
                  {vehicles.length === 0 && <tr><td colSpan="4" className="text-center py-4 text-muted">No vehicles present in the database.</td></tr>}
                </tbody>
              </table>
            </div>
          </div>
        </div>

        <div className="col-12 col-xl-6">
          <div className="glass-card h-100">
            <h4 className="mb-4 text-warning fw-bold">Cargo Registry</h4>
            <div className="table-responsive">
              <table className="table table-hover align-middle">
                <thead>
                  <tr>
                    <th>ID</th>
                    <th>Cargo Type</th>
                    <th>Weight</th>
                    <th>Distance</th>
                    <th>Status</th>
                  </tr>
                </thead>
                <tbody>
                  {cargos.map(c => (
                    <tr key={c.id}>
                      <td className="fw-medium">#{c.id}</td>
                      <td>
                        {c.type === 'StandardCargo' ? 'Standard' : (
                          <span className="text-info">Perishable <small className="text-light opacity-50">(Max: {c.requiredTemperature}°C)</small></span>
                        )}
                      </td>
                      <td>{c.weight} kg</td>
                      <td>{c.destinationDistance} km</td>
                      <td><span className={`badge bg-${c.status}`}>{c.status}</span></td>
                    </tr>
                  ))}
                  {cargos.length === 0 && <tr><td colSpan="5" className="text-center py-4 text-muted">No cargo orders mapped.</td></tr>}
                </tbody>
              </table>
            </div>
          </div>
        </div>
      </div>

      {routes.length > 0 && (
        <div className="glass-card mt-5 fade-in border-success">
          <h4 className="mb-4 text-success fw-bold">Resolved Optimized Manifest</h4>
          <div className="table-responsive">
            <table className="table table-hover align-middle">
              <thead>
                <tr>
                  <th>Vehicle ID</th>
                  <th>Cargo Reference</th>
                  <th>Assigned Load</th>
                  <th>Computational Cost ($)</th>
                  <th>Proj. Fuel Demand</th>
                </tr>
              </thead>
              <tbody>
                {routes.map(r => (
                  <tr key={r.id}>
                    <td className="fw-bold">Vehicle #{r.vehicle.id}</td>
                    <td className="text-light fw-medium"><span className="text-light opacity-50">Cargo</span> #{r.cargo.id}</td>
                    <td className="text-warning fw-medium">{r.assignedWeight.toFixed(2)} kg</td>
                    <td className="text-success fw-bold">${r.totalCost.toFixed(2)}</td>
                    <td>{r.estimatedFuelUsage.toFixed(2)} Liters</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}
    </div>
  );
};

export default Dashboard;
