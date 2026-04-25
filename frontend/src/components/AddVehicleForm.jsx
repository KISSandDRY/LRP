import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { addVehicle } from '../services/api';

const AddVehicleForm = () => {
  const navigate = useNavigate();
  const [type, setType] = useState('Truck');
  const [formData, setFormData] = useState({
    capacityWeight: '',
    fuelConsumptionPer100km: '',
    maxAxleLoad: '',
    minTemperature: '',
    securityLevel: 5,
    hasBaffles: true,
    shockAbsorptionRating: 10.0
  });

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const payload = {
        type: type,
        capacityWeight: parseFloat(formData.capacityWeight),
        fuelConsumptionPer100km: parseFloat(formData.fuelConsumptionPer100km),
        status: 'AVAILABLE'
      };

      if (type === 'Truck') {
        payload.maxAxleLoad = parseFloat(formData.maxAxleLoad);
      } else if (type === 'RefrigeratedVan') {
        payload.minTemperature = parseFloat(formData.minTemperature);
      } else if (type === 'ArmoredTransport') {
        payload.securityLevel = parseInt(formData.securityLevel);
      } else if (type === 'TankerTruck') {
        payload.hasBaffles = formData.hasBaffles === 'true' || formData.hasBaffles === true;
      } else if (type === 'AirRideTruck') {
        payload.shockAbsorptionRating = parseFloat(formData.shockAbsorptionRating);
      }

      await addVehicle(payload);
      navigate('/');
    } catch (err) {
      console.error(err);
      alert('Error creating vehicle');
    }
  };

  return (
    <div className="row justify-content-center">
      <div className="col-md-6">
        <div className="glass-card">
          <h3 className="mb-4">Add New Vehicle</h3>
          <form onSubmit={handleSubmit}>
            <div className="mb-3">
              <label className="form-label">Vehicle Type</label>
              <select className="form-select" value={type} onChange={(e) => setType(e.target.value)}>
                <option value="Truck">Standard Truck</option>
                <option value="RefrigeratedVan">Refrigerated Van</option>
                <option value="FlatbedTruck">Flatbed Truck</option>
                <option value="ArmoredTransport">Armored Transport</option>
                <option value="TankerTruck">Tanker Truck</option>
                <option value="AirRideTruck">AirRide Truck</option>
              </select>
            </div>

            <div className="mb-3">
              <label className="form-label">Capacity Weight (kg)</label>
              <input type="number" step="0.1" className="form-control" required
                value={formData.capacityWeight} onChange={e => setFormData({...formData, capacityWeight: e.target.value})} />
            </div>

            <div className="mb-3">
              <label className="form-label">Fuel Consumption (L / 100km)</label>
              <input type="number" step="0.1" className="form-control" required
                value={formData.fuelConsumptionPer100km} onChange={e => setFormData({...formData, fuelConsumptionPer100km: e.target.value})} />
            </div>

            {type === 'Truck' && (
              <div className="mb-3">
                <label className="form-label">Max Axle Load</label>
                <input type="number" step="0.1" className="form-control" required
                  value={formData.maxAxleLoad} onChange={e => setFormData({...formData, maxAxleLoad: e.target.value})} />
              </div>
            )}

            {type === 'RefrigeratedVan' && (
              <div className="mb-4">
                <label className="form-label">Min Temperature (°C)</label>
                <input type="number" step="0.1" className="form-control" required
                  value={formData.minTemperature} onChange={e => setFormData({...formData, minTemperature: e.target.value})} />
              </div>
            )}

            {type === 'ArmoredTransport' && (
              <div className="mb-4">
                <label className="form-label">Security Tier Level</label>
                <input type="number" step="1" className="form-control" required
                  value={formData.securityLevel} onChange={e => setFormData({...formData, securityLevel: e.target.value})} />
              </div>
            )}
            
            {type === 'TankerTruck' && (
              <div className="mb-4">
                <label className="form-label">Internal Baffling Structure</label>
                <select className="form-select" value={formData.hasBaffles} onChange={e => setFormData({...formData, hasBaffles: e.target.value})}>
                  <option value={true}>Baffled (Anti-Slosh)</option>
                  <option value={false}>Unbaffled</option>
                </select>
              </div>
            )}
            
            {type === 'AirRideTruck' && (
              <div className="mb-4">
                <label className="form-label">Shock Absorption (G-Rating Limit)</label>
                <input type="number" step="0.1" className="form-control" required
                  value={formData.shockAbsorptionRating} onChange={e => setFormData({...formData, shockAbsorptionRating: e.target.value})} />
              </div>
            )}

            <button type="submit" className="btn btn-primary w-100">Register Vehicle</button>
          </form>
        </div>
      </div>
    </div>
  );
};

export default AddVehicleForm;
