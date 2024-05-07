import { useState } from 'react';
import axios from 'axios';

function FileUpload() {
    const [name, setName] = useState([]);
    const [file, setFile] = useState(null);
    const [res, setRes] = useState([]);

    const baseURL = 'http://localhost:8080';

    const DownloadFile = (response) => {
        const contentDispositionHeader = response.headers['content-disposition'];
        const fileName = contentDispositionHeader
            ? contentDispositionHeader.split('filename=')[1].trim()
            : 'downloadedFile.xls';
    
        const blob = new Blob([response.data], { type: 'application/vnd.ms-excel' });
        const url = window.URL.createObjectURL(blob);
        const link = document.createElement('a');
        link.href = url;
        link.setAttribute('download', fileName);
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
    };    

    const handleFileUpload = () => {
        console.log(file);
        //multipart request
        const formData = new FormData();
        formData.append('file', file);
        formData.append('Name', name);
        axios.post(`${baseURL}/api/excelUpload`, formData)
        .then(response => {
            console.log(response);
            setRes(response.data);
        })
        .catch(err => {
            console.log(err);
            setRes(err.message);
        })
    }

    const handleFileDownload = () => {
        axios.get(`${baseURL}/api/excelDownload`, { responseType: 'blob' })
        .then(response => {
           DownloadFile(response);
        })
        .catch(err => {
            console.log(err);
            setRes(err.message);
        })
    }

    const setDetails = (e) => {
        const file = e.target.files[0];
        setName(file.name);
        setFile(file);
    }
  
    return (
        <div className="App">
        <h1 className='text-4xl text-blue-400 p-4'>File Upload To Server</h1>
        <center>
            <input type="file" onChange={setDetails} />
        </center>

        <br />

        <button className="bg-sky-400 p-2 rounded-lg" onClick={handleFileUpload}>
            Upload File
        </button>

        <br />
        <br />

        <hr />
        <br />

        {res === 'Errors' && (
            <>
                <p className="text-red-500">There are few errors in the file. Please download the error file and correct the errors.</p>
                <button className="bg-sky-400 p-2 rounded-lg" onClick={handleFileDownload}>
                    Download Error File
                </button>
            </>
        )} 

        </div>
    );
}

export default FileUpload;
