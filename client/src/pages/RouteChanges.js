import * as React from 'react';
import Box from '@mui/material/Box';
import TextField from '@mui/material/TextField';
import MenuItem from '@mui/material/MenuItem';
import Navbar from '../Components/Navbar';
import Stack from '@mui/material/Stack';
import Button from '@mui/material/Button';
import { useEffect } from 'react';
import axios from 'axios';
import { useState } from 'react';
import { set } from 'date-fns';
import DoughnutChart from '../Components/CustomDoughnutChart';
import { ProgressBar } from 'react-bootstrap';

const baseURL = 'http://localhost:8080';

export default function RouteChanges() {
    const [sites, setSites] = useState([]);
    const [selectedSite, setSelectedSite] = useState('');
    const [selectedStatus, setSelectedStatus] = useState('');
    const [responseReceived, setResponseReceived] = useState(false);
    const [isDownloaded, setIsDownloaded] = useState(false);
    const [selectedPlannedStartDate, setSelectedPlannedStartDate] = useState('');
    const [file, setFile] = useState([]);
    const [res, setRes] = useState([]);
    const [error, setError] = useState([]);
    const [success, setSuccess] = useState([]);
    const [uploadProgress, setUploadProgress] = useState(0);
    const [uploadStarted, setUploadStarted] = useState(false);
    const [uploadComplete, setUploadComplete] = useState(false);
    const [name, setName] = useState([]);

    const status = [
        { value: 'NEW', label: 'New' },
        { value: 'UNDERPREPARATION', label: 'Under Preparation' },
        { value: 'PREPARED', label: 'Prepared' },
        { value: 'RELEASED', label: 'Released' },
        { value: 'WORKSTARTED', label: 'Work Started' },
        { value: 'WORKDONE', label: 'Work Done' },
        { value: 'REPORTED', label: 'Reported' },
        { value: 'FINISHED', label: 'Finished' },
        { value: 'CANCELLED', label: 'Cancelled' }
    ];

    useEffect(() => {
        const accessToken = localStorage.getItem('accessToken');

        axios.get(`${baseURL}/api/getSites`, {
            params: {
                accessToken: accessToken
            },
            headers: {
                'Content-Type': 'application/json'
            }
        })
            .then((response) => {
                if (response.data.length === 0) {
                    console.log("No data found");
                } else {
                    // Assuming response.data is an array of strings (site names)
                    let options = response.data.map((site) => ({
                        value: site,
                        label: site
                    }));
                    setSites(options);
                }
            })
            .catch((err) => {
                console.log(err);
            });
    }, []);

    const DownloadFile = (response) => {
        const contentDispositionHeader = response.headers['content-disposition'];
        const fileName = contentDispositionHeader
            ? contentDispositionHeader.split('filename=')[1].trim()
            : 'downloadedFile.xlsx';

        const blob = new Blob([response.data], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' });
        const url = window.URL.createObjectURL(blob);
        const link = document.createElement('a');
        link.href = url;
        link.setAttribute('download', fileName);
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
    };

    const sendRequest = (event) => {
        event.preventDefault();
        const accessToken = localStorage.getItem('accessToken');
        const data = {
            accessToken: accessToken,
            site: extractSite(selectedSite),
            status: selectedStatus,
            plannedStart: selectedPlannedStartDate
        };
        console.log(data);

        axios.get(`${baseURL}/api/getRouteChanges`, {
            params: data,
            headers: {
                'Content-Type': 'application/json'
            },
            responseType: 'blob'
        })
            .then((response) => {
                console.log(response);
                setFile(response);
                setResponseReceived(true);
            })
            .catch((err) => {
                console.log(err);
            });
    }

    const extractSite = (site) => {
        console.log(site);
        return site.split('-')[0];
    }

    // Log the state to debug
    useEffect(() => {
        console.log("Selected Site:", selectedSite);
        console.log("Selected Status:", selectedStatus);
        console.log("Selected Planned Start Date:", selectedPlannedStartDate);
    }, [selectedSite, selectedStatus, selectedPlannedStartDate]);

    const handleFileDownload = () => {
        DownloadFile(file);
        setIsDownloaded(true)
    }

    const DownloadErrorFile = (response) => {
        const contentDispositionHeader = response.headers['content-disposition'];
        const fileName = contentDispositionHeader
            ? contentDispositionHeader.split('filename=')[1].trim()
            : 'downloadedErrorFile.xlsx';

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
        setUploadProgress(40);
        setUploadStarted(true);
        const formData = new FormData();
        formData.append('file', file);
        formData.append('accessToken', localStorage.getItem('accessToken'));
        // formData.append('Name', name);
        axios.post(`${baseURL}/api/excelUploadRC`, formData, {
            headers: {
                'Content-Type': 'multipart/form-data'
            },
        })
            .then(response => {
                console.log(response);
                setRes("Success");
                setError(response.data.errorCount);
                setSuccess(response.data.successCount);
                setUploadProgress(100);
                console.log(response.data.errorCount);
                console.log(response.data.successCount);
                setResponseReceived(true);
            })
            .catch(err => {
                console.log(err);
                setRes(err.message);
                setUploadComplete(false);
            })
    }

    const handleErrorFileDownload = () => {
        axios.get(`${baseURL}/api/excelDownloadRC`, { responseType: 'blob' })
            .then(response => {
                DownloadErrorFile(response);
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

    const donutChart = {
        chart1: {
            label: "Summary",
            series: [success, error],
            colors: ["#FFD668", "#00e396"],
            dataLabels: ["Success", "Error"]
        }
    }

    return (
        <>
            <Navbar />
            <center>
                <h1 className='text-4xl text-blue-400 p-4'>Route Changes</h1>
                <br />
                <Box
                    component="form"
                    sx={{
                        '& .MuiTextField-root': { m: 1, width: '50ch' },
                    }}
                    noValidate
                    autoComplete="off"
                >
                    <div>
                        <TextField
                            id="filled-select-site"
                            select
                            SelectProps={{
                                native: true,
                            }}
                            helperText="Select the site for which the route changes are to be made"
                            value={selectedSite}
                            onChange={(e) => setSelectedSite(e.target.value)}
                        >
                            <option value="">Select Site</option>
                            {sites.map((option) => (
                                <option key={option.value} value={option.value}>
                                    {option.label}
                                </option>
                            ))}
                        </TextField>
                    </div>
                    <div>
                        <TextField
                            id="filled-select-status"
                            select
                            SelectProps={{
                                native: true,
                            }}
                            helperText="Select the status for which the route changes are to be made"
                            value={selectedStatus}
                            onChange={(e) => setSelectedStatus(e.target.value)}
                        >
                            <option value="">Select Status</option>
                            {status.map((option) => (
                                <option key={option.value} value={option.value}>
                                    {option.label}
                                </option>
                            ))}
                        </TextField>
                    </div>
                    <div>
                        <TextField
                            id="date"
                            label="Planned Start Date"
                            type="date"
                            sx={{ width: 220 }}
                            InputLabelProps={{
                                shrink: true,
                            }}
                            value={selectedPlannedStartDate}
                            onChange={(e) => setSelectedPlannedStartDate(e.target.value)}
                        />
                    </div>
                    <Button variant="contained" onClick={sendRequest}>Submit</Button>
                </Box>

                {responseReceived > 0 && (
                    <>
                        <p className="text-green-500">Download the Task Details from here</p>
                        <button className="bg-sky-400 p-2 rounded-lg" onClick={handleFileDownload}>
                            Download
                        </button>
                    </>
                )}

                <br />

                {/* {isDownloaded && ( */}
                    <>
                        <hr />

                        <h1 className='text-4xl text-blue-400 p-4'>Upload the updated details here</h1>
                        <input type="file" onChange={setDetails} />
                        <br />

                        <button className="bg-sky-400 p-2 rounded-lg" onClick={handleFileUpload}>
                            Upload
                        </button>
                    </>
                {/* )} */}
                <br />
                <br />

                {uploadStarted && (
                    <ProgressBar now={uploadProgress} label={`${uploadProgress}%`} style={{ width: '400px' }} />
                )}

                {uploadComplete && (
                    <center>
                        <h5>File uploaded successfully!</h5>
                    </center>
                )}

                <hr />
                <br />

                {error > 0 && (
                    <>
                        <p className="text-red-500">There are few errors in the file. Please download the error file and correct the errors.</p>
                        <button className="bg-sky-400 p-2 rounded-lg" onClick={handleErrorFileDownload}>
                            Download Error File
                        </button>
                    </>
                )}


                {res === "Success" && (
                    <DoughnutChart data={donutChart.chart1} />
                )}

            </center>
        </>
    );
}
