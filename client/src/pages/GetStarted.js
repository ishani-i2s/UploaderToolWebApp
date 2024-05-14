import React from 'react';
import {useNavigate} from 'react-router-dom';
import { useEffect} from 'react';
import axios from 'axios';

export default function Started() {

    // useEffect(() => {
    //   const extraxtCodeFromUrl = () => {
    //     const urlParams = new URLSearchParams(window.location.search);
    //     return urlParams.get('code');
    //   };

    //   const extractstateFromUrl = () => {
    //     const urlParams = new URLSearchParams(window.location.search);
    //     return urlParams.get('session_state');
    //   }
    //   const code = extraxtCodeFromUrl();
    //   const state = extractstateFromUrl();
    //   console.log('Code:', code);
    //   console.log('State:', state);

    //   const getAccessToken = async (code) => {
    //     try {
    //       const response = await axios.post('https://pemuto8-dev1.build.ifs.cloud/auth/realms/pemuto8dev1/protocol/openid-connect/token', {
    //         code: code,
    //         grant_type: 'authorization_code',
    //         client_id: 'I2S_TASK_CARD',
    //         code_verifier: 'a1dlzZXCfHqdfb44kvTKkBHyfJzBPb03',
    //         state: state,
    //       });
  
    //       // Access token will be in the response
    //       const accessToken = response.data.access_token;
    //       console.log('Access Token:', accessToken);
  
    //       // Now you can use the access token for further API requests
    //     } catch (error) {
    //       console.error('Error exchanging code for access token:', error);
    //     }
    //   };
  
    //   if (code) {
    //     // If code exists, call the function to get access token
    //     getAccessToken(code);
    //   }
    // }, []);

    const navigate = useNavigate();

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
            <h1>Logged User</h1>
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