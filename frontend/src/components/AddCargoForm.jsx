import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { addCargo } from '../services/api';

const AddCargoForm = () => {
  const navigate = useNavigate();
  const [type, setType] = useState('StandardCargo');
  const [formData, setFormData] = useState({
    weight: '',
    destinationDistance: '',
    requiredTemperature: '',
    hazmatCode: '',
    maxGForce: '',
    viscosity: '',
    declaredValue: ''
  });

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const payload = {
        type: type,
        weight: parseFloat(formData.weight),
        destinationDistance: parseFloat(formData.destinationDistance),
        status: 'PENDING'
      };

      if (type === 'PerishableCargo') {
        payload.requiredTemperature = parseFloat(formData.requiredTemperature);
      } else if (type === 'HazardousCargo') {
        payload.hazmatCode = formData.hazmatCode;
      } else if (type === 'FragileCargo') {
        payload.maxGForce = parseFloat(formData.maxGForce);
      } else if (type === 'LiquidCargo') {
        payload.viscosity = parseFloat(formData.viscosity);
      } else if (type === 'ValuableCargo') {
        payload.declaredValue = parseFloat(formData.declaredValue);
      }

      await addCargo(payload);
      navigate('/');
    } catch (err) {
      console.error(err);
      alert('Error adding cargo');
    }
  };

  return (
    <div className="row justify-content-center">
      <div className="col-md-6">
        <div className="glass-card">
          <h3 className="mb-4">Add Pending Cargo</h3>
          <form onSubmit={handleSubmit}>
            <div className="mb-3">
              <label className="form-label">Cargo Type</label>
              <select className="form-select" value={type} onChange={(e) => setType(e.target.value)}>
                <option value="StandardCargo">Standard Cargo</option>
                <option value="PerishableCargo">Perishable Cargo</option>
                <option value="HazardousCargo">Hazardous Cargo</option>
                <option value="FragileCargo">Fragile Cargo</option>
                <option value="LiquidCargo">Liquid Cargo</option>
                <option value="ValuableCargo">Valuable Cargo</option>
              </select>
            </div>

            <div className="mb-3">
              <label className="form-label">Weight (kg)</label>
              <input type="number" step="0.1" className="form-control" required
                value={formData.weight} onChange={e => setFormData({...formData, weight: e.target.value})} />
            </div>

            <div className="mb-3">
              <label className="form-label">Destination Distance (km)</label>
              <input type="number" step="0.1" className="form-control" required
                value={formData.destinationDistance} onChange={e => setFormData({...formData, destinationDistance: e.target.value})} />
            </div>

            {type === 'PerishableCargo' && (
              <div className="mb-4">
                <label className="form-label">Required Max Temperature (°C)</label>
                <input type="number" step="0.1" className="form-control" required
                  value={formData.requiredTemperature} onChange={e => setFormData({...formData, requiredTemperature: e.target.value})} />
              </div>
            )}
            
            {type === 'HazardousCargo' && (
              <div className="mb-4">
                <label className="form-label">Hazmat Certification Code</label>
                <input type="text" className="form-control" required
                  value={formData.hazmatCode} onChange={e => setFormData({...formData, hazmatCode: e.target.value})} />
              </div>
            )}
            
            {type === 'FragileCargo' && (
              <div className="mb-4">
                <label className="form-label">Max Allowed G-Force</label>
                <input type="number" step="0.1" className="form-control" required
                  value={formData.maxGForce} onChange={e => setFormData({...formData, maxGForce: e.target.value})} />
              </div>
            )}
            
            {type === 'LiquidCargo' && (
              <div className="mb-4">
                <label className="form-label">Kinematic Viscosity (cSt)</label>
                <input type="number" step="0.1" className="form-control" required
                  value={formData.viscosity} onChange={e => setFormData({...formData, viscosity: e.target.value})} />
              </div>
            )}
            
            {type === 'ValuableCargo' && (
              <div className="mb-4">
                <label className="form-label">Declared Item Value ($USD)</label>
                <input type="number" step="0.01" className="form-control" required
                  value={formData.declaredValue} onChange={e => setFormData({...formData, declaredValue: e.target.value})} />
              </div>
            )}

            <button type="submit" className="btn btn-primary w-100">Add Cargo Request</button>
          </form>
        </div>
      </div>
    </div>
  );
};

export default AddCargoForm;
