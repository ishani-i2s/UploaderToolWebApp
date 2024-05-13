import React from 'react';
import {useNavigate} from 'react-router-dom';


export default function Home() {

    const navigate = useNavigate();
    const navigateToFO = () => {
        // 👇️ Navigate to /contacts
        navigate('/fileUpload');
      };

    const navigateToFA = () => {
        // 👇️ Navigate to /contacts
        navigate('/fixedAssets');
      };
    
    return (
        <div style={{ display: 'flex', flexDirection: 'column', height: '100vh' }}>
          {/* Top Navigation Bar */}
          <div style={{ backgroundColor: '#333', color: 'white', padding: '10px' }}>
            {/* Your navigation items go here */}
            <ul style={{ listStyleType: 'none', margin: 0, padding: 0 }}>
              <li style={{ display: 'inline', marginRight: '10px' }}>Home</li>
              <li style={{ display: 'inline', marginRight: '10px' }}>About</li>
              <li style={{ display: 'inline', marginRight: '10px' }}>Contact</li>
            </ul>
          </div>
    
        {/* Main Content */}
        <div style={{ flex: 1, display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', backgroundColor: '#f4f4f4', padding: '50px' }}>
        <h1 style={{ marginBottom: '40px', color: '#333', fontSize: '2rem', fontWeight: 'bold', textAlign: 'center' }}>Welcome to UPLOADER TOOL</h1>
        {/* Your buttons */}
        <div style={{ display: 'flex', justifyContent: 'center' }}>
            <button style={{ padding: '12px 24px', margin: '0 10px', fontSize: '1rem', borderRadius: '8px', backgroundColor: '#007bff', color: '#fff', border: 'none', cursor: 'pointer', boxShadow: '0 4px 6px rgba(0, 0, 0, 0.1)', transition: 'background-color 0.3s' }} onClick={navigateToFO}>Functional Objects</button>
            <button style={{ padding: '12px 24px', margin: '0 10px', fontSize: '1rem', borderRadius: '8px', backgroundColor: '#28a745', color: '#fff', border: 'none', cursor: 'pointer', boxShadow: '0 4px 6px rgba(0, 0, 0, 0.1)', transition: 'background-color 0.3s' }} onClick={navigateToFA}>Fixed Assets</button>
        </div>
        </div>
    
          {/* Footer */}
          <div style={{ backgroundColor: '#333', color: 'white', padding: '10px', textAlign: 'center' }}>
            {/* Your footer content */}
            <p>© 2024 My Website. All rights reserved.</p>
          </div>
        </div>
      );
}