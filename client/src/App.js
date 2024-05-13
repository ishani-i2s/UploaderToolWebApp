// import './App.css';
// import './index.css';
// import FileUpload from './pages/FileUpload';
// import LoginCard from './pages/Login';

// function App() {
//   return (
//     <div className="App">
//       {/* <FileUpload /> */}
//       <LoginCard />
//     </div>
//   );
// }

// export default App;



import './App.css';
import './index.css';
import { Routes, Route, BrowserRouter } from 'react-router-dom';
import FileUpload from './pages/FileUpload';
import LoginCard from './pages/Login';
import Home from './pages/Home';
import FixedAssets from './pages/FixedAssets';

export default function App() {
  return (
    <div>
      <BrowserRouter>
        <Routes>
          <Route path="/" element={<LoginCard />} />
          <Route path="/home" element={<Home />} />
          <Route path="/fileUpload" element={<FileUpload />} />
          <Route path="/fixedAssets" element={<FixedAssets />} />
        </Routes>
      </BrowserRouter>
    </div>
  );
}


