import React from 'react';
import {useNavigate} from 'react-router-dom';

export default function LoginCard() {

    const navigate = useNavigate();
    const navigateToHome = () => {
        // 👇️ Navigate to /contacts
        navigate('/home');
      };

    return (
        <div className="flex items-center justify-center h-screen">
            <div className="bg-white p-8 rounded shadow-2xl w-96">
                <h2 className="text-3xl font-semibold text-center text-gray-800">Login</h2>
                <form className="mt-6">
                    <div>
                        <label className="block text-gray-700">Email Address</label>
                        <input type="email" className="w-full px-4 py-2 mt-2 border rounded-lg text-gray-700 focus:outline-none" />
                    </div>
                    <div className="mt-4">
                        <label className="block text-gray-700">Password</label>
                        <input type="password" className="w-full px-4 py-2 mt-2 border rounded-lg text-gray-700 focus:outline-none" />
                    </div>
                    <button type="submit" className="w-full px-16 py-2 mt-6 font-medium text-white uppercase bg-blue-500 rounded-full hover:bg-blue-600" onClick={navigateToHome}>Login</button>
                </form>
            </div>
        </div>
    ); 
}
