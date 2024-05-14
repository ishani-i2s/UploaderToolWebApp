import './App.css';
import './index.css';
import { Routes, Route, BrowserRouter } from 'react-router-dom';
import FileUpload from './pages/FileUpload';
import AuthComponent from './pages/Login';
import Home from './pages/Home';
import FixedAssets from './pages/FixedAssets';
import Started from './pages/GetStarted';

export default function App() {
  return (
    <div>
      <BrowserRouter>
        <Routes>
          <Route path="/" element={<Started/>} />
          <Route path="/login" element={<AuthComponent />} />
          <Route path="/home" element={<Home />} />
          <Route path="/fileUpload" element={<FileUpload />} />
          <Route path="/fixedAssets" element={<FixedAssets />} />
        </Routes>
      </BrowserRouter>
    </div>
  );
}

