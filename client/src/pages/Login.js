import React, { useContext, useState,useEffect } from 'react';
import { AuthContext, AuthProvider} from "react-oauth2-code-pkce";
import { createRoot } from "react-dom/client";

const getRoandomState = () => {
  return Math.random().toString(36).substring(2, 15) + Math.random().toString(36).substring(2, 15);
}

const authConfig = {
  clientId: 'I2S_TASK_CARD',
  authorizationEndpoint: 'https://pemuto8-dev1.build.ifs.cloud/auth/realms/pemuto8dev1/protocol/openid-connect/auth',
  redirectUri: 'http://localhost:3000/',
  tokenEndpoint: 'https://pemuto8-dev1.build.ifs.cloud/auth/realms/pemuto8dev1/protocol/openid-connect/token',
  scope: 'openid microprofile-jwt',
  state:getRoandomState(),
  nonce: getRoandomState(),
  onRefreshTokenExpire: (event) => window.confirm('Session expired. Refresh page to continue using the site?') && event.login(),
}

const UserInfo = () => {
  const { authState } = useContext(AuthContext);
  const [accessToken, setAccessToken] = useState(null);

  // Listen for changes in authState and save accessToken
  // useEffect(() => {
  //   if (authState.accessToken) {
  //     setAccessToken(authState.accessToken);
  //   }else{
  //     setAccessToken(null);
  //   }
  // }, [authState.accessToken]);
  if(authState){
    console.log(authState);
  }

  return (
    <div>
      <h1>Logged User</h1>
    </div>
    // <div>
    //   {/* Render user information if available */}
    //   {authState.user && (
    //     <div>
    //       <p>User ID: {authState.user.sub}</p>
    //       <p>Name: {authState.user.name}</p>
    //       {/* Add more user details as needed */}
    //     </div>
    //   )}

    //   {/* Display access token */}
    //   {accessToken && (
    //     <p>Access Token: {accessToken}</p>
    //   )}
    // </div>
  );
};


const LoginCard = () => {
  return (
    createRoot(document.getElementById('root')).render(
      <AuthProvider authConfig={authConfig}>
      </AuthProvider>
    )
  );
};

export default LoginCard;
