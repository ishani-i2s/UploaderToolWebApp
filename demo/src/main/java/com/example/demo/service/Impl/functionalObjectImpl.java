package com.example.demo.service.Impl;
import com.example.demo.entity.FunctionalObject;
import com.example.demo.repo.FunctionalObjectRepo;
import com.example.demo.service.FunctionalObjectService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import com.example.demo.controller.ExcelHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


@Service
public class functionalObjectImpl implements FunctionalObjectService {
    String baseURL="https://pemuto8-dev1.build.ifs.cloud";
    StringBuilder stringBuilder=new StringBuilder(baseURL);
    String accessToken= "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJzZEpGckdadDMyalpOVkdtaHAyenZ4cDEtQk96UmFPdmRCUkNaaGt1ZlY4In0.eyJleHAiOjE3MTU2NzI2OTcsImlhdCI6MTcxNTY2OTA5NywiYXV0aF90aW1lIjoxNzE1NjY5MDk1LCJqdGkiOiIxNjI4YmQ1NC02OTVmLTQ4NDktOTA0Mi0wNzRkNjE3M2JjNDMiLCJpc3MiOiJodHRwczovL3BlbXV0bzgtZGV2MS5idWlsZC5pZnMuY2xvdWQvYXV0aC9yZWFsbXMvcGVtdXRvOGRldjEiLCJhdWQiOlsicGVtdXRvOGRldjEiLCJhY2NvdW50Il0sInN1YiI6IjhjZjQxZmExLTAxYTItNDY0OC1iNWYxLWVlMTk3MTdhYTY5OSIsInR5cCI6IkJlYXJlciIsImF6cCI6IkkyU19UQVNLX0NBUkQiLCJzZXNzaW9uX3N0YXRlIjoiMmNhY2E4MzgtYTQ2MS00Mjk5LWE1NDYtOGQxNzQ0M2U4NThjIiwicmVhbG1fYWNjZXNzIjp7InJvbGVzIjpbImRlZmF1bHQtcm9sZXMtcGVtdXRvOGRldjEiLCJvZmZsaW5lX2FjY2VzcyIsInVtYV9hdXRob3JpemF0aW9uIl19LCJyZXNvdXJjZV9hY2Nlc3MiOnsiYWNjb3VudCI6eyJyb2xlcyI6WyJtYW5hZ2UtYWNjb3VudCIsIm1hbmFnZS1hY2NvdW50LWxpbmtzIiwidmlldy1wcm9maWxlIl19fSwic2NvcGUiOiJvcGVuaWQgbWljcm9wcm9maWxlLWp3dCBlbWFpbCBhdWRpZW5jZSBwcm9maWxlIiwic2lkIjoiMmNhY2E4MzgtYTQ2MS00Mjk5LWE1NDYtOGQxNzQ0M2U4NThjIiwidXBuIjoiaWZzYXBwIiwiZW1haWxfdmVyaWZpZWQiOmZhbHNlLCJncm91cHMiOlsiZGVmYXVsdC1yb2xlcy1wZW11dG84ZGV2MSIsIm9mZmxpbmVfYWNjZXNzIiwidW1hX2F1dGhvcml6YXRpb24iXSwicHJlZmVycmVkX3VzZXJuYW1lIjoiaWZzYXBwIn0.W6PMDjLOyvbS4gUrNQ_ZojwzDHARqQv8WEWuM-9VKbqEpPpma89WKTkjo_Ob9r_9_q3AsqE-UGzVUWDin-VlP__7zr5ILw9DqIOz7KLHQdGnp5IjwVP3iQxGM9YM6y8sWMhvp9OoXbf4zPwTRMgXlLL5yo3Z0M4GeJ5y73HDH_QwlVmJhE8ubQy6Vph7T8-AqyoZmmuaM2y37tn5KaYZBrhGPYDBmExavoO3GNrXX73Rzz9BvSxIhoNNY0gAzGuUOqYC3CTxJpg2Zaf05-R0UDkSnt-s13ucWvI2EFXPRzzeKbQmFR6c6aEJp76dDK_EBVik__pIx0ac3Mb5qC9npQ";
    @Autowired
    FunctionalObjectRepo functionalObject;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public List<FunctionalObject> save(MultipartFile file) {
        List<FunctionalObject> errorList = new ArrayList<>();
        try{
//            System.out.println("Inside the save method");
            List<FunctionalObject> funList = ExcelHelper.excelToFunList(file.getInputStream());

            //get the valid objLevel from an api call
            errorList = checkObjLevel(funList);
            System.out.println("The error list is"+errorList);

            //remove the invalid objLevel from the list
            funList.removeAll(errorList);
            System.out.println("The funList is"+funList);

            //save the valid functional objects to the database
            functionalObject.saveAll(funList);

            if(funList.size()==0){
                return errorList;
            }else{
                List<FunctionalObject> postStatus= postFunctionalObject(funList);
                System.out.println("The post status is"+postStatus);
                if(postStatus.size()>0){
                    errorList.addAll(postStatus);
                    return errorList;
                }else {
                    return errorList;
                }
            }
        }catch (Exception e) {
              return errorList;
        }
    }

    private List<FunctionalObject> postFunctionalObject(List<FunctionalObject> funList) {
        System.out.println("Inside the postFunctionalObject method");
//        System.out.println("The functional object list is"+funList);
        HttpHeaders headers= gethttpHeaders();
        stringBuilder.setLength(0);
        String url = stringBuilder.append(baseURL).append("/main/ifsapplications/projection/v1/FunctionalObjectHandling.svc/EquipmentFunctionalSet").toString();
        System.out.println("The url is"+url);
        List<FunctionalObject> invalidList= new ArrayList<>();
        int i=0;
        for (FunctionalObject fun : funList) {
            System.out.println("Step "+i++);
            Map<String, Object> payload = new java.util.HashMap<>();
            payload.put("Contract", fun.getSite());
            payload.put("MchCode", fun.getObjectId());
            payload.put("MchName", fun.getDescription());
            payload.put("SupContract", fun.getSite());
            payload.put("PmProgApplicationStatus", false);
            payload.put("IsCategoryObject", false);
            payload.put("IsGeographicObject", false);
            payload.put("SafetyCriticalElement", false);
            payload.put("SafeAccessCode", "NotRequired");
            payload.put("ObjLevel", fun.getObjLevel());
            payload.put("Company", "I2S100");
            HttpEntity<Map> httpEntity = new HttpEntity<>(payload, headers);
            System.out.println("The payload is"+payload);
            System.out.println("HttpEntity is"+httpEntity);
            try{
                var response= restTemplate.exchange(url, HttpMethod.POST, httpEntity, Map.class);
                if(response.getStatusCode().toString().equals("201 CREATED")){
                    fun.setLog("Successfull");
                    invalidList.add(fun);
                    continue;
                }else {
                    fun.setLog("POST API call failed");
                    invalidList.add(fun);
                }
            }catch (Exception e) {
                String errorResponse = e.getMessage();
                String errorMessage = extractErrorMessageFromJson(errorResponse);
                fun.setLog(errorMessage);
                invalidList.add(fun);
            }
        }
        System.out.println("The invalid list is"+invalidList);
        System.out.println("Posted all the functional objects");
        return invalidList;
    }


    private String extractErrorMessageFromJson(String errorResponse) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(errorResponse.substring(errorResponse.indexOf("{")));
            System.out.println("The root node is"+rootNode);
            JsonNode errorMessageNode = rootNode.path("error").path("message");
            return errorMessageNode.asText();
        } catch (IOException e) {
            e.printStackTrace();
            return "Error occurred while parsing JSON response.";
        }
    }

    private List<FunctionalObject> checkObjLevel(List<FunctionalObject> funList) {
        HttpEntity<Void> httpEntity= new HttpEntity<>(gethttpHeaders());
        stringBuilder.setLength(0);
        stringBuilder.append(baseURL);
        String url=stringBuilder.append("/main/ifsapplications/projection/v1/FunctionalObjectHandling.svc/Reference_EquipmentObjectLevel").toString();
        try{
            ResponseEntity<Map> responseEntity = restTemplate.exchange(url, HttpMethod.GET, httpEntity, Map.class);
            Map<String, Object> responseMap = responseEntity.getBody();
            System.out.println("The response map is" + responseMap);
            List<Map<String, Object>> responseList = (List<Map<String, Object>>) responseMap.get("value");
            List<String> objLevelList = new ArrayList<>();
            for (Map<String, Object> map : responseList) {
                objLevelList.add((String) map.get("ObjLevel"));
            }
            List<FunctionalObject> invalidList = new ArrayList<>();
            for (FunctionalObject fun : funList) {
                if (!objLevelList.contains(fun.getObjLevel())) {
                    fun.setLog("Invalid ObjLevel");
                    invalidList.add(fun);
                }
            }
            return invalidList;
        } catch (Exception e){
            List<FunctionalObject> invalidList = new ArrayList<>();
            for(FunctionalObject fun: funList){
                fun.setLog("API call failed");
                invalidList.add(fun);
            }
            return invalidList;
        }
    }

    private HttpHeaders gethttpHeaders() {
        HttpHeaders headers= new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + accessToken);
        return headers;
    }

    @Override
    public List<FunctionalObject> findAll() {
        return List.of();
    }
}
