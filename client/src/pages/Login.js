// import React, { useEffect,useState } from 'react';
import { View, Text, Button } from 'react-native';
import { authorize } from 'react-native-app-auth';

const config = {
    issuer: "https://pemuto8-dev1.build.ifs.cloud/auth/realms/pemuto8dev1/.well-known/openid-configuration",
    clientId:"I2S_Task_Card",
    redirectUrl:"https://pemuto8-dev1.build.ifs.cloud/auth/realms/pemuto8dev1/protocol/openid-connect/auth",
    scopes: ['openid', 'profile'],
};

const LoginApp=()=>{
    const signIn = async () => {
        try {
          const result = await authorize(config);
          console.log('Authorization result:', result);
          // Handle successful sign-in
        } catch (error) {
          console.error('Sign-in error:', error);
          // Handle sign-in error
        }
    };
    
    return (
    <View style={{ flex: 1, justifyContent: 'center', alignItems: 'center' }}>
        <Text>Welcome to MyApp</Text>
        <Button title="Sign In" onPress={signIn} />
    </View>
    );
};

export default LoginApp;

    // return (
    //     <div className="flex items-center justify-center h-screen">
    //         <div className="bg-white p-8 rounded shadow-2xl w-96">
    //             <h2 className="text-3xl font-semibold text-center text-gray-800">Login</h2>
    //             <form className="mt-6">
    //                 <div>
    //                     <label className="block text-gray-700">Email Address</label>
    //                     <input type="email" className="w-full px-4 py-2 mt-2 border rounded-lg text-gray-700 focus:outline-none" />
    //                 </div>
    //                 <div className="mt-4">
    //                     <label className="block text-gray-700">Password</label>
    //                     <input type="password" className="w-full px-4 py-2 mt-2 border rounded-lg text-gray-700 focus:outline-none" />
    //                 </div>
    //                 <button type="submit" className="w-full px-16 py-2 mt-6 font-medium text-white uppercase bg-blue-500 rounded-full hover:bg-blue-600">Login</button>
    //             </form>
    //         </div>
    //     </div>
    // ); 

