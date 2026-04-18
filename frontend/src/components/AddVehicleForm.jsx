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
    minTemperature: ''
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
      } else {
        payload.minTemperature = parseFloat(formData.minTemperature);
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

            <button type="submit" className="btn btn-primary w-100">Register Vehicle</button>
          </form>
        </div>
      </div>
    </div>
  );
};

export default AddVehicleForm;
